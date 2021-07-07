<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

[![Gradle Build Github Action Status](<https://img.shields.io/github/workflow/status/com-pas/compas-cim-mapping/Gradle%20Build?logo=GitHub>)](https://github.com/com-pas/compas-cim-mapping/actions?query=workflow%3A%22Gradle+Build%22)
[![REUSE status](https://api.reuse.software/badge/github.com/com-pas/compas-cim-mapping)](https://api.reuse.software/info/github.com/com-pas/compas-cim-mapping)

# compas-cim-mapping


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw package io.quarkus:quarkus-maven-plugin:2.0.0.Final:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `app/target/quarkus-app/` directory. Be aware that it’s not an _über-jar_ as
the dependencies are copied into the `app/target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar app/target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./app/target/code-with-quarkus-0.0.1-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html
.
