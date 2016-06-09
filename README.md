# akka-serialization-test
Study on [akka-serialization][ser] using [Google Protocol Buffers][pb], [Kryo][kryo] and [Avro][avro]

# TL;DR
Define domain command and events messages in the companion object of the `PersistentActor` using DDD concepts.
Configure the serialization library you wish to use, looking at the examples. Register the serializer to use 
in `application.conf` in the path `akka.actor.serializers` and register the classes to bind to a certain serializer in the path 
`akka.actor.serialization-bindings`. When the serializer and bindings have been configured, Akka serialization will transparently 
serialize/deserialize messages.

# Overview
[Akka serialization][ser] is a good way for domain messages
like commands and events to be serialized to a format of choice. In this example, the domain messages are defined
in the companion object of the `Person` which is an `PersistentActor`. The actor handles commands like `RegisterName`,
`ChangeName` and `ChangeSurname`, and stores events like `NameRegisted`, `NameChanged` and `SurnameChanged` to 
persistent storage. The serialization method and the details of the persistent storage is unknown to the `PersistentActor`
which is a good thing.

Using [Akka serialization][ser], three custom serializers [NameRegisteredSerializer](https://github.com/dnvriend/akka-serialization-test/blob/master/src/main/scala/com/github/dnvriend/serializer/NameRegisteredSerializer.scala),
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

The three custom serializers use [Google Protocol Buffers][pb],
so the `Array[Byte]` that the persistent storage will store is actually a protobuf object.

# The example
The example project shows the following:

* How to setup sbt to compile .proto files,
* How to create custom serializers,
* How to create a simple Person domain object that handles processes messages and stores state,
* How to configure akka to use the custom serializers and configure the serialization bindings,
* How to test the Person domain object
* How to test the custom serializers.

# Kryo, Twitter Chill and Akka
[Kryo][kryo] is a fast and efficient object graph serialization framework for Java. The goals of the project are speed, 
efficiency, and an easy to use API. The project is useful any time objects need to be persisted, whether to a file, database, 
or over the network. To be able to use Kryo effectively on [Scala][scala], I will be using [Twitter's Chill][chill] which provides 
extensions for the [Kryo][kryo] serialization library including serializers and a set of classes to ease configuration of 
[Kryo][kryo] in systems like [Hadoop][hadoop], [Storm][storm], [Akka][akka] and is available on [maven-central][chill-maven-central].
 
## Kryo Akka Serialization
[Chill][chill] provides a [Kryo Akka Serializer][chill-akka] out of the box.

## Apache Avro
[Avro][avro-wiki] is a remote procedure call and data serialization framework developed within Apache's Hadoop project. 
It uses JSON for defining data types and protocols, and serializes data in a compact binary format. Its primary use is 
in Apache Hadoop, where it can provide both a serialization format for persistent data, and a wire format for communication 
between Hadoop nodes, and from client programs to the Hadoop services.

It is similar to [Thrift][thrift-wiki], but does not require running a code-generation program when a schema changes 
(unless desired for statically-typed languages).

We will be using [Stephen Samuel's][sksamuel] (also known for [Elastic4s][elastic4s], a non-blocking, type safe DSL and Scala client for Elasticsearch),
[Avro4s][avro4s] project, which is a schema/class generation and serializing/deserializing library for Avro written in Scala. The objective of [Avro4s][avro4s] 
is to allow seamless use with Scala without the need to to write boilerplate conversions yourself, and without the runtime overhead of reflection. 
Hence, this is a _macro based_ library and generates code for use with Avro at compile time.

## Apache Thrift
_not yet available_

# What's new?
## 1.0.1 (2016-06-08)
  - Merged PR #1 [Giampaolo Trapasso][trapasso] Added Apache Avro serialization example, thanks!

## 1.0.0 (2016-06-07)
  - Added Kryo serialization example

Have fun!

[trapasso]: https://github.com/giampaolotrapasso
[sksamuel]: https://github.com/sksamuel

[akka]: http://akka.io/
[hadoop]: http://hadoop.apache.org/
[storm]: http://storm.apache.org/
[ser]: http://doc.akka.io/docs/akka/current/scala/serialization.html
[pb]: https://developers.google.com/protocol-buffers/docs/overview
[kryo]: https://github.com/EsotericSoftware/kryo
[scala]: http://www.scala-lang.org/
[chill]: https://github.com/twitter/chill
[chill-akka]: https://github.com/twitter/chill#chill-akka
[chill-maven-central]: http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.twitter%22%20AND%20a%3A%22chill-akka_2.11%22
[avro]: https://avro.apache.org/
[avro-wiki]: https://en.wikipedia.org/wiki/Apache_Avro
[avro4s]: https://github.com/sksamuel/avro4s
[elastic4s]: https://github.com/sksamuel/elastic4s
[thrift-wiki]: https://en.wikipedia.org/wiki/Apache_Thrift