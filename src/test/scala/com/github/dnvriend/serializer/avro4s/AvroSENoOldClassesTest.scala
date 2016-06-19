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

/**
 * https://avro.apache.org/docs/1.7.6/spec.html
 * https://docs.oracle.com/cd/NOSQL/html/GettingStartedGuide/schemaevolution.html
 * https://martin.kleppmann.com/2012/12/05/schema-evolution-in-avro-protocol-buffers-thrift.html
 * http://ben-tech.blogspot.nl/2013/05/avro-schema-evolution.html
 */
class AvroSENoOldClassesTest extends AvroTestSpec {
  "String" should "encode to base64" in {
    "Hello World!".getBytes().encode[Base64].value shouldBe "SGVsbG8gV29ybGQh"
  }

  it should "decode from base64" in {
    Base64("SGVsbG8gV29ybGQh").decode[String] shouldBe "Hello World!"
  }

  "MovieChangedV1" should "encode to base64" in {
    val event = MovieChangedV1("foo", 1990)
    val encoder = implicitly[Encoder[MovieChangedV1, Base64]]
    val base64 = encoder.encode(event)
    base64.value shouldBe "BmZvb4wf"
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[Base64, MovieChangedV1]]
    decoder.decode(Base64("BmZvb4wf")) shouldBe MovieChangedV1("foo", 1990)
  }

  "MovieChangedV2" should "encode to base64" in {
    val event = MovieChangedV2("foo", 1990, "bar")
    val encoder = implicitly[Encoder[MovieChangedV2, Base64]]
    val base64 = encoder.encode(event)
    base64.value shouldBe "BmZvb4wfBmJhcg=="
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[Base64, MovieChangedV2]]
    decoder.decode(Base64("BmZvb4wfBmJhcg==")) shouldBe MovieChangedV2("foo", 1990, "bar")
  }

  "MovieChangedV3" should "encode to base64" in {
    val event = MovieChangedV3("foo", 1990, "bar")
    val encoder = implicitly[Encoder[MovieChangedV3, Base64]]
    val base64 = encoder.encode(event)
    base64.value shouldBe "BmZvb4wfBmJhcg=="
  }

  it should "decode from base64" in {
    val decoder = implicitly[Decoder[Base64, MovieChangedV3]]
    decoder.decode(Base64("BmZvb4wfBmJhcg==")) shouldBe MovieChangedV3("foo", 1990, "bar")
  }

  "v1 to v2" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroSchemaEvolution, MovieChangedV2]]
    decoder.decode(AvroSchemaEvolution(
      Base64("BmZvb4wf"), //MovieChangedV1("foo", 1990)
      oldSchema = SchemaRegistry.movieChanged(1),
      newSchema = SchemaRegistry.movieChanged(2)
    )) shouldBe MovieChangedV2("foo", 1990, "unknown")
  }

  "v2 to v3" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroSchemaEvolution, MovieChangedV3]]
    decoder.decode(AvroSchemaEvolution(
      Base64("BmZvb4wfBmJhcg=="), //MovieChangedV2("foo", 1990, "bar")
      oldSchema = SchemaRegistry.movieChanged(2),
      newSchema = SchemaRegistry.movieChanged(3)
    )) shouldBe MovieChangedV3("foo", 1990, "bar")
  }

  "v1 to v3" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroSchemaEvolution, MovieChangedV3]]
    decoder.decode(AvroSchemaEvolution(
      Base64("BmZvb4wf"), // MovieChangedV1("foo", 1990)
      oldSchema = SchemaRegistry.movieChanged(1),
      newSchema = SchemaRegistry.movieChanged(3)
    )) shouldBe MovieChangedV3("foo", 1990, "unknown")
  }

  "v1 to v4" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroSchemaEvolution, MovieChangedV4]]
    decoder.decode(AvroSchemaEvolution(
      Base64("BmZvb4wf"), // MovieChangedV1("foo", 1990)
      oldSchema = SchemaRegistry.movieChanged(1),
      newSchema = SchemaRegistry.movieChanged(4)
    )) shouldBe MovieChangedV4("foo", "unknown", 0)
  }

  "v1 to v5" should "decode with format" in {
    val decoder = implicitly[Decoder[AvroSchemaEvolution, MovieChangedV5]]
    decoder.decode(AvroSchemaEvolution(
      Base64("BmZvb4wf"), //MovieChangedV1("foo", 1990)
      oldSchema = SchemaRegistry.movieChanged(1),
      newSchema = SchemaRegistry.movieChanged(5)
    )) shouldBe MovieChangedV5("foo", "unknown", 0, Map.empty)
  }
}
