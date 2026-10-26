"""Unit tests for the Monitoreo app.

These tests exercise the domain rules of monitoring configuration, event
history and report aggregation at model level. They do not import
Monitoreo.views, because that module instantiates the AI recognition classes
and resolves the host name at import time; see docs/testing/test-plan.md,
Section 3.1.

Run with:

    cd src/backend
    python manage.py test Monitoreo
"""

import datetime
from unittest.mock import Mock

from django.test import TestCase, override_settings

from Monitoreo.models import (Camaras, Historial, Monitoreo, Objetos,
                              PermisosObjetos, TiposDistraccion)
from Persona.models import Supervisados, Tutores

# Identifiers assigned to the four distraction types by the recognition
# pipeline (Monitoreo/reconocimiento.py).
DIS_PERSONA, DIS_EXPRESION, DIS_SUENO, DIS_OBJETO = 1, 2, 3, 4


class TorddisTestCase(TestCase):
    """Shared fixture: one guardian, one child, the four distraction types."""

    def setUp(self):
        Tutores().guardar({
            'usuario': 'tutora01', 'clave': 'c', 'correo': 'a@b.org',
            'persona__nombres': 'Maria', 'persona__apellidos': 'Gomez',
            'persona__fecha_nacimiento': '1985-06-01',
        })
        self.tutor = Tutores.objects.get(usuario='tutora01')

        hace_nueve = datetime.date.today().replace(
            year=datetime.date.today().year - 9).isoformat()
        Supervisados().guardar({
            'tutor_id': self.tutor.pk,
            'persona__nombres': 'Luis', 'persona__apellidos': 'Gomez',
            'persona__fecha_nacimiento': hace_nueve,
        })
        self.supervisado = Supervisados.objects.first()

        for pk, nombre in ((DIS_PERSONA, 'Persona desconocida'),
                           (DIS_EXPRESION, 'Expresion facial'),
                           (DIS_SUENO, 'Sueño'),
                           (DIS_OBJETO, 'Objeto distractor')):
            TiposDistraccion.objects.create(pk=pk, nombre=nombre)

    def _request(self, **params):
        request = Mock()
        request.GET = {k: str(v) for k, v in params.items()}
        return request


class CamarasTests(TorddisTestCase):
    """UT-BE-09 — Camaras.guardar (FR-01, PU-05).

    One guardian may register exactly one device; the pilot deployed a single
    prototype per household.
    """

    def test_registra_un_dispositivo(self):
        camara = Camaras()
        resultado = camara.guardar({'direccion_ruta': '192.0.2.10',
                                    'tutor_id': self.tutor.pk})
        self.assertEqual(resultado, 'Dispositivo guardado')
        self.assertEqual(Camaras.objects.count(), 1)
        self.assertTrue(Camaras.objects.first().habilitada)

    def test_rechaza_un_segundo_dispositivo_para_el_mismo_tutor(self):
        Camaras().guardar({'direccion_ruta': '192.0.2.10',
                           'tutor_id': self.tutor.pk})
        resultado = Camaras().guardar({'direccion_ruta': '192.0.2.11',
                                       'tutor_id': self.tutor.pk})
        self.assertEqual(resultado, 'El tutor ya tiene una cámara')
        self.assertEqual(Camaras.objects.count(), 1)

    def test_permite_editar_el_dispositivo_existente(self):
        Camaras().guardar({'direccion_ruta': '192.0.2.10',
                           'tutor_id': self.tutor.pk})
        camara = Camaras.objects.first()
        resultado = camara.guardar({'direccion_ruta': '192.0.2.99',
                                    'tutor_id': self.tutor.pk})
        self.assertEqual(resultado, 'Dispositivo guardado')
        camara.refresh_from_db()
        self.assertEqual(camara.direccion_ruta, '192.0.2.99')

    def test_un_tutor_inexistente_devuelve_error(self):
        resultado = Camaras().guardar({'direccion_ruta': '192.0.2.10',
                                       'tutor_id': 999999})
        self.assertEqual(resultado, 'error')

    def test_obtener_datos_filtra_por_tutor(self):
        Camaras().guardar({'direccion_ruta': '192.0.2.10',
                           'tutor_id': self.tutor.pk})
        datos = list(Camaras.obtener_datos(self._request(tutor_id=self.tutor.pk)))
        self.assertEqual(len(datos), 1)
        self.assertEqual(datos[0]['direccion_ruta'], '192.0.2.10')


