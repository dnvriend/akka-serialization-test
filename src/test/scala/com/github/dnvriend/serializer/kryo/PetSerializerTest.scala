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

package com.github.dnvriend.serializer.kryo

import akka.persistence.PersistentRepr
import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.PetDomain._
import com.github.dnvriend.implicits.ArrayOps._

class PetSerializerTest extends TestSpec {
  "Pet" should "be serialized to a byte array" in withPet() { pet ⇒
    val serializer = serialization.findSerializerFor(pet)
    serializer.getClass.getName shouldBe "com.twitter.chill.akka.AkkaSerializer"
    val bytes: Array[Byte] = serializer.toBinary(pet)
    bytes.size shouldBe 320 // kryo = 320 bytes, java = 991 bytes
    //    println(bytes.toHex())
  }

  it should "be deserialized" in withPet() { pet ⇒
    val serializer = serialization.findSerializerFor(pet)
    serializer.getClass.getName shouldBe "com.twitter.chill.akka.AkkaSerializer"
    val bytes: Array[Byte] = serializer.toBinary(pet)

    serializer.fromBinary(bytes, Option(pet.getClass)) should matchPattern {
      case `pet` ⇒
    }
  }

  it should "be serialized in a PersistentRepr" in withPet() { pet ⇒
    // first we will serialize pet on its own
    val petSerializer = serialization.findSerializerFor(pet)
    petSerializer.getClass.getName shouldBe "com.twitter.chill.akka.AkkaSerializer"
    val petBytes: Array[Byte] = petSerializer.toBinary(pet)
    petBytes.size shouldBe 320

    // next we'll wrap pet in a PersistentRepr
    val repr = PersistentRepr(pet, 1, "pid1")
    val serializer = serialization.findSerializerFor(repr)
    serializer.getClass.getName should startWith("akka.persistence.serialization.MessageSerializer")
    val reprBytes: Array[Byte] = serializer.toBinary(repr)
    reprBytes.size shouldBe 339 // the repr should be serialized using Protobuf and the Payload using Kryo, which would seem to be the case

    // let's deserialize
    serializer.fromBinary(reprBytes, classOf[PersistentRepr]) should matchPattern {
      case `repr` ⇒
    }
  }
}
