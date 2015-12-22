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
import com.github.dnvriend.domain.Person.NameRegistered

/**
 * The PersonCreated serializer does the following:
 * <ul>
 *   <li>Uses the case class as identifier for the manifest</li>
 *   <li>It will receive a case class in the `toBinary` method, and maps it to a protobuf type that will be mapped to a byte array</li>
 *   <li>The format stored in the journal is a protobuf byte array, it must be mapped to a protobuf type and then to a case class</li>
 * </ul>
 */
class NameRegisteredSerializer extends SerializerWithStringManifest {
  import com.github.dnvriend.domain.person.proto._

  override def identifier: Int = 100

  final val Manifest = classOf[NameRegistered].getName

  override def manifest(o: AnyRef): String = o.getClass.getName

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef =
    manifest match {
      case Manifest ⇒
        val PersonEvents.NameRegistered(name, surname) = PersonEvents.NameRegistered.parseFrom(bytes)
        NameRegistered(name, surname)
      case _ ⇒ throw new IllegalArgumentException("Unable to handle manifest: " + manifest)
    }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case NameRegistered(name, surname) ⇒
      PersonEvents.NameRegistered(name, surname).toByteArray
  }
}
