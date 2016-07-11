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
import akka.stream.scaladsl.Source
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import scala.collection.immutable.Seq
import scala.concurrent.duration._

trait AkkaStreamUtils { _: TestSpec ⇒
  implicit class SourceOps[A, M](src: Source[A, M]) {
    def testProbe(f: TestSubscriber.Probe[A] ⇒ Unit): Unit = {
      val tp = src.runWith(TestSink.probe(system))
      tp.within(10.seconds)(f(tp))
    }
  }

  def withIteratorSrc[T](start: Int = 0)(f: Source[Int, NotUsed] ⇒ Unit): Unit =
    f(Source.fromIterator(() ⇒ Iterator from start))

  def fromCollectionProbe[A](xs: Seq[A])(f: TestSubscriber.Probe[A] ⇒ Unit): Unit =
    f(Source(xs).runWith(TestSink.probe(system)))
}
