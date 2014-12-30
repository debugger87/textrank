package com.debugger87.textrank

import java.io.StringReader
import java.text.BreakIterator
import java.util.Locale

import akka.event.slf4j.SLF4JLogging
import edu.stanford.nlp.process.{CoreLabelTokenFactory, PTBTokenizer}
import edu.stanford.nlp.tagger.maxent.MaxentTagger
import org.ansj.domain.Term
import org.ansj.splitWord.analysis.ToAnalysis
import org.ansj.util.recognition.NatureRecognition
import org.graphstream.graph.implementations.SingleGraph

import scala.collection.mutable
import scala.io.Source

/**
 * Created by yangchaozhong on 12/30/14.
 */
class TextGraph(val graphName: String,
                val doc: String) extends SLF4JLogging {
  val graph = new SingleGraph(graphName)
  val tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger")
  val stopwords = Source.fromURL(getClass.getResource("/stopwords/stopwords-en.txt")).
    getLines().toSet

  if (containsChinese(doc)) {
    constructTextGraph(true)
  } else {
    constructTextGraph(false)
  }

  private def constructTextGraph(isChinese: Boolean) = {
    val bi = BreakIterator.getSentenceInstance(Locale.ENGLISH)
    bi.setText(doc)
    var lastIndex = bi.first()
    while (lastIndex != BreakIterator.DONE) {
      val firstIndex = lastIndex
      lastIndex = bi.next()

      if (lastIndex != BreakIterator.DONE &&
          Character.isLetterOrDigit(doc.charAt(firstIndex))) {
        val sentence = doc.substring(firstIndex, lastIndex)
        var wordSet: mutable.HashSet[String] = mutable.HashSet.empty
        if (isChinese) {
          wordSet = chinesWordSet(sentence)
        } else {
          wordSet = englishWordSet(sentence)
        }

        val wordList = wordSet.toList
        wordList foreach {
          word => if (graph.getNode(word) == null) graph.addNode(word)
        }

        wordList.combinations(2).toList foreach {
          words =>
            if (graph.getEdge(s"${words(0)}-${words(1)}") == null &&
                graph.getEdge(s"${words(1)}-${words(0)}") == null) {
              graph.addEdge(s"${words(0)}-${words(1)}", words(0), words(1))
              None
            }
        }
      }
    }
  }

  private def chinesWordSet(sentence: String) = {
    val terms = ToAnalysis.paser(sentence)
    new NatureRecognition(terms).recogntion()
    val wordSet = new mutable.HashSet[String]()
    terms.toArray.foreach {
      term =>
        val word = term.asInstanceOf[Term].getName
        val nature = term.asInstanceOf[Term].getNatrue.natureStr
        if (!(nature == "null") && word.length >= 2) {
          val reg = "^[ne]".r
          if (reg.findFirstMatchIn(nature).isDefined && !stopwords.contains(word))
            wordSet.add(word.toLowerCase)
        }
    }

    wordSet
  }

  private def englishWordSet(sentence: String) = {
    val ptbt = new PTBTokenizer(new StringReader(sentence), new CoreLabelTokenFactory, "")
    val wordSet = new mutable.HashSet[String]()
    while (ptbt.hasNext) {
      val label = ptbt.next()
      val tagged = tagger.tagString(label.word())
      val start = tagged.lastIndexOf("_") + 1
      val reg = "^[N]".r
      if (reg.findFirstMatchIn(tagged.substring(start)).isDefined &&
          !stopwords.contains(label.word()) &&
          label.word().length >= 2)
        wordSet.add(label.word())
    }

    wordSet
  }

  private def containsChinese(doc: String) = {
    doc.count {
      word =>
        val ub = Character.UnicodeBlock.of(word)
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
            ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
            ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
            ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
            ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
            ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
            ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
          true
        } else false
    } > 0
  }
}