<!--
SPDX-FileCopyrightText: 2021 Alliander N.V.

SPDX-License-Identifier: Apache-2.0
-->

# Development for CIM Mapping

This project uses Java 17 and Quarkus to build and run the application. The project is split into multi modules.
The app module will use Quarkus to expose the services as REST XML Endpoints. The service module contains all
the logic to convert CIM Data into IEC 61850 XML. This module uses no Quarkus dependencies, but mainly standard java
dependencies and PowSyBl/MapStruct dependencies to do the conversion. This way the service module can also be used in
other environment as Java library, for instance a Spring project.

## Building the application

You can use Maven to build the application and see if all tests are working using:

```shell script
./mvnw clean verify
```

This should normally be enough to also run the application, but there were cases that we need to build using:

```shell script
./mvnw clean install
```

This to make the local modules available for the app module to run the application.

## Running the application locally in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw package io.quarkus:quarkus-maven-plugin::dev
```

### Application depends on a running KeyCloak instance for dev mode

A KeyCloak instance needs to be running on port 8089 by default in dev mode. If a custom KeyCloak instance is used see
[Security](README.md#security) for more details.

There is a preconfigured Demo KeyCloak instance available for CoMPAS in the
[CoMPAS Deployment Repository](https://github.com/com-pas/compas-deployment). This repository can be cloned and
used to execute the following commands to create a local Docker Image with the CoMPAS Demo configuration.

```shell
cd <CoMPAS Deployment Repository Directory>/compas/keycloak
docker build -t compas_keycloak . 
```

A Docker Image `compas_keycloak` is created that can be started using the following command

```shell
docker run --rm --name compas_keycloak \
   -p 8089:8080 
   -d compas_keycloak:latest
```

There are now 3 users available to be used, `scl-data-editor`, `scl-data-reader`, `scd-reader`. See
[CoMPAS Deployment Repository](https://github.com/com-pas/compas-deployment) for more information about the users.

## Testing the application

The application is tested with unit and integration tests, but you can also manually test the application using for
instance Postman. And there is also a way to test this service with the CoMPAS OpenSCD Frontend application.

### Postman

To manually test the application there is a Postman collection in the directory `postman` that can be imported
and used to execute REST XML Calls.

To make the call work we also need to import an environment and authorisation collection. These files can be found
in [CoMPAS Deployment Repository](https://github.com/com-pas/compas-deployment) in the directory `postman`
(`auth.collection.json` and `local.environment.json`).

In the authorisation collection there are called for the 3 users known within the Demo KeyCloak instance.
If one of these calls are executed there is a variable `bearer` filled.

Now one of the CIM Mapping calls can be executed, the variable `bearer` is added to the header of the request.
After the call is executed the result should be shown in Postman.

### CoMPAS OpenSCD Frontend application

To test the CIM Mapping with the CoMPAS OpenSCD application just run the application in dev mode, including the
KeyCloak instance. For further instruction how to start the CoMPAS OpenSCD application and use this locally see
the file `DEVELOPMENT.md` in [CoMPAS OpenSCD application](https://github.com/com-pas/compas-open-scd).

## Docker Images

### Creating a Docker image with native executable

The releases created in the repository will create a docker image with a native executable. If you're running a Linux
system it's possible to create and run the executable locally. You can create a Docker image with native executable
using:

```shell script
./mvnw package -Pnative-image
```

You can then execute your native executable with: `./app/target/app-local-SNAPSHOT-runner`

### Creating a Docker image with JVM executable

There is also a profile to create a Docker Image which runs the application using a JVM. You can create a Docker Image
with JVM executable using:

```shell script
./mvnw package -Pjvm-image
```

The JVM Image can also (temporary) be created by the release action if there are problems creating or running the
native executable.
