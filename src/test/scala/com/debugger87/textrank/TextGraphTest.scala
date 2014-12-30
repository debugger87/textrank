package com.debugger87.textrank

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(classOf[JUnit4])
class TextGraphTest {

  @Test
  def testTextGraph = {
    val textGraph = new TextGraph("hello", "中国有什么好玩的地方？黄山也许是中国最美的地方。")
    textGraph.graph.getEdgeSet.toArray.foreach(println)

    assert(textGraph.graph.getEdgeSet.size() > 0)
  }
}