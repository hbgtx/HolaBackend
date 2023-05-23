# Hola Backend


Backend for Hola chat app [See here](https://github.com/hbgtx/Hola)


## Quick Start:  
Go to project Directory and build using gradle:

```console
gradle clean && gradle fatJar
```

A jar file will be created in the build directory, run the jar file using following command:

```console
java -jar build/libs/HolaBackend-1.0-SNAPSHOT.jar
```

For custom port use:

```console
java -jar build/libs/HolaBackend-1.0-SNAPSHOT.jar port=<port_number>
```