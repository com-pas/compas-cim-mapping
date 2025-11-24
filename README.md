<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

[![Maven Build Github Action Status](<https://img.shields.io/github/workflow/status/com-pas/compas-cim-mapping/Maven%20Build?logo=GitHub>)](https://github.com/com-pas/compas-cim-mapping/actions?query=workflow%3A%22Maven+Build%22)
[![REUSE status](https://api.reuse.software/badge/github.com/com-pas/compas-cim-mapping)](https://api.reuse.software/info/github.com/com-pas/compas-cim-mapping)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com-pas_compas-cim-mapping&metric=alert_status)](https://sonarcloud.io/dashboard?id=com-pas_compas-cim-mapping)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/5925/badge)](https://bestpractices.coreinfrastructure.org/projects/5925)
[![Slack](https://raw.githubusercontent.com/com-pas/compas-architecture/master/public/LFEnergy-slack.svg)](http://lfenergy.slack.com/)

# CoMPAS CIM Mapping

## Mapping between IEC CIM and IEC 61850

The Mapping between IEC CIM and IEC 61850 is described in [Mapping](MAPPING.md).

## Development

Information about how to run and develop for this project, check [Development](DEVELOPMENT.md).

## Environment variables

Below environment variable(s) can be used to configure which claim is used to fill user information, for instance the
Who Attribute in the History Record.

| Environment variable             | Java Property                  | Description                                   | Example          |
| -------------------------------- | ------------------------------ | --------------------------------------------- | ---------------- |
| USERINFO_WHO_CLAIMNAME           | compas.userinfo.who.claimname  | The Name of the user used in the Who History. | name             |

## Security

To use most of the endpoints the user needs to be authenticated using JWT in the authorization header. There are 4
environment variables that can be set in the container to configure the validation/processing of the JWT.

| Environment variable             | Java Property                    | Description                                        | Example                                                                |
| -------------------------------- | -------------------------------- | -------------------------------------------------- | ---------------------------------------------------------------------- |
| JWT_VERIFY_KEY                   | smallrye.jwt.verify.key.location | Location of certificates to verify the JWT.        | http://localhost:8089/auth/realms/compas/protocol/openid-connect/certs |
| JWT_VERIFY_ISSUER                | mp.jwt.verify.issuer             | The issuer of the JWT.                             | http://localhost:8089/auth/realms/compas                               |
| JWT_VERIFY_CLIENT_ID             | mp.jwt.verify.audiences          | The Client ID that should be in the "aud" claim.   | cim-mapping                                                            |
| JWT_GROUPS_PATH                  | smallrye.jwt.path.groups         | The JSON Path where to find the roles of the user. | resource_access/cim-mapping/roles                                      |

There are no roles defined in this service, only need to be authenticated.

## Contributing

Please refer to the [CoMPAS Contributing Guide](https://com-pas.github.io/contributing/) for contribution guidelines.