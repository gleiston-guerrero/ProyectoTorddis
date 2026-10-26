# Security controls required for a networked deployment of Torddis

**Status of the prototype.** Torddis was evaluated as a self-contained local system installed inside each participating household. There was no cloud component and no exposure to the internet: the ESP32-CAM device, the Django application server and the Android application communicated over the domestic local-area network only. Because no personal or authentication datum left the home network, the cryptographic mechanisms specified in **NFR-04** were judged unnecessary for the pilot and their implementation was deferred. Communication between components uses HTTP.

**Residual risk of that decision, stated explicitly.** Within the household network the traffic is readable by any other device attached to it. During the evaluation the exposure was bounded by direct supervision on networks known to the participating families, but it was not eliminated. This document specifies what must be in place before Torddis is deployed on any infrastructure that a child's data can leave.

This plan corresponds to Section S12 of Online Resource 1 of the manuscript. Control references are to Annex A of ISO/IEC 27001:2022.

---

## 1. Threat model

**Assets**, in decreasing order of sensitivity:

1. Enrolment face images (200 per child) and the per-child LBPH model.
2. Per-event evidence images.
3. The derived event stream — it reveals when a named child was studying, distracted or drowsy.
4. Guardian authentication credentials.
5. The live camera stream.

**Adversaries considered:**

| | Adversary | Present in local-only deployment? |
|---|---|---|
| (i) | Passive observer on the same LAN | Yes, bounded to the family's own network |
| (ii) | Active attacker on the path household ↔ remote server | No — removed by construction |
| (iii) | Attacker with access to server storage | No — removed by construction |
| (iv) | Malicious or compromised app on the guardian's device | Yes |
| (v) | Insider with administrative access to a deployed service | No — removed by construction |

A networked deployment reintroduces (ii), (iii) and (v) and widens (i). The controls below are therefore a **precondition** of that step, not an improvement to it.

---

## 2. Controls

Three controls are **blocking**: no deployment leaving the household may proceed without C1, C2 and C7.

### C1 — Transport encryption **(blocking)**
*Threats (i), (ii). Annex A.8.24, A.8.20.*

- TLS 1.3 on every link: mobile ↔ server, device ↔ server, server ↔ database.
- HTTP Strict Transport Security enabled.
- Plain HTTP **disabled at the listener**, not merely redirected.
- Django: `SECURE_SSL_REDIRECT`, `SESSION_COOKIE_SECURE`, `CSRF_COOKIE_SECURE`, `SECURE_HSTS_SECONDS` all set; `DEBUG = False`; `ALLOWED_HOSTS` explicit.
- ESP32-CAM: use `WiFiClientSecure` with a pinned CA certificate. Note the practical constraint — TLS on the ESP32 costs RAM and adds handshake latency, so the NFR-05 latency budget must be re-measured after C1 is implemented, not assumed to hold.

### C2 — Device and client authentication **(blocking)**
*Threats (ii), (iv). Annex A.5.16, A.8.5.*

- Mutual TLS with a per-device certificate provisioned at manufacture for the ESP32-CAM.
- Short-lived bearer tokens with refresh rotation for the mobile application.
- **No shared secret across households.** Each deployment gets its own credentials.

### C3 — Encryption at rest
*Threats (iii), (v). Annex A.8.24, A.5.33.*

- Full-disk or volume encryption on the server.
- AES-256-GCM at field level for the biometric model, the enrolment images and the evidence images.
- Keys in a key-management service, **never in the code base**.

### C4 — Credential storage
*Threat (iii). Annex A.5.17.*

- Argon2id for password derivation (PBKDF2-HMAC-SHA256 with a current iteration count where Argon2 is unavailable).
- Per-user salt.
- Rate limiting with lockout on repeated failure.

### C5 — Least privilege and separation of duties
*Threat (v). Annex A.5.15, A.8.2.*

- Distinct service accounts for the web tier, the inference tier and the database.
- No administrative account able to read image storage **and** event data simultaneously.
- Four-eyes rule for any bulk export.

### C6 — Audit logging
*Threats (iii), (v). Annex A.8.15, A.5.28.*

- Append-only log of every access to an image, model or event record: actor, subject, timestamp, purpose.
- Logs retained separately from the data they describe.
- Reviewed on a fixed schedule.

### C7 — Live-stream indicator **(blocking)**
*Threats (iv), (v). Annex A.8.16.*

- Hardware indicator on the device, **not defeasible from software**, lit whenever the camera feed is being watched.
- One log entry per streaming session, visible to the guardian.

This addresses the privacy risk that the prototype's Privacy Impact Assessment identifies but does not resolve. In a local-only deployment the guardian is physically present in the same home, which limits but does not remove the asymmetry. In a networked deployment the feed can be watched from anywhere, and an indicator that software cannot override becomes an **ethical** requirement rather than a usability feature. That is why it is blocking.

### C8 — Retention and erasure
*Threats (iii), (v). Annex A.8.10, A.5.34.*

- Automatic expiry of evidence images after a guardian-configurable window.
- Irreversible erasure of enrolment images and the per-child model on withdrawal.
- **Machine-verifiable deletion receipt** issued to the guardian.

This converts into a technical guarantee the deletion procedure that was executed manually and witnessed by the guardian during the pilot. A deployment at scale cannot rely on a researcher performing the erasure in the family's presence.

### C9 — Secrets and supply chain
*Threat (iii). Annex A.8.8, A.8.28.*

- No secret in version control (already enforced from commit `f1d5837`).
- Configuration through environment variables or a secrets manager.
- Pinned dependencies, automated vulnerability scanning, documented patching window.

### C10 — Verification
*All threats. Annex A.5.35, A.8.29.*

- Independent penetration test **and** a review against this document before the first deployment that processes a child's data.
- Repeated on any change to the deployment topology.

---

## 3. Sequencing

The controls are not independent and the order matters.

1. **C1, C2** — must precede any deployment outside the household. Without them every other control operates on data an attacker on the path can already read.
2. **C3, C4, C9** — data and secrets at rest on the server that C1 and C2 make reachable.
3. **C5, C6** — meaningful only once a multi-user service exists.
4. **C7, C8** — tied to product decisions (is remote live streaming offered at all? what retention window may a guardian set?) that should be taken *before* implementation.
5. **C10** — closes the sequence; repeat on any topology change.

**Until C1, C2 and C7 are in place, the only configuration in which Torddis is fit to process a child's data is the local-only one evaluated in the study.**
