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

import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.persistence.{ PersistentActor, RecoveryCompleted }

object Person {
  sealed trait PersonEvent
  final case class NameRegisteredPersonEvent(name: String, surname: String) extends PersonEvent
  final case class NameChangedPersonEvent(name: String) extends PersonEvent
  final case class SurnameChangedPersonEvent(surname: String) extends PersonEvent

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
    case e: NameRegisteredPersonEvent ⇒

      handleEvent(e)
    case e: NameChangedPersonEvent    ⇒ handleEvent(e)
    case e: SurnameChangedPersonEvent ⇒ handleEvent(e)
    case RecoveryCompleted            ⇒ println("==> Recovery completed")
    case e                            ⇒ println("Dropping event: " + e.getClass.getName)
  }

  def handleEvent(event: NameRegisteredPersonEvent): Unit = {
    this.name = event.name
    this.surname = event.surname
    log.debug(s"[NameRegistered]: Person $persistenceId => name: $name, surname: $surname")
  }

  def handleEvent(event: NameChangedPersonEvent): Unit = {
    this.name = event.name
    log.debug(s"[NameChanged]: Person $persistenceId => name: $name, surname: $surname")
  }

  def handleEvent(event: SurnameChangedPersonEvent): Unit = {
    this.surname = event.surname
    log.debug(s"[SurnameChanged]: Person $persistenceId => name: $name, surname: $surname")
  }

  override def receiveCommand: Receive = LoggingReceive {
    case RegisterName(name, surname) ⇒
      persist(NameRegisteredPersonEvent(name, surname)) { e ⇒
        handleEvent(e)
        sender() ! akka.actor.Status.Success("")
      }
    case ChangeName(newName) ⇒
      persist(NameChangedPersonEvent(newName)) { e ⇒
        handleEvent(e)
        sender() ! akka.actor.Status.Success("")
      }
    case ChangeSurname(newSurname) ⇒
      persist(SurnameChangedPersonEvent(newSurname)) { e ⇒
        handleEvent(e)
        sender() ! akka.actor.Status.Success("")
      }
  }

  override def postStop(): Unit = {
    log.debug(s"Stopped $persistenceId")
    super.postStop()
  }
}
