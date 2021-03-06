package com.simplesys.jrebel

import sbt.Keys._
import sbt._

import scala.xml._

object JRebelPlugin extends AutoPlugin {
  object jrebel {
    val classpath = SettingKey[Seq[File]]("jrebel-classpath")
    val enabled = SettingKey[Boolean]("jrebel-enabled")
    val rebelXml = SettingKey[File]("jrebel-rebel-xml")
    val webLinks = SettingKey[Seq[File]]("jrebel-web-links")
  }

  val jrebelGenerate = TaskKey[Seq[File]]("jrebel-generate")

  val jrebelSettings: Seq[Def.Setting[_]] = Seq[Setting[_]](
    jrebel.classpath <<= Seq(Keys.classDirectory in Compile, Keys.classDirectory in Test).join,
    jrebel.enabled := (java.lang.Package.getPackage("com.zeroturnaround.javarebel") != null),
    jrebel.rebelXml <<= (resourceManaged in Compile) { _ / "rebel.xml" },
    jrebel.webLinks := Seq(),
    jrebelGenerate <<= rebelXmlTask,
    resourceGenerators in Compile <+= jrebelGenerate
  )

  private def dirXml(dir: File):NodeSeq = <dir name={ dir.absolutePath } />

  private def webLinkXml(link: File):NodeSeq =
    <web>
      <link>{dirXml(link)}</link>
    </web>

  private def rebelXmlTask: Def.Initialize[Task[Seq[File]]] =
    (jrebel.enabled, jrebel.classpath, jrebel.rebelXml, jrebel.webLinks, state) map {
      (enabled, classpath, rebelXml, webLinks, state) =>
        if (!enabled) Nil
        else {
          val xml =
            <application xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.zeroturnaround.com" xsi:schemaLocation="http://www.zeroturnaround.com/alderaan/rebel-2_0.xsd">
              <classpath>{classpath.map(dirXml) }</classpath>
              {webLinks.map(webLinkXml)}
            </application>

          IO.touch(rebelXml)
          XML.save(rebelXml.absolutePath, xml, "UTF-8", true)

          state.log.info("Wrote rebel.xml to %s".format(rebelXml.absolutePath))

          rebelXml :: Nil
        }
    }
}
