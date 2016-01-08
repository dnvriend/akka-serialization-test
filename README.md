# akka-serialization-test
Study on [akka-serialization](http://doc.akka.io/docs/akka/2.4.1/scala/serialization.html) using [Google Protocol Buffers](https://developers.google.com/protocol-buffers/docs/overview).

# TL;DR
Define domain command and events messages in the companion object of the `PersistentActor` using DDD concepts. 
Define protobuf messages in the .proto file that will be written to persistent storage using Akka Persistence. 
Create Akka `SerializerWithStringManifest` that map event case classes to and from protobuf classes.
Register the custom serializers and domain event case classes in `application.conf` so the serialization will be
done transparently.

# Overview
[Akka serialization](http://doc.akka.io/docs/akka/2.4.1/scala/serialization.html) is a good way for domain messages
like commands and events to be serialized to a format of choice. In this example, the domain messages are defined
in the companion object of the `Person` which is an `PersistentActor`. The actor handles commands like `RegisterName`,
`ChangeName` and `ChangeSurname`, and stores events like `NameRegisted`, `NameChanged` and `SurnameChanged` to 
persistent storage. The serialization method and the details of the persistent storage is unknown to the `PersistentActor`
which is a good thing.

Using [Akka serialization](http://doc.akka.io/docs/akka/2.4.1/scala/serialization.html), three custom serializers [NameRegisteredSerializer](https://github.com/dnvriend/akka-serialization-test/blob/master/src/main/scala/com/github/dnvriend/serializer/NameRegisteredSerializer.scala),
[NameChangedSerializer](https://github.com/dnvriend/akka-serialization-test/blob/master/src/main/scala/com/github/dnvriend/serializer/NameChangedSerializer.scala), [SurnameChangedSerializer](https://github.com/dnvriend/akka-serialization-test/blob/master/src/main/scala/com/github/dnvriend/serializer/SurnameChangedSerializer.scala) are registered in `application.conf`: 

```
akka {
    actor {
        serializers {
          personCreated = "com.github.dnvriend.serializer.NameRegisteredSerializer"
          nameChanged = "com.github.dnvriend.serializer.NameChangedSerializer"
          surnameChanged = "com.github.dnvriend.serializer.SurnameChangedSerializer"
        }
    }
}
```

Also, the serialization-binding, which domain class will be handled by which serializer are registered in `application.conf`:

```
akka {
    actor {
         serialization-bindings {
              "com.github.dnvriend.domain.Person$NameRegistered" = personCreated
              "com.github.dnvriend.domain.Person$NameChanged" = nameChanged
              "com.github.dnvriend.domain.Person$SurnameChanged" = surnameChanged
        }
    }
}
```

Message serialization is now the responsibility of the custom serializer. When no serialization binding can be found 
to a certain message, the default `akka.serialization.JavaSerializer` will be used, which may or may not be a good thing.

All serializers are responsible for turning an object into an `Array[Byte]` (marshal) and an `Array[Byte]` 
into an object (unmarshal). Its the responsibility of the serializer to choose an appropriate method for 
serialization. For example, the domain message may be converted to a String representation, eg. CSV, XML or JSON, 
afterwards the formatted string must be converted to an `Array[Byte]`, because that must be the return type of the 
serializer when it marshals an object.

The serializer can also be used to convert an `Array[Byte]` into an object (unmarshal). The serializer has all 
the knowledge to interpret the `Array[Byte]`. When the `Array[Byte]` is actually a CSV, the array must first be 
converted into a string, then the fields must be parsed, and then an object must be created, because the serializer 
must return an `AnyRef` type when it unmarshals the `Array[Byte]`.

The three custom serializers use [Google Protocol Buffers](https://developers.google.com/protocol-buffers/docs/overview),
so the `Array[Byte]` that the persistent storage will store is actually a protobuf object.

# The example
The example project shows the following:

* How to setup sbt to compile .proto files,
* How to create custom serializers,
* How to create a simple Person domain object that handles processes messages and stores state,
* How to configure akka to use the custom serializers and configure the serialization bindings,
* How to test the Person domain object
* How to test the custom serializers.

Have fun!
