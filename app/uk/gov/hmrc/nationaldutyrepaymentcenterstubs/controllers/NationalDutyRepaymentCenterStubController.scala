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

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.connectors.MicroserviceAuthConnector
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.models.{AmendCaseRequest, CaseResponseSuccess, NDRCCreateCaseResponse, ResponseFailure, Validator}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.wiring.AppConfig

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json.{JsValue, Json, Reads}
import java.{util => ju}
import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

import uk.gov.hmrc.nationaldutyrepaymentcenter.models.CreateCaseRequest

import scala.util.Try


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

  // POST /amend-case
  def amendCaseMDTPStub: Action[JsValue] =
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

  // POST /cpr/caserequest/ndrc/create/v1
  def createCaseEISStub: Action[String] = {

    val errorScenarioEPU: Set[String] = Set("666", "667")

    def isSuccessCase(payload: CreateCaseRequest): Boolean =
      !errorScenarioEPU.contains(payload.Content.ClaimDetails.EntryDetails.EPU)

    def errorMessageFor(payload: CreateCaseRequest): (String, String) =
      payload.Content.ClaimDetails.EntryDetails.EPU match {
        case "667" => ("400", "999 : PC12010081330XGBNZJO04")
        case _     => ("400", "Invalid request")
      }

    Action.async(parse.tolerantText) { implicit request =>
      withValidHeaders { correlationId =>
        withValidCasePayload[CreateCaseRequest](correlationId) { payload =>
          Future.successful(
            if (isSuccessCase(payload))
              Ok(
                Json.toJson(
                  CaseResponseSuccess(
                    "PC12010081330XGBNZJO04",
                    processingDateFormat.format(ZonedDateTime.now),
                    "Success",
                    "Case Created Successfully"
                  )
                )
              )
            else
              errorMessageFor(payload) match {
                case (code, message) =>
                  InternalServerError(
                    Json.toJson(
                      ResponseFailure(
                        processingDateFormat.format(ZonedDateTime.now),
                        correlationId,
                        code,
                        message
                      )
                    )
                  )
              }
          )
        }
      }
    }
  }

  // POST /cpr/caserequest/ndrc/update/v1
  def amendCaseEISStub: Action[String] = {

    val errorScenarioCaseID: Set[String] = Set("666", "667")

    def isSuccessCase(payload: AmendCaseRequest): Boolean =
      !errorScenarioCaseID.contains(payload.Content.CaseID)

    def errorMessageFor(payload: AmendCaseRequest): (String, String) =
      payload.Content.CaseID match {
        case "667" => ("400", "999 : PC12010081330XGBNZJO04")
        case _     => ("400", "Invalid request")
      }

    Action.async(parse.tolerantText) { implicit request =>
      withValidHeaders { correlationId =>
        withValidCasePayload[AmendCaseRequest](correlationId) { payload =>
          Future.successful(
            if (isSuccessCase(payload))
              Ok(
                Json.toJson(
                  CaseResponseSuccess(
                    "PC12010081330XGBNZJO04",
                    processingDateFormat.format(ZonedDateTime.now),
                    "Success",
                    "Case Updated Successfully"
                  )
                )
              )
            else
              errorMessageFor(payload) match {
                case (code, message) =>
                  InternalServerError(
                    Json.toJson(
                      ResponseFailure(
                        processingDateFormat.format(ZonedDateTime.now),
                        correlationId,
                        code,
                        message
                      )
                    )
                  )
              }
          )
        }
      }
    }
  }

  /**
   * Check all required headers and retrieve correlationId if any, then continue.
   * Return 400 if missing or invalid header.
   */
  private def withValidHeaders(
                                continue: String => Future[Result]
                              )(implicit request: Request[_]): Future[Result] = {
    val correlationIdHeader = request.headers.get("x-correlation-id")
    val hasCorrelationIdHeader = correlationIdHeader.exists(_.nonEmpty)
    val hasDateHeader =
      request.headers.get("date").exists(date => Try(httpDateFormat.parse(date)).isSuccess)
    val hasContentTypeHeader =
      request.headers.get("content-type").exists(_ == "application/json")
    val hasAcceptHeader = request.headers.get("accept").exists(_ == "application/json")
    val hasAuthorizationHeader =
      request.headers.get("authorization").exists(_.startsWith("Bearer "))
    val hasEnvironmentHeader =
      request.headers.get("environment").exists(_ == "stub")
    val hasCustomProcessesHostHeader =
      request.headers.get("CustomProcessesHost").exists(_ == "Digital")

    if (
      hasCorrelationIdHeader &&
        hasDateHeader &&
        hasContentTypeHeader &&
        hasAcceptHeader &&
        hasAuthorizationHeader &&
        hasEnvironmentHeader &&
        hasCustomProcessesHostHeader
    ) {
      continue(correlationIdHeader.getOrElse(""))
    } else {
      // In case of missing headers
      Future.successful(BadRequest)
    }
  }

  /**
   * Parse request message and continue.
   * Return 400 if parsing or validation fails.
   * Return 500 if other errors.
   */
  private def withValidCasePayload[T](correlationId: String)(
    continue: T => Future[Result]
  )(implicit
    request: Request[String],
    reads: Reads[T],
    validate: Validator.Validate[T],
    ec: ExecutionContext
                                     ): Future[Result] =
    withPayload[T](continue) {
      // In case of failure to parse or validate request message
      case (error, message) =>
        BadRequest(
          Json.toJson(
            ResponseFailure(
              processingDateFormat.format(ZonedDateTime.now),
              correlationId,
              "400",
              message
            )
          )
        )
    }.recover {
      // In case of other errors
      case e: Exception =>
        InternalServerError(
          Json.toJson(
            ResponseFailure(
              processingDateFormat.format(ZonedDateTime.now),
              correlationId,
              "500",
              e.getMessage()
            )
          )
        )
    }

}
