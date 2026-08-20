"""
Elimina las imágenes de evidencia cuya antigüedad supera el período de
retención configurado, conservando el registro del evento (fecha, tipo de
distracción y observación).

Uso:
    python manage.py purgar_evidencias
    python manage.py purgar_evidencias --dias 3
    python manage.py purgar_evidencias --simular

Programación recomendada (Windows, Programador de tareas), diariamente:
    python D:\\ruta\\al\\backend\\manage.py purgar_evidencias
"""

from django.core.management.base import BaseCommand
from django.utils import timezone
from datetime import timedelta

from Monitoreo.models import Historial


class Command(BaseCommand):
    help = ('Elimina las imágenes de evidencia con más de N días de antigüedad, '
            'conservando el evento asociado.')

    def add_arguments(self, parser):
        parser.add_argument(
            '--dias',
            type=int,
            default=None,
            help='Días de retención. Por defecto usa TORDDIS_DIAS_RETENCION_EVIDENCIAS.',
        )
        parser.add_argument(
            '--simular',
            action='store_true',
            help='Muestra qué se eliminaría sin borrar nada.',
        )

    def handle(self, *args, **options):
        from django.conf import settings

        dias = options['dias']
        if dias is None:
            dias = getattr(settings, 'TORDDIS_DIAS_RETENCION_EVIDENCIAS', 7)

        limite = timezone.now() - timedelta(days=dias)

        pendientes = (Historial.objects
                      .filter(fecha_hora__lt=limite)
                      .exclude(imagen_evidencia='')
                      .exclude(imagen_evidencia__isnull=True))

        total = pendientes.count()

        if options['simular']:
            self.stdout.write(
                f'[simulación] Se eliminarían {total} imágenes anteriores a '
                f'{limite:%Y-%m-%d %H:%M} (retención: {dias} días).'
            )
            return

        eliminadas = 0
        errores = 0
        for h in pendientes.iterator():
            try:
                h.imagen_evidencia.delete(save=False)
                h.imagen_evidencia = ''
                h.save(update_fields=['imagen_evidencia'])
                eliminadas += 1
            except Exception as exc:
                errores += 1
                self.stderr.write(f'  Error en historial {h.pk}: {exc}')

        self.stdout.write(self.style.SUCCESS(
            f'Imágenes eliminadas: {eliminadas} de {total} '
            f'(retención: {dias} días). Errores: {errores}. '
            f'Los eventos correspondientes se conservan.'
        ))
