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

package uk.gov.hmrc.nationaldutyrepaymentcenter.models

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.nationaldutyrepaymentcenterstubs.models.Validator

/**
 * Create specified case in the PEGA system.
 * Based on spec "CPR01-1.0.0-EIS API Specification-Create Case from MDTP"
 * m
 *
 * @param AcknowledgementReference Unique id created at source after a form is saved Unique ID throughout the journey of a message-stored in CSG data records, may be passed to Decision Service, CSG records can be searched using this field etc.
 * @param ApplicationType          Its key value to create the case for respective process.
 * @param OriginatingSystem        “Digital” for all requests originating in Digital
 */
case class CreateCaseRequest(
                              AcknowledgementReference: String,
                              ApplicationType: String,
                              OriginatingSystem: String,
                              Content: CreateCaseRequest.Content
                            )

object CreateCaseRequest {

  import Validator._
  import CommonValues._

  /**
   * @param ClaimDetails       see ClaimDetails structure.
   * @param AgentDetails       Agent/Representative of the importer Information (see UserDetails structure).
   * @param ImporterDetails    see UserDetails structure.
   * @param BankDetails        bank details of the payee required for BACS payments.
   * @param DutyTypeTaxDetails XXX.
   * @param DocumentList       CHIEF entry date in YYYYMMDD format.
   */
  case class Content(
                      ClaimDetails: ClaimDetails,
                      AgentDetails: Option[UserDetails],
                      ImporterDetails: UserDetails,
                      BankDetails: Option[AllBankDetails],
                      DutyTypeTaxDetails: DutyTypeTaxDetails,
                      DocumentList: Seq[DocumentList]
                    )

  object Content {
    implicit val formats: Format[Content] = Json.format[Content]
  }

  case class ClaimDetails(
                           FormType: String,
                           CustomRegulationType: String,
                           ClaimedUnderArticle: String,
                           Claimant: String,
                           ClaimType: String,
                           EntryDetails: EntryDetails,
                           ClaimReason: String,
                           ClaimDescription: String,
                           DateReceived: String,
                           ClaimDate: String,
                           PayeeIndicator: String,
                           PaymentMethod: String
                         )

  object ClaimDetails {
    implicit val formats: Format[ClaimDetails] = Json.format[ClaimDetails]

    val FormTypeValidator: Validate[String] =
      check(
        _.isOneOf(FormTypeEnum),
        s""""Invalid FormType, should be one of [${FormTypeEnum.mkString(", ")}]"""
      )

    val CustomRegulationTypeValidator: Validate[String] =
      check(
        _.isOneOf(CustomRegulationTypeEnum),
        s""""Invalid CustomRegulationType, should be one of [${CustomRegulationTypeEnum.mkString(", ")}]"""
      )

    val ClaimedUnderArticleValidator: Validate[String] =
      check(
        _.isOneOf(ClaimedUnderArticleEnum),
        s""""Invalid ClaimedUnderArticle, should be one of [${ClaimedUnderArticleEnum.mkString(", ")}]"""
      )

    val ClaimantValidator: Validate[String] =
      check(
        _.isOneOf(ClaimantEnum),
        s""""Invalid Claimant, should be one of [${ClaimantEnum.mkString(", ")}]"""
      )

    val ClaimTypeValidator: Validate[String] =
      check(
        _.isOneOf(ClaimTypeEnum),
        s""""Invalid ClaimType, should be one of [${ClaimTypeEnum.mkString(", ")}]"""
      )

    val ClaimReasonValidator: Validate[String] =
      check(
        _.isOneOf(ClaimReasonEnum),
        s""""Invalid ClaimReason, should be one of [${ClaimReasonEnum.mkString(", ")}]"""
      )

    val ClaimDescriptionValidator: Validate[String] =
      check(
        _.matches(ClaimDescriptionPattern),
        s""""Invalid ClaimDescription, should be one of [${ClaimDescriptionPattern.mkString(", ")}]"""
      )

    val DateReceivedValidator: Validate[String] =
      check(
        _.matches(DateReceivedPattern),
        s""""Invalid DateReceived, should be one of [${DateReceivedPattern.mkString(", ")}]"""
      )

    val ClaimDateValidator: Validate[String] =
      check(
        _.matches(ClaimDatePattern),
        s""""Invalid ClaimDate, should be one of [${ClaimDatePattern.mkString(", ")}]"""
      )

    val PayeeIndicatorValidator: Validate[String] =
      check(
        _.isOneOf(PayeeIndicatorEnum),
        s""""Invalid PayeeIndicator, should be one of [${PayeeIndicatorEnum.mkString(", ")}]"""
      )

