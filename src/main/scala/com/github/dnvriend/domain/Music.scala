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

import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.persistence.PersistentActor

object Music {

  type Title = String
  type Year = Int

  final case class Song(title: Title, duration: Duration)

  sealed trait AlbumEvent

  final case class TitleChanged(title: Title) extends AlbumEvent

  final case class YearChanged(year: Year) extends AlbumEvent

  final case class SongAdded(song: Song) extends AlbumEvent

  final case class SongRemoved(song: Song) extends AlbumEvent

  sealed trait AlbumCommand

  final case class ChangeAlbumTitle(title: Title) extends AlbumCommand

  final case class ChangeAlbumYear(year: Year) extends AlbumCommand

  final case class AddSong(song: Song) extends AlbumCommand

  final case class RemoveSong(song: Song) extends AlbumCommand

}

class Album(val persistenceId: String) extends PersistentActor with ActorLogging {

  import Music._

  var title: Title = _
  var year: Year = _
  var songs: Set[Song] = Set[Song]()

  override def receiveRecover: Receive = LoggingReceive {
    case e: TitleChanged ⇒ handleEvent(e)
    case e: YearChanged  ⇒ handleEvent(e)
    case e: SongAdded    ⇒ handleEvent(e)
    case e: SongRemoved  ⇒ handleEvent(e)
  }

  def handleEvent(event: TitleChanged): Unit = {
    this.title = event.title
    log.debug(s"[TitleChanged]: Album $persistenceId => title: $title, year: $year songs: $songs")
  }

  def handleEvent(event: YearChanged): Unit = {
    this.year = event.year
    log.debug(s"[YearChanged]: Album $persistenceId => title: $title, year: $year songs: $songs")
  }

  def handleEvent(event: SongAdded): Unit = {
    this.songs = this.songs + event.song
    log.debug(s"[SongAdded]: Album $persistenceId => title: $title, year: $year songs: $songs")
  }

  def handleEvent(event: SongRemoved): Unit = {
    this.songs = this.songs - event.song
    log.debug(s"[SongRemoved]: Album $persistenceId => title: $title, year: $year songs: $songs")
  }

  override def receiveCommand: Receive = LoggingReceive {
    case ChangeAlbumTitle(newTitle) ⇒
      persistAll(List(TitleChanged(newTitle)))(handleEvent)
    case ChangeAlbumYear(newYear) ⇒
      persistAll(List(YearChanged(newYear)))(handleEvent)
    case AddSong(newSong) ⇒
      persistAll(List(SongAdded(newSong)))(handleEvent)
    case RemoveSong(oldSong) ⇒
      persistAll(List(SongRemoved(oldSong)))(handleEvent)
  }

  override def postStop(): Unit = {
    log.debug(s"Stopped $persistenceId")
    super.postStop()
  }
}
