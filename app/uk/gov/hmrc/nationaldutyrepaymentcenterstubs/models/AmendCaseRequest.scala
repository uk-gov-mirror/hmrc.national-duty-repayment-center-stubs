/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.nationaldutyrepaymentcenterstubs.models

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.nationaldutyrepaymentcenter.models.CreateCaseRequest.CommonValues
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.models.Validator.{Validate, check, checkProperty}

/**
 * Create specified case in the PEGA system.
 * Based on spec "CPR01-1.0.0-EIS API Specification-Create Case from MDTP"
 *
 * @param AcknowledgementReference Unique id created at source after a form is saved Unique ID throughout the journey of a message-stored in CSG data records, may be passed to Decision Service, CSG records can be searched using this field etc.
 * @param ApplicationType          Its key value to create the case for respective process.
 * @param OriginatingSystem        “Digital” for all requests originating in Digital
 */
case class AmendCaseRequest(
                                 AcknowledgementReference: String,
                                 ApplicationType: String,
                                 OriginatingSystem: String,
                                 Content: AmendCaseRequest.Content
                               )

object AmendCaseRequest {
  import CommonValues._
  import Validator._

  implicit val formats: Format[AmendCaseRequest] = Json.format[AmendCaseRequest]

  case class Content(
                      CaseID: String,
                      Description: String
                    )

  object Content {
    implicit val formats: Format[Content] = Json.format[Content]

    val DescriptionValidator: Validate[String] =
      check(
        _.matches(DescriptionPattern),
        s""""Invalid Description, should be one of [${DescriptionPattern.mkString(", ")}]"""
      )

    val CaseIDValidator: Validate[String] =
      check(
        _.matches(CaseIDPattern),
        s""""Invalid City, should be one of [${CaseIDPattern.mkString(", ")}]"""
      )

    val validate: Validate[Content] = Validator(
      checkProperty(_.CaseID, CaseIDValidator),
      checkProperty(_.Description, DescriptionValidator)
    )
  }

  object CommonValues {
    val DescriptionPattern = """([a-zA-Z0-9 ]{1,1500})"""
    val CaseIDPattern = """([a-zA-Z0-9]{2,64})"""
  }

  val AcknowledgementReferenceValidator: Validate[String] = check(
    _.lengthMinMaxInclusive(1, 32),
    s""""Invalid length of AcknowledgementReference, should be between 1 and 32 (inclusive) character long"""
  )

  val ApplicationTypeValidator: Validate[String] = check(
    _ == "NDRC",
    s""""Invalid ApplicationType, should be "NDRC""""
  )

  val OriginatingSystemValidator: Validate[String] = check(
    _ == "Digital",
    s""""Invalid OriginatingSystem, should be "Digital""""
  )

  implicit val validate: Validate[AmendCaseRequest] = Validator(
    checkProperty(_.AcknowledgementReference, AcknowledgementReferenceValidator),
    checkProperty(_.ApplicationType, ApplicationTypeValidator),
    checkProperty(_.OriginatingSystem, OriginatingSystemValidator),
    checkProperty(_.Content, Content.validate)
  )
}
