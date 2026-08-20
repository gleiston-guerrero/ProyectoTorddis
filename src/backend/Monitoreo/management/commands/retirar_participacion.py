"""
Retira por completo la participación de un menor: elimina su modelo
biométrico, sus imágenes de entrenamiento si aún existieran, sus imágenes
de evidencia y, opcionalmente, su historial de eventos.

Implementa el derecho de supresión que el formulario de consentimiento
reconoce al representante legal.

Uso:
    python manage.py retirar_participacion --supervisado 3 --simular
    python manage.py retirar_participacion --supervisado 3
    python manage.py retirar_participacion --supervisado 3 --borrar-eventos
"""

import os
import shutil

from django.core.management.base import BaseCommand, CommandError

from Monitoreo.models import Historial
from Persona.models import Supervisados

RUTA_MODELOS = os.path.join('Monitoreo', 'modelos_entrenados')
RUTA_ROSTROS = os.path.join('media', 'Perfiles', 'img_entrenamiento')


class Command(BaseCommand):
    help = 'Elimina los datos biométricos y las imágenes asociadas a un menor.'

    def add_arguments(self, parser):
        parser.add_argument('--supervisado', type=int, required=True,
                            help='Identificador del menor (Supervisados.pk).')
        parser.add_argument('--borrar-eventos', action='store_true',
                            help='Elimina también los registros del historial, '
                                 'no solo sus imágenes.')
        parser.add_argument('--simular', action='store_true',
                            help='Muestra qué se eliminaría sin borrar nada.')

    def handle(self, *args, **options):
        pk = options['supervisado']
        simular = options['simular']
        prefijo = '[simulación] ' if simular else ''

        try:
            supervisado = Supervisados.objects.get(pk=pk)
        except Supervisados.DoesNotExist:
            raise CommandError(f'No existe un menor con identificador {pk}.')

        # 1. Modelo biométrico individual
        modelo = os.path.join(RUTA_MODELOS, f'reconocedor_{pk}.xml')
        if os.path.isfile(modelo):
            self.stdout.write(f'{prefijo}Eliminando modelo biométrico: {modelo}')
            if not simular:
                os.remove(modelo)
        else:
            self.stdout.write(f'  No se encontró modelo individual en {modelo}')

        # 2. Imágenes de entrenamiento residuales
        carpeta = os.path.join(RUTA_ROSTROS, str(pk))
        if os.path.isdir(carpeta):
            n = len(os.listdir(carpeta))
            self.stdout.write(f'{prefijo}Eliminando {n} imágenes de entrenamiento: {carpeta}')
            if not simular:
                shutil.rmtree(carpeta, ignore_errors=True)
        else:
            self.stdout.write('  No quedan imágenes de entrenamiento.')

        # 3. Imágenes de evidencia del historial
        eventos = Historial.objects.filter(supervisado_id=pk)
        con_imagen = eventos.exclude(imagen_evidencia='').exclude(imagen_evidencia__isnull=True)
        self.stdout.write(f'{prefijo}Eliminando {con_imagen.count()} imágenes de evidencia.')
        if not simular:
            for h in con_imagen.iterator():
                h.imagen_evidencia.delete(save=False)
                h.imagen_evidencia = ''
                h.save(update_fields=['imagen_evidencia'])

        # 4. Fotografía de perfil
        persona = supervisado.persona
        if getattr(persona, 'foto_perfil', None):
            self.stdout.write(f'{prefijo}Eliminando fotografía de perfil.')
            if not simular:
                persona.foto_perfil.delete(save=False)
                persona.foto_perfil = None
                persona.save(update_fields=['foto_perfil'])

        # 5. Eventos, solo si se solicita expresamente
        if options['borrar_eventos']:
            self.stdout.write(f'{prefijo}Eliminando {eventos.count()} registros de historial.')
            if not simular:
                eventos.delete()
        else:
            self.stdout.write('  Los eventos del historial se conservan sin imagen. '
                              'Use --borrar-eventos para eliminarlos también.')

        if simular:
            self.stdout.write(self.style.WARNING('Simulación finalizada. No se borró nada.'))
        else:
            self.stdout.write(self.style.SUCCESS(
                f'Participación del menor {pk} retirada correctamente.'
            ))
