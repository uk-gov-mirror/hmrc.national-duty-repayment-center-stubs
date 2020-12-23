package uk.gov.hmrc.nationaldutyrepaymentcenterstubs.controllers

import org.scalatest.Suite
import org.scalatestplus.play.ServerProvider
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSClient
import java.{util => ju}
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.stubs.AuthStubs
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.support.{JsonMatchers, ServerBaseISpec}
import play.api.libs.ws.BodyWritable
import play.api.libs.ws.InMemoryBody
import akka.util.ByteString

class NationalDutyRepaymentCenterStubISpec extends ServerBaseISpec with AuthStubs with JsonMatchers {

  this: Suite with ServerProvider =>

  val url = s"http://localhost:$port"

  val wsClient = app.injector.instanceOf[WSClient]

  "NationalDutyRepaymentCenterStub" when {

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

    "POST /cpr/caserequest/ndrc/create/v1" should {
      "respond with 200 and body containing CaseID" in {
        val result = wsClient
          .url(s"$url/cpr/caserequest/ndrc/create/v1")
          .withHttpHeaders(
            "x-forwarded-host"    -> "127.0.0.1",
            "x-correlation-id"    -> ju.UUID.randomUUID().toString(),
            "date"                -> "Fri, 06 Nov 2020 08:23:57 GMT",
            "accept"              -> "application/json",
            "authorization"       -> "Bearer 321367126376217367621736716362",
            "environment"         -> "stub",
            "CustomProcessesHost" -> "Digital"
          )
          .post(Json.parse("""{
                             |"ApplicationType" : "NDRC",
                             |"OriginatingSystem" : "Digital",
                             |"AcknowledgementReference" : "1234",
                             |  "Content": {
                             |  "ClaimDetails": {
                             |      "FormType" : "01",
                             |      "CustomRegulationType" : "02",
                             |      "ClaimedUnderArticle" : "117",
                             |      "Claimant" : "02",
                             |      "ClaimType" : "02",
                             |      "EntryDetails" : {
                             |        "EPU" : "777",
                             |        "EntryNumber" : "123456A",
                             |        "EntryDate" : "20200101"
                             |       },
                             |      "ClaimReason" : "06",
                             |      "ClaimDescription" : "this is a claim description",
                             |      "DateReceived" : "20200805",
                             |      "ClaimDate" : "20200805",
                             |      "PayeeIndicator" : "01",
                             |      "PaymentMethod" : "02",
                             |      "DeclarantRefNumber" : "NA"
                             |    },
                             |    "AgentDetails" : {
                             |      "IsVATRegistered" : "true",
                             |      "EORI" : "GB123456789123456",
                             |      "Name" : "Joe Bloggs",
                             |      "Address" : {
                             |        "AddressLine1" : "line 1",
                             |        "AddressLine2" : "line 2",
                             |        "City" : "city",
                             |        "Region" : "region",
                             |        "CountryCode" : "GB",
                             |        "PostalCode" : "ZZ111ZZ",
                             |        "TelephoneNumber" : "12345678",
                             |        "EmailAddress" : "example@example.com"
                             |      }
                             |    },
                             |    "ImporterDetails" : {
                             |      "IsVATRegistered" : "true",
                             |      "EORI" : "GB123456789123456",
                             |      "Name" : "Joe Bloggs",
                             |      "Address" : {
                             |        "AddressLine1" : "line 1",
                             |        "AddressLine2" : "line 2",
                             |        "City" : "city",
                             |        "Region" : "region",
                             |        "CountryCode" : "GB",
                             |        "PostalCode" : "ZZ111ZZ",
                             |        "TelephoneNumber" : "12345678",
                             |        "EmailAddress" : "example@example.com"
                             |      }
                             |    },
                             |    "BankDetails" : {
                             |      "ImporterBankDetails" : {
                             |        "AccountName" : "account name",
                             |        "SortCode" : "123456",
                             |        "AccountNumber" : "12345678"
                             |      },
                             |      "AgentBankDetails" : {
                             |        "AccountName" : "account name",
                             |        "SortCode" : "123456",
                             |        "AccountNumber" : "12345678"
                             |      }
                             |    },
                             |    "DutyTypeTaxDetails" : {
                             |      "DutyTypeTaxList" : [
                             |        {
                             |          "Type" : "01",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        },
                             |        {
                             |          "Type" : "02",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        },
                             |        {
                             |          "Type" : "03",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        }
                             |      ]
                             |    },
                             |    "DocumentList" : [
                             |      {
                             |        "Type" : "03",
                             |        "Description" : "this is a copy of c88"
                             |      },
                             |      {
                             |        "Type" : "01",
                             |        "Description" : "this is an invoice"
                             |      },
                             |      {
                             |        "Type" : "04",
                             |        "Description" : "this is a packing list"
                             |      }
                             |    ]
                             |    }
                             |}""".stripMargin))
          .futureValue
        result.status shouldBe 200
        result.json.as[JsObject] should (
          haveProperty[String]("ProcessingDate") and
            haveProperty[String]("CaseID", be("PC12010081330XGBNZJO04")) and
            haveProperty[String]("Status", be("Success")) and
            haveProperty[String]("StatusText", be("Case Created Successfully"))
          )
      }

      "response with 400 if missing header" in {
        val result = wsClient
          .url(s"$url/cpr/caserequest/ndrc/create/v1")
          .post(Json.parse("""{
                             |"ApplicationType" : "NDRC",
                             |"OriginatingSystem" : "Digital",
                             |"AcknowledgementReference" : "1234",
                             |  "Content": {
                             |  "ClaimDetails": {
                             |      "FormType" : "01",
                             |      "CustomRegulationType" : "02",
                             |      "ClaimedUnderArticle" : "117",
                             |      "Claimant" : "02",
                             |      "ClaimType" : "02",
                             |      "NoOfEntries" : "10",
                             |      "EntryDetails" : {
                             |        "EPU" : "777",
                             |        "EntryNumber" : "123456A",
                             |        "EntryDate" : "20200101"
                             |       },
                             |      "ClaimReason" : "06",
                             |      "ClaimDescription" : "this is a claim description",
                             |      "DateReceived" : "20200805",
                             |      "ClaimDate" : "20200805",
                             |      "PayeeIndicator" : "01",
                             |      "PaymentMethod" : "02",
                             |      "DeclarantRefNumber" : "NA"
                             |    },
                             |    "AgentDetails" : {
                             |      "IsVATRegistered" : "true",
                             |      "EORI" : "GB123456789123456",
                             |      "Name" : "Joe Bloggs",
                             |      "Address" : {
                             |        "AddressLine1" : "line 1",
                             |        "AddressLine2" : "line 2",
                             |        "City" : "city",
                             |        "Region" : "region",
                             |        "CountryCode" : "GB",
                             |        "PostalCode" : "ZZ111ZZ",
                             |        "TelephoneNumber" : "12345678",
                             |        "EmailAddress" : "example@example.com"
                             |      }
                             |    },
                             |    "ImporterDetails" : {
                             |      "IsVATRegistered" : "true",
                             |      "EORI" : "GB123456789123456",
                             |      "Name" : "Joe Bloggs",
                             |      "Address" : {
                             |        "AddressLine1" : "line 1",
                             |        "AddressLine2" : "line 2",
                             |        "City" : "city",
                             |        "Region" : "region",
                             |        "CountryCode" : "GB",
                             |        "PostalCode" : "ZZ111ZZ",
                             |        "TelephoneNumber" : "12345678",
                             |        "EmailAddress" : "example@example.com"
                             |      }
                             |    },
                             |    "BankDetails" : {
                             |      "ImporterBankDetails" : {
                             |        "AccountName" : "account name",
                             |        "SortCode" : "123456",
                             |        "AccountNumber" : "12345678"
                             |      },
                             |      "AgentBankDetails" : {
                             |        "AccountName" : "account name",
                             |        "SortCode" : "123456",
                             |        "AccountNumber" : "12345678"
                             |      }
                             |    },
                             |    "DutyTypeTaxDetails" : {
                             |      "DutyTypeTaxList" : [
                             |        {
                             |          "Type" : "01",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        },
                             |        {
                             |          "Type" : "02",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        },
                             |        {
                             |          "Type" : "03",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        }
                             |      ]
                             |    },
                             |    "DocumentList" : [
                             |      {
                             |        "Type" : "03",
                             |        "Description" : "this is a copy of c88"
                             |      },
                             |      {
                             |        "Type" : "01",
                             |        "Description" : "this is an invoice"
                             |      },
                             |      {
                             |        "Type" : "04",
                             |        "Description" : "this is a packing list"
                             |      }
                             |    ]
                             |    }
                             |}""".stripMargin))
          .futureValue
        result.status shouldBe 400
      }

      "response with 400 if malformed json" in {
        val correlationId = ju.UUID.randomUUID().toString()

        // force malformed json string
        implicit val bodyWritable: BodyWritable[String] =
          BodyWritable(str => InMemoryBody(ByteString.fromString(str)), "application/json")

        val result = wsClient
          .url(s"$url/cpr/caserequest/ndrc/create/v1")
          .withHttpHeaders(
            "x-forwarded-host"    -> "127.0.0.1",
            "x-correlation-id"    -> correlationId,
            "date"                -> "Fri, 06 Nov 2020 08:23:57 GMT",
            "accept"              -> "application/json",
            "authorization"       -> "Bearer 321367126376217367621736716362",
            "environment"         -> "stub",
            "CustomProcessesHost" -> "Digital"
          )
          .post("""{
                  |"ApplicationType" : "NDRC",
                  |"OriginatingSystem" : "Digital",
                  |"AcknowledgementReference" : "1234",
                  |  "Content": {
                  |  "ClaimDetails": {
                  |      "FormType" : "01",
                  |      "CustomRegulationType" : "02",
                  |      "ClaimedUnderArticle" : "117",
                  |      "Claimant" : "02",
                  |      "ClaimType" : "02",
                  |      "EntryDetails" : {
                  |        "EPU" : "777",
                  |        "EntryNumber" : "123456A",
                  |        "EntryDate" : "20200101"
                  |       },""".stripMargin)
          .futureValue
        result.status shouldBe 400
        result.json.as[JsObject] should (
          haveProperty[JsObject](
            "errorDetail",
            haveProperty[String]("timestamp") and
              haveProperty[String]("correlationId", be(correlationId)) and
              haveProperty[String]("errorCode", be("400")) and
              haveProperty[String]("errorMessage")
          )
          )
      }

      "response with 400 if the payload doesn't follow the schema" in {
        val correlationId = ju.UUID.randomUUID().toString()
        val result = wsClient
          .url(s"$url/cpr/caserequest/ndrc/create/v1")
          .withHttpHeaders(
            "x-forwarded-host"    -> "127.0.0.1",
            "x-correlation-id"    -> correlationId,
            "date"                -> "Fri, 06 Nov 2020 08:23:57 GMT",
            "accept"              -> "application/json",
            "authorization"       -> "Bearer 321367126376217367621736716362",
            "environment"         -> "stub",
            "CustomProcessesHost" -> "Digital"
          )
          .post(Json.parse("""{
                             |"ApplicationType" : "NDCR",
                             |"OriginatingSystem" : "Digital",
                             |"AcknowledgementReference" : "1234",
                             |  "Content": {
                             |  "ClaimDetails": {
                             |      "FormType" : "01",
                             |      "CustomRegulationType" : "02",
                             |      "ClaimedUnderArticle" : "117",
                             |      "Claimant" : "02",
                             |      "ClaimType" : "02",
                             |      "EntryDetails" : {
                             |        "EPU" : "777",
                             |        "EntryNumber" : "123456A",
                             |        "EntryDate" : "20200101"
                             |       },
                             |      "EntryDate" : "20200101",
                             |      "ClaimReason" : "06",
                             |      "ClaimDescription" : "this is a claim description",
                             |      "DateReceived" : "20200805",
                             |      "ClaimDate" : "20200805",
                             |      "PayeeIndicator" : "01",
                             |      "PaymentMethod" : "02",
                             |      "DeclarantRefNumber" : "NA"
                             |    },
                             |    "AgentDetails" : {
                             |      "IsVATRegistered" : "true",
                             |      "EORI" : "GB123456789123456",
                             |      "Name" : "Joe Bloggs",
                             |      "Address" : {
                             |        "AddressLine1" : "line 1",
                             |        "AddressLine2" : "line 2",
                             |        "City" : "city",
                             |        "Region" : "region",
                             |        "CountryCode" : "GB",
                             |        "PostalCode" : "ZZ111ZZ",
                             |        "TelephoneNumber" : "12345678",
                             |        "EmailAddress" : "example@example.com"
                             |      }
                             |    },
                             |    "ImporterDetails" : {
                             |      "IsVATRegistered" : "true",
                             |      "EORI" : "GB123456789123456",
                             |      "Name" : "Joe Bloggs",
                             |      "Address" : {
                             |        "AddressLine1" : "line 1",
                             |        "AddressLine2" : "line 2",
                             |        "City" : "city",
                             |        "Region" : "region",
                             |        "CountryCode" : "GB",
                             |        "PostalCode" : "ZZ111ZZ",
                             |        "TelephoneNumber" : "12345678",
                             |        "EmailAddress" : "example@example.com"
                             |      }
                             |    },
                             |    "BankDetails" : {
                             |      "ImporterBankDetails" : {
                             |        "AccountName" : "account name",
                             |        "SortCode" : "123456",
                             |        "AccountNumber" : "12345678"
                             |      },
                             |      "AgentBankDetails" : {
                             |        "AccountName" : "account name",
                             |        "SortCode" : "123456",
                             |        "AccountNumber" : "12345678"
                             |      }
                             |    },
                             |    "DutyTypeTaxDetails" : {
                             |      "DutyTypeTaxList" : [
                             |        {
                             |          "Type" : "01",
                             |          "PaidAmount" : "100",
                             |          "DueAmount" : "50",
                             |          "ClaimAmount" : "50"
                             |        },
                             |        {
                             |          "Type" : "02",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        },
                             |        {
                             |          "Type" : "03",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        }
                             |      ]
                             |    },
                             |    "DocumentList" : [
                             |      {
                             |        "Type" : "03",
                             |        "Description" : "this is a copy of c88"
                             |      },
                             |      {
                             |        "Type" : "01",
                             |        "Description" : "this is an invoice"
                             |      },
                             |      {
                             |        "Type" : "04",
                             |        "Description" : "this is a packing list"
                             |      }
                             |    ]
                             |    }
                             |}""".stripMargin))
          .futureValue
        result.status shouldBe 400
        result.json.as[JsObject] should (
          haveProperty[JsObject](
            "errorDetail",
            haveProperty[String]("timestamp") and
              haveProperty[String]("correlationId", be(correlationId)) and
              haveProperty[String]("errorCode", be("400")) and
              haveProperty[String]("errorMessage")
          )
          )
      }

      "response with 500 if an upstream error occurs" in {
        val correlationId = ju.UUID.randomUUID().toString()
        val result = wsClient
          .url(s"$url/cpr/caserequest/ndrc/create/v1")
          .withHttpHeaders(
            "x-forwarded-host"    -> "127.0.0.1",
            "x-correlation-id"    -> correlationId,
            "date"                -> "Fri, 06 Nov 2020 08:23:57 GMT",
            "accept"              -> "application/json",
            "authorization"       -> "Bearer 321367126376217367621736716362",
            "environment"         -> "stub",
            "CustomProcessesHost" -> "Digital"
          )
          .post(Json.parse("""{
                             |"ApplicationType" : "NDRC",
                             |"OriginatingSystem" : "Digital",
                             |"AcknowledgementReference" : "1234",
                             |  "Content": {
                             |  "ClaimDetails": {
                             |      "FormType" : "01",
                             |      "CustomRegulationType" : "02",
                             |      "ClaimedUnderArticle" : "117",
                             |      "Claimant" : "02",
                             |      "ClaimType" : "02",
                             |      "NoOfEntries" : "10",
                             |      "EntryDetails" : {
                             |        "EPU" : "666",
                             |        "EntryNumber" : "123456A",
                             |        "EntryDate" : "20200101"
                             |       },
                             |      "ClaimReason" : "06",
                             |      "ClaimDescription" : "this is a claim description",
                             |      "DateReceived" : "20200805",
                             |      "ClaimDate" : "20200805",
                             |      "PayeeIndicator" : "01",
                             |      "PaymentMethod" : "02",
                             |      "DeclarantRefNumber" : "NA"
                             |    },
                             |    "AgentDetails" : {
                             |      "IsVATRegistered" : "true",
                             |      "EORI" : "GB123456789123456",
                             |      "Name" : "Joe Bloggs",
                             |      "Address" : {
                             |        "AddressLine1" : "line 1",
                             |        "AddressLine2" : "line 2",
                             |        "City" : "city",
                             |        "Region" : "region",
                             |        "CountryCode" : "GB",
                             |        "PostalCode" : "ZZ111ZZ",
                             |        "TelephoneNumber" : "12345678",
                             |        "EmailAddress" : "example@example.com"
                             |      }
                             |    },
                             |    "ImporterDetails" : {
                             |      "IsVATRegistered" : "true",
                             |      "EORI" : "GB123456789123456",
                             |      "Name" : "Joe Bloggs",
                             |      "Address" : {
                             |        "AddressLine1" : "line 1",
                             |        "AddressLine2" : "line 2",
                             |        "City" : "city",
                             |        "Region" : "region",
                             |        "CountryCode" : "GB",
                             |        "PostalCode" : "ZZ111ZZ",
                             |        "TelephoneNumber" : "12345678",
                             |        "EmailAddress" : "example@example.com"
                             |      }
                             |    },
                             |    "BankDetails" : {
                             |      "ImporterBankDetails" : {
                             |        "AccountName" : "account name",
                             |        "SortCode" : "123456",
                             |        "AccountNumber" : "12345678"
                             |      },
                             |      "AgentBankDetails" : {
                             |        "AccountName" : "account name",
                             |        "SortCode" : "123456",
                             |        "AccountNumber" : "12345678"
                             |      }
                             |    },
                             |    "DutyTypeTaxDetails" : {
                             |      "DutyTypeTaxList" : [
                             |        {
                             |          "Type" : "01",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        },
                             |        {
                             |          "Type" : "02",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        },
                             |        {
                             |          "Type" : "03",
                             |          "PaidAmount" : "100.00",
                             |          "DueAmount" : "50.00",
                             |          "ClaimAmount" : "50.00"
                             |        }
                             |      ]
                             |    },
                             |    "DocumentList" : [
                             |      {
                             |        "Type" : "03",
                             |        "Description" : "this is a copy of c88"
                             |      },
                             |      {
                             |        "Type" : "01",
                             |        "Description" : "this is an invoice"
                             |      },
                             |      {
                             |        "Type" : "04",
                             |        "Description" : "this is a packing list"
                             |      }
                             |    ]
                             |    }
                             |}""".stripMargin))
          .futureValue
        result.status shouldBe 500
        result.json.as[JsObject] should (
          haveProperty[JsObject](
            "errorDetail",
            haveProperty[String]("timestamp") and
              haveProperty[String]("correlationId", be(correlationId)) and
              haveProperty[String]("errorCode", be("400")) and
              haveProperty[String]("errorMessage")
          )
          )
      }
    }
  }
}
