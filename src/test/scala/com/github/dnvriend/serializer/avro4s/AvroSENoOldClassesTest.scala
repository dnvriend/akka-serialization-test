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

import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.BookStore.ChangedBookV4
import com.github.dnvriend.domain.Movie.{ Country, Year, MovieChanged }
import com.github.dnvriend.serializer.avro.{ MovieChangedSerializer, BookSerializerV1 }
import com.sksamuel.avro4s.{ RecordFormat, AvroSchema }
import com.typesafe.sslconfig.Base64
import org.apache.avro.Schema
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.generic.{ GenericDatumReader, GenericRecord }
import org.apache.avro.io.DecoderFactory

class AvroSENoOldClassesTest extends TestSpec {

  // this corresponds to MovieChanged("Eternal sunshine of a spotless mind", 2014)
  // converted to base64 using rfc2045
  //
  //    val bytes: Array[Byte] = serializerV1.toBinary(MovieChanged("Eternal sunshine of a spotless mind", 2014))
  //    val s = Base64.rfc2045.encodeToString(bytes, false)
  //
  val version1 = "RkV0ZXJuYWwgc3Vuc2hpbmUgb2YgYSBzcG90bGVzcyBtaW5kvB8="
  // this is the corresponding schema
  val schema1 =
    """
      |{
      |  "type" : "record",
      |  "name" : "MovieChanged",
      |  "namespace" : "com.github.dnvriend.domain",
      |  "fields" : [ {
      |    "name" : "title",
      |    "type" : "string"
      |  }, {
      |    "name" : "year",
      |    "type" : "int"
      |  } ]
      |}
    """.stripMargin

  // this corresponds to MovieChanged("Eternal sunshine of a spotless mind", 2014, "Michel Gondry")
  val version2 = "RkV0ZXJuYWwgc3Vuc2hpbmUgb2YgYSBzcG90bGVzcyBtaW5kvB8aTWljaGVsIEdvbmRyeQ=="
  val schema2 =
    """
      |{
      |  "type" : "record",
      |  "name" : "MovieChanged",
      |  "namespace" : "com.github.dnvriend.domain",
      |  "fields" : [ {
      |    "name" : "title",
      |    "type" : "string"
      |  }, {
      |    "name" : "year",
      |    "type" : "int"
      |  }, {
      |    "name" : "director",
      |    "type" : "string",
      |    "default" : "unknown"
      |  } ]
      |}
      |
    """.stripMargin

  // this corresponds to MovieChanged("Eternal sunshine of a spotless mind", 2014, "Michel Gondry")
  val version3 = "RkV0ZXJuYWwgc3Vuc2hpbmUgb2YgYSBzcG90bGVzcyBtaW5kvB8aTWljaGVsIEdvbmRyeQ=="
  val schema3 =
    """
      |{
      |  "type" : "record",
      |  "name" : "MovieChanged",
      |  "namespace" : "com.github.dnvriend.domain",
      |  "fields" : [ {
      |    "name" : "title",
      |    "type" : "string"
      |  }, {
      |    "name" : "release_year",
      |    "type" : "int"
      |  }, {
      |    "name" : "director",
      |    "type" : "string",
      |    "default" : "unknown"
      |  }
    """.stripMargin

  // this corresponds to MovieChanged("Eternal sunshine of a spotless mind", "Michel Gondry", 1)
  val version4 = "RkV0ZXJuYWwgc3Vuc2hpbmUgb2YgYSBzcG90bGVzcyBtaW5kGk1pY2hlbCBHb25kcnkC"
  val schema4 =
    """
      |{
      |  "type" : "record",
      |  "name" : "MovieChanged",
      |  "namespace" : "com.github.dnvriend.domain",
      |  "fields" : [ {
      |    "name" : "title",
      |    "type" : "string"
      |  }, {
      |    "name" : "director",
      |    "type" : "string",
      |    "default" : "unknown"
      |  }, {
      |    "name" : "wonOscars",
      |    "type" : "int",
      |    "default" : "0"
      |  } ]
      |
    """.stripMargin

  // this corresponds to
  // MovieChanged("Eternal sunshine of a spotless mind",
  //              "Michel Gondry",
  //              1,
  //              Map("Italy" → 2004, "Japan" → 2005))
  //
  val version5 = "RkV0ZXJuYWwgc3Vuc2hpbmUgb2YgYSBzcG90bGVzcyBtaW5kGk1pY2hlbCBHb25kcnkCBApJdGFseagfCkphcGFuqh8A"
  val schema5 =
    """
      |{
      |  "type" : "record",
      |  "name" : "MovieChanged",
      |  "namespace" : "com.github.dnvriend.domain",
      |  "fields" : [ {
      |    "name" : "title",
      |    "type" : "string"
      |  }, {
      |    "name" : "director",
      |    "type" : "string",
      |    "default" : "unknown"
      |  }, {
      |    "name" : "wonOscars",
      |    "type" : "int",
      |    "default" : 0
      |  }, {
      |    "name" : "releases",
      |    "type" : {
      |      "type" : "map",
      |      "values" : "int"
      |    }
      |  } ]
      |}
      |
    """.stripMargin

  @Override
  def fromBytes(bytes: Array[Byte], schema: Schema): GenericRecord = {
    val serveReader = new GenericDatumReader[GenericRecord](schema)
    serveReader.read(null, DecoderFactory.get().binaryDecoder(bytes, null))
  }

  "AvroSENoOldClassesTest" should "deserialize old class with renamed field" in {
    val title = "Eternal sunshine of a spotless mind"
    val year = 2014
    val director = "Michel Gondry"
    val oscars = 1
    val releases: Map[Country, Year] = Map("Italy" → 2004, "Japan" → 2005)

    val current: Schema = new (Schema.Parser).parse(schema5)

    val objects = List(version1
    //, version2, version3, version4, version5
    ).map(Base64.rfc2045.decode)
    val schemas = List(
      schema1
    // schema2,
    // schema3,
    // schema4,
    // schema5
    ).map(x ⇒ new (Schema.Parser).parse(x))

    val zip = objects.zip(schemas)

    val deserialized: List[MovieChanged] = zip map {
      case (bytes, schema) ⇒
        val gdr = new GenericDatumReader[GenericRecord](schema, current)
        val in = new SeekableByteArrayInput(bytes)
        val binDecoder = DecoderFactory.get().binaryDecoder(in, null)
        val record: GenericRecord = gdr.read(null, binDecoder)
        val format = RecordFormat[MovieChanged]
        format.from(record)
    }

    forAll(deserialized) {
      movieChanged ⇒
        movieChanged should matchPattern {
          case MovieChanged(`title`, dir, wonOscar, rel) if Set("unknown", director).contains(dir) &&
            Set(0, oscars).contains(wonOscar) &&
            Set(Map[Country, Year](), releases).contains(rel) ⇒
        }
    }

  }
}