    val PaymentMethodValidator: Validate[String] =
      check(
        _.isOneOf(PaymentMethodEnum),
        s""""Invalid PaymentMethod, should be one of [${PaymentMethodEnum.mkString(", ")}]"""
      )


    val validate: Validate[ClaimDetails] = Validator(
      checkProperty(_.FormType, FormTypeValidator),
      checkProperty(_.CustomRegulationType, CustomRegulationTypeValidator),
      checkProperty(_.ClaimedUnderArticle, ClaimedUnderArticleValidator),
      checkProperty(_.Claimant, ClaimantValidator),
      checkProperty(_.ClaimType, ClaimTypeValidator),
      checkProperty(_.ClaimReason, ClaimReasonValidator),
      checkProperty(_.ClaimDescription, ClaimDescriptionValidator),
      checkProperty(_.DateReceived, DateReceivedValidator),
      checkProperty(_.ClaimDate, ClaimDateValidator),
      checkProperty(_.PayeeIndicator, PayeeIndicatorValidator),
      checkProperty(_.PaymentMethod, PaymentMethodValidator)
    )
  }

  case class EntryDetails(
                           EPU: String,
                           EntryNumber: String,
                           EntryDate: String
                         )

  object EntryDetails {
    implicit val formats: Format[EntryDetails] = Json.format[EntryDetails]

    val EPUValidator: Validate[String] =
      check(
        _.matches(EPUPattern),
        s""""Invalid EPU, should be one of [${EPUPattern.mkString(", ")}]"""
      )

    val EntryNumberValidator: Validate[String] =
      check(
        _.matches(EntryNumberPattern),
        s""""Invalid EntryNumber, should be one of [${EntryNumberPattern.mkString(", ")}]"""
      )

    val EntryDateValidator: Validate[String] =
      check(
        _.matches(EntryDatePattern),
        s""""Invalid EntryDate, should be one of [${EntryDatePattern.mkString(", ")}]"""
      )

    val validate: Validate[EntryDetails] = Validator(
      checkProperty(_.EPU, EPUValidator),
      checkProperty(_.EntryNumber, EntryNumberValidator),
      checkProperty(_.EntryDate, EntryDateValidator)
    )
  }

  case class UserDetails(
                          IsVATRegistered: String,
                          EORI: String,
                          Name: String,
                          Address: Address
                        )

  object UserDetails {
    implicit val formats: Format[UserDetails] = Json.format[UserDetails]

    val IsVATRegisteredValidator: Validate[String] =
      check(
        _.isOneOf(IsVATRegisteredEnum),
        s""""Invalid IsVATRegistered, should be one of [${IsVATRegisteredEnum.mkString(", ")}]"""
      )

    val EORIValidator: Validate[String] =
      check(
        _.matches(EORIPattern),
        s""""Invalid EORI, should be one of [${EORIPattern.mkString(", ")}]"""
      )

    val NameValidator: Validate[String] =
      check(
        _.matches(NamePattern),
        s""""Invalid Name, should be one of [${NamePattern.mkString(", ")}]"""
      )

    val validate: Validate[UserDetails] = Validator(
      checkProperty(_.IsVATRegistered, IsVATRegisteredValidator),
      checkProperty(_.EORI, EORIValidator),
      checkProperty(_.Name, NameValidator)
    )
  }

  case class AllBankDetails(
                             ImporterBankDetails: Option[BankDetails],
                             AgentBankDetails: Option[BankDetails]
                           )

  object AllBankDetails {
    implicit val formats: Format[AllBankDetails] = Json.format[AllBankDetails]

    val validate: Validate[AllBankDetails] = Validator(
      checkIfSome(_.AgentBankDetails, BankDetails.validate),
      checkIfSome(_.ImporterBankDetails, BankDetails.validate)
    )

  }



  case class BankDetails(
                          AccountName: String,
                          SortCode: String,
                          AccountNumber: String)

  object BankDetails {
    implicit val formats: Format[BankDetails] = Json.format[BankDetails]

    val AccountNameValidator: Validate[String] =
      check(
        _.matches(AccountNamePattern),
        s""""Invalid AccountName, should be one of [${AccountNamePattern.mkString(", ")}]"""
      )

    val SortCodeValidator: Validate[String] =
      check(
        _.matches(SortCodePattern),
        s""""Invalid SortCode, should be one of [${SortCodePattern.mkString(", ")}]"""
      )

    val AccountNumberValidator: Validate[String] =
      check(
        _.matches(AccountNumberPattern),
        s""""Invalid AccountNumber, should be one of [${AccountNumberPattern.mkString(", ")}]"""
      )

