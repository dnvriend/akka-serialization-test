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
  final case class NameRegisteredEvent(name: String, surname: String) extends PersonEvent
  final case class NameChangedEvent(name: String) extends PersonEvent
  final case class SurnameChangedEvent(surname: String) extends PersonEvent

  sealed trait PersonCommand
  final case class RegisterNameCommand(name: String, surname: String) extends PersonCommand
  final case class ChangeNameCommand(name: String) extends PersonCommand
  final case class ChangeSurnameCommand(surname: String) extends PersonCommand
}

class Person(val persistenceId: String) extends PersistentActor with ActorLogging {
  import Person._
  var name: String = _
  var surname: String = _

  override def receiveRecover: Receive = LoggingReceive {
    case e: NameRegisteredEvent ⇒

      handleEvent(e)
    case e: NameChangedEvent    ⇒ handleEvent(e)
    case e: SurnameChangedEvent ⇒ handleEvent(e)
    case RecoveryCompleted      ⇒ println("==> Recovery completed")
    case e                      ⇒ println("Dropping event: " + e.getClass.getName)
  }

  def handleEvent(event: NameRegisteredEvent): Unit = {
    this.name = event.name
    this.surname = event.surname
    log.debug(s"[NameRegistered]: Person $persistenceId => name: $name, surname: $surname")
  }

  def handleEvent(event: NameChangedEvent): Unit = {
    this.name = event.name
    log.debug(s"[NameChanged]: Person $persistenceId => name: $name, surname: $surname")
  }

  def handleEvent(event: SurnameChangedEvent): Unit = {
    this.surname = event.surname
    log.debug(s"[SurnameChanged]: Person $persistenceId => name: $name, surname: $surname")
  }

  override def receiveCommand: Receive = LoggingReceive {
    case RegisterNameCommand(name, surname) ⇒
      persist(NameRegisteredEvent(name, surname)) { e ⇒
        handleEvent(e)
        sender() ! akka.actor.Status.Success("")
      }
    case ChangeNameCommand(newName) ⇒
      persist(NameChangedEvent(newName)) { e ⇒
        handleEvent(e)
        sender() ! akka.actor.Status.Success("")
      }
    case ChangeSurnameCommand(newSurname) ⇒
      persist(SurnameChangedEvent(newSurname)) { e ⇒
        handleEvent(e)
        sender() ! akka.actor.Status.Success("")
      }
  }

  override def postStop(): Unit = {
    log.debug(s"Stopped $persistenceId")
    super.postStop()
  }
}
