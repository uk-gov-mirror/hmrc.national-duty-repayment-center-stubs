package uk.gov.hmrc.nationaldutyrepaymentcenterstubs.controllers

import play.api.mvc.Result
import play.api.mvc.Results._
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisationException}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.support.AppBaseISpec
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.wiring.AppConfig

import scala.concurrent.Future

class AuthActionsISpec extends AppBaseISpec {

  object TestController extends AuthActions {

    override def authConnector: AuthConnector = app.injector.instanceOf[AuthConnector]

    override val appConfig: AppConfig = new AppConfig {
      override val appName: String = "dummy"
      override val authBaseUrl: String = "dummy"
      override val authorisedServiceName: String = "HMRC-XYZ"
      override val authorisedIdentifierKey: String = "XYZNumber"
    }

    implicit val hc = HeaderCarrier()
    implicit val request = FakeRequest().withSession(SessionKeys.authToken -> "Bearer XYZ")
    import scala.concurrent.ExecutionContext.Implicits.global

    def withAuthorised[A]: Result =
      await(super.withAuthorised {
        Future.successful(Ok)
      })

  }

  "withAuthorised" should {

    "call body when authorized" in {
      stubForAuthAuthorise(
        "{}",
        "{}"
      )
      val result = TestController.withAuthorised
      status(result) shouldBe 200
    }

    "throw an AutorisationException when user not logged in" in {
      givenUnauthorisedWith("MissingBearerToken")
      an[AuthorisationException] shouldBe thrownBy {
        TestController.withAuthorised
      }
    }
  }

}
