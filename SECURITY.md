# Security Policy

## Reporting a Vulnerability

The Torddis team takes security bugs seriously. Thank you for improving the security of this reproducibility package.

### How to report

**Please do NOT report security vulnerabilities through public GitHub issues.**

Instead, please report them via email to the corresponding authors of the manuscript:

- Miguel J. Hornos --- <mhornos@ugr.es>
- Gleiston Guerrero-Ulloa --- <gguerrero@uteq.edu.ec>

Include the following information in your report (as much as you can provide) to help us better understand the nature and scope of the possible issue:

- Type of issue (e.g. buffer overflow, SQL injection, cross-site scripting, exposure of personal data of participants, weak cryptography, etc.).
- Full paths of source file(s) related to the manifestation of the issue.
- The location of the affected source code (tag/branch/commit or direct URL).
- Any special configuration required to reproduce the issue.
- Step-by-step instructions to reproduce the issue.
- Proof-of-concept or exploit code (if possible).
- Impact of the issue, including how an attacker might exploit it.

## Supported Versions

Only the latest release (currently `v1.0.0`) receives security updates.

| Version | Supported |
|---------|-----------|
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x: |

## What to expect

You will receive a response from us within **7 days**. If the issue is confirmed as a vulnerability, we will:

1. Confirm the problem and determine the affected components.
2. Audit code to find any potential similar problems.
3. Prepare a fix for all supported versions.
4. Publish the fix as a new release with a `SECURITY` prefix in the `CHANGELOG.md`.
5. Publicly disclose the vulnerability after the fix is released.

## Special note on participants' data

If the reported vulnerability could allow the re-identification of a participant (a child or a guardian), we will treat it with the highest priority and act in accordance with the applicable data-protection regulations, including immediate withdrawal of the affected dataset from public access until the mitigation is in place.

## Attribution

If you responsibly disclose a vulnerability and consent to being named, we will acknowledge your contribution in the `CHANGELOG.md` and in the acknowledgements of any subsequent publication that reports the fix.
