package sne.bs.parser

import sne.bs.BsException

case class TOCLabel(label: Seq[Int]) {
    val labelString = label.mkString(".")
    val level = label.length

    def hrefLabel: String = label.map(h => f"$h%03d").mkString

    def next(atLevel: Int): TOCLabel = {
        if (atLevel == level) {
            val buf = label.toBuffer
            buf(buf.length-1) += 1
            TOCLabel(buf.toSeq)
        }
        else if (atLevel < level) {
            val buf = label.toBuffer
            buf(atLevel-1) += 1
            buf.takeInPlace(atLevel)
            TOCLabel(buf.toSeq)
        }
        else if (atLevel == level + 1) {
            TOCLabel(label :+ 1)
        }
        else {
            throw BsException(s"Can not calculate next header label: last is $labelString, required level $atLevel")
        }
    }

    override def toString: String = s"$labelString"
}

case class TOCEntry(label: TOCLabel, title: String, uRef: Option[String]) {
    def level: Int = label.level
    def ref: String = s"__RefHeading___Toc_${label.hrefLabel}"
    def userRef: Option[String] = uRef.map(TOCEntry.userReference)
    override def toString: String = s"$label $title"
}

object TOCEntry {
    def userReference(name: String) = s"__Ref_$name"
}

class TableOfContents(val entries: Vector[TOCEntry]) {

    def add(level: Int, title: String, userRef: Option[String]): TableOfContents = {
        val lastLabel = entries.lastOption.map(_.label).getOrElse(TOCLabel(Seq.empty))
        val e = TOCEntry(lastLabel.next(level), title, userRef)
        new TableOfContents(entries :+ e)
    }

    def apply(index: Int): TOCEntry = entries(index)
    
    def entriesWithUserRef: Vector[TOCEntry] = entries.filter(_.uRef.isDefined)

    override def toString: String = "Table Of Contents\n" + entries.mkString("\n")
}
