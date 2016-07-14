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

trait ProtobufWriter[A] {
  def write(event: A): Message
}

trait ProtobufReader[A] {
  def read(proto: Message): A
}

trait ProtobufFormat[A] extends ProtobufWriter[A] with ProtobufReader[A]

object ProtobufFormats {
  implicit val surnameChangedProtobufProtocol = new ProtobufFormat[SurnameChangedPersonEvent] {
    override def write(event: SurnameChangedPersonEvent): Message = {
      val builder = SurnameChanged.newBuilder
      builder.setSurname(event.surname)
      builder.build()
    }

    override def read(proto: Message): SurnameChangedPersonEvent = proto match {
      case p: SurnameChanged ⇒ SurnameChangedPersonEvent(p.getSurname)
    }
  }

  implicit val nameChangedProtobufProtocol = new ProtobufFormat[NameChangedPersonEvent] {
    override def write(event: NameChangedPersonEvent): Message = {
      val builder = NameChanged.newBuilder()
      builder.setName(event.name)
      builder.build()
    }

    override def read(proto: Message): NameChangedPersonEvent = proto match {
      case p: NameChanged ⇒ NameChangedPersonEvent(p.getName)
    }
  }

  implicit val nameRegisteredProtobufProtocol = new ProtobufFormat[NameRegisteredPersonEvent] {
    override def write(event: NameRegisteredPersonEvent): Message = {
      val builder = NameRegistered.newBuilder()
      builder.setName(event.name)
      builder.setSurname(event.surname)
      builder.build()
    }

    override def read(proto: Message): NameRegisteredPersonEvent = proto match {
      case p: NameRegistered ⇒ NameRegisteredPersonEvent(p.getName, p.getSurname)
    }
  }
}
