# Torddis — Reproducibility Package

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.XXXXXXX.svg)](https://doi.org/10.5281/zenodo.XXXXXXX)
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

- 📄 **Full documentation** of the system architecture (ISO/IEC/IEEE 42010:2022), the functional and non-functional requirements (ISO/IEC/IEEE 29148:2018) and the testing artefacts (ISO/IEC/IEEE 29119-1:2022).
- 💻 **Complete source code** for the three system layers: the Android mobile application (Java), the ESP32-CAM / NodeMCU firmware (C/C++), and the Django-based back-end + AI modules (Python).
- 📊 **Pseudonymised experimental data** (SUS responses, effectiveness Likert responses, open-ended answers and recognition-latency measurements) collected from 12 guardians and their children between 7 and 12 years old.
- 📈 **Statistical analysis notebooks** (Jupyter) that regenerate every table and figure of the Results section (descriptive statistics, Shapiro--Wilk, Kolmogorov--Smirnov, Pearson, Spearman, ANOVA, post-hoc tests and Kruskal--Wallis).
- 📋 **Data-collection instruments** (SUS, demographic survey, effectiveness questionnaire, open-ended questions) in reusable form.
- 🔐 **Ethics documentation** and templates for informed consent and minor assent.

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
    doi       = {10.5281/zenodo.XXXXXXX},
    url       = {https://doi.org/10.5281/zenodo.XXXXXXX}
}
```

A machine-readable citation is provided in [`CITATION.cff`](CITATION.cff).

---

## Repository structure

```
torddis/
├── README.md                          ← this file
├── LICENSE-CODE                       ← MIT (applies to src/, analysis/)
├── LICENSE-DOCS                       ← CC-BY 4.0 (applies to docs/, data/, instruments/, ethics/)
├── CITATION.cff                       ← machine-readable citation metadata
├── CHANGELOG.md                       ← version history (semver)
├── .zenodo.json                       ← DOI metadata for Zenodo
│
├── docs/                              ← Scientific documentation
│   ├── architecture/                  ← ISO/IEC/IEEE 42010:2022
│   │   ├── 01-stakeholders-and-concerns.md
│   │   ├── 02-deployment-view.pdf     ← Fig. 3 of the manuscript
│   │   ├── 03-logical-view.pdf        ← Fig. 5 of the manuscript
│   │   ├── 04-process-view.md
│   │   └── 05-rationale.md
│   ├── requirements/                  ← ISO/IEC/IEEE 29148:2018
│   │   ├── functional-requirements.csv       (FR-01…FR-07)
│   │   ├── non-functional-requirements.csv   (NFR-01…NFR-05 + ISO/IEC 25010:2023 mapping)
│   │   ├── use-cases/                        (semi-structured use cases)
│   │   └── traceability-matrix.csv           (FR/NFR ↔ UC ↔ Test ↔ Code module)
│   ├── testing/                       ← ISO/IEC/IEEE 29119-1:2022
│   │   ├── test-plan.md
│   │   ├── test-design-specifications/
│   │   ├── test-case-specifications/
│   │   └── test-execution-reports/
│   ├── standards-compliance/
│   │   ├── ISO-IEC-25010-2023-checklist.md
│   │   ├── ISO-IEC-27001-2022-controls.md
│   │   ├── ISO-IEC-IEEE-29148-2018.md
│   │   └── ISO-IEC-IEEE-42010-2022.md
│   ├── maintenance-plan.md
│   └── user-manual/
│
├── src/                               ← System source code
│   ├── mobile-app/                    ← Android application (Java)
│   ├── iot-device/                    ← ESP32-CAM + NodeMCU firmware (C/C++)
│   ├── backend/                       ← Django REST back-end (Python)
│   └── ai-modules/                    ← AI recognition modules (Python)
│
├── data/                              ← Pseudonymised experimental data
│   ├── raw/                           ← as collected (after anonymisation)
│   ├── processed/                     ← derived tables
│   ├── data-dictionary.md
│   └── README.md
│
├── analysis/                          ← Statistical analysis notebooks
│   ├── 01-normality-tests.ipynb
│   ├── 02-pearson-correlation.ipynb
│   ├── 03-anova-analysis.ipynb
│   ├── 04-post-hoc-tests.ipynb
│   ├── 05-spearman-correlation.ipynb
│   ├── 06-kruskal-wallis.ipynb
│   ├── 07-sus-score-computation.ipynb
│   ├── environment.yml                (Conda)
│   ├── requirements.txt               (pip)
│   └── README.md
│
├── instruments/                       ← Data-collection instruments
│   ├── SUS-questionnaire-EN.md
│   ├── SUS-questionnaire-ES.md
│   ├── demographic-survey-ES.md
│   ├── effectiveness-likert-ES.md
│   └── open-ended-questions-ES.md
│
├── ethics/                            ← Ethics and privacy documentation
│   ├── informed-consent-template-ES.md
│   ├── minor-assent-template-ES.md
│   ├── data-management-plan.md
│   └── privacy-impact-assessment.md
│
└── .github/workflows/                 ← Continuous integration
    ├── ci-android.yml
    ├── ci-python.yml
    └── docs-build.yml
```

---

## Getting started

### Prerequisites

- Python **3.11 or later** (for the analysis notebooks and back-end).
- Android Studio **Hedgehog (2023.1.1)** or later, with SDK 33+, for the mobile app.
- PlatformIO **6.x** or the Arduino IDE **2.x**, for the ESP32 / NodeMCU firmware.
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

Every table and figure of the Results section of the manuscript is regenerated by one of the notebooks in `analysis/`:

| Manuscript element | Notebook | Data input |
|--------------------|----------|------------|
| Table 6 --- Aggregated recognition results | Direct from `data/raw/recognition-latencies.csv` | — |
| Table 7 --- Central tendency of latency | `analysis/01-descriptive.ipynb` | `data/raw/recognition-latencies.csv` |
| Table 8 --- Correlation matrix of latencies | `analysis/02-pearson-correlation.ipynb` | `data/raw/recognition-latencies.csv` |
| Table 9 --- Descriptive statistics | `analysis/01-descriptive.ipynb` | `data/raw/likert-effectiveness.csv` |
| Table 10 --- Normality analysis | `analysis/01-normality-tests.ipynb` | `data/raw/likert-effectiveness.csv` |
| Table 11 --- Pearson correlations | `analysis/02-pearson-correlation.ipynb` | `data/raw/likert-effectiveness.csv` |
| Table 12 --- ANOVA | `analysis/03-anova-analysis.ipynb` | `data/raw/likert-effectiveness.csv` |
| Tables 13--16 --- Post-hoc tests | `analysis/04-post-hoc-tests.ipynb` | `data/raw/likert-effectiveness.csv` |
| Table 17 --- Kruskal--Wallis + Spearman | `analysis/05-spearman-correlation.ipynb`, `analysis/06-kruskal-wallis.ipynb` | `data/raw/likert-effectiveness.csv` |
| SUS score (Fig. 8) | `analysis/07-sus-score-computation.ipynb` | `data/raw/sus-responses.csv` |

All random seeds are pinned; identical outputs are guaranteed on any platform running the versions declared in `environment.yml`.

---

## Reproducing the IoT prototype

### 1. Mobile application (Android)

```
cd src/mobile-app
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

Requires the back-end URL to be configured in `res/values/strings.xml` (`api_base_url` field).

### 2. IoT device firmware

```
cd src/iot-device/esp32-cam
pio run --target upload
cd ../esp8266-nodemcu
pio run --target upload
```

Wire the components as depicted in Fig. 4 of the manuscript. The full bill of materials is in `docs/architecture/hardware-BOM.md`.

### 3. Back-end + AI modules

```
cd src/backend
docker compose up -d          # starts PostgreSQL + Django + AI workers
```

The AI models (`person-recognition`, `facial-expression`, `drowsiness-detection`, `object-recognition`) load pre-trained weights from `src/ai-modules/models/`. Retraining is documented in `src/ai-modules/README.md`.

---

## Standards compliance

The Torddis system and its documentation follow the international standards listed below. The corresponding checklists and evidence are in `docs/standards-compliance/`.

| Standard | Scope | Evidence |
|----------|-------|----------|
| ISO/IEC/IEEE 12207:2017 | Software life-cycle processes | Whole repository |
| ISO/IEC/IEEE 15288:2023 | System life-cycle processes | `docs/` |
| ISO/IEC/IEEE 29148:2018 | Requirements engineering | `docs/requirements/` |
| ISO/IEC 25010:2023 | Product quality model | `docs/requirements/non-functional-requirements.csv` |
| ISO/IEC/IEEE 42010:2022 | Architecture description | `docs/architecture/` |
| ISO/IEC/IEEE 29119-1:2022 | Software testing | `docs/testing/` |
| ISO/IEC 27001:2022 | Information security | `docs/standards-compliance/ISO-IEC-27001-2022-controls.md` |
| FAIR Data Principles (2016) | Data reuse | This repository is Findable (DOI), Accessible (Zenodo/GitHub), Interoperable (CSV, MD, PDF) and Reusable (CC-BY 4.0 licence, data dictionary) |

---

## Ethical considerations and data protection

The study underlying this repository was reviewed and approved by the Research Committee of the Faculty of Engineering Sciences of the participating university and was conducted in accordance with the principles of the Declaration of Helsinki (1964, and its later amendments).

- Written informed consent was obtained from every guardian on behalf of the child.
- Children provided verbal assent before each session.
- Every personal identifier (names, addresses, government IDs, exact ages) was removed before deposition in this repository. Only the pseudonymised responses and event logs are shared here.
- No image or video of a participant is included in this repository. The screenshots in `docs/user-manual/` were captured with adult research staff.
- Information security controls follow ISO/IEC 27001:2022 (see `docs/standards-compliance/`).

If, despite our anonymisation efforts, you believe the data allow the re-identification of a participant, please contact the corresponding author immediately and we will act in accordance with the applicable data-protection regulations.

---

## Licences

This repository is released under a **dual-licence scheme**:

- **Source code** (`src/`, `analysis/`, `.github/`, all `.py`, `.java`, `.c`, `.cpp`, `.ipynb`, `.yml` files) is released under the **MIT licence** (see [`LICENSE-CODE`](LICENSE-CODE)).
- **Documentation, instruments, ethics templates and data** (`docs/`, `instruments/`, `ethics/`, `data/`, `README.md`, `CITATION.cff`, `CHANGELOG.md`) are released under **Creative Commons Attribution 4.0 International (CC-BY 4.0)** (see [`LICENSE-DOCS`](LICENSE-DOCS)).

Attribution should include the citation of both the manuscript and the Zenodo DOI (see [How to cite](#how-to-cite)).

---

## Contact and support

- **Principal corresponding author**: Miguel J. Hornos --- mhornos@ugr.es
- **Secondary corresponding author**: Gleiston Guerrero-Ulloa --- gguerrero@uteq.edu.ec
- **Issues and questions**: please open an issue at <https://github.com/CarlosAlmeida2000/ProyectoTorddis/issues>.

We welcome pull requests. Before contributing please read [`CONTRIBUTING.md`](CONTRIBUTING.md).

---

*Last updated: 2026-01. This is version 1.0.0 of the reproducibility package, corresponding to the manuscript submission to Education and Information Technologies.*
