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

package uk.gov.hmrc.icedeisstub.controllers
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.nationaldutyrepaymentcenter.models.requests.CreateClaimRequest
import uk.gov.hmrc.nationaldutyrepaymentcenter.models.responses.ClientClaimFailureResponse
import uk.gov.hmrc.nationaldutyrepaymentcenter.models.responses.ClientClaimSuccessResponse


import scala.concurrent.ExecutionContext
import play.api.libs.json.Json

@Singleton()
class ClaimRepaymentsController @Inject()(
                               cc: ControllerComponents
                               )(implicit ec: ExecutionContext)
  extends BackendController(cc) {


  def sendClaim: Action[CreateClaimRequest] = Action(parse.json[CreateClaimRequest]) {
    implicit request =>

      request.body.Content.ClaimDetails.FormType.value.charAt(0) match {
        case '1' =>
          Ok(Json.toJson(ClientClaimSuccessResponse("success", "22", "ee", Some("35s"), "www")))
        case _ =>
          Ok(Json.toJson(ClientClaimFailureResponse("failure", "ww", "ee", "35s", "333", Some("ddd"))))
      }
  }


  }