class PermisosObjetosTests(TorddisTestCase):
    """UT-BE-10 — PermisosObjetos.activar / desactivar (FR-03, PU-06)."""

    def setUp(self):
        super().setUp()
        self.objeto = Objetos.objects.create(nombre='Telefono movil')

    def test_activa_un_objeto(self):
        resultado = PermisosObjetos().activar({'tutor_id': self.tutor.pk,
                                               'objeto_id': self.objeto.pk})
        self.assertEqual(resultado, 'activado')
        self.assertEqual(PermisosObjetos.objects.count(), 1)

    def test_no_duplica_un_objeto_ya_activado(self):
        PermisosObjetos().activar({'tutor_id': self.tutor.pk,
                                   'objeto_id': self.objeto.pk})
        resultado = PermisosObjetos().activar({'tutor_id': self.tutor.pk,
                                               'objeto_id': self.objeto.pk})
        self.assertEqual(resultado, 'el objeto ya está activado')
        self.assertEqual(PermisosObjetos.objects.count(), 1)

    def test_objeto_inexistente(self):
        resultado = PermisosObjetos().activar({'tutor_id': self.tutor.pk,
                                               'objeto_id': 999999})
        self.assertEqual(resultado, 'no existe el objeto')

    def test_desactiva_un_objeto(self):
        PermisosObjetos().activar({'tutor_id': self.tutor.pk,
                                   'objeto_id': self.objeto.pk})
        resultado = PermisosObjetos().desactivar(
            self._request(tutor_id=self.tutor.pk, objeto_id=self.objeto.pk))
        self.assertEqual(resultado, 'eliminado')
        self.assertEqual(PermisosObjetos.objects.count(), 0)

    def test_desactivar_lo_que_no_estaba_activo(self):
        resultado = PermisosObjetos().desactivar(
            self._request(tutor_id=self.tutor.pk, objeto_id=self.objeto.pk))
        self.assertEqual(resultado, 'el objeto no tiene un permiso')

    def test_obtener_datos_marca_el_estado_de_cada_objeto(self):
        Objetos.objects.create(nombre='Juguete')
        PermisosObjetos().activar({'tutor_id': self.tutor.pk,
                                   'objeto_id': self.objeto.pk})
        datos = list(PermisosObjetos.obtener_datos(
            self._request(tutor_id=self.tutor.pk)))
        por_nombre = {d['nombre']: d['habilitado'] for d in datos}
        self.assertTrue(por_nombre['Telefono movil'])
        self.assertFalse(por_nombre['Juguete'])


class MonitoreoParametrosTests(TorddisTestCase):
    """UT-BE-11 — Monitoreo.activar / desactivar (FR-04)."""

    def test_activa_un_tipo_de_distraccion(self):
        fallo, mensaje = Monitoreo().activar({'tutor_id': self.tutor.pk,
                                              'tipo_dist_id': DIS_SUENO})
        self.assertNotEqual(fallo, 'error')
        self.assertEqual(mensaje, 'Parámetro activado')

    def test_no_duplica_un_parametro_activo(self):
        Monitoreo().activar({'tutor_id': self.tutor.pk,
                             'tipo_dist_id': DIS_SUENO})
        fallo, mensaje = Monitoreo().activar({'tutor_id': self.tutor.pk,
                                              'tipo_dist_id': DIS_SUENO})
        self.assertEqual(fallo, 'error')
        self.assertEqual(mensaje, 'el tipo de distracción ya está activado')
        self.assertEqual(Monitoreo.objects.count(), 1)

    def test_tipo_de_distraccion_inexistente(self):
        fallo, mensaje = Monitoreo().activar({'tutor_id': self.tutor.pk,
                                              'tipo_dist_id': 999})
        self.assertEqual(fallo, 'error')
        self.assertEqual(mensaje, 'no existe el tipo de distracción')

    def test_los_cuatro_parametros_pueden_coexistir(self):
        for tipo in (DIS_PERSONA, DIS_EXPRESION, DIS_SUENO, DIS_OBJETO):
            Monitoreo().activar({'tutor_id': self.tutor.pk,
                                 'tipo_dist_id': tipo})
        self.assertEqual(
            Monitoreo.objects.filter(tutor=self.tutor).count(), 4)

    def test_desactiva_un_parametro(self):
        Monitoreo().activar({'tutor_id': self.tutor.pk,
                             'tipo_dist_id': DIS_SUENO})
        resultado = Monitoreo().desactivar(
            self._request(tutor_id=self.tutor.pk, tipo_dist_id=DIS_SUENO))
        self.assertEqual(resultado, 'Parámetro desactivado')
        self.assertEqual(Monitoreo.objects.count(), 0)


