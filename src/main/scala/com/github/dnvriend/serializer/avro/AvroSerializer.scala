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

package com.github.dnvriend.serializer.avro

import java.io.ByteArrayOutputStream

import akka.serialization.SerializerWithStringManifest
import com.github.dnvriend.domain.Music._
import com.sksamuel.avro4s._

import CustomMapping._

abstract class AvroSerializer[T] extends SerializerWithStringManifest {
  override def manifest(o: AnyRef): String = o.getClass.getName
}

class TitleChangedSerializer extends AvroSerializer[TitleChanged] {
  override def identifier: Int = 100010
  final val Manifest = classOf[TitleChanged].getName

  override def toBinary(o: AnyRef): Array[Byte] = {
    val output = new ByteArrayOutputStream
    val avro = AvroOutputStream[TitleChanged](output)
    avro.write(o.asInstanceOf[TitleChanged])
    avro.close()
    output.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    if (Manifest == manifest) {

      val is = AvroInputStream[TitleChanged](bytes)
      val events = is.iterator.toList
      is.close()

      events(0)

    } else throw new IllegalArgumentException(s"Unable to handle manifest $manifest, required $Manifest")
  }
}

class YearChangedSerializer extends AvroSerializer[YearChanged] {
  override def identifier: Int = 100011
  final val Manifest = classOf[YearChanged].getName

  override def toBinary(o: AnyRef): Array[Byte] = {
    val output = new ByteArrayOutputStream
    val avro = AvroOutputStream[YearChanged](output)
    avro.write(o.asInstanceOf[YearChanged])
    avro.close()
    output.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    if (Manifest == manifest) {

      val is = AvroInputStream[YearChanged](bytes)
      val events = is.iterator.toList
      is.close()

      events(0)

    } else throw new IllegalArgumentException(s"Unable to handle manifest $manifest, required $Manifest")
  }
}

class SongAddedSerializer extends AvroSerializer[SongAdded] {
  override def identifier: Int = 100012
  final val Manifest = classOf[SongAdded].getName

  override def toBinary(o: AnyRef): Array[Byte] = {
    val output = new ByteArrayOutputStream
    val avro = AvroOutputStream[SongAdded](output)
    avro.write(o.asInstanceOf[SongAdded])
    avro.close()
    output.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    if (Manifest == manifest) {

      val is = AvroInputStream[SongAdded](bytes)
      val events = is.iterator.toList
      is.close()

      events(0)

    } else throw new IllegalArgumentException(s"Unable to handle manifest $manifest, required $Manifest")
  }
}

class SongRemovedSerializer extends AvroSerializer[SongRemoved] {
  override def identifier: Int = 100013
  final val Manifest = classOf[SongRemoved].getName

  override def toBinary(o: AnyRef): Array[Byte] = {
    val output = new ByteArrayOutputStream
    val avro = AvroOutputStream[SongRemoved](output)
    avro.write(o.asInstanceOf[SongRemoved])
    avro.close()
    output.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    if (Manifest == manifest) {

      val is = AvroInputStream[SongRemoved](bytes)
      val events = is.iterator.toList
      is.close()

      events(0)

    } else throw new IllegalArgumentException(s"Unable to handle manifest $manifest, required $Manifest")
  }
}

