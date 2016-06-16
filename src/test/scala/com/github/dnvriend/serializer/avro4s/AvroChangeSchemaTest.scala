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
import com.github.dnvriend.domain.BookStore.{ ChangedBookV1, ChangedBookV2, ChangedBookV3, ChangedBookV4 }
import com.github.dnvriend.serializer.avro.{ BookSerializerV1, BookSerializerV2, BookSerializerV3 }
import com.sksamuel.avro4s.{ AvroSchema, RecordFormat }
import org.apache.avro.Schema
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.generic.{ GenericDatumReader, GenericRecord }
import org.apache.avro.io.DecoderFactory

class AvroChangeSchemaTest extends TestSpec {

  @Override
  def fromBytes(bytes: Array[Byte], schema: Schema): GenericRecord = {
    val serveReader = new GenericDatumReader[GenericRecord](schema)
    serveReader.read(null, DecoderFactory.get().binaryDecoder(bytes, null))
  }

  val title = "Moby-Dick; or, The Whale"
  val year = 1851
  val editor = "Scala Books"

  "ChangeVersionTest" should "deserialize old class with renamed field" in {
    // in this case, two different serializers can be used

    val obj = ChangedBookV1(title, year)
    val serializerV1 = new BookSerializerV1
    val bytes: Array[Byte] = serializerV1.toBinary(obj)
    val serializerV2 = new BookSerializerV2

    serializerV2.fromBinary(bytes) should matchPattern {
      case ChangedBookV2(`title`, `year`) ⇒
    }
  }

  it should "deserialize old class without new field" in {

    val obj = ChangedBookV2(title, year)
    val serializerV2 = new BookSerializerV2
    val bytes: Array[Byte] = serializerV2.toBinary(obj)

    val in = new SeekableByteArrayInput(bytes)

    val schema2 = AvroSchema[ChangedBookV2]
    val schema3 = AvroSchema[ChangedBookV3]

    val gdr = new GenericDatumReader[GenericRecord](schema2, schema3)
    val binDecoder = DecoderFactory.get().binaryDecoder(in, null)
    val record: GenericRecord = gdr.read(null, binDecoder)
    val format = RecordFormat[ChangedBookV3]
    val r = format.from(record)

    r should matchPattern {
      case ChangedBookV3(`title`, `year`, "") ⇒
    }

  }

  it should "deserialize old class with dropped field" in {

    val obj = ChangedBookV3(title, year, editor)
    val serializerV3 = new BookSerializerV3
    val bytes: Array[Byte] = serializerV3.toBinary(obj)

    val in = new SeekableByteArrayInput(bytes)

    val schema3 = AvroSchema[ChangedBookV3]
    val schema4 = AvroSchema[ChangedBookV4]

    val gdr = new GenericDatumReader[GenericRecord](schema3, schema4)
    val binDecoder = DecoderFactory.get().binaryDecoder(in, null)
    val record: GenericRecord = gdr.read(null, binDecoder)
    val format = RecordFormat[ChangedBookV4]
    val r = format.from(record)

    r should matchPattern {
      case ChangedBookV4(`title`, `editor`) ⇒
    }

  }

}
