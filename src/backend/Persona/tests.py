"""Unit tests for the Persona app.

These tests exercise the domain rules of the Torddis back-end at model level.
They deliberately avoid the Monitoreo views, because importing them
instantiates the AI recognition classes and resolves the host name at module
load time; see docs/testing/test-plan.md, Section 3.1.

Run with:

    cd src/backend
    python manage.py test Persona
"""

from datetime import date, timedelta
from unittest.mock import Mock

from django.test import TestCase, TransactionTestCase

from Persona.image import Image
from Persona.models import Personas, Supervisados, Tutores


def _fecha_hace_anios(anios, dias=0):
    """Return an ISO date exactly `anios` years and `dias` days ago."""
    hoy = date.today()
    try:
        objetivo = hoy.replace(year=hoy.year - anios)
    except ValueError:          # 29 February on a non-leap year
        objetivo = hoy.replace(year=hoy.year - anios, day=28)
    return (objetivo - timedelta(days=dias)).isoformat()


class CalcularEdadTests(TestCase):
    """UT-BE-01 — Personas.calcularEdad.

    Age drives the eligibility rule of FR-02 (a supervised child may not be
    older than 12), so it is tested independently of the persistence layer.
    """

    def test_devuelve_cero_para_la_fecha_de_hoy(self):
        anios, meses, dias = Personas.calcularEdad(date.today().isoformat())
        self.assertEqual((anios, meses, dias), (0, 0, 0))

    def test_cuenta_los_anios_completos(self):
        anios, _, _ = Personas.calcularEdad(_fecha_hace_anios(9))
        self.assertEqual(anios, 9)

    def test_un_dia_antes_del_cumpleanios_no_suma_el_anio(self):
        # 10 years ago plus one day into the future -> still 9 years old.
        casi_diez = (date.today().replace(year=date.today().year - 10)
                     + timedelta(days=1)).isoformat()
        anios, _, _ = Personas.calcularEdad(casi_diez)
        self.assertEqual(anios, 9)

    def test_acepta_un_objeto_date_ademas_de_una_cadena(self):
        anios, _, _ = Personas.calcularEdad(date.today().replace(
            year=date.today().year - 7))
        self.assertEqual(anios, 7)


class PersonasGuardarTests(TransactionTestCase):
    """UT-BE-02 — Personas.guardar.

    TransactionTestCase rather than TestCase: the rollback path of guardar()
    calls transaction.savepoint_rollback(), which is a no-op under autocommit
    (the production configuration) but raises TransactionManagementError
    inside the atomic block that TestCase wraps around each test. Running
    without that wrapper reproduces production behaviour.

    Finding: the savepoint/rollback pattern used here and in Tutores.guardar,
    Supervisados.guardar, Camaras.guardar, PermisosObjetos.activar and
    Monitoreo.activar is inert in production, because transaction.savepoint()
    returns None under autocommit. The atomicity the code appears to provide
    does not exist. Recorded rather than fixed, since fixing it changes
    production behaviour and belongs in a separate change.
    """

    def test_guarda_los_campos_recibidos_y_devuelve_si(self):
        persona = Personas()
        estado, guardada = persona.guardar({
            'persona__nombres': 'Ana',
            'persona__apellidos': 'Perez',
            'persona__fecha_nacimiento': '2015-04-10',
        })
        self.assertEqual(estado, 'si')
        self.assertIsNotNone(guardada.pk)
        self.assertEqual(guardada.nombres, 'Ana')
        self.assertEqual(Personas.objects.count(), 1)

    def test_una_fecha_invalida_no_deja_registros_a_medias(self):
        persona = Personas()
        estado, guardada = persona.guardar({
            'persona__nombres': 'Ana',
            'persona__apellidos': 'Perez',
            'persona__fecha_nacimiento': 'no-es-una-fecha',
        })
        self.assertEqual(estado, 'error')
        self.assertIsNone(guardada)
        self.assertEqual(Personas.objects.count(), 0)


