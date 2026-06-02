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
import generators.ModelGenerators
import base.SpecBase

class PensionSchemeIdSpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  "PensionSchemeId" - {

    "fold" - {

      "call PsaId function when given PsaId" in {
        forAll(psaIdGen) { psaId =>
          val result = psaId.fold(
            psa => s"PSA: ${psa.value}",
            psp => s"PSP: ${psp.value}"
          )
          result must startWith("PSA:")
        }
      }

      "call PspId function when given PspId" in {
        forAll(pspIdGen) { pspId =>
          val result = pspId.fold(
            psa => s"PSA: ${psa.value}",
            psp => s"PSP: ${psp.value}"
          )
          result must startWith("PSP:")
        }
      }
    }

    "isPSP" - {

      "return true for PspId" in {
        forAll(pspIdGen) { pspId =>
          pspId.isPSP mustBe true
        }
      }

      "return false for PsaId" in {
        forAll(psaIdGen) { psaId =>
          psaId.isPSP mustBe false
        }
      }
    }
  }
}
