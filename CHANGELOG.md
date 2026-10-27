## [2.0.0] — Unreleased

### Changed
- Sample extended from 12 guardians / 14 children to 19 guardians / 21 children.
  Data collection continued after v1.0.1 under the same ethics approval.
- Recognition data restructured from 14 session-level binary flags to 108
  event-level records across 19 sessions (`recognition-events.csv`), with an
  explicit `*_outcome` field (TP / FP / FN) replacing the previous success flag.
- Transcription errors in the recognition sheet corrected: ten rows in sessions
  1–3 had been populated with values copied from later sessions, and a number of
  rows carried a latency of 0.00 against a recognised target. Both were corrected
  against the source records. The exact count of the second group was not
  recorded at the time of the correction and is not recoverable from the
  surviving files; `recognition-events.csv` is the corrected dataset.
- `likert-effectiveness.csv` gains `child_index` (two guardians participated
  with two children each) and `school_type`; demographic variables are now
  documented with their actual string coding.

### Note on superseded values
- The v1.0.1 file `recognition-latencies.csv` records a drowsiness latency of
  5.10 s for session 1. No corresponding value appears in the re-collected
  event-level dataset, and the discrepancy could not be reconstructed from the
  records that survive, so it is reported here rather than silently dropped.
  The figures published in the manuscript derive exclusively from
  `recognition-events.csv`.

### Deprecated
- `recognition-latencies.csv` is superseded by `recognition-events.csv`.
  v1.0.1 remains available for citation integrity.

### Pending at release
- `CITATION.cff` still declares `version: "1.0.1"` and `date-released: "2026-07-15"`.
  Both must be updated, and this heading dated, when v2.0.0 is tagged.
