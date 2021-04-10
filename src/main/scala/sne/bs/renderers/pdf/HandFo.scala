package sne.bs.renderers.pdf

import sne.bs.BsException
import sne.bs.parser.BsHand
import sne.bs.renderers.pdf.FOConstants._

object HandFo {

    def foTitle(title: String) = s"""$fo_hand_title$title$fo_block_e"""

    def render(x: BsHand): String = {
        val title = x.title.map(foTitle).getOrElse("")
        val titleWidth = x.title.map(_.length).getOrElse(0)
        val (handStr, handWidth) = makeHand(x.hand)
        // https://stackoverflow.com/questions/46248925/how-to-specify-an-indent-of-2ch-in-xml-fo-using-fop
        // "1em ... represents (even while using the fixed-width font monospace) a width about as wide as 1.5 characters"
        val width = ((handWidth + 2) max titleWidth) / 1.5
        s"""<fo:table table-layout="fixed" width="100%">
           |<fo:table-column column-width="${width}em"/>
           |<fo:table-body>
           |<fo:table-row>
           |<fo:table-cell>
           |$fo_hand_s$title$handStr
           |$fo_block_e
           |</fo:table-cell>
           |</fo:table-row>
           |</fo:table-body>
           |</fo:table>
           |""".stripMargin
    }
    
    private val suitLabels = Vector(
        "\\s", "\\h", "\\d", "\\c"
    ).map(InlineFo.suits)

    def makeHand(hand: String): (String, Int) = {
        val suits = hand.split('.').map(_.toUpperCase).map { s =>
            if (s.isEmpty) "-" // todo "&ndash;"
            else s
        }
        if (suits.length != 4) throw BsException(s"Bad hand `$hand`")

        val handStr = (suitLabels zip suits).map { case (l, s) => s"$l $s" }.mkString("\n")
        val width = suits.map(_.length).max
        
        (handStr, width)
    }

    private val fo_hand_s =
        s"""
           |<fo:block font-size="${textSizeNormal}pt"
           |          font-family="Courier New" font-weight="bold"
           |          text-align="start"
           |          border-width="1pt" border-color="black" border-style="solid"
           |          padding-start="1em" padding-end="1em" padding-after="1em"
           |          background-color="#ddf1f1"
           |          space-after.optimum="12pt"
           |          white-space-collapse="false"
           |          linefeed-treatment="preserve"
           |          white-space-treatment="preserve">
           |"""  // linefeed-treatment="preserve" => no newline
    //wrap-option="no-wrap">

    private val fo_hand_title =
        s"""<fo:block font-size="${textSizeNormal}pt"
           |          font-family="Arial" font-weight="bold"
           |          margin-bottom="5pt"
           |          space-after.optimum="0pt">""".stripMargin
}
