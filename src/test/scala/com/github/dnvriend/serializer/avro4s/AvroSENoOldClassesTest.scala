/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend.serializer.avro4s

class AvroSENoOldClassesTest extends AvroTestSpec {

  "String" should "encode to base64" in {
    val encoder = implicitly[Encoder[Array[Byte], String]]
    val base64String = encoder.encode("Hello World!".getBytes())
    base64String shouldBe "SGVsbG8gV29ybGQh"
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, Array[Byte]]]
    new String(decoder.decode("SGVsbG8gV29ybGQh")) shouldBe "Hello World!"
  }

  "MovieChangedV1" should "encode to base64" in {
    val event = MovieChangedV1("foo", 1990)
    val encoder = implicitly[Encoder[MovieChangedV1, String]]
    val base64String = encoder.encode(event)
    base64String shouldBe "BmZvb4wf"
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, MovieChangedV1]]
    decoder.decode("BmZvb4wf") shouldBe MovieChangedV1("foo", 1990)
  }

  "MovieChangedV2" should "encode to base64" in {
    val event = MovieChangedV2("foo", 1990, "bar")
    val encoder = implicitly[Encoder[MovieChangedV2, String]]
    val base64String = encoder.encode(event)
    base64String shouldBe "BmZvb4wfBmJhcg=="
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, MovieChangedV2]]
    decoder.decode("BmZvb4wfBmJhcg==") shouldBe MovieChangedV2("foo", 1990, "bar")
  }

  "MovieChangedV3" should "encode to base64" in {
    val event = MovieChangedV3("foo", 1990, "bar")
    val encoder = implicitly[Encoder[MovieChangedV3, String]]
    val base64String = encoder.encode(event)
    base64String shouldBe "BmZvb4wfBmJhcg=="
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[String, MovieChangedV3]]
    decoder.decode("BmZvb4wfBmJhcg==") shouldBe MovieChangedV3("foo", 1990, "bar")
  }

  "v1 to v2" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroCommand, MovieChangedV2]]
    decoder.decode(AvroCommand(
      "BmZvb4wf",
      oldSchema =
        """
            |{
            |  "type" : "record",
            |  "name" : "MovieChanged",
            |  "version" : 1,
            |  "namespace" : "com.github.dnvriend.serializer.avro4s",
            |  "fields" : [
            |   { "name" : "title", "type" : "string" },
            |   { "name" : "year", "type" : "int" }
            |  ]
            |}
          """.stripMargin.toSchema,
      newSchema =
        """
          |{
          |  "type" : "record",
          |  "name" : "MovieChanged",
          |  "version" : 2,
          |  "namespace" : "com.github.dnvriend.serializer.avro4s",
          |  "fields" : [
          |   { "name" : "title", "type" : "string" },
          |   { "name" : "year", "type" : "int" },
          |   { "name" : "director", "type" : "string", "default" : "unknown" }
          |  ]
          |}
        """.stripMargin.toSchema
    )) shouldBe MovieChangedV2("foo", 1990, "unknown")
  }

  "v2 to v3" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroCommand, MovieChangedV3]]
    decoder.decode(AvroCommand(
      "BmZvb4wfBmJhcg==",
      oldSchema = """
        |{
        |  "type" : "record",
        |  "name" : "MovieChanged",
        |  "version" : 2,
        |  "namespace" : "com.github.dnvriend.serializer.avro4s",
        |  "fields" : [
        |   { "name" : "title", "type" : "string" },
        |   { "name" : "year", "type" : "int" },
        |   { "name" : "director", "type" : "string" }
        |  ]
        |}
      """.stripMargin.toSchema,
      newSchema = """
        |{
        |  "type" : "record",
        |  "name" : "MovieChanged",
        |  "version" : 3,
        |  "namespace" : "com.github.dnvriend.serializer.avro4s",
        |  "fields" : [
        |   { "name" : "title", "type" : "string"},
        |   { "name" : "released_year", "type" : "int", "aliases" : ["year"] },
        |   { "name" : "director", "type" : "string"}
        |  ]
        |}
      """.stripMargin.toSchema
    )) shouldBe MovieChangedV3("foo", 1990, "bar")
  }

  "v1 to v3" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroCommand, MovieChangedV3]]
    decoder.decode(AvroCommand(
      "BmZvb4wf",
      oldSchema =
        """
        |{
        |  "type" : "record",
        |  "name" : "MovieChanged",
        |  "version" : 1,
        |  "namespace" : "com.github.dnvriend.serializer.avro4s",
        |  "fields" : [
        |   { "name" : "title", "type" : "string" },
        |   { "name" : "year", "type" : "int" }
        |  ]
        |}
      """.stripMargin.toSchema,
      newSchema =
        """
        |{
        |  "type" : "record",
        |  "name" : "MovieChanged",
        |  "version" : 3,
        |  "namespace" : "com.github.dnvriend.serializer.avro4s",
        |  "fields" : [
        |   { "name" : "title", "type" : "string"},
        |   { "name" : "released_year", "type" : "int", "aliases" : ["year"] },
        |   { "name" : "director", "type" : "string", "default" : "unknown" }
        |  ]
        |}
      """.stripMargin.toSchema
    )) shouldBe MovieChangedV3("foo", 1990, "unknown")
  }
}
