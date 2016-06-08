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

object PetDomain {
  type FirstName = String
  type LastName = String
  type PetName = String
  type Age = Int
  type StreetName = String
  type HouseNumber = Int

  sealed trait Species
  case object Species {
    case object Cat extends Species
    case object Dog extends Species
  }

  sealed trait Gender
  case object Gender {
    case object Male extends Gender
    case object Female extends Gender
  }

  final case class Address(street: StreetName, houseNumber: HouseNumber)
  final case class Owner(firstName: FirstName, lastName: LastName, age: Age, address: Address)

  final case class Pet(name: PetName = "Fido", species: Species = Species.Dog, gender: Gender = Gender.Male, age: Age = 1, owner: Option[Owner] = Some(Owner("John", "Doe", 25, Address("first street", 10)))) {
    val GenderTypes = Gender
    val SpeciesTypes = Species
  }

  def withPet(f: Pet ⇒ Pet = identity[Pet])(g: Pet ⇒ Unit): Unit = (g compose f)(Pet())
}
