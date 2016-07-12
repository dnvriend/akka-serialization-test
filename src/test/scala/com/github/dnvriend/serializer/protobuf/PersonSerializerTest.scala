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

package com.github.dnvriend.serializer.protobuf

import com.github.dnvriend.TestSpec
import proto.datamodel2.Person

class PersonSerializerTest extends TestSpec {
  it should "serialize proto.Person to protobuf" in {
    val javaProtobufPerson = proto.Datamodel2.Person.newBuilder().setId(1).build

    val person = Person(1)
    val array = person.toByteArray
    val personSerializer = serialization.findSerializerFor(person)
    val javaProtobufPersonSerializer = serialization.findSerializerFor(javaProtobufPerson)
    val arraySerializer = serialization.findSerializerFor(array)
    // not akka.remote.serialization.ProtobufSerializer
    personSerializer.getClass.getName shouldBe "akka.serialization.JavaSerializer"
    arraySerializer.getClass.getName shouldBe "akka.serialization.ByteArraySerializer"
    javaProtobufPersonSerializer.getClass.getName shouldBe "akka.remote.serialization.ProtobufSerializer"
  }

}
