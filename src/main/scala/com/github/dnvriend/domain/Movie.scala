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

object Movie {

  type Country = String
  type Year = Int

  // version 1 - starting class
  // case class MovieChanged(title: String, year: Int)
  // version 2 - added field
  // case class MovieChanged(title: String, year: Int, director: String = "unknown")
  // version 3 - renamed field year to release_year
  // case class MovieChanged(title: String, release_year: Int, director: String)
  // version 4 - removed field year
  // case class MovieChanged(title: String, director: String = "unknown", wonOscars: Int = 0)
  // version 5 - added releases as map country/year
  case class MovieChanged(title: String, director: String = "unknown", wonOscars: Int = 0, releases: Map[Country, Year])

}
