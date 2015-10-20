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

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import scala.math._


object VerificationCode extends VerificationCode

trait VerificationCode {

  private def getKey(): String = {
    // key for HMAC-SHA512 - 64 bytes
    val key = "3132333435363738393031323334353637383930" + "3132333435363738393031323334353637383930" + "3132333435363738393031323334353637383930" + "31323334"
    //val key = new Random().nextInt(127).toString

    key
  }

  private def hmac_sha(crypto: String, keyBytes: Array[Byte], text: Array[Byte]): Array[Byte] = {
    val hmac = Mac.getInstance(crypto)
    val macKey = new SecretKeySpec(keyBytes, "RAW")
    hmac.init(macKey)
    hmac.doFinal(text)
  }

  def generator(numberOfDigits: Int): String = {

    val cryptoFunction: String = "HmacSHA512"
    val key = getKey()
    val time: Long = System.currentTimeMillis / 1000 / 30

    val message = BigInt(time).toByteArray
    val secretKey = BigInt(key).toByteArray
    val hash: Array[Byte] = hmac_sha(cryptoFunction, secretKey, message)

    val offset = hash(hash.length - 1) & 0xf

    val binary: BigInt = ((hash(offset) & 0x7f) << 24) | ((hash(offset + 1) & 0xff) << 16) | ((hash(offset + 2) & 0xff) << 8) | ((hash(offset + 3) & 0xff))

    val otp = binary % (pow(10, numberOfDigits)).toLong

    ("0" * numberOfDigits + otp.toString).takeRight(numberOfDigits)
  }

  def validator(key: String, numberOfDigits: Int, cryptoFunction: String, currentTime: Long, verificationCode: String, timeWindowSize: Int): Boolean = {

    val timeWindow = -timeWindowSize to timeWindowSize
    var generatedCodes = List[String]()

    for (counter <- timeWindow) {
      val time = currentTime + counter
      generatedCodes = generator(6) :: generatedCodes
    }

    println("Verification Codes for validation")
    generatedCodes.foreach(println)
    generatedCodes.contains(verificationCode)
  }

//  def main(args: Array[String]) = {
//
//    val currentTime: Long = System.currentTimeMillis / 1000 / 30
//    val verificationCode = generator(getKey(), 6, "HmacSHA512", currentTime)
//    println(s"Generated verification code ${verificationCode}")
//
//    //Thread.sleep(50000)
//
//    val currentTimeValidator: Long = System.currentTimeMillis / 1000 / 30
//    val status = validator(getKey(), 6, "HmacSHA512", currentTimeValidator, verificationCode, 0)
//
//    println(s"Status: ${status}")
//  }
}
