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
import play.api.Play
import play.api.Play.current
import scala.math._


object VerificationCode extends VerificationCode

trait VerificationCode {

  private def numberOfDigitsForOTPGeneration = Play.configuration.getString("numberOfDigitsForOTPGeneration").getOrElse(throw new IllegalArgumentException("Failed to resolve configuration 'numberOfDigitsForOTPGeneration'."))

  private def timeStep = Play.configuration.getString("timeStep").getOrElse(throw new IllegalArgumentException("Failed to resolve configuration 'timeStep'."))

  private def cryptographicHashFunction = Play.configuration.getString("cryptographicHashFunction").getOrElse(throw new IllegalArgumentException("Failed to resolve configuration 'cryptographicHashFunction'."))

  private def timeWindowCount = Play.configuration.getString("timeWindow").getOrElse(throw new IllegalArgumentException("Failed to resolve configuration 'timeWindow'."))

  def generator(): String = {
    val currentTime: Long = getCurrentTime()
    generator(currentTime)
  }

  def validator(verificationCode: String): Boolean = {
    val timeWindowSize: Int = timeWindowCount.toInt
    val timeWindow = -timeWindowSize to timeWindowSize
    val currentTime: Long = getCurrentTime()

    val generatedCodes = timeWindow.foldLeft(List[String]())((code, counter) => generator(currentTime + counter) :: code)

    generatedCodes.contains(verificationCode)
  }

  private def getKey(): String = {
    // key for HMAC-SHA512 - 64 bytes
    //TODO This key should come from OTP service in future.
    val key = "31323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334"
    key
  }

  private def generateHMACHash(crypto: String, keyBytes: Array[Byte], text: Array[Byte]): Array[Byte] = {
    val hmac = Mac.getInstance(crypto)
    val macKey = new SecretKeySpec(keyBytes, "RAW")
    hmac.init(macKey)
    hmac.doFinal(text)
  }

  private def generator(currentTime: Long): String = {
    val numberOfDigits = numberOfDigitsForOTPGeneration.toInt
    val cryptoFunction = cryptographicHashFunction
    val key = getKey()

    // generate HMAC hash as per the specified cryptographic hash function in config
    val hash: Array[Byte] = generateHMACHash(cryptoFunction, BigInt(key).toByteArray, BigInt(currentTime).toByteArray)

    // standard mathematical computations as per RFC specifications
    val offset = hash(hash.length - 1) & 0xf
    val binary: BigInt = ((hash(offset) & 0x7f) << 24) | ((hash(offset + 1) & 0xff) << 16) | ((hash(offset + 2) & 0xff) << 8) | ((hash(offset + 3) & 0xff))

    // extracting specified number of digits for OTP from binary string
    val otp = binary % pow(10, numberOfDigits).toLong

    // make sure always, specified number of digits are returned
    paddedOTP(otp, numberOfDigits)

  }

  private def paddedOTP(otp: BigInt, numberOfDigits: Int): String = {
    val zeroAppendedOTP = "0" * numberOfDigits + otp.toString
    zeroAppendedOTP.takeRight(numberOfDigits)
  }

  private def getCurrentTime(): Long = {
    val timeSlice = timeStep.toInt
    // get current time, as steps of time slices specified
    System.currentTimeMillis / 1000 / timeSlice
  }

}
