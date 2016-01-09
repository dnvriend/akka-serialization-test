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

import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.persistence.PersistentActor

object Person {
  sealed trait PersonEvent
  final case class NameRegistered(name: String, surname: String) extends PersonEvent
  final case class NameChanged(name: String) extends PersonEvent
  final case class SurnameChanged(surname: String) extends PersonEvent

  sealed trait PersonCommand
  final case class RegisterName(name: String, surname: String) extends PersonCommand
  final case class ChangeName(name: String) extends PersonCommand
  final case class ChangeSurname(surname: String) extends PersonCommand
}

class Person(val persistenceId: String) extends PersistentActor with ActorLogging {
  import Person._
  var name: String = _
  var surname: String = _

  override def receiveRecover: Receive = LoggingReceive {
    case e: NameRegistered ⇒ handleEvent(e)
    case e: NameChanged    ⇒ handleEvent(e)
    case e: SurnameChanged ⇒ handleEvent(e)
  }

  def handleEvent(event: NameRegistered): Unit = {
    this.name = event.name
    this.surname = event.surname
    log.debug(s"[NameRegistered]: Person $persistenceId => name: $name, surname: $surname")
  }

  def handleEvent(event: NameChanged): Unit = {
    this.name = event.name
    log.debug(s"[NameChanged]: Person $persistenceId => name: $name, surname: $surname")
  }

  def handleEvent(event: SurnameChanged): Unit = {
    this.surname = event.surname
    log.debug(s"[SurnameChanged]: Person $persistenceId => name: $name, surname: $surname")
  }

  override def receiveCommand: Receive = LoggingReceive {
    case RegisterName(name, surname) ⇒
      persist(NameRegistered(name, surname))(handleEvent)
    case ChangeName(newName) ⇒
      persist(NameChanged(newName))(handleEvent)
    case ChangeSurname(newSurname) ⇒
      persist(SurnameChanged(newSurname))(handleEvent)
  }

  override def postStop(): Unit = {
    log.debug(s"Stopped $persistenceId")
    super.postStop()
  }
}
