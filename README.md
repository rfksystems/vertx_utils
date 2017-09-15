# Vert.x utils

Common Vert.x utilities, written in Kotlin.

[![Maven Central](https://img.shields.io/maven-central/v/com.rfksystems/vertx_utils.svg?style=flat-square)](http://mvnrepository.com/artifact/com.rfksystems/vertx_utils)

## Installation

### Maven

Add Maven dependency:

```xml
<dependency>
    <groupId>com.github.rfksystems</groupId>
    <artifactId>vertx_utils</artifactId>
    <version>${vertx_utils.version}</version>
</dependency>
```

## Utility classes

### [ManagedVerticleFactory](./src/main/kotlin/com/rfksystems/vertx_utils/ManagedVerticleFactory.kt)

`io.vertx.core.spi.VerticleFactory` implementation that sources all options, including class name,
deployment options and configuration from `io.vertx.core.json.JsonObject` structure.

Designed to work with Vert.x Config component, it will also work with anything else 
provided that the data structure for `JsonObject` remains the same.

#### Usage

Define configuration (example in Yaml, but all providers should work given same structure):

```yaml
com.example.someapp.services.web:
    main: com.example.someapp.services.WebServiceVerticle # Verticle class
    options: # Verticle Deployment options
        ha: true
        instances: 5
        worker: true
        config: # Verticle config, available via config() in verticle class
            foo: bar
            port: 8091
```

Load configuration and construct the `ManagedVerticleFactory`
providing instance of `io.vertx.core.json.JsonObject` with above configuration.

Register it to Vert.x 

```java
final ManagedVerticleFactory = new ManagedVerticleFactory(servicesConfiguration);
vertx.registerVerticleFactory(managedVerticleFactory);
```

Deploy verticle:

```java
vertx.deploy("local:com.example.someapp.services.web")
```

You can change the default `local` prefix to whatever you please using secondary
constructor of `ManagedVerticleFactory`.

### Other managers

See [https://jitpack.io](https://jitpack.io)


### License

Apache License, Version 2.0
