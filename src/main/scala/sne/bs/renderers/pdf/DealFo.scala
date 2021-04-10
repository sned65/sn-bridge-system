package sne.bs.renderers.pdf

import sne.bs.parser.BsDeal
import sne.bs.renderers.pdf.FOConstants._

object DealFo {

    private val empty_cell = s"<fo:table-cell><fo:block>$nbsp</fo:block></fo:table-cell>"


    // Maybe table cell can contain only 3 lines,
    // maybe 'linefeed-treatment="preserve"' sometime does not work (in table cell).
    // I don't understand.
    // Workaround: I artificially add the empty block after diamonds. 
    private def correctHand(hand: String): String = {
        val suits = hand.split('\n')
        suits(2) += "<fo:block/>"
        suits.mkString("\n")
    }

    private def makeHandNS(x: BsDeal, player: String): (String, Int) = {
        if (x.deal.contains(player)) {
            val hand = x.deal(player)
            val (handStr, width) = HandFo.makeHand(hand)
            val foHand =
                s"""  <fo:table-row>
                   |    $empty_cell<fo:table-cell>$fo_hand_s${correctHand(handStr)}
                   |    </fo:block></fo:table-cell>$empty_cell
                   |  </fo:table-row>
                   |""".stripMargin
            (foHand, width)
        }
        else ("", 0)
    }

    def makeHandEW(x: BsDeal): (String, Int, Int) = {
        val handWest =
            if (x.deal.contains("W")) {
                val (westHand, westWidth) = HandFo.makeHand(x.deal("W"))
                (s"""$fo_hand_s${correctHand(westHand)}</fo:block>""", westWidth)
            }
            else ("<fo:block/>", 0)

        val handEast =
            if (x.deal.contains("E")) {
                val (eastHand, eastWidth) = HandFo.makeHand(x.deal("E"))
                (s"""$fo_hand_s${correctHand(eastHand)}</fo:block>""", eastWidth)
            }
            else ("<fo:block/>", 0)
        
        val foWE = s"    <fo:table-cell>${handWest._1}</fo:table-cell> $empty_cell <fo:table-cell>${handEast._1}</fo:table-cell>"
        (foWE, handWest._2, handEast._2)
    }

    def render(x: BsDeal): String = {
        val (foN, widthN) = makeHandNS(x, "N")
        val (foS, widthS) = makeHandNS(x, "S")
        val (foWE, widthW, widthE) = makeHandEW(x)
        val wNS = ((widthN max widthS) + 2)
        val wW = (widthW + 2)
        val wE = (widthE + 2)
        val totalWidth = wW + wNS + wE
        s"""<fo:table table-layout="fixed" width="${totalWidth}em"
           |          margin-left="10px" margin-right="10px" margin-bottom="10px" margin-top="20px"
           |          keep-together="always" background-color="#ddf1f1"
           |          border-width="1pt" border-color="black" border-style="solid">
           |<fo:table-column column-width="${wW}em"/>
           |<fo:table-column column-width="${wNS}em"/>
           |<fo:table-column column-width="${wE}em"/>
           |<fo:table-body>
           |$foN
           |  <fo:table-row>
           |$foWE
           |  </fo:table-row>
           |$foS
           |</fo:table-body>
           |</fo:table>
           |""".stripMargin
    }

    private val fo_hand_s =
        s"""
           |<fo:block font-size="${textSizeNormal}pt"
           |          font-family="Courier New" font-weight="bold"
           |          text-align="start"
           |          padding-start="1em" padding-end="1em"
           |          space-after.optimum="12pt"
           |          white-space-collapse="false"
           |          linefeed-treatment="preserve"
           |          white-space-treatment="preserve">
           |"""  // linefeed-treatment="preserve" => no newline
}
