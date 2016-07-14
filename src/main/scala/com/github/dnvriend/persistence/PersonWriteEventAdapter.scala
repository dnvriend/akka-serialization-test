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

class PersonWriteEventAdapter extends ProtobufWriteEventAdapter {
  import ProtobufFormats._

  override def manifest(event: Any): String =
    event.getClass.getSimpleName

  override def toJournal(event: Any): Any = event match {
    case event: NameRegisteredEvent ⇒ serializeTagged(event)
    case event: NameChangedEvent    ⇒ serializeTagged(event)
    case event: SurnameChangedEvent ⇒ serializeTagged(event)
    case _                          ⇒ throw new RuntimeException(s"Cannot serialize '${event.getClass.getName}' to protobuf")
  }
}
