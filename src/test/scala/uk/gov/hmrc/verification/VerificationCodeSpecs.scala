/*
 * Copyright 2015 HM Revenue & Customs
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

package uk.gov.hmrc.verification

import org.scalatest.WordSpecLike

class VerificationCodeSpecs extends WordSpecLike {

  "Verification Algoritm" should {
    "generate a 6 digit OTP" in {

      val verificationCode = VerificationCode
      val generatedCode = verificationCode.generator(6)

      generatedCode.length should be 6
      generatedCode should be "123455"

      println(s"Generated verification code ${verificationCode}")


    }
  }
}
