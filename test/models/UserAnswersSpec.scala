/*
 * Copyright 2026 HM Revenue & Customs
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

package models

import org.scalatest.freespec.AnyFreeSpec
import queries.{Gettable, Settable}
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsObject, JsPath, Json}

class UserAnswersSpec extends AnyFreeSpec with Matchers {

  "UserAnswers" - {

    "get" - {

      "must return Some when value exists at path" in {
        val data = Json.obj("test" -> "value")
        val userAnswers = UserAnswers("id", data)

        val testGettable = new Gettable[String] {
          override def path: JsPath = JsPath \ "test"
        }

        val result = userAnswers.get(testGettable)
        result mustBe Some("value")
      }

      "must return None when value does not exist at path" in {
        val data = Json.obj()
        val userAnswers = UserAnswers("id", data)

        val testGettable = new Gettable[String] {
          override def path: JsPath = JsPath \ "nonexistent"
        }

        val result = userAnswers.get(testGettable)
        result mustBe None
      }

      "must return None when value is wrong type" in {
        val data = Json.obj("test" -> 123)
        val userAnswers = UserAnswers("id", data)

        val testGettable = new Gettable[String] {
          override def path: JsPath = JsPath \ "test"
        }

        val result = userAnswers.get(testGettable)
        result mustBe None
      }
    }

    "set" - {

      "must successfully set a value at a path" in {
        val userAnswers = UserAnswers("id")

        val testSettable = new Settable[String] {
          override def path: JsPath = JsPath \ "test"
        }

        val result = userAnswers.set(testSettable, "value")
        result.isSuccess mustBe true
        result.get.data mustBe Json.obj("test" -> "value")
      }

      "must return Failure when setObject fails with invalid path" in {
        val userAnswers = UserAnswers("id", Json.obj("nested" -> 123))

        val testSettable = new Settable[String] {
          override def path: JsPath = JsPath \ "nested" \ "deep"
        }

        val result = userAnswers.set(testSettable, "value")
        result.isFailure mustBe true
      }
    }

    "remove" - {

      "must successfully remove a value at an existing path" in {
        val data = Json.obj("test" -> "value", "other" -> "keep")
        val userAnswers = UserAnswers("id", data)

        val testSettable = new Settable[String] {
          override def path: JsPath = JsPath \ "test"
        }

        val result = userAnswers.remove(testSettable)
        result.isSuccess mustBe true
        result.get.data mustBe Json.obj("other" -> "keep")
      }

      "must return unchanged UserAnswers when path does not exist" in {
        val data = Json.obj("other" -> "keep")
        val userAnswers = UserAnswers("id", data)

        val testSettable = new Settable[String] {
          override def path: JsPath = JsPath \ "nonexistent"
        }

        val result = userAnswers.remove(testSettable)
        result.isSuccess mustBe true
        result.get.data mustBe data
      }
    }

    "JSON format" - {

      "must read valid JSON" in {
        val json = Json.obj(
          "_id" -> "test-id",
          "data" -> Json.obj("field" -> "value"),
          "lastUpdated" -> Json.obj("$date" -> Json.obj("$numberLong" -> "1704067200000"))
        )

        val result = json.as[UserAnswers]
        result.id mustBe "test-id"
      }

      "must write valid JSON" in {
        val userAnswers = UserAnswers("test-id", Json.obj("field" -> "value"))
        val json = Json.toJson(userAnswers)

        (json \ "_id").as[String] mustBe "test-id"
      }
    }

    "default values" - {

      "must have default empty data" in {
        val userAnswers = UserAnswers("id")
        userAnswers.data mustBe Json.obj()
      }

      "must have default lastUpdated timestamp" in {
        val userAnswers = UserAnswers("id")
        userAnswers.lastUpdated must not be null
      }
    }
  }
}