class ExisteDistraccionTests(TorddisTestCase):
    """UT-BE-12 — Monitoreo.existeDistraccion.

    This is the endpoint the ESP32-CAM polls to decide whether to fire the
    buzzer and the LED (FR-06).
    """

    def setUp(self):
        super().setUp()
        Camaras().guardar({'direccion_ruta': '192.0.2.10',
                           'tutor_id': self.tutor.pk})

    def test_sin_distraccion_devuelve_false(self):
        self.assertFalse(Monitoreo.existeDistraccion(
            self._request(direccion_ruta='192.0.2.10')))

    def test_con_distraccion_devuelve_true(self):
        Supervisados.cambiarEstado(self.supervisado.pk, True)
        self.assertTrue(Monitoreo.existeDistraccion(
            self._request(direccion_ruta='192.0.2.10')))

    def test_una_camara_desconocida_devuelve_false(self):
        Supervisados.cambiarEstado(self.supervisado.pk, True)
        self.assertFalse(Monitoreo.existeDistraccion(
            self._request(direccion_ruta='203.0.113.7')))

    def test_sin_parametro_devuelve_false(self):
        self.assertFalse(Monitoreo.existeDistraccion(self._request()))


class HistorialCrearTests(TorddisTestCase):
    """UT-BE-13 — Historial.crear (FR-05).

    The privacy-relevant behaviour is that no evidence image is stored unless
    the deployment opts in through TORDDIS_GUARDAR_EVIDENCIAS. This is the
    setting that implements the data-minimisation commitment declared in the
    manuscript, so it is tested in both states.
    """

    @override_settings(TORDDIS_GUARDAR_EVIDENCIAS=False)
    def test_registra_el_evento_sin_imagen_por_defecto(self):
        creado = Historial.crear(self.supervisado.pk, 'Feliz',
                                 DIS_EXPRESION, imagen=None)
        self.assertTrue(creado)
        historial = Historial.objects.get()
        self.assertEqual(historial.observacion, 'Feliz')
        self.assertEqual(historial.tipo_distraccion_id, DIS_EXPRESION)
        self.assertFalse(historial.imagen_evidencia)

    @override_settings(TORDDIS_GUARDAR_EVIDENCIAS=False)
    def test_descarta_la_imagen_aunque_se_reciba(self):
        """Opt-out must win even when the caller supplies a frame."""
        import numpy as np
        frame = np.zeros((48, 48, 3), dtype=np.uint8)
        Historial.crear(self.supervisado.pk, 'Triste', DIS_EXPRESION, frame)
        self.assertFalse(Historial.objects.get().imagen_evidencia)

    def test_un_supervisado_inexistente_no_crea_historial(self):
        self.assertFalse(Historial.crear(999999, 'Feliz',
                                         DIS_EXPRESION, None))
        self.assertEqual(Historial.objects.count(), 0)

    def test_un_tipo_de_distraccion_inexistente_no_crea_historial(self):
        self.assertFalse(Historial.crear(self.supervisado.pk, 'X', 99, None))
        self.assertEqual(Historial.objects.count(), 0)

    def test_registra_la_fecha_y_hora_del_evento(self):
        antes = datetime.datetime.now()
        Historial.crear(self.supervisado.pk, 'Neutral', DIS_EXPRESION, None)
        historial = Historial.objects.get()
        self.assertGreaterEqual(historial.fecha_hora.replace(tzinfo=None),
                                antes.replace(microsecond=0))


