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

package com.github.dnvriend.serializer

import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.Person.NameChanged

class NameChangedSerializerTest extends TestSpec {
  "NameChanged" should "be serialized to a byte array" in {
    val obj = NameChanged("Foo")
    val serializer = serialization.findSerializerFor(obj)
    val bytes: Array[Byte] = serializer.toBinary(obj)
    bytes.toList should not be 'empty
  }

  it should "turn a byte array back into an object" in {
    val obj = NameChanged("Foo")
    val serializer = serialization.findSerializerFor(obj)
    val bytes = serializer.toBinary(obj)

    serializer.fromBinary(bytes, Option(obj.getClass)) should matchPattern {
      case NameChanged("Foo") ⇒
    }
  }
}
