# Torddis — Reproducibility Package

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.21367334.svg)](https://doi.org/10.5281/zenodo.21367334)
[![License: MIT (code)](https://img.shields.io/badge/Code%20License-MIT-yellow.svg)](LICENSE-CODE)
[![License: CC BY 4.0 (docs)](https://img.shields.io/badge/Docs%20License-CC%20BY%204.0-lightgrey.svg)](LICENSE-DOCS)
[![Python 3.11+](https://img.shields.io/badge/Python-3.11+-blue.svg)](https://www.python.org/)
[![Android SDK 33+](https://img.shields.io/badge/Android-SDK%2033+-green.svg)](https://developer.android.com/)

Reproducibility package for the manuscript:

> **Torddis: A real-time IoT and AI-based system to support at-home learning and mitigate parental absenteeism**
> Submitted to *Education and Information Technologies* (Springer Nature).

---

## Table of contents

1. [What this repository contains](#what-this-repository-contains)
2. [How to cite](#how-to-cite)
3. [Repository structure](#repository-structure)
4. [Getting started](#getting-started)
5. [Reproducing the statistical results](#reproducing-the-statistical-results)
6. [Reproducing the IoT prototype](#reproducing-the-iot-prototype)
7. [Standards compliance](#standards-compliance)
8. [Ethical considerations and data protection](#ethical-considerations-and-data-protection)
9. [Licences](#licences)
10. [Contact and support](#contact-and-support)

---

## What this repository contains

Torddis is a real-time IoT + Artificial Intelligence system that helps guardians (parents, tutors, elder siblings) supervise primary-school children while they perform homework at home. The system monitors four distraction parameters --- presence, facial expression, drowsiness and use of non-permitted objects --- and emits visual (LED) and auditory (buzzer) alerts to help the child re-focus.

This repository is the **companion reproducibility package** of the manuscript submitted to *Education and Information Technologies*. It contains, in one place, every artefact that a reader needs to understand, verify, replicate and extend the study:

- 📄 **Documentation** of the system architecture (aligned with ISO/IEC/IEEE 42010:2022), the functional and non-functional requirements (aligned with ISO/IEC/IEEE 29148:2018) and the testing artefacts (aligned with ISO/IEC/IEEE 29119-1:2022).
- 💻 **Complete source code** for the three system layers: the Android mobile application (Java), the ESP32-CAM firmware (C/C++), and the Django-based back-end + AI modules (Python).
- 📊 **Pseudonymised experimental data** collected from 12 guardians and their primary-school children (SUS responses, effectiveness Likert responses, open-ended answers and recognition-latency measurements from 14 test sessions).
- 📈 **Statistical analysis notebooks** (Jupyter) that regenerate the results reported in the manuscript.
- 📋 **Data-collection instruments** (SUS, demographic survey, effectiveness questionnaire, open-ended questions) in Spanish (as administered) and English (translated for international review).
- 🔐 **Ethics documentation** and templates for informed consent and minor assent in Spanish (canonical) and English (translated).

---

## How to cite

If you use this repository, please cite **both** the manuscript **and** the Zenodo deposit:

```bibtex
@article{TorddisEAIT2026,
    author  = {Almeida-Due\~nas, Carlos and Plazarte-Su\'arez, John and
               Guerrero-Ulloa, Gleiston and Erazo-Moreta, Orlando and
               Hornos, Miguel J. and Rodr\'iguez-Dom\'inguez, Carlos},
    title   = {Torddis: A real-time {IoT} and {AI}-based system to support
               at-home learning and mitigate parental absenteeism},
    journal = {Education and Information Technologies},
    year    = {2026},
    note    = {Under review}
}

@dataset{TorddisRepo2026,
    author    = {Almeida-Due\~nas, Carlos and Plazarte-Su\'arez, John and
                 Guerrero-Ulloa, Gleiston and Erazo-Moreta, Orlando and
                 Hornos, Miguel J. and Rodr\'iguez-Dom\'inguez, Carlos},
    title     = {{Torddis} reproducibility package (source code, data,
                 documentation and analysis notebooks)},
    year      = {2026},
    publisher = {Zenodo},
    doi       = {10.5281/zenodo.21367334},
    url       = {https://doi.org/10.5281/zenodo.21367334}
}
```

A machine-readable citation is provided in [`CITATION.cff`](CITATION.cff).

---

## Repository structure

```
torddis/
├── README.md                                     ← this file
├── LICENSE-CODE                                  ← MIT (applies to src/, analysis/)
├── LICENSE-DOCS                                  ← CC-BY 4.0 (applies to docs/, data/, instruments/, ethics/)
├── CITATION.cff                                  ← machine-readable citation metadata
├── CHANGELOG.md                                  ← version history (semver)
├── CONTRIBUTING.md                               ← contribution guidelines
├── CODE_OF_CONDUCT.md                            ← Contributor Covenant v2.1
├── SECURITY.md                                   ← how to report security issues
├── .zenodo.json                                  ← DOI metadata for Zenodo
├── .gitignore                                    ← files excluded from version control
├── .gitattributes                                ← linguist and EOL configuration
│
├── docs/                                         ← Scientific documentation
│   ├── architecture/                             ← ISO/IEC/IEEE 42010:2022
│   │   ├── 01-stakeholders-and-concerns.md
│   │   └── 05-rationale.md                       ← architecture decision records (ADRs)
│   ├── requirements/                             ← ISO/IEC/IEEE 29148:2018
│   │   ├── functional-requirements.csv           (FR-01…FR-07)
│   │   ├── non-functional-requirements.csv       (NFR-01…NFR-05 + ISO/IEC 25010:2023 mapping)
│   │   ├── traceability-matrix.csv               (FR/NFR ↔ UC ↔ Test ↔ Code module)
│   │   └── use-cases/
│   │       └── UC-04-real-time-monitoring.md
│   ├── testing/                                  ← ISO/IEC/IEEE 29119-1:2022
│   │   └── test-plan.md
│   └── standards-compliance/
│       └── ISO-IEC-25010-2023-checklist.md
│
├── src/                                          ← System source code
│   ├── mobile-app/                               ← Android application (Java, SDK 33+)
│   ├── iot-device/
│   │   └── esp32-cam/                            ← ESP32-CAM firmware (C/C++)
│   ├── backend/                                  ← Django REST back-end (Python 3.11)
│   └── README.md                                 ← how each module maps to the manuscript
│
├── data/                                         ← Pseudonymised experimental data
│   ├── raw/                                      ← as collected (after anonymisation)
│   │   ├── sus-responses.csv                     (12 guardians × 10 SUS items)
│   │   ├── likert-effectiveness.csv              (12 guardians × 7 statements + demographics)
│   │   ├── recognition-latencies.csv             (14 sessions × 4 AI modules)
│   │   └── open-ended-responses.csv              (12 guardians × 8 open questions)
│   ├── processed/                                ← derived tables (generated by notebooks)
│   ├── data-dictionary.md                        ← variable-by-variable description
│   └── README.md                                 ← data-use protocol
│
├── analysis/                                     ← Statistical analysis notebooks
│   ├── 01-normality-tests.ipynb                  ← Shapiro–Wilk and Kolmogorov–Smirnov
│   ├── 02-pearson-correlation.ipynb              ← Pearson r matrix and correlation with Statement (5)
│   ├── 03-anova-analysis.ipynb                   ← one-way ANOVA
│   ├── 07-sus-score-computation.ipynb            ← SUS scoring and adjective rating
│   ├── environment.yml                           (Conda)
│   ├── requirements.txt                          (pip)
│   └── README.md
│
├── instruments/                                  ← Data-collection instruments
│   ├── SUS-questionnaire-EN.md                   ← original SUS reference
│   ├── SUS-questionnaire-ES.md                   ← version administered to participants
│   ├── demographic-survey-EN.md                  ← English translation
│   ├── demographic-survey-ES.md                  ← canonical version
│   ├── effectiveness-likert-EN.md                ← English translation
│   ├── effectiveness-likert-ES.md                ← canonical version
│   ├── open-ended-questions-EN.md                ← English translation
│   ├── open-ended-questions-ES.md                ← canonical version
│   └── README.md
│
├── ethics/                                       ← Ethics and privacy documentation
│   ├── informed-consent-template-EN.md           ← English translation
│   ├── informed-consent-template-ES.md           ← canonical version
│   ├── minor-assent-template-EN.md               ← English translation
│   ├── minor-assent-template-ES.md               ← canonical version
│   ├── data-management-plan.md                   ← FAIR-aligned DMP
│   ├── privacy-impact-assessment.md              ← PIA aligned with ISO/IEC 27001:2022
│   └── README.md
│
├── evaluation/                                   ← Original evaluation spreadsheets
│   ├── Resultados de evaluaciones.xlsx           ← raw pseudonymised data (5 sheets)
│   └── Resultados interpretados de los indicadores de evaluación.xlsx
│
└── .github/
    ├── workflows/
    │   ├── ci-android.yml                        ← builds the Android APK on push
    │   └── ci-python.yml                         ← executes analysis notebooks on push
    ├── ISSUE_TEMPLATE/
    │   ├── bug_report.md
    │   └── feature_request.md
    ├── PULL_REQUEST_TEMPLATE.md
    ├── FUNDING.yml
    └── dependabot.yml
```

---

## Getting started

### Prerequisites

- Python **3.11 or later** (for the analysis notebooks and back-end).
- Android Studio **Hedgehog (2023.1.1)** or later, with SDK 33+, for the mobile app.
- PlatformIO **6.x** or the Arduino IDE **2.x**, for the ESP32-CAM firmware.
- Git **2.30+**.

### One-command bootstrap for the analysis

```bash
git clone https://github.com/CarlosAlmeida2000/ProyectoTorddis.git torddis
cd torddis/analysis
conda env create -f environment.yml
conda activate torddis
jupyter lab
```

Open `01-normality-tests.ipynb` and execute all cells to regenerate the normality-test results reported in Table 10 of the manuscript.

---

## Reproducing the statistical results

Every table and figure of the Results section of the manuscript is regenerated by one of the notebooks in `analysis/` from the real (pseudonymised) data in `data/raw/`:

| Manuscript element | Notebook | Data input |
|--------------------|----------|------------|
| Table 6 --- Aggregated recognition results | Direct from `data/raw/recognition-latencies.csv` | recognition-latencies.csv |
| Table 7 --- Central tendency of latency | `02-pearson-correlation.ipynb` | recognition-latencies.csv |
| Table 8 --- Correlation matrix of latencies | `02-pearson-correlation.ipynb` | recognition-latencies.csv |
| Table 9 --- Descriptive statistics | `01-normality-tests.ipynb` | likert-effectiveness.csv |
| Table 10 --- Normality analysis | `01-normality-tests.ipynb` | likert-effectiveness.csv |
| Table 11 --- Pearson correlations | `02-pearson-correlation.ipynb` | likert-effectiveness.csv |
| Table 12 --- ANOVA | `03-anova-analysis.ipynb` | likert-effectiveness.csv |
| SUS score aggregate (81.46 ± 11.65) | `07-sus-score-computation.ipynb` | sus-responses.csv |

Random seeds are pinned (`RANDOM_SEED = 42`); identical outputs are guaranteed on any platform running the pinned versions declared in `environment.yml`.

The Tukey HSD post-hoc tests (Tables 13-16) and the Kruskal-Wallis + Spearman analyses (Table 17) can be computed directly from `likert-effectiveness.csv` using `scipy.stats` and `statsmodels.stats.multicomp.pairwise_tukeyhsd`; additional dedicated notebooks are planned for a future release.

---

## Reproducing the IoT prototype

### 1. Mobile application (Android)

```bash
cd src/mobile-app
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

Requires the back-end URL to be configured in `res/values/strings.xml` (`api_base_url` field).

### 2. IoT device firmware (ESP32-CAM)

```bash
cd src/iot-device/esp32-cam
# Using the Arduino IDE 2.x: open CameraTorddis.ino, select "AI Thinker ESP32-CAM" as board, and upload.
# Using PlatformIO 6.x: pio run --target upload
```

Wire the buzzer and LED indicators to available GPIOs of the ESP32-CAM as described in Section 3.2 of the manuscript. The full bill of materials is in Section 3.2 of the manuscript.

### 3. Back-end + AI modules

```bash
cd src/backend
python -m venv .venv
source .venv/bin/activate   # or .venv\Scripts\activate on Windows
pip install -r requirements.txt
python manage.py migrate
python manage.py loaddata Persona/fixtures/*.json Monitoreo/fixtures/*.json
python manage.py runserver
```

The AI recognition pipeline (`Monitoreo/reconocimiento.py`, `Monitoreo/entrenamiento_facial.py`) loads pre-trained weights from `Monitoreo/modelos_entrenados/`. Retraining is documented in `src/backend/comandos.txt`.

---

## Standards compliance

The Torddis system and its documentation follow the international standards listed below. The corresponding checklists and evidence are in `docs/standards-compliance/`.

| Standard | Scope | Evidence |
|----------|-------|----------|
| ISO/IEC/IEEE 12207:2017 | Software life-cycle processes | Whole repository (TDDM4IoTS methodology followed) |
| ISO/IEC/IEEE 15288:2023 | System life-cycle processes | `docs/` |
| ISO/IEC/IEEE 29148:2018 | Requirements engineering | `docs/requirements/` |
| ISO/IEC 25010:2023 | Product quality model | `docs/requirements/non-functional-requirements.csv`, `docs/standards-compliance/ISO-IEC-25010-2023-checklist.md` |
| ISO/IEC/IEEE 42010:2022 | Architecture description | `docs/architecture/` |
| ISO/IEC/IEEE 29119-1:2022 | Software testing | `docs/testing/test-plan.md` |
| ISO/IEC 27001:2022 | Information security | `ethics/privacy-impact-assessment.md` |
| FAIR Data Principles (2016) | Data reuse | This repository is Findable (DOI), Accessible (Zenodo/GitHub), Interoperable (CSV, MD, PDF) and Reusable (CC-BY 4.0 licence, data dictionary) |

---

## Ethical considerations and data protection

The study underlying this repository was reviewed and approved by the Research Committee of the Faculty of Engineering Sciences of the Universidad Técnica Estatal de Quevedo (UTEQ) and was conducted in accordance with the principles of the Declaration of Helsinki (1964, and its later amendments).

- Written informed consent was obtained from every guardian on behalf of the child (`ethics/informed-consent-template-ES.md`).
- Children provided verbal assent before each session (`ethics/minor-assent-template-ES.md`).
- Every personal identifier (names, addresses, government IDs, exact ages) was removed before deposition in this repository. Only pseudonymous identifiers `G01`–`G12` and the derived responses appear in the datasets.
- No image, video or audio of a participant is included in this repository.
- Information security controls follow ISO/IEC 27001:2022 (see `ethics/privacy-impact-assessment.md`).

If, despite our anonymisation efforts, you believe the data allow the re-identification of a participant, please contact the corresponding author immediately and we will act in accordance with the applicable data-protection regulations.

---

## Licences

This repository is released under a **dual-licence scheme**:

- **Source code** (`src/`, `analysis/`, `.github/`, and every file with an extension like `.py`, `.java`, `.c`, `.cpp`, `.ino`, `.ipynb`, `.yml`) is released under the **MIT licence** (see [`LICENSE-CODE`](LICENSE-CODE)).
- **Documentation, instruments, ethics templates and data** (`docs/`, `instruments/`, `ethics/`, `data/`, `README.md`, `CITATION.cff`, `CHANGELOG.md`) are released under **Creative Commons Attribution 4.0 International (CC-BY 4.0)** (see [`LICENSE-DOCS`](LICENSE-DOCS)).

Attribution should include the citation of both the manuscript and the Zenodo DOI (see [How to cite](#how-to-cite)).

---

## Contact and support

- **Corresponding author**: Miguel J. Hornos — mhornos@ugr.es (Software Engineering Department, Research Centre for Information and Communication Technologies (CITIC-UGR), University of Granada, Spain)
- **Principal investigator**: Gleiston Guerrero-Ulloa — gguerrero@uteq.edu.ec (Faculty of Engineering Sciences, Universidad Técnica Estatal de Quevedo, Ecuador)
- **Issues and questions**: please open an issue at <https://github.com/CarlosAlmeida2000/ProyectoTorddis/issues>.
- **Security matters**: see [`SECURITY.md`](SECURITY.md).

We welcome pull requests. Before contributing please read [`CONTRIBUTING.md`](CONTRIBUTING.md) and [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).

---

*Last updated: 2026-07-12. This is version 1.0.0 of the reproducibility package, corresponding to the manuscript submission to Education and Information Technologies.*
