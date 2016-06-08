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

package com.github.dnvriend.serializer.json

import akka.serialization.Serializer
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.json4s.{ Formats, NoTypeHints }

case class EventWrapper(manifest: String, payload: String)

class JsonSerializer extends Serializer {

  implicit val formats: Formats = Serialization.formats(NoTypeHints)

  override def identifier: Int = Int.MaxValue

  override def includeManifest: Boolean = true

  override def toBinary(o: AnyRef): Array[Byte] =
    write(EventWrapper(o.getClass.getName, write(o))).getBytes()

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val wrapper: EventWrapper = parse(new String(bytes)).extract[EventWrapper]
    implicit val mf = Manifest.classType(Class.forName(wrapper.manifest))
    read(wrapper.payload)
  }
}