class HistorialGraficosTests(TorddisTestCase):
    """UT-BE-14 — Historial.graficos (FR-07, PU-10).

    The report covers a seven-day window ending on the requested date.
    """

    def _evento(self, observacion, tipo, dias_atras=0):
        historial = Historial.objects.create(
            fecha_hora=datetime.datetime.now() - datetime.timedelta(days=dias_atras),
            observacion=observacion,
            supervisado=self.supervisado,
            tipo_distraccion=TiposDistraccion.objects.get(pk=tipo),
        )
        return historial

    def test_sin_historial_devuelve_lista_vacia(self):
        hoy = datetime.date.today().isoformat()
        resultado = Historial.graficos(
            self._request(supervisado_id=self.supervisado.pk, fecha=hoy))
        self.assertEqual(resultado, [])

    def test_devuelve_los_tres_graficos(self):
        self._evento('Feliz', DIS_EXPRESION)
        resultado = Historial.graficos(self._request(
            supervisado_id=self.supervisado.pk,
            fecha=datetime.date.today().isoformat()))
        tipos = [g['tipo_grafico'] for g in resultado]
        self.assertEqual(tipos, ['Expresiones', 'Sueño', 'Objetos'])

    def test_cuenta_las_expresiones_por_categoria(self):
        self._evento('Feliz', DIS_EXPRESION)
        self._evento('Feliz', DIS_EXPRESION)
        self._evento('Triste', DIS_EXPRESION)
        resultado = Historial.graficos(self._request(
            supervisado_id=self.supervisado.pk,
            fecha=datetime.date.today().isoformat()))
        expresiones = resultado[0]
        self.assertEqual(expresiones['feliz'], 2)
        self.assertEqual(expresiones['triste'], 1)
        self.assertEqual(expresiones['enfadado'], 0)

    def test_agrupa_sueno_y_objetos_por_dia(self):
        self._evento('Sueño', DIS_SUENO, dias_atras=0)
        self._evento('Sueño', DIS_SUENO, dias_atras=0)
        self._evento('Telefono', DIS_OBJETO, dias_atras=2)
        resultado = Historial.graficos(self._request(
            supervisado_id=self.supervisado.pk,
            fecha=datetime.date.today().isoformat()))
        sueno, objetos = resultado[1], resultado[2]
        self.assertEqual(sueno['dia_1'], 2)      # today
        self.assertEqual(objetos['dia_3'], 1)    # two days ago
        self.assertEqual(objetos['dia_1'], 0)

    def test_la_ventana_es_de_siete_dias(self):
        """An event eight days old falls outside the seven-day window.

        Note the asymmetry in graficos(): with no events at all it returns an
        empty list, but when every event falls outside the window it returns
        the three charts with zero counts, because the emptiness check runs
        on the whole history before the date filter is applied. Two different
        representations of "nothing to report".
        """
        self._evento('Sueño', DIS_SUENO, dias_atras=8)
        resultado = Historial.graficos(self._request(
            supervisado_id=self.supervisado.pk,
            fecha=datetime.date.today().isoformat()))
        self.assertEqual(len(resultado), 3)
        self.assertEqual(resultado[1]['tipo_grafico'], 'Sueño')
        self.assertEqual(sum(resultado[1][f'dia_{i}'] for i in range(1, 8)), 0)

    def test_supervisado_inexistente(self):
        resultado = Historial.graficos(self._request(
            supervisado_id=999999,
            fecha=datetime.date.today().isoformat()))
        self.assertEqual(resultado, {'grafico': 'no existe el supervisado'})


class HistorialObtenerDatosTests(TorddisTestCase):
    """UT-BE-15 — Historial.obtener_datos (FR-05, PU-09)."""

    def test_sin_parametros_devuelve_lista_vacia(self):
        self.assertEqual(Historial.obtener_datos(self._request()), [])

    def test_filtra_por_supervisado_y_fecha(self):
        Historial.objects.create(
            fecha_hora=datetime.datetime.now(), observacion='Feliz',
            supervisado=self.supervisado,
            tipo_distraccion=TiposDistraccion.objects.get(pk=DIS_EXPRESION))
        datos = list(Historial.obtener_datos(self._request(
            supervisado_id=self.supervisado.pk,
            fecha=datetime.date.today().isoformat())))
        self.assertEqual(len(datos), 1)
        self.assertEqual(datos[0]['observacion'], 'Feliz')
