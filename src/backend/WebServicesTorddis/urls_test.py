"""Minimal URL configuration for the test suite.

The production URL configuration imports Monitoreo.views, which instantiates
the AI recognition classes at module level and therefore loads TensorFlow,
MediaPipe and OpenCV before a single test can run. The model-level tests do
not exercise any HTTP endpoint, so they are run against an empty URL
configuration instead.
"""

urlpatterns = []
