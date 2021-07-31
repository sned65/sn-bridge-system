package sne.bs.parser

import fastparse._
import sne.bs.BsException


sealed trait BsElem

object BsElem {

    def parseBsStrings(lines: Seq[BsString],
                       parser: P[_] => P[Seq[BsElem]],
                       errorMessageTitle: String): Seq[BsElem] = {
        val desc = lines.map(_.value).mkString("", "\n", "\n")
        parseText(desc, parser) match {
            case Left(msg) =>
                throw BsException(
                    s"""$errorMessageTitle:
                       |$msg
                       |=== Start of Text ===
                       |$desc
                       |=== End of Text ===
                       |""".stripMargin)
            case Right(elems) => elems
        }
    }

    def parseText(text: String, parser: P[_] => P[Seq[BsElem]]): Either[String, Seq[BsElem]] = {

        parse(text, parser) match {
            case Parsed.Success(value, _) => Right(value)
            case failure: Parsed.Failure => Left(failure.extra.trace().longMsg)
        }
    }
}

case class BsDoc(elems: Seq[BsElem]) {

    //println(s"BsDoc. Total of ${elems.length} elements:")
    //println(s"${elems.map(_.getClass.getSimpleName).mkString("BsDoc. ", "\nBsDoc. ", "\n")}")

    def makeTOC: TableOfContents = {
        val headers = elems
          .filter(_.isInstanceOf[BsHdr])
          .map(_.asInstanceOf[BsHdr])

        headers.foldLeft(new TableOfContents(Vector.empty)) {
            case (toc, h) => toc.add(h.level, h.title, h.reference)
        }
    }
}

case class BsString(value: String) extends BsElem {
    //if (value.endsWith("  ")) println(s"BsString($value)")
}

/**
 * Normal paragraph
 */
case class BsPar(value: Seq[BsString]) extends BsElem {
    //println(s"BsPar(\n${value.mkString("\n")}\n)")

    def singleLine: String = {
        value.map(_.value).map { s =>
            if (s.endsWith("  ")) s + "<br>"
            else s
        }.mkString(" ")
    }
}

/**
 * Paragraph with zero top/bottom margins
 */
case class BsPar0(value: Seq[BsString]) extends BsElem

case class BsHdr(level: Int, line: String) extends BsElem {
    private val line0 = BsHdr.dropTrailingHashes(line)
    val (title, reference) =
        if (line0.contains("\\ref")) {
            val BsHdr.hdrWithRef(t, r) = line0
            (t, Some(r))
        }
        else (line0, None)
}

object BsHdr {

    private val hdrWithRef = raw"""(.+?)\s*\\ref\s*\{\s*(\w+)\s*}\s*""".r
    private val trailingHashes = raw"""(.+?)\s*#*""".r

    def dropTrailingHashes(s: String): String = {
        val trailingHashes(ans) = s
        ans
    }
}

case class BsCitation(value: Seq[BsString]) extends BsElem {
    //println(s"${value.map(_.value).mkString("BsCitation:\n|", "|\n|", "|\nEnd of BsCitation")}")
}

case class BsCode(value: Seq[BsString]) extends BsElem {
    //println(s"${value.map(_.value).mkString("BsCode:\n|", "|\n|", "|\nEnd of BsCode")}")
}

case class BsHRule() extends BsElem

case class BsListItem(value: Seq[BsString]) extends BsElem {
    //println(s"${value.mkString("BsListItem:\n|", "|\n|", "|")}")

    def parsedItem: Seq[BsElem] =
        BsElem.parseBsStrings(
                value,
                GrammarList.makeParser(_),
                s"Error parsing list item")
}

case class BsUnorderedList(value: Seq[BsListItem]) extends BsElem
case class BsOrderedList(value: Seq[BsListItem]) extends BsElem

case class BsHdrAsTOCElem(tocEntry: TOCEntry) extends BsElem

case class BsTableDef(value: String) extends BsElem {
    //println(s"BsTableDef($value)")

    val (nColumns, columnAlignment) = {
        val cols = value.stripSuffix("|").split('|')
        val alignments = cols.map(_.trim).map { c =>
            import BsTable._
            if (c.length < 3) throw BsException(s"Each column’s header of a table must contain at least three hyphens (---): `$value`")
            var al = ALIGN_NONE
            if (c.startsWith(":")) al |= ALIGN_LEFT
            if (c.endsWith(":")) al |= ALIGN_RIGHT
            al
        }
        (alignments.length, alignments.toVector)
    }
}

case class BsTable(tableDef: BsTableDef, header: Option[BsString], rows: Seq[BsString]) extends BsElem {
    val nColumns: Int = tableDef.nColumns
    val columnAlignment: Vector[Int] = tableDef.columnAlignment
    val headerCells: Option[Vector[String]] = header.map(r => splitRowOnCells(r.value))
    val cells: Vector[Vector[String]] = rows.map(r => splitRowOnCells(r.value)).toVector
    //println(s"BsTable. nColumns=$nColumns, nRows=${cells.length}, header=${header.isDefined}")
    
    private def splitRowOnCells(row: String): Vector[String] = {
        row.stripPrefix("|").stripSuffix("|").split('|').map(_.trim).toVector    
    }
}
object BsTable {
    val ALIGN_NONE = 0
    val ALIGN_LEFT = 1
    val ALIGN_RIGHT = 2
    val ALIGN_CENTER: Int = ALIGN_LEFT | ALIGN_RIGHT
}

///////////////////
// Bridge elements
///////////////////

case class BsHand(hand: String, title: Option[String]) extends BsElem
case class BsDeal(deal: Map[String, String]) extends BsElem

case class BsBdnFootnote(label: String, text: String) extends BsElem {
    //println(s"BsBdnFootnote($label -> $text)")
}
case class BsBdnElem(bid: String, footnote: Option[BsBdnFootnote], nlFlag: Boolean) extends BsElem {
    //println(s"BsBdnElem($bid -> $footnote; $nlFlag)")
}

case class BsBdn(value: Seq[BsBdnElem]) extends BsElem {
    //println(s"BsBdn(${value.mkString(", ")})")
    def splitByRounds: Seq[Seq[BsBdnElem]] = {
        val rows = value.tail.foldLeft(Seq(Seq(value.head)))((s, e) => {
            val seq = s.last :+ e
            val updated = s.dropRight(1) :+ seq
            if (e.nlFlag) updated :+ Seq.empty[BsBdnElem]
            else updated
        })
        
        if (rows.endsWith(Seq.empty[BsBdnElem])) rows.dropRight(1)
        else rows
    }
}

case class BsBids(value: Seq[(String, Seq[BsString])]) extends BsElem {
    
    //println(s"$this")
    
    val parsedDescription: Seq[(String, Seq[BsElem])] =
        for ((b, descLines) <- value) yield
            (b, BsElem.parseBsStrings(
                descLines,
                GrammarBids.makeParser(_),
                s"Error parsing description of bid `$b`")
            )

    override def toString: String = {
        (for ((b, desc) <- value) yield s"$b -> ${desc.map(_.value).mkString("    |", "|\n    |", "|")}").mkString("BsBids{\n", "\n", "\n}\n")
    }
}

case class BsArg(title: String, elems: Seq[BsElem]) extends BsElem
case class BsArgIndexed(arg: BsArg, index: Int) extends BsElem
