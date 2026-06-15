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

package config

import play.api.test.Helpers.running
import base.SpecBase

class FrontendAppConfigSpec extends SpecBase {

  "FrontendAppConfig" - {

    "must build the exit survey URL from the feedback frontend host" in {

      val application =
        applicationBuilder()
          .configure(
            "feedback-frontend.host" -> "http://feedback-frontend.example",
            "microservice.services.feedback-frontend.host" -> "old-feedback-frontend.example"
          )
          .build()

      running(application) {
        val appConfig = application.injector.instanceOf[FrontendAppConfig]

        appConfig.exitSurveyUrl mustEqual "http://feedback-frontend.example/feedback/death-in-service-frontend"
      }
    }
  }
}
