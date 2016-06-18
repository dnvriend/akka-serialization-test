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

import java.io.ByteArrayOutputStream

import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.Movie.{ Country, Year }
import com.sksamuel.avro4s._
import org.apache.avro.Schema
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.generic.{ GenericDatumReader, GenericRecord }
import org.apache.avro.io.DecoderFactory

object Encoder {
  implicit val Base64Encoder = new Encoder[Array[Byte], Base64] {
    override def encode(in: Array[Byte]): Base64 =
      Base64(java.util.Base64.getEncoder.encodeToString(in))
  }

  implicit def EventAvroEncoder[A <: Event: SchemaFor: ToRecord] = new Encoder[A, Array[Byte]] {
    override def encode(in: A): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream[A](output)
      avro.write(in.asInstanceOf[A])
      avro.close()
      output.toByteArray
    }
  }

  implicit def EventBase64Encoder[A <: Event: SchemaFor: ToRecord] = new Encoder[A, Base64] {
    override def encode(in: A): Base64 = {
      val avro = implicitly[Encoder[A, Array[Byte]]].encode _
      val base64 = implicitly[Encoder[Array[Byte], Base64]].encode _
      (avro andThen base64)(in)
    }
  }
}
import annotation.implicitNotFound
@implicitNotFound("No member of type class Encoder in scope for in: ${IN}, out: ${OUT}")
trait Encoder[IN, OUT] {
  def encode(in: IN): OUT
}

object Decoder {
  implicit val Base64ToByteArrayDecoder = new Decoder[Base64, Array[Byte]] {
    override def decode(in: Base64): Array[Byte] =
      java.util.Base64.getDecoder.decode(in.value)
  }

  implicit val Base64ToStringDecoder = new Decoder[Base64, String] {
    override def decode(in: Base64): String =
      new String(implicitly[Decoder[Base64, Array[Byte]]].decode(in))
  }

  implicit def AvroInputStreamDecoder[A <: Event] = new Decoder[AvroInputStream[A], A] {
    override def decode(in: AvroInputStream[A]): A =
      try in.iterator.next() finally in.close
  }

  implicit def EventAvroDecoder[A <: Event: SchemaFor: FromRecord] = new Decoder[Array[Byte], A] {
    override def decode(in: Array[Byte]): A =
      implicitly[Decoder[AvroInputStream[A], A]]
        .decode(AvroInputStream[A](in))
  }

  implicit def EventBase64Decoder[A <: Event: SchemaFor: FromRecord] = new Decoder[Base64, A] {
    override def decode(in: Base64): A = {
      val base64 = implicitly[Decoder[Base64, Array[Byte]]].decode _
      val avro = implicitly[Decoder[Array[Byte], A]].decode _
      (base64 andThen avro)(in)
    }
  }

  implicit def AvroSchemaDecoder[A <: Event: ToRecord: FromRecord: RecordFormat] = new Decoder[AvroSchemaEvolution, A] {
    override def decode(in: AvroSchemaEvolution): A = {
      val gdr = new GenericDatumReader[GenericRecord](in.oldSchema, in.newSchema) // (writer, reader)
      val seek = new SeekableByteArrayInput(implicitly[Decoder[Base64, Array[Byte]]].decode(in.base64))
      val binDecoder = DecoderFactory.get().binaryDecoder(seek, null)
      val record: GenericRecord = gdr.read(null, binDecoder)
      val format = RecordFormat[A]
      format.from(record)
    }
  }
}

import annotation.implicitNotFound
@implicitNotFound("No member of type class Decoder in scope for in: ${IN}, out: ${OUT}")
trait Decoder[IN, OUT] {
  def decode(in: IN): OUT
}

case class Base64(value: String)
case class AvroSchemaEvolution(base64: Base64, oldSchema: Schema, newSchema: Schema)

trait Event
final case class MovieChangedV1(title: String, year: Int) extends Event
final case class MovieChangedV2(title: String, year: Int, director: String) extends Event
final case class MovieChangedV3(title: String, released_year: Int, director: String) extends Event
final case class MovieChangedV4(title: String, director: String, wonOscars: Int = 0) extends Event
final case class MovieChangedV5(title: String, director: String, wonOscars: Int = 0, releases: Map[Country, Year]) extends Event

class AvroTestSpec extends TestSpec {
  implicit class StringToSchema(json: String) {
    def toSchema: Schema = new (Schema.Parser).parse(json)
  }

  implicit class ArrayOps(xs: Array[Byte]) {
    def encode[OUT](implicit ev: Encoder[Array[Byte], OUT]): OUT = ev.encode(xs)
  }

  implicit class Base64Ops(base64: Base64) {
    def decode[OUT](implicit ev: Decoder[Base64, OUT]): OUT = ev.decode(base64)
  }
}