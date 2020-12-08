package uk.gov.hmrc.nationaldutyrepaymentcenterstubs.controllers

import org.scalatest.Suite
import org.scalatestplus.play.ServerProvider
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSClient
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.stubs.AuthStubs
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.support.{JsonMatchers, ServerBaseISpec}

class NationalDutyRepaymentCenterStubISpec extends ServerBaseISpec with AuthStubs with JsonMatchers {

  this: Suite with ServerProvider =>

  val url = s"http://localhost:$port"

  val wsClient = app.injector.instanceOf[WSClient]

  "TraderServicesRouteOneStub" when {

    "POST /create-case" should {
      "respond with 200 and body containing some result" in {
        givenAuthorised()
        val result = wsClient
          .url(s"$url/create-case")
          .post(Json.parse("""{}"""))
          .futureValue
        result.status shouldBe 201
        result.json.as[JsObject] should (haveProperty[String]("correlationId") and
          haveProperty[String]("result", be("PC12010081330XGBNZJO04")))
      }
    }
  }
}
