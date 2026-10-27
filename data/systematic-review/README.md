# Systematic review — screening records

This directory holds the reproducibility artefacts of the systematic review reported in Section 3 of the manuscript and in Supplementary Section S10 (Online Resource 1).

## Files

| File | Content |
|------|---------|
| `cribado-torddis.xlsx` | The 4,321 unique records retained after deduplication, one row per record, with the decision and reason of the automated pass, the final decision and reason, and the stage at which each decision was taken. Includes the PRISMA counts, the provenance of every decision and the dual-reviewer verification sample. |
| `torddis-registros-retirados.xlsx` | Detail of the automatic pre-screen: 201 records withdrawn, applying the criteria in order and recording only the first one violated (39 theses or dissertations, 26 proceedings volumes or front matter, 106 records without a valid DOI after the manual DOI recovery, and 30 records published before 2015). The raw property counts are larger, because a record may fail more than one criterion: 323 records carried no DOI in the export and 47 predate 2015. |
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
| `Muestra titulo-resumen` | Random sample of 353 of the 4,237 records excluded before full-text assessment (Cochran's formula with a finite-population correction, 95% confidence, 5% margin of error; stratified by exclusion category, fixed seed), with the independent decision of the first and third authors, the fourth-author decision where they disagreed, and the agreement with the automated decision. |
| `Texto completo` | The 84 reports assessed for eligibility, with the independent decision of the second and third authors, the consensus decision where they disagreed, the final decision and its agreement with the automated one. |
| `Ejecuciones automaticas` | Register of the runs of the automated screening pass. |

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
| Withdrawn by the automatic pre-screen | 201 |
| Reports assessed for eligibility | 84 |
| Excluded after eligibility assessment | 81 |
| **Studies included** | **3** |

## Use of AI and human verification

The title-and-abstract screening was carried out with four generative AI tools (ChatGPT, OpenAI; Claude, Anthropic; DeepSeek; and Gemini, Google), each run between three and five times, with the four sets of decisions combined by inter-tool consensus; the extraction of eligibility evidence from the full-text reports was carried out with Claude (Anthropic). Both are declared in the manuscript. All 84 reports assessed for eligibility were examined independently by the second and third authors, who agreed on 79 of them and settled the remaining 5 by consensus; the reviewers retained four reports, one of which was excluded later, when its eligibility was re-examined against the full text during the preparation of the manuscript, leaving three included studies. To verify the preceding stage, a random sample of 353 of the 4,237 records excluded before full-text assessment was re-assessed independently by the first and third authors, who agreed on 340 and referred the remaining 13 to the fourth author; the final decision coincided with that of the tools in all 353 records. Three reports could not be retrieved in full text and were assessed on their abstract and metadata alone; they are flagged in the `etapa_revisor` column.
