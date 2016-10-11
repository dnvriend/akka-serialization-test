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

package com.github.dnvriend.serializer.csv

import com.github.dnvriend.TestSpec
import com.github.dnvriend.domain.CsvText

class CsvSerializerTest extends TestSpec {
  it should "find a serializer for CsvText" in {
    val serializer = serialization.findSerializerFor(CsvText("foo"))
    serializer.getClass.getName shouldBe "com.github.dnvriend.serializer.csv.CsvSerializer"
    val bytes: Array[Byte] = serializer.toBinary(CsvText("foo"))
    serializer.fromBinary(bytes) shouldBe CsvText("foo" + "-from-csv-serializer")
  }
}
