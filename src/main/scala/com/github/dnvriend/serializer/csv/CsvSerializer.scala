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

package com.github.dnvriend.serializer.csv

import akka.actor.ExtendedActorSystem
import akka.serialization.BaseSerializer
import com.github.dnvriend.domain.CsvText

/**
 * A base serializer should be registered in reference.conf in
 * akka.actor.serialization-identifiers."com.github.dnvriend.serializer.csv.CsvSerializer" = 1
 */
class CsvSerializer(val system: ExtendedActorSystem) extends BaseSerializer {
  assert(system != null, "No actor system injected")
  override def includeManifest: Boolean = true

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case CsvText(str) ⇒ str.getBytes
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef =
    CsvText(new String(bytes) + "-from-csv-serializer")
}