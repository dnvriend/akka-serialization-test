footer: © Dennis Vriend, 2016
slidenumbers: true

### Google Protocol Buffers
#### (Google's data interchange format)

---

### What is Data Serialization

^ The process of translating a data structure and its
object state into a format that can be stored in a memory buffer,
file or transported on a network

^ End goal being that it can be reconstructed in annother computer environment

---

### Reasons why we do this

- Persist objects (store and retrieve)
- Preform Remote Prodecure Calls

---

### Popular Platform independent solutions

- JSON and XML
- BSON and Binary XML
- Google Protocol Buffers,
- Thrift (Facebook),
- Avro
- Kryo
- Chill (Twitter),
- MessagePack (.Net)

---

### JSON and XML

- Popular formats
- (Easily?) human readable format
- Used by web-based APIs
- Generators for creation and consuming

---

### Google Protocol Buffers

- Structured and typed platform independent binary format
- Very small when compared to XML and JSON
- Available since 2001, open source since 2008

---

### What are protocol buffers

- Language that describes data structures
- Defines encoding as bytes
- Compiler generates code for Java, C++, Python,
Go, Ruby, JavaScript, Scala, etc

---

### Parsing speed

- Binary is close to machine speed since it is already a
binary format; there is no need to parse it first

---

### Versioning

- Schema evolution
- Backward compatibity between old and new protocol buffers
- Even if a field has changed, data will be parsed

---

### Why use Protocol Buffers

- Small size; efficient binary format
- Easier to programmatically access the data
- Typesafe inter proces communication
- Typesafe cross team communication
- Safe persistent storage format
- Great for evolving schemas using optional fields

---

### Why not to use

- Maintain both client and server
- Not an industry standard

---

### Installation

- https://developers.google.com/protocol-buffers/

- OSX: brew install protobuf
stable 2.6.1 (bottled), devel 3.0.0-beta-3, HEAD

$ protoc --version
libprotoc 2.6.1

---

### What is an IDL

- Write an Interface Description Language (IDL) file
- In case of Protocol buffers a .proto file
- Compile the IDL
- Get interface code for your language of choice ie.
Java, Scala, Python, JavaScript, C++ and many more

---

### Protocol buffer messages

- Specify structure and details of the information being serialized
- Each protocol buffer message is a small logical record of information
containing a series of name-value pairs
- .proto file is a contract

---

### .proto message

- Data is hierarchical structured in messages
- Each message has one or more uniquely numbered fields
- Each field has a name and value
- Value types can be numbers, boolean, strings, raw bytes,
other protobuf message types

---

### .proto message

- Field types: required, optional, repeated
- It sends tag/value over the wire
(1-value, 2-value, 3-value, 4-value, etc)

---

### simple .proto example

```
message Person {
  optional string first_name = 1;
  optional string last_name = 2;
  optional int32 age = 3;
}
```

```
$ protoc person.proto --java_out=.
```

---

### more complex .proto example

```
message Person {
  required string name = 1;
  required int32 id = 2;
  optional string email = 3;
  enum PhoneType { MOBILE=0; HOME=1; WORK=2 }
  message PhoneNumber {
   required string number = 1;
   optional PhoneType type = 2 [default = HOME];
  }
  repeated PhoneNumber phone = 4;
}
```

---

### Schema evolution

# Before

```
message Person {
  optional string first_name = 1;
  optional string last_name = 2;
  optional int32 age = 3;
}
```

# After

```
message Person {
  optional string given_name = 1;
  optional string surname = 2;
  // optional int32 age = 3;
  optional int32 year_of_birth = 4;
}
```

---

### Schema evolution

You __can__ do:
- add optional fields
- remove optional fields
- rename fields
- convert between compatible types in32 -> int64 -> boolean (with loss)
- convert optional to repeated
- convert repeated to optional (keeping last)

---

### Schema evolution

You __cannot__ do:
- Change a field type (unless it's compatible) eg. int32 -> string
- Remove a required field, parser will crash
- Add a required field, parser will crash

(just don't use required fields); use application level validation
for required fields, they are deprecated, proto3 doesn't have required

---

### Compilation of .proto messages

- install compiler (brew)
- from .proto file, protoc generates code of the language of choice
- For Java, compiler generates java classes containing POJO builder
- Parsing and serialization API is available

---

### ScalaPB

- protocol buffer compiler plugin for scala
- generates scala case classes, parses, serializers for you protocol buffers
- uses 'protoc' for full compatibility and processes its output to scala
- conversion to and from JSON

---

### SBT plugin

---

### Compatibility

---

### GRPC

- High performance open source general RPC framework for mobile and HTTP/2 first
- http://www.grpc.io/
- https://www.infoq.com/news/2015/02/grpc

---

