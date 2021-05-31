/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class DatabaseTest extends Simulation {
  val httpProtocol = http
    .baseUrl("http://localhost:9090/")
    .inferHtmlResources()
    .acceptHeader("text/html,application/json,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,/;q=0.8,application/signed-exchange;v=b3;q=0.9")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36")

  val scn = scenario("DatabaseTest")
    .exec(
    	http("Board_Request")
      .get("/board")
  	)
    .pause(2)
    .exec(
      http("Set_Players")
        .post("/board/setPlayers")
        .queryParam("player1", "sara")
      	.queryParam("player2", "andre")
    )
    .pause(2)
    .exec(
      http("Player_Move")
        .post("/board/move")
        .queryParam("row", 1)
        .queryParam("col", 2)
        .queryParam("grid", 2)
    )
    .pause(2)
    .exec(
      http("DB_Players")
        .get("/game/database/players")
    )
    .pause(2)
    .exec(
      http("DB_Moves")
        .get("/game/database/moves")
    )
    .pause(2)
    .exec(
      http("DB_Save")
        .get("/game/database/save")
    )
    .pause(2)
    .exec(
      http("DB_Load")
        .get("/game/database/load")
    )
  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}