class TutoresGuardarTests(TestCase):
    """UT-BE-03 — Tutores.guardar (FR-01)."""

    datos_base = {
        'usuario': 'tutora01',
        'clave': 'clave-de-prueba',
        'correo': 'tutora01@example.org',
        'persona__nombres': 'Maria',
        'persona__apellidos': 'Gomez',
        'persona__fecha_nacimiento': '1985-06-01',
    }

    def test_registra_un_tutor_nuevo(self):
        self.assertEqual(Tutores().guardar(dict(self.datos_base)), 'guardado')
        self.assertEqual(Tutores.objects.count(), 1)
        self.assertEqual(Personas.objects.count(), 1)

    def test_rechaza_un_usuario_repetido(self):
        Tutores().guardar(dict(self.datos_base))
        otros = dict(self.datos_base, correo='otra@example.org')
        self.assertEqual(Tutores().guardar(otros), 'usuario repetido')
        self.assertEqual(Tutores.objects.count(), 1)

    def test_la_clave_no_se_almacena_en_claro(self):
        """The `clave` column is an EncryptedTextField; the ciphertext stored
        in the database must not equal the plaintext."""
        Tutores().guardar(dict(self.datos_base))
        tutor = Tutores.objects.get(usuario='tutora01')
        # The ORM decrypts transparently...
        self.assertEqual(tutor.clave, 'clave-de-prueba')
        # ...but the raw column must not contain the plaintext.
        from django.db import connection
        with connection.cursor() as cur:
            cur.execute('SELECT clave FROM "Persona_tutores" WHERE id = %s',
                        [tutor.pk])
            crudo = cur.fetchone()[0]
        self.assertNotIn('clave-de-prueba', str(crudo))


class TutoresLoginTests(TestCase):
    """UT-BE-04 — Tutores.login (FR-01, PU-02)."""

    def setUp(self):
        Tutores().guardar({
            'usuario': 'tutora01',
            'clave': 'clave-de-prueba',
            'correo': 'tutora01@example.org',
            'persona__nombres': 'Maria',
            'persona__apellidos': 'Gomez',
            'persona__fecha_nacimiento': '1985-06-01',
        })

    def test_credenciales_correctas_devuelven_los_datos_del_tutor(self):
        resultado = Tutores.login({'usuario': 'tutora01',
                                   'clave': 'clave-de-prueba'})
        self.assertEqual(resultado['usuario'], 'tutora01')
        self.assertEqual(resultado['persona__nombres'], 'Maria')
        self.assertIn('id', resultado)

    def test_la_respuesta_de_login_no_incluye_la_clave(self):
        """A successful login must not echo the credential back to the client."""
        resultado = Tutores.login({'usuario': 'tutora01',
                                   'clave': 'clave-de-prueba'})
        self.assertNotIn('clave', resultado)

    def test_clave_incorrecta(self):
        resultado = Tutores.login({'usuario': 'tutora01', 'clave': 'otra'})
        self.assertEqual(resultado, {'tutores': 'credenciales incorrectas'})

    def test_usuario_inexistente_da_el_mismo_mensaje_que_clave_incorrecta(self):
        """Both failures return an identical message, so the response does not
        disclose whether the user name exists."""
        inexistente = Tutores.login({'usuario': 'nadie', 'clave': 'x'})
        incorrecta = Tutores.login({'usuario': 'tutora01', 'clave': 'x'})
        self.assertEqual(inexistente, incorrecta)


class SupervisadosGuardarTests(TestCase):
    """UT-BE-05 — Supervisados.guardar (FR-02).

    The age ceiling of 12 years is the eligibility rule of the study
    population and is therefore tested at its boundary.
    """

    def setUp(self):
        Tutores().guardar({
            'usuario': 'tutora01',
            'clave': 'clave-de-prueba',
            'correo': 'tutora01@example.org',
            'persona__nombres': 'Maria',
            'persona__apellidos': 'Gomez',
            'persona__fecha_nacimiento': '1985-06-01',
        })
        self.tutor = Tutores.objects.get(usuario='tutora01')

    def _guardar(self, fecha_nacimiento):
        return Supervisados().guardar({
            'tutor_id': self.tutor.pk,
            'persona__nombres': 'Luis',
            'persona__apellidos': 'Gomez',
            'persona__fecha_nacimiento': fecha_nacimiento,
        })

    def test_acepta_un_nino_de_nueve_anios(self):
        self.assertEqual(self._guardar(_fecha_hace_anios(9)), 'guardado')
        self.assertEqual(Supervisados.objects.count(), 1)

    def test_acepta_el_limite_de_doce_anios(self):
        self.assertEqual(self._guardar(_fecha_hace_anios(12)), 'guardado')

    def test_rechaza_trece_anios(self):
        self.assertEqual(self._guardar(_fecha_hace_anios(13)),
                         'Edad máxima 12 años')
        self.assertEqual(Supervisados.objects.count(), 0)

    def test_rechaza_un_dia_despues_del_decimotercer_cumpleanios(self):
        self.assertEqual(self._guardar(_fecha_hace_anios(13, dias=1)),
                         'Edad máxima 12 años')

    def test_un_tutor_puede_registrar_dos_ninos(self):
        """Two of the 19 participating guardians took part with two children
        each, so this combination must be supported."""
        self._guardar(_fecha_hace_anios(8))
        self._guardar(_fecha_hace_anios(11))
        self.assertEqual(
            Supervisados.objects.filter(tutor=self.tutor).count(), 2)


