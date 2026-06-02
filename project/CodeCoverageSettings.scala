import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  private val coverageExcludedPackages: Seq[String] = Seq(
    "<empty>",
    "Reverse.*",
    ".*handlers.*",
    ".*components.*",
    ".*Routes.*",
    ".*controllers.testonly.*",
    ".*viewmodels.govuk.*",
    ".*viewmodels.ImplicitConversions",
    "testOnlyDoNotUseInAppConf.*",
    "pages.Page",
    "views.*",
    ".*\\$anon.*"
  )

  val settings: Seq[Setting[?]] = Seq(
    ScoverageKeys.coverageExcludedPackages := coverageExcludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
