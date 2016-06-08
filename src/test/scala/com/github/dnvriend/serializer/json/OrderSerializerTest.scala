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

package com.github.dnvriend.serializer.json

import akka.serialization.Serializer
import com.github.dnvriend.TestSpec
import com.github.dnvriend.generator.OrderGenerator
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._

class OrderSerializerTest extends TestSpec {

  it should "serialize random orders to JSON" in {
    forAll(OrderGenerator.genOrder) { order ⇒
      val serializer: Serializer = serialization.findSerializerFor(order)
      val binary = serializer.toBinary(order)
      parse(new String(binary)) shouldBe a[JValue]
    }
  }

  it should "serialize random orders from AnyRef to JSON and back to AnyRef" in {
    forAll(OrderGenerator.genOrder) { order ⇒
      val serializer: Serializer = serialization.findSerializerFor(order)
      val binary = serializer.toBinary(order)
      parse(new String(binary)) shouldBe a[JValue]
      serializer.fromBinary(binary) shouldBe order
    }
  }
}
