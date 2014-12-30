package com.debugger87.textrank

import org.graphstream.graph.{Edge, Node}

import scala.collection.mutable

/**
 * Created by yangchaozhong on 12/30/14.
 */
object KeywordExtractor {

  def extractKeywords(doc: String) = {
    val graph = new TextGraph("keywords", doc).graph
    val nodes = graph.getNodeSet.toArray.map(_.asInstanceOf[Node])
    val scoreMap = new mutable.HashMap[String, Float]()

    // Initialization
    nodes.foreach(node => scoreMap.put(node.getId, 1.0f))

    // Iteration
    (1 to 50).foreach {
      i =>
        nodes.foreach {
          node =>
            val edges = node.getEdgeSet.toArray.map(_.asInstanceOf[Edge])
            var score = 1.0f - 0.85f
            edges.foreach {
              edge =>
                val node0 = edge.getNode0.asInstanceOf[Node]
                val node1 = edge.getNode1.asInstanceOf[Node]
                val tempNode = if (node0.getId.equals(node.getId)) node1 else node0
                score += 0.85f * (1.0f * scoreMap(tempNode.getId) / tempNode.getDegree)
            }
            scoreMap.put(node.getId, score)
        }
    }

    scoreMap.toList.sortWith(_._2 > _._2).slice(0, 20).map(_._1)
  }
}
