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

import com.github.dnvriend.domain.Movie.MovieChanged
import com.sksamuel.avro4s.{ AvroInputStream, AvroOutputStream }

class MovieChangedSerializer extends AvroSerializer[MovieChanged] {
  override def identifier: Int = 100011
  final val Manifest = classOf[MovieChanged].getName

  override def toBinary(o: AnyRef): Array[Byte] = {
    val output = new ByteArrayOutputStream
    val avro = AvroOutputStream[MovieChanged](output)
    avro.write(o.asInstanceOf[MovieChanged])
    avro.close()
    output.toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    if (Manifest == manifest) {

      val is = AvroInputStream[MovieChanged](bytes)
      val events = is.iterator.toList
      is.close()

      events(0)

    } else throw new IllegalArgumentException(s"Unable to handle manifest $manifest, required $Manifest")
  }
}
