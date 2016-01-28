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

import akka.actor.{ ActorRef, PoisonPill, ActorSystem }
import akka.event.{ LoggingAdapter, Logging }
import akka.serialization.SerializationExtension
import akka.stream.{ ActorMaterializer, Materializer }
import akka.testkit.TestProbe
import org.scalatest.concurrent.{ Eventually, ScalaFutures }
import org.scalatest.prop.PropertyChecks
import org.scalatest.{ BeforeAndAfterAll, Matchers, GivenWhenThen, FlatSpec }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

import scala.concurrent.duration._

trait TestSpec extends FlatSpec with Matchers with GivenWhenThen with ScalaFutures with BeforeAndAfterAll with Eventually with PropertyChecks {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()
  implicit val log: LoggingAdapter = Logging(system, this.getClass)
  implicit val pc: PatienceConfig = PatienceConfig(timeout = 50.seconds)
  val serialization = SerializationExtension(system)

  implicit class FutureToTry[T](f: Future[T]) {
    def toTry: Try[T] = Try(f.futureValue)
  }

  /**
   * Sends the PoisonPill command to an actor and waits for it to die
   */
  def cleanup(actors: ActorRef*): Unit = {
    val probe = TestProbe()
    actors.foreach { (actor: ActorRef) ⇒
      actor ! PoisonPill
      probe watch actor
      probe.expectTerminated(actor)
    }
  }

  override protected def afterAll(): Unit = {
    system.terminate()
    system.whenTerminated.toTry should be a 'success
  }
}