    val validate: Validate[BankDetails] = Validator(
      checkProperty(_.AccountName, AccountNameValidator),
      checkProperty(_.SortCode, SortCodeValidator),
      checkProperty(_.AccountNumber, AccountNumberValidator)
    )
  }

  case class DutyTypeTaxDetails(
                                 DutyTypeTaxList: Seq[DutyTypeTaxList]
                               )

  object DutyTypeTaxDetails {
    implicit val formats: Format[DutyTypeTaxDetails] = Json.format[DutyTypeTaxDetails]

    val validate: Validate[DutyTypeTaxDetails] = Validator(
      checkEach(_.DutyTypeTaxList, DutyTypeTaxList.validate)
    )
  }

  case class DutyTypeTaxList(
                              Type: String,
                              PaidAmount: String,
                              DueAmount: String,
                              ClaimAmount: String
                            )

  object DutyTypeTaxList {
    implicit val formats: Format[DutyTypeTaxList] = Json.format[DutyTypeTaxList]

    val TypeValidator: Validate[String] =
      check(
        _.isOneOf(TypeEnum),
        s""""Invalid Type (duty type tax list), should be one of [${TypeEnum.mkString(", ")}]"""
      )

    val PaidAmountValidator: Validate[String] =
      check(
        _.matches(PaidAmountPattern),
        s""""Invalid PaidAmount, should be one of [${PaidAmountPattern.mkString(", ")}]"""
      )

    val DueAmountValidator: Validate[String] =
      check(
        _.matches(DueAmountPattern),
        s""""Invalid DueAmount, should be one of [${DueAmountPattern.mkString(", ")}]"""
      )

    val ClaimAmountValidator: Validate[String] =
      check(
        _.matches(ClaimAmountPattern),
        s""""Invalid ClaimAmount, should be one of [${ClaimAmountPattern.mkString(", ")}]"""
      )

    val validate: Validate[DutyTypeTaxList] = Validator(
      checkProperty(_.Type, TypeValidator),
      checkProperty(_.PaidAmount, PaidAmountValidator),
      checkProperty(_.DueAmount, DueAmountValidator),
      checkProperty(_.ClaimAmount, ClaimAmountValidator)
    )
  }

  case class DocumentList(
                           Type: String,
                           Description: Option[String]
                         )

  object DocumentList {
    implicit val formats: Format[DocumentList] = Json.format[DocumentList]

    val DocumentListTypeValidator: Validate[String] =
      check(
        _.isOneOf(DocumentListEnum),
        s""""Invalid DocumentListType, should be one of [${DocumentListEnum.mkString(", ")}]"""
      )

    val DescriptionValidator: Validate[String] =
      check(
        _.matches(DescriptionPattern),
        s""""Invalid Description, should be one of [${DescriptionPattern.mkString(", ")}]"""
      )

    val validate: Validate[DocumentList] = Validator(
      checkProperty(_.Type, DocumentListTypeValidator),
      checkIfSome(_.Description, DescriptionValidator)
    )
  }

  case class Address(
                      AddressLine1: String,
                      AddressLine2: Option[String],
                      City: String,
                      Region: String,
                      CountryCode: String,
                      PostalCode: Option[String],
                      TelephoneNumber: Option[String],
                      EmailAddress: Option[String]
                    )

  object Address {
    implicit val formats: Format[Address] = Json.format[Address]

    val AddressLine1Validator: Validate[String] =
      check(
        _.matches(AddressLine1Pattern),
        s""""Invalid AddressLine1, should be one of [${AddressLine1Pattern.mkString(", ")}]"""
      )

    val AddressLine2Validator: Validate[String] =
      check(
        _.matches(AddressLine2Pattern),
        s""""Invalid AddressLine2, should be one of [${AddressLine2Pattern.mkString(", ")}]"""
      )

    val CityValidator: Validate[String] =
      check(
        _.matches(CityPattern),
        s""""Invalid City, should be one of [${CityPattern.mkString(", ")}]"""
      )

    val RegionValidator: Validate[String] =
      check(
        _.matches(RegionPattern),
        s""""Invalid Region, should be one of [${RegionPattern.mkString(", ")}]"""
      )

    val CountryCodeValidator: Validate[String] =
      check(
        _.matches(CountryCodePattern),
        s""""Invalid CountryCode, should be one of [${CountryCodePattern.mkString(", ")}]"""
      )

    val PostalCodeValidator: Validate[String] =
      check(
        _.matches(PostalCodePattern),
        s""""Invalid PostalCode, should be one of [${PostalCodePattern.mkString(", ")}]"""
      )

    val TelephoneNumberValidator: Validate[String] =
      check(
        _.matches(TelephoneNumberPattern),
        s""""Invalid TelephoneNumber, should be one of [${TelephoneNumberPattern.mkString(", ")}]"""
      )

