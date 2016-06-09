name := "Grifball"

version := "1.0"

val lwjglVersion = "3.0.0"

lazy val `grifball` = (project in file("."))

libraryDependencies ++= Seq(
	"org.lwjgl" % "lwjgl" % lwjglVersion,
	"org.lwjgl" % "lwjgl-platform" % lwjglVersion classifier "natives-windows",
	"org.lwjgl" % "lwjgl-platform" % lwjglVersion classifier "natives-linux",
	"org.lwjgl" % "lwjgl-platform" % lwjglVersion classifier "natives-osx",
	"io.netty" % "netty-common" % "4.1.0.Final"
)

scalaVersion := "2.11.8"
    