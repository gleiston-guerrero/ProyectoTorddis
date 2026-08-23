# Data dictionary

This document describes every dataset shared in the `data/` directory. All datasets are pseudonymised: no direct personal identifier is present. The pseudonymisation protocol is documented in `../ethics/data-management-plan.md`.

Every table below lists the column name, its data type, its unit or coding, and a short description. Likert scales are coded 1 = strongly disagree … 5 = strongly agree throughout.

---

## `raw/demographic-survey.csv` — 19 rows (one per guardian)

Baseline supervision context, collected before the controlled session.

| Column | Type | Coding | Description |
|--------|------|--------|-------------|
| `participant_id` | string | P01…P19 | Pseudonymous guardian identifier. |
| `monitoring_difficulty` | string | Complejo / Regular / Fácil | How difficult the guardian finds it to notice signs of distraction during homework. |
| `monitoring_frequency` | string | Siempre / Casi siempre / A veces | How often the guardian supervises homework. |
| `strategies_used_ES` | string | free text (redacted) | Strategies the guardian uses to keep the child focused. |

---

## `raw/sus-responses.csv` — 19 rows (one per guardian)

Responses to the ten-item System Usability Scale, administered after the 30-minute controlled session.

| Column | Type | Coding | Description |
|--------|------|--------|-------------|
| `participant_id` | string | P01…P19 | Pseudonymous guardian identifier. |
| `sus_1` | int | 1–5 | I think I would like to use this system frequently. |
| `sus_2` | int | 1–5 | I found the system unnecessarily complex. (reverse-scored) |
| `sus_3` | int | 1–5 | I thought the system was easy to use. |
| `sus_4` | int | 1–5 | I think I would need the support of a technical person to be able to use this system. (reverse-scored) |
| `sus_5` | int | 1–5 | I found the various functions in this system were well integrated. |
| `sus_6` | int | 1–5 | I thought there was too much inconsistency in this system. (reverse-scored) |
| `sus_7` | int | 1–5 | I would imagine that most people would learn to use this system very quickly. |
| `sus_8` | int | 1–5 | I found the system very cumbersome to use. (reverse-scored) |
| `sus_9` | int | 1–5 | I felt very confident using the system. |
| `sus_10` | int | 1–5 | I needed to learn a lot of things before I could get going with this system. (reverse-scored) |
| `sus_score` | float | 0–100 | SUS score after reverse-scoring and rescaling. Mean = 83.03, SD = 10.26, n = 19. |

---

## `raw/likert-effectiveness.csv` — 21 rows (one per guardian–child pair)

Demographics and the seven-item perception and acceptability questionnaire. **The unit of this file is the guardian–child pair, not the guardian.** Guardians P12 and P17 each participated with two children and therefore contribute two rows, distinguished by `child_index`; five of the seven statements ask about that particular child. Nineteen guardians contribute 21 rows.

| Column | Type | Coding | Description |
|--------|------|--------|-------------|
| `participant_id` | string | P01…P19 | Pseudonymous guardian identifier. Repeats for guardians with two children. |
| `child_index` | int | 1–2 | Index of the child within the household. |
| `guardian_age` | int | years | Age of the guardian (25–74; M = 41.2, SD = 12.5). |
| `guardian_education` | string | PRIMARIA / SECUNDARIA / SUPERIOR | Highest completed education level. |
| `guardian_gender` | string | F / M | Gender of the guardian. |
| `residence_area` | string | URBANA / RURAL | Area of residence. |
| `guardian_occupation` | string | free text | Guardian's stated occupation. |
| `internet_type` | string | WIFI / MOVIL DATOS / WIFI Y DATOS MÓVILES | Domestic internet connection. |
| `school_type` | string | FISCAL / PARTICULAR | Type of school attended by the child. |
| `supervision_time_h` | int | hours per day | Daily time the guardian devotes to supervising the child's schoolwork. |
| `child_age` | int | 6–12 | Age of the monitored child. |
| `statement_1` | int | 1–5 | The device was perceived by the child as appealing. |
| `statement_2` | int | 1–5 | The mobile application accurately recorded the events that occurred. |
| `statement_3` | int | 1–5 | The child did not perceive the presence of the device as intrusive. **Positively keyed** (agreement = absence of intrusiveness); it is **not** reverse-scored before the internal-consistency estimate. |
| `statement_4` | int | 1–5 | The device was used throughout the effective time devoted to school tasks. |
| `statement_5` | int | 1–5 | After discontinuing use of the device, the child demonstrated improvement in self-regulation and concentration while completing homework. |
| `statement_6` | int | 1–5 | Although the device usage time was short, it is expected to contribute to task completion and academic performance. |
| `statement_7` | int | 1–5 | You would be willing to continue using the device. |

