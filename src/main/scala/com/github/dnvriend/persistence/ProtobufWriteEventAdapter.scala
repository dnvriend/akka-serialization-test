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

package com.github.dnvriend.persistence

import akka.persistence.journal.{ Tagged, WriteEventAdapter }

import scala.reflect.ClassTag

trait ProtobufWriteEventAdapter extends WriteEventAdapter {
  protected def serializeTagged[A: ClassTag: ProtobufWriter](msg: A, tags: Set[String] = Set.empty): Tagged = {
    val proto = implicitly[ProtobufWriter[A]].write(msg)
    Tagged(proto, tags + implicitly[ClassTag[A]].runtimeClass.getSimpleName)
  }
}
