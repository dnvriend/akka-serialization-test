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

import java.time.Duration

import akka.persistence.inmemory.query.journal.scaladsl.InMemoryReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.testkit.scaladsl.TestSink
import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.Music._
import com.github.dnvriend.repository.AlbumRepository

class AlbumTest extends TestSpec {
  lazy val queries = PersistenceQuery(system).readJournalFor[InMemoryReadJournal](InMemoryReadJournal.Identifier)

  def eventsForPersistenceIdSource(id: String) =
    queries.currentEventsByPersistenceId(id, 0L, Long.MaxValue).map(_.event)

  "Album" should "register a title" in {
    val album = AlbumRepository.forId("album-1")
    val xs = List(ChangeAlbumTitle("Dark side of the Moon"))
    xs foreach (album ! _)

    eventually {
      eventsForPersistenceIdSource("album-1")
        .runWith(TestSink.probe[Any])
        .request(Int.MaxValue)
        .expectNextN(xs.map(cmd ⇒ TitleChanged(cmd.title)))
        .expectComplete()
    }

    cleanup(album)
  }

  it should "update its title and year and songs" in {
    val album = AlbumRepository.forId("album-2")
    val xs = List(
      ChangeAlbumTitle("Dark side of the Moon"),
      ChangeAlbumYear(1973),
      AddSong(Song("Money", Duration.ofSeconds(390))),
      AddSong(Song("Redemption Song", Duration.ofSeconds(227))),
      RemoveSong(Song("Redemption Song", Duration.ofSeconds(227)))
    )

    val expectedEvents = xs.map {
      case ChangeAlbumTitle(title) ⇒ TitleChanged(title)
      case ChangeAlbumYear(year)   ⇒ YearChanged(year)
      case AddSong(song)           ⇒ SongAdded(song)
      case RemoveSong(song)        ⇒ SongRemoved(song)
    }

    xs foreach (album ! _)
    eventually {
      eventsForPersistenceIdSource("album-2")
        .runWith(TestSink.probe[Any])
        .request(Int.MaxValue)
        .expectNextN(
          expectedEvents
        )
        .expectComplete()
    }
  }
}
