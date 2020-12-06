package uk.gov.hmrc.nationaldutyrepaymentcenterstubs.support

import org.scalatestplus.play.OneAppPerSuite
import play.api.Application
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.stubs.AuthStubs

abstract class AppBaseISpec
    extends BaseISpec with OneAppPerSuite with TestApplication with AuthStubs {

  override implicit lazy val app: Application = appBuilder.build()

}
