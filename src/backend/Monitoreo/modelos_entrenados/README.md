# Trained models

| File | Tracked | Provenance |
|------|---------|------------|
| `haarcascade_frontalface_default.xml` | yes | OpenCV Haar cascade for frontal face detection. Copyright (c) 2000, Intel Corporation; redistributed under the OpenCV BSD-3-Clause licence. Not trained by the authors. |
| `keras_model.h5`, `labels.txt` | yes | Facial-expression classifier. |
| `keras_model2.h5`, `labels2.txt` | yes | Distracting-object classifier. |
| `model.h5` | yes | Drowsiness classifier. |
| `reconocedor_facial.xml` | **no** | Per-child LBPH face-identity recogniser. Generated at run time; never committed. |

## Why the face-identity recogniser is not distributed

The face-identity module is enrolled separately for each child. At enrolment,
`Monitoreo/entrenamiento_facial.py` captures 200 frames of the child's face
through the ESP32-CAM stream, detects the face region with the Haar cascade
above, and fits an OpenCV LBPH model (radius 1, 8 neighbours, 8x8 grid,
16 384-bin histogram per training image). The resulting `reconocedor_facial.xml`
is a biometric template of an identified minor: its purpose is precisely to
decide whether a newly observed face belongs to that individual.

Such a file is biometric data for the purpose of unique identification. It is
therefore excluded from version control, from the public repository and from
the Zenodo deposit, and is listed in `.gitignore`. Reducing its numerical
precision or shuffling its histograms would not anonymise it; it would only
destroy the model while retaining the underlying data.

Excluding it costs nothing in reproducibility. The file recognises exactly one
individual, who is not the person reading this repository, and it is
regenerated from scratch on every enrolment.

### Regenerating it for your own deployment

1. Register a guardian account and a supervised child (use case UC-01).
2. Run the facial-training use case (UC-02) with the ESP32-CAM streaming.
3. The file is written to this directory automatically and ignored by Git.

## Training data of the distributed models

| Model | Training data | Reported accuracy |
|-------|---------------|-------------------|
| Facial expression | FER-2013 (48x48 greyscale, 7 expression classes). Evaluated on 7,178 held-out FER-2013 images; 50 epochs, batch size 64. | 86.28% training, 81% validation |
| Distracting object | 15 object classes (eraser, bottle, mobile phone, notebook, pen, pencil, laptop, glasses, backpack, plate, ruler, sharpener, scissors, USB stick, glass), 25 images per class, sourced from iStock. | see manuscript |
| Drowsiness | eye-state classification over the detected face region. | see manuscript |

The 15 object image sets are **not** redistributed here: the source images are
licensed stock content whose licence does not permit redistribution of the
original files. The class list and the per-class count above are provided so
that the training set can be reconstituted from an equivalent source.

### Known limitations

FER-2013 consists of low-resolution greyscale images of predominantly adult
faces, and was not validated on children or on Latin American populations.
Neither the expression model nor the object model was re-validated on the
study population. This is reported as a limitation in the manuscript.
