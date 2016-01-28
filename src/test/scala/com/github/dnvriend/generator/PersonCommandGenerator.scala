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

package com.github.dnvriend.generator

import com.github.dnvriend.domain.Person.{ PersonCommand, ChangeSurname, ChangeName, RegisterName }
import org.scalacheck.Gen

object PersonCommandGenerator {

  val genRegisterName = for {
    name ← Gen.alphaStr
    surname ← Gen.alphaStr
  } yield RegisterName(name, surname)

  val genChangeName = for {
    name ← Gen.alphaStr
  } yield ChangeName(name)

  val genChangeSurname = for {
    surname ← Gen.alphaStr
  } yield ChangeSurname(surname)

  val genCommands: Gen[PersonCommand] = Gen.oneOf(genRegisterName, genChangeName, genChangeSurname)

  def registerNameCommands: List[RegisterName] = Gen.nonEmptyListOf(genRegisterName).sample.get

  def personCommands: List[PersonCommand] = Gen.nonEmptyListOf(genCommands).sample.get
}
