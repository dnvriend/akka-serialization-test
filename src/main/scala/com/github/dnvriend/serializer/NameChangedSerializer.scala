/*
 * Copyright 2015 Dennis Vriend
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

package com.github.dnvriend.serializer

import akka.serialization.SerializerWithStringManifest
import com.github.dnvriend.domain.Person.NameChanged

class NameChangedSerializer extends SerializerWithStringManifest {
  import com.github.dnvriend.domain.person.proto._

  override def identifier: Int = 101

  final val Manifest = classOf[NameChanged].getName

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef =
    manifest match {
      case Manifest ⇒
        val PersonEvents.NameChanged(name) = PersonEvents.NameChanged.parseFrom(bytes)
        NameChanged(name)
      case _ ⇒ throw new IllegalArgumentException("Unable to handle manifest: " + manifest)
    }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case NameChanged(name) ⇒
      PersonEvents.NameChanged(name).toByteArray
  }
}
