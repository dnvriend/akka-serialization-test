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

import akka.actor.{ ActorRef, Props }
import akka.pattern.ask
import akka.persistence.query.EventEnvelope
import akka.stream.scaladsl.{ Sink, Source }
import akka.testkit.TestProbe
import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.Person._
import com.github.dnvriend.persistence.ProtobufReader
import proto.person.Command._

class PersonTest extends TestSpec {

  import com.github.dnvriend.persistence.ProtobufFormats._

  def withPerson(id: String)(f: ActorRef ⇒ TestProbe ⇒ Unit): Unit = {
    val tp = TestProbe()
    val ref = system.actorOf(Props(new Person(id)))
    try f(ref)(tp) finally killActors(ref)
  }

  "Person" should "register a name" in {
    withPerson("p1") { ref ⇒ tp ⇒
      Source(List(RegisterNameCommand("dennis", "vriend")))
        .mapAsync(1)(ref ? _).runWith(Sink.ignore).futureValue
    }

    withPerson("p1") { ref ⇒ tp ⇒
      Source(List(RegisterNameCommand("dennis", "vriend")))
        .mapAsync(1)(ref ? _).runWith(Sink.ignore).futureValue
    }

    // note that the persistence-query does not use the deserializer
    // so the protobuf must be deserialized inline
    eventsForPersistenceIdSource("p1").collect {
      case EventEnvelope(_, _, _, proto: NameRegisteredMessage) ⇒
        implicitly[ProtobufReader[NameRegisteredEvent]].read(proto)
    }.testProbe { tp ⇒
      tp.request(Int.MaxValue)
      tp.expectNext(NameRegisteredEvent("dennis", "vriend"))
      tp.expectNext(NameRegisteredEvent("dennis", "vriend"))
      tp.expectComplete()
    }
  }

  it should "update its name and surname" in {
    withPerson("p2") { ref ⇒ tp ⇒
      Source(List(RegisterNameCommand("dennis", "vriend"), ChangeNameCommand("jimi"), ChangeSurnameCommand("hendrix")))
        .mapAsync(1)(ref ? _).runWith(Sink.ignore).futureValue
    }

    eventsForPersistenceIdSource("p2").collect {
      case EventEnvelope(_, _, _, proto: NameRegisteredMessage) ⇒
        implicitly[ProtobufReader[NameRegisteredEvent]].read(proto)
      case EventEnvelope(_, _, _, proto: NameChangedMessage) ⇒
        implicitly[ProtobufReader[NameChangedEvent]].read(proto)
      case EventEnvelope(_, _, _, proto: SurnameChangedMessage) ⇒
        implicitly[ProtobufReader[SurnameChangedEvent]].read(proto)
    }.testProbe { tp ⇒
      tp.request(Int.MaxValue)
      tp.expectNext(NameRegisteredEvent("dennis", "vriend"))
      tp.expectNext(NameChangedEvent("jimi"))
      tp.expectNext(SurnameChangedEvent("hendrix"))
      tp.expectComplete()
    }
  }
}