Descriptive statistics over the 21 questionnaires (Table 6 of the manuscript): means 4.29, 4.48, 3.05, 4.14, 4.14, 4.48, 4.67; SDs 0.78, 0.60, 0.92, 1.01, 0.65, 0.68, 0.66. Cronbach's α = 0.882. Guardian-level sensitivity analysis (averaging within P12 and P17, n = 19): means 4.29, 4.45, 3.03, 4.05, 4.13, 4.47, 4.63; α = 0.892.

---

## `raw/open-ended-responses.csv` — 152 rows (19 guardians × 8 questions)

Anonymised free-text responses to the eight open-ended questions attached to the SUS questionnaire. Any potentially identifying detail was manually redacted and replaced with a bracketed token such as `[NAME]`, `[PLACE]` or `[SCHOOL]`. The redaction protocol is described in `../ethics/data-management-plan.md`.

| Column | Type | Description |
|--------|------|-------------|
| `participant_id` | string | Pseudonymous guardian identifier. |
| `question_number` | int | 1–8. |
| `question_text_ES` | string | The open-ended question as it appeared on the questionnaire (see `../instruments/open-ended-questions-ES.md`). |
| `response_text_ES` | string | The guardian's free-text response, after redaction. |

---

## `raw/recognition-events.csv` — 108 rows (one per elicited event)

Outcome and latency of each of the four AI recognition modules at each elicited checkpoint, across the 19 monitored sessions (4–9 events per session, median 5). Events were elicited under a scripted protocol in which the evaluator prompted each target condition in turn, so **every event carries a ground-truth target for all four modules simultaneously and the dataset contains no true negatives**. Latencies are wall-clock times from frame capture to event emission.

| Column | Type | Coding | Description |
|--------|------|--------|-------------|
| `session_id` | int | 1–19 | Session sequence number (one session per household). |
| `event_id` | int | 1–n | Checkpoint index within the session. |
| `person_latency_s` | float | seconds | Person-recognition latency (0.00 when the outcome is FN, for which no latency is defined). |
| `person_outcome` | string | TP / FP / FN | Person-recognition outcome. |
| `facial_latency_s` | float | seconds | Facial-expression recognition latency (0.00 on FN). |
| `facial_outcome` | string | TP / FP / FN | Facial-expression recognition outcome. |
| `drowsiness_latency_s` | float | seconds | Drowsiness-detection latency (0.00 on FN). |
| `drowsiness_outcome` | string | TP / FP / FN | Drowsiness-detection outcome. |
| `object_latency_s` | float | seconds | Distracting-object detection latency (0.00 on FN). |
| `object_outcome` | string | TP / FP / FN | Distracting-object detection outcome. |

Aggregates (Table 7 of the manuscript): person 101 TP / 1 FP / 6 FN; facial expression 96 / 4 / 8; drowsiness 100 / 4 / 4; distracting object 100 / 5 / 3. Overall 397 / 14 / 21 over 432 module-level observations. Mean latency over true positives: 0.639, 1.117, 1.943 and 1.881 s respectively.

---

## `systematic-review/Torddis_cribado_SLR.xlsx`

The 4,321 unique records retained after deduplication, with the screening decision and exclusion reason for each. The `source_database` column records the database in which the record was first retrieved; `also_found_in` lists any further databases in which it appears. Counts in the PRISMA sheet reproduce Figure 1 and Table 2 of the manuscript.
