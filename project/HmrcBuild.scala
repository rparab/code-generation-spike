import sbt.Keys._
import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning


object HmrcBuild extends Build {

  import BuildDependencies._
  import uk.gov.hmrc.DefaultBuildSettings._

  val appName = "code-generator"

  lazy val CodeGenerator = Project(appName, file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning)
    .settings(
      targetJvm := "jvm-1.8",
      libraryDependencies ++= Seq(
        Test.scalaTest,
        Test.pegdown,
        Compile.playConfig,
        Compile.playHealth,
        Compile.hmrcTest
      ),
      Developers()
    )
}

private object BuildDependencies {

  object Compile {
    val playConfig = "uk.gov.hmrc" %% "play-config" % "2.0.0"
    val playHealth = "uk.gov.hmrc" %% "play-health" % "1.1.0"
    val hmrcTest = "uk.gov.hmrc" %% "hmrctest" % "0.3.0"
  }

  sealed abstract class Test(scope: String) {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % scope
    val pegdown = "org.pegdown" % "pegdown" % "1.5.0" % scope
  }

  object Test extends Test("test")

}

object Developers {

  def apply() = developers := List[Developer]()
}
