<!--
SPDX-FileCopyrightText: 2023 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->
# Security Policy

## Verifying Docker images

Docker images published by this project are signed using [Cosign](https://github.com/sigstore/cosign) keyless signing via [Sigstore](https://www.sigstore.dev/). Signatures are recorded in the public [Rekor](https://rekor.sigstore.dev/) transparency log — no private key is stored or required.

To verify an image, install Cosign ([instructions](https://docs.sigstore.dev/cosign/system_config/installation/)) and run:

```sh
cosign verify \
  --certificate-identity "https://github.com/com-pas/compas-cim-mapping/.github/workflows/release-please.yml@refs/heads/main" \
  --certificate-oidc-issuer "https://token.actions.githubusercontent.com" \
  lfenergy/compas-cim-mapping:<tag>
```

Replace `<tag>` with the specific release tag (e.g. `0.12.1`) or `latest`.

## Reporting a Vulnerability

Please go to [Security Advisories](https://github.com/com-pas/compas-cim-mapping/security/advisories) to privately report a security vulnerability, 
our contributors will try to respond within a week of your report with a rough plan for a fix and new tests.
