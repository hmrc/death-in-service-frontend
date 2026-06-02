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

package forms.mappings

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import models.Enumerable
import play.api.data.FormError

class FormattersSpec extends AnyFreeSpec with Matchers with OptionValues with Formatters {

  "intFormatter" - {

    val formatter = intFormatter("error.required", "error.wholeNumber", "error.nonNumeric")

    "must bind a valid integer" in {
      val result = formatter.bind("value", Map("value" -> "123"))
      result mustBe Right(123)
    }

    "must bind an integer with commas" in {
      val result = formatter.bind("value", Map("value" -> "1,234"))
      result mustBe Right(1234)
    }

    "must not bind a decimal number" in {
      val result = formatter.bind("value", Map("value" -> "12.34"))
      result mustBe Left(Seq(FormError("value", "error.wholeNumber")))
    }

    "must not bind a non-numeric string" in {
      val result = formatter.bind("value", Map("value" -> "abc"))
      result mustBe Left(Seq(FormError("value", "error.nonNumeric")))
    }

    "must not bind an empty value" in {
      val result = formatter.bind("value", Map("value" -> ""))
      result mustBe Left(Seq(FormError("value", "error.required")))
    }

    "must unbind a valid value" in {
      val result = formatter.unbind("value", 123)
      result mustEqual Map("value" -> "123")
    }
  }

  "enumerableFormatter" - {

    sealed trait TestEnum
    case object OptionA extends TestEnum
    case object OptionB extends TestEnum

    object TestEnum {
      val values: Set[TestEnum] = Set(OptionA, OptionB)

      implicit val enumerable: Enumerable[TestEnum] =
        Enumerable(values.toSeq.map(v => v.toString -> v)*)
    }

    val formatter = enumerableFormatter[TestEnum]("error.required", "error.invalid")

    "must bind a valid option" in {
      val result = formatter.bind("value", Map("value" -> "OptionA"))
      result mustBe Right(OptionA)
    }

    "must not bind an invalid option" in {
      val result = formatter.bind("value", Map("value" -> "InvalidOption"))
      result mustBe Left(Seq(FormError("value", "error.invalid")))
    }

    "must not bind an empty value" in {
      val result = formatter.bind("value", Map("value" -> ""))
      result mustBe Left(Seq(FormError("value", "error.required")))
    }

    "must unbind a valid value" in {
      val result = formatter.unbind("value", OptionA)
      result mustEqual Map("value" -> "OptionA")
    }
  }

  "currencyFormatter" - {

    val formatter = currencyFormatter("error.required", "error.invalidNumeric", "error.nonNumeric")

    "must bind a valid integer" in {
      val result = formatter.bind("value", Map("value" -> "123"))
      result mustBe Right(BigDecimal(123))
    }

    "must bind a valid decimal with 2 decimal places" in {
      val result = formatter.bind("value", Map("value" -> "123.45"))
      result mustBe Right(BigDecimal("123.45"))
    }

    "must bind with £ symbol" in {
      val result = formatter.bind("value", Map("value" -> "£123"))
      result mustBe Right(BigDecimal(123))
    }

    "must bind with commas and spaces" in {
      val result = formatter.bind("value", Map("value" -> "£ 1,234.56"))
      result mustBe Right(BigDecimal("1234.56"))
    }

    "must not bind non-numeric characters" in {
      val result = formatter.bind("value", Map("value" -> "abc"))
      result mustBe Left(Seq(FormError("value", "error.nonNumeric")))
    }

    "must not bind more than 2 decimal places" in {
      val result = formatter.bind("value", Map("value" -> "123.456"))
      result mustBe Left(Seq(FormError("value", "error.invalidNumeric")))
    }

    "must not bind an empty value" in {
      val result = formatter.bind("value", Map("value" -> ""))
      result mustBe Left(Seq(FormError("value", "error.required")))
    }

    "must unbind a valid value" in {
      val result = formatter.unbind("value", BigDecimal("123.45"))
      result mustEqual Map("value" -> "123.45")
    }
  }
}
