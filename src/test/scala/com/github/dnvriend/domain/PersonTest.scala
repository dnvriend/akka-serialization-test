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

package com.github.dnvriend.domain

import akka.persistence.inmemory.query.InMemoryReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.testkit.scaladsl.TestSink
import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.Person._
import com.github.dnvriend.repository.PersonRepository

class PersonTest extends TestSpec {
  lazy val queries = PersistenceQuery(system).readJournalFor[InMemoryReadJournal](InMemoryReadJournal.Identifier)

  def eventsForPersistenceIdSource(id: String) =
    queries.currentEventsByPersistenceId(id, 0L, Long.MaxValue).map(_.event)

  "Person" should "register a name" in {
    val person = PersonRepository.forId("person-1")
    person ! RegisterName("John", "Doe")

    eventsForPersistenceIdSource("person-1")
      .runWith(TestSink.probe[Any])
      .request(1)
      .expectNext(NameRegistered("John", "Doe"))
      .expectComplete()

    cleanup(person)
  }

  it should "update its name and surname" in {
    val person = PersonRepository.forId("person-1")
    person ! ChangeName("Robin")
    person ! ChangeSurname("Hood")

    eventually {
      eventsForPersistenceIdSource("person-1")
        .runWith(TestSink.probe[Any])
        .request(3)
        .expectNext(NameRegistered("John", "Doe"), NameChanged("Robin"), SurnameChanged("Hood"))
        .expectComplete()
    }
  }
}
