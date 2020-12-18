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

import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsObject

case class ResponseFailure(
                            errorDetail: ResponseFailure.ErrorDetail
                          )

object ResponseFailure {

  def apply(
             timestamp: String,
             correlationId: String,
             errorCode: String,
             errorMessage: String
           ): ResponseFailure =
    ResponseFailure(errorDetail =
      ErrorDetail(correlationId, timestamp, Some(errorCode), Some(errorMessage))
    )

  case class ErrorDetail(
                          correlationId: String,
                          timestamp: String,
                          errorCode: Option[String] = None,
                          errorMessage: Option[String] = None,
                          source: Option[String] = None,
                          sourceFaultDetail: Option[ResponseFailure.ErrorDetail.SourceFaultDetail] = None
                        )

  object ErrorDetail {

    case class SourceFaultDetail(
                                  detail: Option[Seq[String]] = None,
                                  restFault: Option[JsObject] = None,
                                  soapFault: Option[JsObject] = None
                                )

    object SourceFaultDetail {
      implicit val formats: Format[SourceFaultDetail] =
        Json.format[SourceFaultDetail]

    }

    implicit val formats: Format[ErrorDetail] =
      Json.format[ErrorDetail]

  }

  implicit val formats: Format[ResponseFailure] =
    Json.format[ResponseFailure]

}
