"""Test-only settings: run the suite on an in-memory SQLite database.

The production configuration targets PostgreSQL (see settings.py). These
tests exercise domain logic through the ORM and are backend-agnostic, so
running them on SQLite lets anyone who clones the repository execute them
without provisioning a database server.

    python manage.py test Persona Monitoreo --settings=WebServicesTorddis.settings_test
"""

from .settings import *  # noqa: F401,F403

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': ':memory:',
    }
}

# Evidence images are opt-in; the tests assert both states explicitly.
TORDDIS_GUARDAR_EVIDENCIAS = False

# Speed up the suite: the tests do not exercise password hashing.
PASSWORD_HASHERS = ['django.contrib.auth.hashers.MD5PasswordHasher']

# The tests exercise domain logic, not HTTP endpoints. Loading the production
# URLconf would import the AI recognition stack; see urls_test.py.
ROOT_URLCONF = 'WebServicesTorddis.urls_test'
