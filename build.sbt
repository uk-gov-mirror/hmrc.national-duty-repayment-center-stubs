import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc.SbtAutoBuildPlugin


lazy val compileDeps = Seq(
  ws,
  "uk.gov.hmrc"        %% "bootstrap-backend-play-26" % "2.25.0",
  "uk.gov.hmrc"        %% "auth-client"               % "3.2.0-play-26",
  "uk.gov.hmrc"        %% "agent-mtd-identifiers"     % "0.19.0-play-26",
  "com.kenshoo"        %% "metrics-play"              % "2.6.19_0.7.0",
  "uk.gov.hmrc"        %% "domain"                    % "5.10.0-play-26",
  "com.github.blemale" %% "scaffeine"                 % "3.1.0",
  "org.typelevel"      %% "cats-core"                 % "2.2.0",
  ws
)

def testDeps(scope: String) = Seq(
  "uk.gov.hmrc"            %% "hmrctest"           % "3.9.0-play-26" % scope,
  "org.scalatest"          %% "scalatest"          % "3.0.9"         % scope,
  "org.mockito"             % "mockito-core"       % "3.1.0"         % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3"         % scope,
  "com.github.tomakehurst"  % "wiremock"           % "2.27.2"        % scope
)

val jettyVersion = "9.2.24.v20180105"

val jettyOverrides = Seq(
  "org.eclipse.jetty"           % "jetty-server"       % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-servlet"      % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-security"     % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-servlets"     % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-continuation" % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-webapp"       % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-xml"          % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-client"       % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-http"         % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-io"           % jettyVersion % IntegrationTest,
  "org.eclipse.jetty"           % "jetty-util"         % jettyVersion % IntegrationTest,
  "org.eclipse.jetty.websocket" % "websocket-api"      % jettyVersion % IntegrationTest,
  "org.eclipse.jetty.websocket" % "websocket-common"   % jettyVersion % IntegrationTest,
  "org.eclipse.jetty.websocket" % "websocket-client"   % jettyVersion % IntegrationTest
)

lazy val root = (project in file("."))
  .settings(
    name := "national-duty-repayment-center-stubs",
    organization := "uk.gov.hmrc",
    scalaVersion := "2.12.12",
    PlayKeys.playDefaultPort := 9382,
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.bintrayRepo("hmrc", "release-candidates"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    ),
    libraryDependencies ++= compileDeps ++ testDeps("test") ++ testDeps("it"),
    dependencyOverrides ++= jettyOverrides,
    publishingSettings,
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
    routesImport ++= Seq("uk.gov.hmrc.nationaldutyrepaymentcenterstubs.binders.UrlBinders._")
  )
  .configs(IntegrationTest)
  .settings(
    Keys.fork in IntegrationTest := false,
    Defaults.itSettings,
    unmanagedSourceDirectories in IntegrationTest += baseDirectory(_ / "it").value,
    parallelExecution in IntegrationTest := false,
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    majorVersion := 0
  )
  .enablePlugins(
    PlayScala,
    SbtAutoBuildPlugin,
    SbtGitVersioning,
    SbtDistributablesPlugin,
    SbtArtifactory
  )


def oneForkedJvmPerTest(tests: Seq[TestDefinition]) =
  tests.map { test =>
    new Group(
      test.name,
      Seq(test),
      SubProcess(ForkOptions().withRunJVMOptions(Vector(s"-Dtest.name=${test.name}")))
    )
  }
