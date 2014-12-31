package com.debugger87.textrank

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import scala.io.Source

/**
 * Created by yangchaozhong on 12/30/14.
 */

@RunWith(classOf[JUnit4])
class KeywordExtractorTest {

  @Test
  def testExtractKeyword = {
    (1 to 5).foreach {
      i =>
        val text1 = Source.fromURL(getClass.getResource(s"/text/${i}.txt")).getLines().mkString("\n")
        KeywordExtractor.extractKeywords(text1).foreach(println)
        println((1 to 30).map(i => "=").mkString)
    }
  }
}
