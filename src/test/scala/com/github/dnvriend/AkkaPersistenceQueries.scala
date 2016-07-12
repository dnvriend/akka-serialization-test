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

package com.github.dnvriend

import akka.NotUsed
import akka.persistence.inmemory.query.scaladsl.InMemoryReadJournal
import akka.persistence.query.{ EventEnvelope, PersistenceQuery }
import akka.persistence.query.scaladsl.{ CurrentEventsByPersistenceIdQuery, ReadJournal }
import akka.stream.scaladsl.Source

trait AkkaPersistenceQueries { _: TestSpec ⇒
  lazy val queries = PersistenceQuery(system).readJournalFor[ReadJournal with CurrentEventsByPersistenceIdQuery](InMemoryReadJournal.Identifier)

  def eventsForPersistenceIdSource(id: String): Source[EventEnvelope, NotUsed] =
    queries.currentEventsByPersistenceId(id, 0L, Long.MaxValue)
}
