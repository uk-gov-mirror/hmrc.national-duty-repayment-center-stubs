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

package uk.gov.hmrc.nationaldutyrepaymentcenterstubs.controllers

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.{util => ju}

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.config.AppConfig
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.connectors.MicroserviceAuthConnector
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.models.NDRCCreateCaseResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NationalDutyRepaymentCenterStubController @Inject()(
  val authConnector: MicroserviceAuthConnector,
  val env: Environment,
  val appConfig: AppConfig,
  cc: ControllerComponents
)(implicit val configuration: Configuration, ec: ExecutionContext)
    extends BackendController(cc) with AuthActions with ControllerHelper {

  // POST /create-case
  def createCaseMDTPStub: Action[JsValue] =
    Action.async(parse.json) { implicit request =>
      withAuthorised {
        val correlationId = request.headers
          .get("x-correlation-id")
          .getOrElse(ju.UUID.randomUUID.toString())
        Future.successful(
          Created(
            Json.toJson(
              NDRCCreateCaseResponse(
                correlationId = correlationId,
                result = Some("PC12010081330XGBNZJO04")
              )
            )
          )
        )
      }
    }

  val processingDateFormat = DateTimeFormatter.ISO_INSTANT
  val httpDateFormat = DateTimeFormatter
    .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", ju.Locale.ENGLISH)
    .withZone(ZoneId.of("GMT"))

}
