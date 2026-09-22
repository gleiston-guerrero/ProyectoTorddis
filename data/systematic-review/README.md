# Systematic review — screening records

This directory holds the reproducibility artefacts of the systematic review reported in Section 3 of the manuscript and in Supplementary Section S10 (Online Resource 1).

## Files

| File | Content |
|------|---------|
| `cribado-torddis.xlsx` | The 4,321 unique records retained after deduplication, one row per record, with the decision and reason of the automated pass, the final decision and reason, and the stage at which each decision was taken. Includes the PRISMA counts, the provenance of every decision and the dual-reviewer verification sample. |
| `torddis-registros-retirados.xlsx` | Detail of the automatic pre-screen: 345 records withdrawn (39 theses, 323 records without a verifiable DOI, 47 records published before 2015; 409 reasons in total, because some records failed more than one criterion). |
| `informe-doi.xlsx` | Audit of the DOI field of the 4,321 records (valid, malformed and missing DOIs). |
| `informe-recuperacion-doi.xlsx` | Automated recovery of missing DOIs through bibliographic services, with accepted, to-review and not-found records and the query log. |

## `cribado-torddis.xlsx`

| Sheet | Content |
|-------|---------|
| `Instrucciones` | Instructions for reading the workbook. |
| `Cribado` | One row per unique record (see columns below). |
| `PRISMA` | Counts computed with formulas from the `Cribado` sheet; they reproduce Figure 1 and Table 2 of the manuscript. |
| `Revisores` | Reviewer register. |
| `Reconstruccion` | Provenance of the reviewer decisions, stage by stage, with the number of records in each stage. |
| `Muestra verificacion` | Stratified random sample of 42 of the 80 reports excluded after full-text assessment (fixed seed 20260919), with the independent decision of two reviewers, the third-reviewer decision where they disagreed, and the agreement with the automated decision. |

Columns of the `Cribado` sheet:

| Column | Description |
|--------|-------------|
| `id` | Record identifier. |
| `decision_automatica`, `motivo_automatico` | Decision (`Incluir`, `Dudoso`, `Excluir`) and reason assigned by the automated rule-based pass. |
| `revisor_decision`, `revisor_motivo` | Final decision and reason after verification. |
| `etapa_revisor` | Stage at which the final decision was taken: full text, abstract only (full text not retrievable), probe for omissions, or resolution by scope. |
| `categoria_exclusion_elegibilidad` | For reports excluded after eligibility assessment, the first eligibility criterion violated. |
| `score`, `conceptos` | Relevance score and concepts detected by the automated pass. |
| `base`, `tambien_en` | Database in which the record was first retrieved, and further databases in which it appears. |
| `year`, `doc_type`, `authors`, `title`, `abstract`, `venue`, `keywords`, `doi` | Bibliographic metadata as exported by the databases. |

## Counts

| Stage | Records |
|-------|---------|
| Identified in the five databases | 5,194 |
| Duplicates removed | 873 |
| Unique records screened | 4,321 |
| Withdrawn by the automatic pre-screen | 345 |
| Reports assessed for eligibility | 84 |
| Excluded after eligibility assessment | 80 |
| **Studies included** | **4** |

## Use of AI and human verification

The title-and-abstract screening and the extraction of eligibility evidence from the full-text reports were carried out with the assistance of a generative AI tool (Claude, Anthropic), as declared in the manuscript. The four included studies were assessed independently by two authors, who agreed on all four. A stratified random sample of 42 of the 80 exclusions was re-assessed independently by the same two authors; they agreed on 39 of the 42 reports, the three disagreements were resolved by a third author, and the final decision coincided with the automated exclusion in all 42 reports. Three reports could not be retrieved in full text and were assessed on their abstract and metadata alone; they are flagged in the `etapa_revisor` column.
