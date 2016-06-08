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

package com.github.dnvriend.domain

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

object OrderDomain {
  final val CD = ItemType.CD.toString
  final val DVD = ItemType.DVD.toString
  final val BluRay = ItemType.BluRay.toString
  final val Game = ItemType.Game.toString

  case object DirectDebitTypeSerializer extends CustomSerializer[ItemType](_ ⇒ ({
    case JString(CD)     ⇒ ItemType.CD
    case JString(DVD)    ⇒ ItemType.DVD
    case JString(BluRay) ⇒ ItemType.BluRay
    case JString(Game)   ⇒ ItemType.Game
  }, {
    case msg: ItemType ⇒ JString(msg.toString)
  }))

  type Title = String
  type Price = Double
  type ItemId = Option[String]

  type ZipCode = String
  type HouseNumber = Int

  type OrderName = String
  type Items = List[Item]
  type UnixTimestamp = Long
  type OrderId = Option[String]

  sealed trait ItemType
  object ItemType {
    case object CD extends ItemType
    case object DVD extends ItemType
    case object BluRay extends ItemType
    case object Game extends ItemType
  }

  final case class Item(itemType: ItemType, title: Title, price: Price, id: ItemId)
  final case class Address(zipcode: ZipCode, houseNumber: HouseNumber)
  final case class Order(name: OrderName, address: Address, items: Items, date: UnixTimestamp, id: OrderId)

  def withOrder(f: Order ⇒ Order = identity[Order])(g: Order ⇒ Unit): Unit = (g compose f)(Order(
    name = "",
    address = Address("", 1),
    items = List(Item(ItemType.BluRay, "", 25.0, Option("itemId"))),
    date = 1L,
    Some("orderId")
  ))
}
