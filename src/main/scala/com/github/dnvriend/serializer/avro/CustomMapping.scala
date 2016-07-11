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

package com.github.dnvriend.serializer.avro

import java.time.Duration

import com.sksamuel.avro4s.{ FromValue, ToValue, ToSchema }
import org.apache.avro.Schema
import org.apache.avro.Schema.Field

object CustomMapping {

  implicit object DurationToSchema extends ToSchema[Duration] {
    override protected val schema: Schema = Schema.create(Schema.Type.STRING)
  }

  implicit object DurationToValue extends ToValue[Duration] {
    override def apply(value: Duration): String = value.toMillis.toString
  }

  implicit object DurationFromValue extends FromValue[Duration] {
    override def apply(value: Any, field: Field): Duration = Duration.ofMillis(value.toString.toInt)
  }

}