    val EmailAddressValidator: Validate[String] =
      check(
        _.matches(EmailAddressPattern),
        s""""Invalid EmailAddress, should be one of [${EmailAddressPattern.mkString(", ")}]"""
      )

    val validate: Validate[Address] = Validator(
      checkProperty(_.AddressLine1, AddressLine1Validator),
      checkIfSome(_.AddressLine2, AddressLine2Validator),
      checkProperty(_.City, CityValidator),
      checkProperty(_.Region, RegionValidator),
      checkProperty(_.CountryCode, CountryCodeValidator),
      checkIfSome(_.PostalCode, PostalCodeValidator),
      checkIfSome(_.TelephoneNumber, TelephoneNumberValidator),
      checkIfSome(_.EmailAddress, EmailAddressValidator)
    )
  }

  implicit val formats: Format[CreateCaseRequest] = Json.format[CreateCaseRequest]

  object CommonValues {
    val FormTypeEnum = Seq("01")
    val CustomRegulationTypeEnum = Seq("01", "02")
    val ClaimedUnderArticleEnum = Seq("117", "119", "120", "999")
    val ClaimantEnum = Seq("01", "02")
    val ClaimTypeEnum = Seq("01", "02")
    val ClaimReasonEnum = Seq("01", "02", "03", "04", "05", "06", "07", "08", "09", "10")
    val ClaimDescriptionPattern = """([a-zA-Z0-9 ]{1,1500})"""
    val DateReceivedPattern = """([0-9]{8})"""
    val ClaimDatePattern = """([0-9]{8})"""
    val PayeeIndicatorEnum = Seq("01", "02", "03")
    val PaymentMethodEnum = Seq("01", "02", "03")
    val EPUPattern = """([0-9]{3})"""
    val EntryNumberPattern = """([0-9]{6}[0-9a-zA-Z]{1})"""
    val EntryDatePattern = """([0-9]{8})"""
    val IsVATRegisteredEnum = Seq("true", "false")
    val EORIPattern = """(([G]{1}[B]{1}[0-9]{15})|([G]{1}[B]{1}[0-9]{12})|([G]{1}[B]{1}[P]{1}[R]{1}))"""
    val NamePattern = """([a-zA-Z0-9 ]{1,512})"""
    val AccountNamePattern = """([a-zA-Z0-9 ]{1,40})"""
    val SortCodePattern = """([0-9]{6})"""
    val AccountNumberPattern = """([0-9]{8})"""
    val TypeEnum = Seq("01", "02", "03")
    val PaidAmountPattern = """(^-?[0-9]{1,11}$|^-?[0-9]{1,11}[.][0-9]{1,2}$)"""
    val DueAmountPattern = """(^-?[0-9]{1,11}$|^-?[0-9]{1,11}[.][0-9]{1,2}$)"""
    val ClaimAmountPattern = """(^-?[0-9]{1,11}$|^-?[0-9]{1,11}[.][0-9]{1,2}$)"""
    val DocumentListEnum = Seq("01", "02", "03", "04", "05", "06", "07", "08")
    val DescriptionPattern = """([a-zA-Z0-9 ]{1,100})"""
    val AddressLine1Pattern = """([a-zA-Z0-9]{1,128})"""
    val AddressLine2Pattern = """([a-zA-Z0-9]{1,128})"""
    val CityPattern = """([a-zA-Z0-9]{1,64})"""
    val RegionPattern = """([a-zA-Z0-9]{1,64})"""
    val CountryCodePattern = """([a-zA-Z]{2,2})"""
    val PostalCodePattern = """([a-zA-Z0-9]{6,9})"""
    val TelephoneNumberPattern = """([0]{1}[0-9]{1,10})"""
    val EmailAddressPattern = """([a-zA-Z0-9]{1,85})"""
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

  implicit val validate: Validate[CreateCaseRequest] = Validator(
    checkProperty(_.AcknowledgementReference, AcknowledgementReferenceValidator),
    checkProperty(_.ApplicationType, ApplicationTypeValidator),
    checkProperty(_.OriginatingSystem, OriginatingSystemValidator),
    checkProperty(_.Content.ClaimDetails, ClaimDetails.validate),
    checkIfSome(_.Content.AgentDetails, UserDetails.validate),
    checkProperty(_.Content.ImporterDetails, UserDetails.validate),
    checkIfSome(_.Content.BankDetails, AllBankDetails.validate),
    checkProperty(_.Content.DutyTypeTaxDetails, DutyTypeTaxDetails.validate),
    checkEach(_.Content.DocumentList, DocumentList.validate),

  )

}
