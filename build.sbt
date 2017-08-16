organization := "com.simplesys"

name := "jrebel-plugin"

version := "0.11.0-SNAPSHOT"
//version := "0.10.1"

sbtPlugin := true

publishTo := {
    val corporateRepo = "http://toucan.simplesys.lan/"
    if (isSnapshot.value)
        Some("snapshots" at corporateRepo + "artifactory/libs-snapshot-local")
    else
        Some("releases" at corporateRepo + "artifactory/libs-release-local")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

overridePublishSettings
