# Systematic review — data extraction

This directory holds the reproducibility artefacts of the systematic review reported in Section 3 of the manuscript.

## Files

| File | Description | Status |
|------|-------------|--------|
| `included-studies.csv` | The 68 included studies with full bibliographic metadata and the seven-capability coding used in Table 3 of the manuscript. | **Complete** (generated from the manuscript source) |
| `screening-decisions.csv` | Record-level screening and eligibility decisions (4,881 identified → 68 included). | **Missing — must be supplied by the authors** |
| `search-strings.csv` | Exact query string executed in each database, with execution date and hit count. | **Missing — must be supplied by the authors** |

## `included-studies.csv`

68 rows, one per included study. Columns:

| Column | Type | Description |
|--------|------|-------------|
| `no` | int | Row number in Table 3 of the manuscript (1–68). |
| `citation_key` | string | BibTeX key in `tordis-eait.bib`. |
| `authors` | string | Author list as recorded in the bibliography. |
| `year` | int | Publication year (2016–2026). |
| `title` | string | Title of the study. |
| `venue` | string | Journal, proceedings or publisher. |
| `volume`, `number`, `pages` | string | Bibliographic locators (empty where not applicable). |
| `doi` | string | DOI. Present for all 68 records. |
| `FE` | 0/1 | Facial-expression / emotion recognition. |
| `DR` | 0/1 | Drowsiness / fatigue detection. |
| `OB` | 0/1 | Distracting-object detection. |
| `AT` | 0/1 | Attention / engagement estimation. |
| `IN` | 0/1 | Real-time in-situ intervention. |
| `IoT` | 0/1 | Deployment on a dedicated IoT / edge device. |
| `HM` | 0/1 | Explicit home / self-regulation orientation. |
| `capabilities_covered` | int | Row sum of the seven capability flags (0–7). |

### Capability frequencies (reproducible from this file)

| Capability | Studies |
|------------|---------|
| AT — attention / engagement | 53 / 68 |
| IN — real-time in-situ intervention | 47 / 68 |
| FE — facial expression / emotion | 32 / 68 |
| DR — drowsiness / fatigue | 27 / 68 |
| IoT — dedicated IoT / edge device | 27 / 68 |
| OB — distracting-object detection | 8 / 68 |
| HM — home / self-regulation orientation | 8 / 68 |

The maximum number of capabilities covered by any single included study is **6**; no study covers all seven, which is the gap claim made in Section 3.4 of the manuscript.

### Verification

```python
import pandas as pd
d = pd.read_csv("included-studies.csv")
assert len(d) == 68
assert d["doi"].notna().all()                      # every included study has a DOI
caps = ["FE", "DR", "OB", "AT", "IN", "IoT", "HM"]
print(d[caps].sum())                               # capability frequencies
assert d[caps].sum(axis=1).max() < 7                # no study covers all seven
```

## Note on the eligibility criteria

The eligibility criteria stated in the manuscript (exclusion of theses, of records published before 2015 and of records without a valid DOI) apply to **this corpus of 68 systematically selected studies**, not to the background references cited narratively in Section 3.1 and in Supplementary Section S10.3. Every one of the 68 records in this file has a DOI and is a peer-reviewed study published in 2016 or later, which can be checked with the assertions above.

## Outstanding

The two files marked *Missing* above are required to make the identification and screening stages of the review reproducible. Until they are deposited, the manuscript must not claim that the complete set of retrieved records with their screening decisions is available in this repository.
