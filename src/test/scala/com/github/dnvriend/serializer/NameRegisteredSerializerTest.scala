/*
 * Copyright 2015 Dennis Vriend
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
import com.github.dnvriend.domain.Person.NameRegistered

class NameRegisteredSerializerTest extends TestSpec {
  "NameRegistered" should "be serialized to a byte array" in {
    val obj = NameRegistered("Foo", "Bar")
    val serializer = serialization.findSerializerFor(obj)
    val bytes: Array[Byte] = serializer.toBinary(obj)
    bytes shouldBe a[Array[Byte]]
  }

  it should "unmarshal the protobuf byte array to a protobuf object bypassing the akka serializer" in {
    val obj = NameRegistered("Foo", "Bar")
    val serializer = serialization.findSerializerFor(obj)
    val bytes: Array[Byte] = serializer.toBinary(obj)

    //convert the byte array into a protobuf object
    import com.github.dnvriend.domain.person.proto._
    PersonEvents.NameRegistered.parseFrom(bytes) should matchPattern {
      case PersonEvents.NameRegistered("Foo", "Bar") ⇒
    }
  }

  it should "turn a byte array back into an object" in {
    val obj = NameRegistered("Foo", "Bar")
    val serializer = serialization.findSerializerFor(obj)
    val bytes = serializer.toBinary(obj)

    serializer.fromBinary(bytes, Option(obj.getClass)) should matchPattern {
      case NameRegistered("Foo", "Bar") ⇒
    }
  }
}
