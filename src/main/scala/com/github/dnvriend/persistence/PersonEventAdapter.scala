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

import akka.persistence.journal.{ EventAdapter, EventSeq, Tagged }
import com.github.dnvriend.domain.Person.{ NameChangedPersonEvent, NameRegisteredPersonEvent, SurnameChangedPersonEvent }
import com.google.protobuf.Message

class PersonEventAdapter extends EventAdapter {
  import PersonEventProtobufEventAdapter._
  println("===> Creating: PersonEventAdapter ")
  override def manifest(event: Any): String = {
    val manifest = event.getClass.getSimpleName
    println("using manifest:  " + manifest)
    manifest
  }

  override def toJournal(event: Any): Any = event match {
    case event: NameRegisteredPersonEvent ⇒
      println("===> (toJournal:NameRegisteredPersonEvent")
      Tagged(implicitly[ProtobufSerializer[NameRegisteredPersonEvent]].serialize(event), Set(NameRegisteredPersonEvent.getClass.getSimpleName))
    case event: NameChangedPersonEvent ⇒
      println("===> (toJournal:NameChangedPersonEvent")
      Tagged(implicitly[ProtobufSerializer[NameChangedPersonEvent]].serialize(event), Set(NameChangedPersonEvent.getClass.getSimpleName))
    case event: SurnameChangedPersonEvent ⇒
      println("===> (toJournal:SurnameChangedPersonEvent")
      Tagged(implicitly[ProtobufSerializer[SurnameChangedPersonEvent]].serialize(event), Set(SurnameChangedPersonEvent.getClass.getSimpleName))
  }

  override def fromJournal(event: Any, manifest: String): EventSeq = event match {
    case proto: Message if manifest == "NameRegisteredPersonEvent" ⇒
      println("===> (fromJournal:NameRegisteredPersonEvent")
      EventSeq.single(implicitly[ProtobufDeserializer[NameRegisteredPersonEvent]].deserialize(proto))
    case proto: Message if manifest == "NameChangedPersonEvent" ⇒
      println("===> (fromJournal:NameChangedPersonEvent")
      EventSeq.single(implicitly[ProtobufDeserializer[NameChangedPersonEvent]].deserialize(proto))
    case proto: Message if manifest == "SurnameChangedPersonEvent" ⇒
      println("===> (fromJournal:SurnameChangedPersonEvent")
      EventSeq.single(implicitly[ProtobufDeserializer[SurnameChangedPersonEvent]].deserialize(proto))
    case _ ⇒
      println("event: " + event + " manifest: " + manifest)
      EventSeq.single(event)
  }
}
