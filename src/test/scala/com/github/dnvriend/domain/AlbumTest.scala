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

///*
// * Copyright 2016 Dennis Vriend
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */

package com.github.dnvriend.domain

import java.time.Duration

import akka.pattern.ask
import akka.stream.scaladsl.{ Sink, Source }
import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.Music._
import com.github.dnvriend.repository.AlbumRepository

class AlbumTest extends TestSpec {

  "Album" should "register a title" in {
    val album = AlbumRepository.forId("album-1")
    val xs = List(ChangeAlbumTitle("Dark side of the Moon"))
    Source(xs).mapAsync(1)(album ? _).runWith(Sink.ignore).futureValue

    eventsForPersistenceIdSource("album-1").map(_.event).testProbe { tp ⇒
      tp.request(Int.MaxValue)
      tp.expectNextN(xs.map(cmd ⇒ TitleChanged(cmd.title)))
      tp.expectComplete()
    }
    killActors(album)
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

    Source(xs).mapAsync(1)(album ? _).runWith(Sink.ignore).futureValue

    eventsForPersistenceIdSource("album-2").map(_.event).testProbe { tp ⇒
      tp.request(Int.MaxValue)
      tp.expectNextN(expectedEvents)
      tp.expectComplete()
    }
  }
}
