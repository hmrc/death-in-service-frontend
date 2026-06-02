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

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import base.SpecBase
import play.api.libs.json.{JsString, Json}

class SchemeDetailsSpec extends SpecBase with ScalaCheckPropertyChecks {

  "MinimalSchemeDetails" - {

    "must return JsError for invalid date format" in {
      val json = Json.obj(
        "name" -> "Test Scheme",
        "referenceNumber" -> "SRN123",
        "schemeStatus" -> "Open",
        "openDate" -> "invalid-date",
        "windUpDate" -> "2026-01-01"
      )
      val result = json.validate[MinimalSchemeDetails]

      result.isError mustBe true
    }
  }

  "MinimalDetails" - {

    "must return JsError for missing required field" in {
      val json = Json.obj(
        "isPsaSuspended" -> false,
        "rlsFlag" -> false,
        "deceasedFlag" -> false
      )
      val result = json.validate[MinimalDetails]
      result.isError mustBe true
    }

    "must return JsError for invalid type" in {
      val json = Json.obj(
        "email" -> 123,
        "isPsaSuspended" -> false,
        "rlsFlag" -> false,
        "deceasedFlag" -> false
      )
      val result = json.validate[MinimalDetails]
      result.isError mustBe true
    }

    "must return JsError for invalid nested IndividualDetails type" in {
      val json = Json.obj(
        "email" -> "test@example.com",
        "isPsaSuspended" -> false,
        "individualDetails" -> 123,
        "rlsFlag" -> false,
        "deceasedFlag" -> false
      )
      val result = json.validate[MinimalDetails]
      result.isError mustBe true
    }

    "must return JsError for missing field in nested IndividualDetails" in {
      val json = Json.obj(
        "email" -> "test@example.com",
        "isPsaSuspended" -> false,
        "individualDetails" -> Json.obj("firstName" -> "John"),
        "rlsFlag" -> false,
        "deceasedFlag" -> false
      )
      val result = json.validate[MinimalDetails]
      result.isError mustBe true
    }
  }

  "IndividualDetails" - {

    "must return JsError for missing required field" in {
      val json = Json.obj(
        "firstName" -> "John"
      )
      val result = json.validate[IndividualDetails]
      result.isError mustBe true
    }

    "must return JsError for invalid type" in {
      val json = Json.obj(
        "firstName" -> 123,
        "lastName" -> "Doe"
      )
      val result = json.validate[IndividualDetails]
      result.isError mustBe true
    }
  }

  "SchemeDetails" - {

    "successfully read from json" in {

      forAll(schemeDetailsGen) { details =>
        val json = Json.toJson(details)
        val ob = json.as[SchemeDetails]
        ob mustBe details
      }
    }

    "must handle empty establishers" in {
      val json = Json.obj(
        "schemeName" -> "Test Scheme",
        "pstr" -> "12345678",
        "schemeStatus" -> "Open",
        "schemeType" -> Json.obj("name" -> "trust"),
        "pspDetails" -> Json.obj("authorisingPSAID" -> "PSA123"),
        "establishers" -> Json.arr()
      )
      val result = json.as[SchemeDetails]

      result.establishers mustBe Nil
    }
  }

  "Establisher" - {

    "must implement equals correctly" in {
      val establisher1 = Establisher("Test Company", EstablisherKind.Company)
      val establisher2 = Establisher("Test Company", EstablisherKind.Company)
      val establisher3 = Establisher("Different Company", EstablisherKind.Company)

      establisher1.equals(establisher2) mustBe true
      establisher1.equals(establisher3) mustBe false
      establisher1.equals("some string") mustBe false
    }

    "must implement hashCode correctly" in {
      val establisher1 = Establisher("Test Company", EstablisherKind.Company)
      val establisher2 = Establisher("Test Company", EstablisherKind.Company)
      val establisher3 = Establisher("Different Company", EstablisherKind.Company)

      establisher1.hashCode mustEqual establisher2.hashCode
      (establisher1.hashCode must not).equal(establisher3.hashCode)
    }

    "must write Company establisher" in {
      val establisher = Establisher("Test Company", EstablisherKind.Company)
      val json = Json.toJson(establisher)

      (json \ "companyDetails" \ "companyName").as[String] mustBe "Test Company"
    }

    "must write Partnership establisher" in {
      val establisher = Establisher("Test Partnership", EstablisherKind.Partnership)
      val json = Json.toJson(establisher)

      (json \ "partnershipDetails" \ "name").as[String] mustBe "Test Partnership"
    }

    "must write Individual establisher" in {
      val establisher = Establisher("John Middle Doe", EstablisherKind.Individual)
      val json = Json.toJson(establisher)

      (json \ "establisherDetails" \ "firstName").as[String] mustBe "John"
      (json \ "establisherDetails" \ "lastName").as[String] mustBe "Doe"
      (json \ "establisherDetails" \ "middleName").as[String] mustBe "Middle"
    }
  }

  "SchemeStatus" - {

    "successfully read from json" in {
      forAll(schemeStatusGen) { status =>
        Json.toJson(status).as[SchemeStatus] mustBe status
      }
    }

    "return a JsError" - {
      "Scheme status is unknown" in {
        forAll(nonEmptyString) { status =>
          JsString(status).asOpt[SchemeStatus] mustBe None
        }
      }
    }
  }

  "ListSchemeDetails" - {

    "successfully read from json" in {
      forAll(listMinimalSchemeDetailsGen) { listMinimalSchemeDetails =>
        Json.toJson(listMinimalSchemeDetails).as[ListMinimalSchemeDetails] mustBe listMinimalSchemeDetails
      }
    }
  }

  "ListMinimalSchemeDetails" - {

    "must return JsError for invalid JSON" in {
      val json = Json.obj("schemeDetails" -> "invalid")
      val result = json.validate[ListMinimalSchemeDetails]
      result.isError mustBe true
    }
  }
}
