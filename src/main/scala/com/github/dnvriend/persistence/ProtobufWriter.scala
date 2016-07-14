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
import proto.person.Command._

trait ProtobufWriter[A] {
  def write(event: A): Message
}

trait ProtobufReader[A] {
  def read(proto: Message): A
}

trait ProtobufFormat[A] extends ProtobufWriter[A] with ProtobufReader[A]

