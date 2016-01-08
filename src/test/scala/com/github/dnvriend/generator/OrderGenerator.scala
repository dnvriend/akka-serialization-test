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

package com.github.dnvriend.generator

import com.github.dnvriend.domain._
import org.scalacheck.Gen

object OrderGenerator {

  val genItemType = Gen.oneOf("CD", "DVD", "BluRay", "Game")

  val genItem = for {
    itemType ← genItemType
    title ← Gen.alphaStr
    price ← Gen.choose(2.50, 60.0)
    id ← Gen.option(Gen.uuid)
  } yield Item(itemType, title, price, id.map(_.toString))

  val genItems = Gen.listOf(genItem)

  val genAddress = for {
    zipcode ← Gen.alphaStr
    houseNumber ← Gen.choose(1, 200)
  } yield Address(zipcode, houseNumber)

  val dateGen = Gen.const(System.currentTimeMillis())

  val genOrder = for {
    name ← Gen.alphaStr
    address ← genAddress
    items ← genItems
    date ← dateGen
  } yield Order(name, address, items, date, None)

  def orders: List[Order] = Gen.listOf(genOrder).sample.getOrElse(Nil)
}
