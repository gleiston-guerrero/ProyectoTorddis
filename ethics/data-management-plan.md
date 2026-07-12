# Data Management Plan (DMP) — Torddis

This document is a Data Management Plan for the Torddis study, aligned with the FAIR principles (Wilkinson et al., 2016) and with the data-protection provisions applicable in the jurisdiction of the study.

## 1. Data description

### 1.1 Types of data generated

| Type | Description | Volume |
|------|-------------|--------|
| Video frames (transient) | Captured by the ESP32-CAM at 2 Hz | Never stored |
| Event log | Timestamp + category (person / facial / drowsiness / object) + confidence | ~ 1 record per second per session |
| Questionnaire responses | SUS (10 items), effectiveness Likert (7 items), demographic (7 items), open-ended (8 items) | 12 guardians × 32 items = 384 records |
| Latency measurements | Wall-clock times per module per session | 16 sessions × 4 modules = 64 records |

### 1.2 File formats

- CSV (UTF-8) for tabular data.
- Markdown for documentation.
- PDF for signed consent forms (kept offline).

## 2. Data collection

- Instruments and their translations are in `../instruments/`.
- The full protocol of each session is described in `../docs/testing/test-plan.md`.
- Data are collected in paper form (questionnaires) and transcribed to CSV, or automatically produced by the IoT device (event logs and latencies).

## 3. Data documentation and metadata

- Every dataset in `../data/` is described column-by-column in `../data/data-dictionary.md`.
- Every processed dataset carries a provenance comment identifying the notebook that produced it.
- The whole repository has a `CITATION.cff` and a `.zenodo.json` file describing it in machine-readable form.

## 4. Ethics and privacy

### 4.1 Pseudonymisation

At capture time, participants are assigned pseudonymous identifiers (`G01`…`G12`). The mapping from real identity to pseudonym is kept **only** in a paper document under lock at the Faculty of Engineering Sciences of the Universidad Técnica Estatal de Quevedo (UTEQ) and is destroyed one year after publication of the manuscript.

### 4.2 Redaction of open-ended responses

Before deposit, every open-ended response is manually inspected and the following patterns are replaced by bracketed tokens:

| Pattern | Token |
|---------|-------|
| Proper names of people | `[NOMBRE]` |
| Geographical names finer than the province | `[LUGAR]` |
| School names | `[COLEGIO]` |
| Workplace names | `[TRABAJO]` |
| Phone numbers, emails, postal addresses | `[CONTACTO]` |

### 4.3 Age bucketing

Tutor ages are bucketed to 5-year ranges to further reduce the risk of re-identification.

### 4.4 No video, audio or image of participants is included in the repository

The video frames captured by the ESP32-CAM are processed in RAM and immediately discarded. No frame ever reaches persistent storage. Screenshots of the mobile application shown in the manuscript were taken with adult research staff, not with participating children.

## 5. Storage and security during the study

- Encrypted at rest (AES-256) on the study laptop and the university's local server.
- Encrypted in transit (TLS 1.3) between devices.
- Access limited to two authenticated researchers.
- Backups on encrypted external drive kept in a locked cabinet.

## 6. Preservation, sharing and access after the study

- The pseudonymised datasets, together with the code, documentation and analysis notebooks, are deposited on **Zenodo** with a persistent DOI upon acceptance of the manuscript.
- Licence: CC-BY 4.0 for the datasets; MIT for the code.
- Zenodo guarantees preservation for at least 20 years.
- Access is public and unrestricted (no request required).
- Re-users are required to cite the manuscript and the Zenodo DOI (see `../README.md`).

## 7. Responsibilities

| Role | Person | Responsibility |
|------|--------|----------------|
| Data Steward | Carlos Almeida-Dueñas and John Plazarte-Suárez (Faculty of Engineering Sciences, UTEQ) | Data collection during the pilot sessions, transcription of paper questionnaires into CSV, and pseudonymisation before deposit |
| Data Analyst | Gleiston Guerrero-Ulloa, Carlos Almeida-Dueñas, John Plazarte-Suárez, Miguel J. Hornos and Carlos Rodríguez-Domínguez | Statistical analysis and preparation of the reproducibility notebooks |
| Data Curator | Gleiston Guerrero-Ulloa (Faculty of Engineering Sciences, UTEQ; gguerrero@uteq.edu.ec) | Deposit on Zenodo, metadata curation, DOI reservation and updates |
| Principal Investigator | Gleiston Guerrero-Ulloa (Faculty of Engineering Sciences, UTEQ; gguerrero@uteq.edu.ec) | Overall scientific responsibility for the study and its data |
| Corresponding Author | Miguel J. Hornos (Department of Software Engineering, ETSIIT, University of Granada; mhornos@ugr.es) | Correspondence with the journal editorial office and coordination of the international dissemination |

## 8. Compliance with regulations

The study protocol was reviewed and approved by the Research Committee of the Faculty of Engineering Sciences of the Universidad Técnica Estatal de Quevedo (UTEQ). The data-handling practices described above align with:

- The principles of the Declaration of Helsinki (1964, and its later amendments).
- ISO/IEC 27001:2022 (Information security management systems).
- The FAIR data principles (Wilkinson et al., 2016).

## 9. Reference

Wilkinson, M. D., Dumontier, M., Aalbersberg, I. J., Appleton, G., Axton, M., Baak, A., ... & Mons, B. (2016). The FAIR Guiding Principles for scientific data management and stewardship. *Scientific Data, 3*, 160018. https://doi.org/10.1038/sdata.2016.18
