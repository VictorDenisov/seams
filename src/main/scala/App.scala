package org.creativelabs

import java.io._

object ScalaApp {
    def main(args: Array[String]) {
        val fis = new FileInputStream(new File("Sample.java"))
        val line = fis.readLine()
        println("Hello world")
    }
}


// vim: set ts=4 sw=4 et:
