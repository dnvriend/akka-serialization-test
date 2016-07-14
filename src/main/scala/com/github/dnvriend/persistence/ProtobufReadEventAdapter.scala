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

import akka.persistence.journal.{ EventSeq, ReadEventAdapter }
import com.github.dnvriend.domain.Person.{ NameChangedEvent, NameRegisteredEvent, SurnameChangedEvent }
import com.google.protobuf.Message

class ProtobufReadEventAdapter extends ReadEventAdapter {
  import ProtobufFormats._

  protected def deserialize[A: ProtobufReader](proto: Message): EventSeq =
    EventSeq.single(implicitly[ProtobufReader[A]].read(proto))

  override def fromJournal(event: Any, manifest: String): EventSeq = event match {
    case proto: Message if manifest == classOf[NameRegisteredEvent].getSimpleName ⇒ deserialize[NameRegisteredEvent](proto)
    case proto: Message if manifest == classOf[NameChangedEvent].getSimpleName    ⇒ deserialize[NameChangedEvent](proto)
    case proto: Message if manifest == classOf[SurnameChangedEvent].getSimpleName ⇒ deserialize[SurnameChangedEvent](proto)
    case _                                                                        ⇒ throw new RuntimeException(s"[${this.getClass.getName}]Cannot deserialize '${event.getClass.getName}' with manifest: '$manifest' from protobuf ")
  }
}
