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

import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.hmrc.play.test.WithFakeApplication

class VerificationCodeSpecs extends Matchers with WordSpecLike with WithFakeApplication{

  "Verification Algoritm" should {

    "generate a 6 digit OTP" in {
      val verificationCode = VerificationCode

      val generatedCode = verificationCode.generator()

      generatedCode.length shouldBe 6
    }

    "contain only digits in the OTP" in {
      val verificationCode = VerificationCode

      val generatedCode = verificationCode.generator()

      true shouldBe isDigits(generatedCode)

    }

    "validates the generated verification code in current time slice" in {
      val verificationCode = VerificationCode

      val generatedCode = verificationCode.generator()

      val isValid = verificationCode.validator(generatedCode)

      true shouldBe isValid
    }

    "validates the generated verification code in previous time slice" in {
      val verificationCode = VerificationCode

      val generatedCode = verificationCode.generator()

      // added for entering a delay for verification.
      Thread.sleep(30000L)

      val isValid = verificationCode.validator(generatedCode)

      true shouldBe isValid
    }

  }

  private def isDigits(verificationCode: String) = verificationCode forall Character.isDigit
}
