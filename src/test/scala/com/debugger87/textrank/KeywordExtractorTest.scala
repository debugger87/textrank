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
    val text1 = Source.fromURL(getClass.getResource("/text/3.txt")).getLines().mkString("\n")
    KeywordExtractor.extractKeywords(text1).foreach(println)
  }
}