class SupervisadosEstadoTests(TestCase):
    """UT-BE-06 — Supervisados.cambiarEstado (FR-04, FR-06)."""

    def setUp(self):
        Tutores().guardar({
            'usuario': 'tutora01', 'clave': 'c', 'correo': 'a@b.org',
            'persona__nombres': 'Maria', 'persona__apellidos': 'Gomez',
            'persona__fecha_nacimiento': '1985-06-01',
        })
        tutor = Tutores.objects.get(usuario='tutora01')
        Supervisados().guardar({
            'tutor_id': tutor.pk,
            'persona__nombres': 'Luis', 'persona__apellidos': 'Gomez',
            'persona__fecha_nacimiento': _fecha_hace_anios(9),
        })
        self.supervisado = Supervisados.objects.first()

    def test_el_estado_inicial_es_no_distraido(self):
        self.assertFalse(self.supervisado.distraido)

    def test_marca_y_desmarca_la_distraccion(self):
        self.assertTrue(Supervisados.cambiarEstado(self.supervisado.pk, True))
        self.supervisado.refresh_from_db()
        self.assertTrue(self.supervisado.distraido)

        self.assertTrue(Supervisados.cambiarEstado(self.supervisado.pk, False))
        self.supervisado.refresh_from_db()
        self.assertFalse(self.supervisado.distraido)

    def test_un_supervisado_inexistente_devuelve_false(self):
        self.assertFalse(Supervisados.cambiarEstado(999999, True))


class ObtenerDatosTests(TestCase):
    """UT-BE-07 — Supervisados.obtener_datos filters by guardian."""

    def setUp(self):
        for usuario in ('tutora01', 'tutor02'):
            Tutores().guardar({
                'usuario': usuario, 'clave': 'c', 'correo': f'{usuario}@b.org',
                'persona__nombres': usuario, 'persona__apellidos': 'X',
                'persona__fecha_nacimiento': '1985-06-01',
            })
        self.t1 = Tutores.objects.get(usuario='tutora01')
        self.t2 = Tutores.objects.get(usuario='tutor02')
        for tutor, nombre in ((self.t1, 'Luis'), (self.t2, 'Sara')):
            Supervisados().guardar({
                'tutor_id': tutor.pk,
                'persona__nombres': nombre, 'persona__apellidos': 'Gomez',
                'persona__fecha_nacimiento': _fecha_hace_anios(9),
            })

    def test_un_tutor_solo_ve_a_sus_propios_supervisados(self):
        request = Mock()
        request.GET = {'tutor_id': str(self.t1.pk)}
        datos = list(Supervisados.obtener_datos(request))
        self.assertEqual(len(datos), 1)
        self.assertEqual(datos[0]['persona__nombres'], 'Luis')

    def test_incluye_la_edad_calculada(self):
        request = Mock()
        request.GET = {'tutor_id': str(self.t1.pk)}
        datos = list(Supervisados.obtener_datos(request))
        self.assertIn('años', datos[0]['persona__edad'])


class ImageTests(TestCase):
    """UT-BE-08 — Persona.image.Image, the base64 <-> file helper."""

    def test_get_file_devuelve_none_si_el_base64_es_invalido(self):
        file = Image()
        file.base64 = 'esto no es base64'
        self.assertIsNone(file.get_file())

    def test_get_base64_devuelve_none_si_la_ruta_no_existe(self):
        file = Image()
        file.ruta = 'no/existe.png'
        self.assertIsNone(file.get_base64())

    def test_get_file_decodifica_un_data_uri_valido(self):
        # 1x1 transparent PNG
        png = ('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAA'
               'Ffbc9AAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==')
        file = Image()
        file.base64 = png
        file.nombre_file = 'prueba'
        resultado = file.get_file()
        self.assertIsNotNone(resultado)
        self.assertTrue(resultado.name.endswith('.png'))
