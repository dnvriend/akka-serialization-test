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

import akka.pattern.ask
import akka.stream.scaladsl.{ Sink, Source }
import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.Person._
import com.github.dnvriend.generator.PersonCommandGenerator
import com.github.dnvriend.repository.PersonRepository

class PersonTest extends TestSpec {

  "Person" should "register a name" in {
    val person = PersonRepository.forId("person-1")
    val xs = PersonCommandGenerator.registerNameCommands
    Source(xs).mapAsync(1)(person ? _).runWith(Sink.ignore).futureValue

    eventsForPersistenceIdSource("person-1").testProbe { tp ⇒
      tp.request(Int.MaxValue)
      tp.expectNextN(xs.map(cmd ⇒ NameRegistered(cmd.name, cmd.surname)))
      tp.expectComplete()
    }

    killActors(person)
  }

  it should "update its name and surname" in {
    val person = PersonRepository.forId("person-2")
    val xs = PersonCommandGenerator.personCommands
    Source(xs).mapAsync(1)(person ? _).runWith(Sink.ignore).futureValue

    eventsForPersistenceIdSource("person-2").testProbe { tp ⇒
      tp.request(Int.MaxValue)
      tp.expectNextN(xs.map {
        case RegisterName(name, surname) ⇒ NameRegistered(name, surname)
        case ChangeName(name)            ⇒ NameChanged(name)
        case ChangeSurname(surname)      ⇒ SurnameChanged(surname)
      })
      tp.expectComplete()
    }
  }
}
