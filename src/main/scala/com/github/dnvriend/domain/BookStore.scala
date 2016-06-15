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

object BookStore {
  // This domain is used only to test avro schema evolutions. In real systems probably
  // you do not have V1, V2, .. versions of the case class but just the latest one
  // Here keeping more versions of the class is needed for the tests
  case class ChangedBookV1(title: String, year: Int)
  case class ChangedBookV2(bookTitle: String, year: Int)
  case class ChangedBookV3(bookTitle: String, year: Int, editor: String = "")
  case class ChangedBookV4(bookTitle: String, editor: String = "")
}
