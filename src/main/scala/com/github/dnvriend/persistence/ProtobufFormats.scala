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

import com.github.dnvriend.domain.Person.{ NameChangedEvent, NameRegisteredEvent, SurnameChangedEvent }
import com.google.protobuf.Message
import proto.person.Command
import proto.person.command.{ NameChangedMessage, NameRegisteredMessage, SurnameChangedMessage }

object ProtobufFormats {
  implicit def strToOptionString(str: String): Option[String] = Option(str)
  implicit def optionStrToString(opt: Option[String]): String = opt.getOrElse("")

  implicit val surnameChangedProtobufProtocol = new ProtobufFormat[SurnameChangedEvent] {
    override def write(event: SurnameChangedEvent): Message =
      SurnameChangedMessage.toJavaProto(SurnameChangedMessage(event.surname))

    override def read(proto: Message): SurnameChangedEvent = proto match {
      case p: Command.SurnameChangedMessage ⇒
        val msg = SurnameChangedMessage.fromJavaProto(p)
        SurnameChangedEvent(msg.surname)
    }
  }

  implicit val nameChangedProtobufProtocol = new ProtobufFormat[NameChangedEvent] {
    override def write(event: NameChangedEvent): Message =
      NameChangedMessage.toJavaProto(NameChangedMessage(event.name))

    override def read(proto: Message): NameChangedEvent = proto match {
      case p: Command.NameChangedMessage ⇒
        val msg = NameChangedMessage.fromJavaProto(p)
        NameChangedEvent(msg.name)
    }
  }

  implicit val nameRegisteredProtobufProtocol = new ProtobufFormat[NameRegisteredEvent] {
    override def write(event: NameRegisteredEvent): Message = {
      NameRegisteredMessage.toJavaProto(NameRegisteredMessage(event.name, event.surname))
    }

    override def read(proto: Message): NameRegisteredEvent = proto match {
      case p: Command.NameRegisteredMessage ⇒
        val msg = NameRegisteredMessage.fromJavaProto(p)
        NameRegisteredEvent(msg.name, msg.surname)
    }
  }
}
