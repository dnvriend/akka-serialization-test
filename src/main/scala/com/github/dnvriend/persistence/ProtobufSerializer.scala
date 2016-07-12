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

package com.github.dnvriend.persistence

import com.github.dnvriend.domain.Person.{ NameChangedPersonEvent, NameRegisteredPersonEvent, SurnameChangedPersonEvent }
import com.google.protobuf.Message
import proto.Datamodel.{ NameChanged, NameRegistered, SurnameChanged }

trait ProtobufSerializer[A] {
  def serialize(event: A): Message
}

trait ProtobufDeserializer[A] {
  def deserialize(proto: Message): A
}

trait ProtobufProtocol[A] extends ProtobufSerializer[A] with ProtobufDeserializer[A]

object PersonEventProtobufEventAdapter {
  implicit val surnameChangedProtobufProtocol = new ProtobufProtocol[SurnameChangedPersonEvent] {
    override def serialize(event: SurnameChangedPersonEvent): Message = {
      val builder = SurnameChanged.newBuilder
      builder.setSurname(event.surname)
      builder.build()
    }

    override def deserialize(proto: Message): SurnameChangedPersonEvent = proto match {
      case p: SurnameChanged ⇒ SurnameChangedPersonEvent(p.getSurname)
    }
  }

  implicit val nameChangedProtobufProtocol = new ProtobufProtocol[NameChangedPersonEvent] {
    override def serialize(event: NameChangedPersonEvent): Message = {
      val builder = NameChanged.newBuilder()
      builder.setName(event.name)
      builder.build()
    }

    override def deserialize(proto: Message): NameChangedPersonEvent = proto match {
      case p: NameChanged ⇒ NameChangedPersonEvent(p.getName)
    }
  }

  implicit val nameRegisteredProtobufProtocol = new ProtobufProtocol[NameRegisteredPersonEvent] {
    override def serialize(event: NameRegisteredPersonEvent): Message = {
      val builder = NameRegistered.newBuilder()
      builder.setName(event.name)
      builder.setSurname(event.surname)
      builder.build()
    }

    override def deserialize(proto: Message): NameRegisteredPersonEvent = proto match {
      case p: NameRegistered ⇒ NameRegisteredPersonEvent(p.getName, p.getSurname)
    }
  }
}
