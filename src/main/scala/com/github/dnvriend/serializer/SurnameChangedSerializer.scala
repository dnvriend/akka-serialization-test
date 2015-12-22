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
import com.github.dnvriend.domain.Person.SurnameChanged

class SurnameChangedSerializer extends SerializerWithStringManifest {
  import com.github.dnvriend.domain.person.proto._

  override def identifier: Int = 102

  final val Manifest = classOf[SurnameChanged].getName

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef =
    manifest match {
      case Manifest ⇒
        val PersonEvents.SurnameChanged(surname) = PersonEvents.SurnameChanged.parseFrom(bytes)
        println("Surname changed")
        SurnameChanged(surname)
      case _ ⇒ throw new IllegalArgumentException("Unable to handle manifest: " + manifest)
    }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case SurnameChanged(surname) ⇒
      println("Surname changed")
      PersonEvents.SurnameChanged(surname).toByteArray
  }
}
