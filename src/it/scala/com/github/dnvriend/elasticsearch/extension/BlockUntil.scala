/*
 * Copyright 2015 Dennis Vriend
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

package com.github.dnvriend.elasticsearch.extension

import org.scalatest.exceptions.TestFailedException

import scala.util.{ Failure, Try }

trait BlockUntil {
  def blockUntil[T](body: ⇒ Unit)(implicit repeatNumOfTimes: Int = 30) = {
    def retry(numTimes: Int): Unit = {
      val triedBlock = Try(body)
      triedBlock match {
        case Failure(t) ⇒ println(s"(${repeatNumOfTimes - numTimes}/$repeatNumOfTimes): ${t.getMessage}")
        case _          ⇒
      }
      if (triedBlock.isSuccess) Unit
      else if (numTimes == 0) throw new TestFailedException(s"Max number of retries ($repeatNumOfTimes) exceeded", 0)
      else {
        Thread.sleep(500)
        retry(numTimes - 1)
      }
    }
    retry(repeatNumOfTimes)
  }
}
