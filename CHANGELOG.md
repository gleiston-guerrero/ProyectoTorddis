## [2.0.0] — 2026-08-XX

### Changed
- Sample extended from 12 guardians / 14 children to 19 guardians / 21 children.
  Data collection continued after v1.0.1 under the same ethics approval.
- Recognition data restructured from 14 session-level binary flags to 108
  event-level records across 19 sessions (`recognition-events.csv`), with an
  explicit `*_outcome` field (TP / FP / FN) replacing the previous success flag.
- Transcription errors in the recognition sheet corrected: ten rows in sessions
  1–3 had been populated with values copied from later sessions, and NN events
  had a latency of 0.00 entered for a recognised target. Both were corrected
  against the source records.
- `likert-effectiveness.csv` gains `child_index` (two guardians participated
  with two children each) and `school_type`; demographic variables are now
  documented with their actual string coding.

### Note on superseded values
- The v1.0.1 file `recognition-latencies.csv` contained a drowsiness latency of
  5.10 s (session 1) that does not appear in the re-collected dataset. [Explicar
  aquí qué ocurrió: sesión remedida / error de digitación / etc.]

### Deprecated
- `recognition-latencies.csv` is superseded by `recognition-events.csv`.
  v1.0.1 remains available for citation integrity.
