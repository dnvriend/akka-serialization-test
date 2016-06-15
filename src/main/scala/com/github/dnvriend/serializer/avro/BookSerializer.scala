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

import com.github.dnvriend.domain.BookStore.{ ChangedBookV3, ChangedBookV2, ChangedBookV1 }
import com.sksamuel.avro4s.{ AvroBinaryOutputStream, AvroInputStream, AvroOutputStream }

abstract class BookSerializer[T] extends AvroSerializer[T] {

  final val Manifest = "ChangedBook"

}

class BookSerializerV1 extends BookSerializer[ChangedBookV1] {
  override def identifier: Int = 200011

  override def toBinary(o: AnyRef): Array[Byte] = {
    val output = new ByteArrayOutputStream
    val avro = AvroOutputStream[ChangedBookV1](output)
    avro.write(o.asInstanceOf[ChangedBookV1])
    avro.close()
    output.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    if (Manifest == manifest) {

      val is = AvroInputStream[ChangedBookV1](bytes)
      val events = is.iterator.toList
      is.close()

      events(0)

    } else throw new IllegalArgumentException(s"Unable to handle manifest $manifest, required $Manifest")
  }
}

class BookSerializerV2 extends BookSerializer[ChangedBookV2] {
  override def identifier: Int = 200012

  override def toBinary(o: AnyRef): Array[Byte] = {
    val output = new ByteArrayOutputStream
    val avro = AvroBinaryOutputStream[ChangedBookV2](output)
    avro.write(o.asInstanceOf[ChangedBookV2])
    avro.close()
    output.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    // if (Manifest == manifest) {
    println("Manifest " + manifest)
    val is = AvroInputStream[ChangedBookV2](bytes)
    val events = is.iterator.toList
    is.close()

    events(0)

    // } else throw new IllegalArgumentException(s"Unable to handle manifest $manifest, required $Manifest")
  }
}

class BookSerializerV3 extends BookSerializer[ChangedBookV3] {
  override def identifier: Int = 200013

  override def toBinary(o: AnyRef): Array[Byte] = {
    val output = new ByteArrayOutputStream
    val avro = AvroOutputStream[ChangedBookV3](output)
    avro.write(o.asInstanceOf[ChangedBookV3])
    avro.close()
    output.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    // if (Manifest == manifest) {
    println("Manifest " + manifest)
    val is = AvroInputStream[ChangedBookV3](bytes)
    val events = is.iterator.toList
    is.close()

    events(0)

    // } else throw new IllegalArgumentException(s"Unable to handle manifest $manifest, required $Manifest")
  }
}