package sne.bs.renderers.html

import sne.bs.parser.BsDeal

object DealHtml {

    private def makeHandNS(x: BsDeal, player: String): String = {
        if (x.deal.contains(player)) {
            val hand = x.deal(player)
            val htmlHand = HandHtml.makeHand(hand)
            s"""  <tr>
               |    <td></td><td>$htmlHand</td><td></td>
               |  </tr>
               |""".stripMargin
        }
        else ""
    }

    def makeHandEW(x: BsDeal): String = {
        val htmlWest =
            if (x.deal.contains("W")) HandHtml.makeHand(x.deal("W"))
            else ""
        
        val htmlEast =
            if (x.deal.contains("E")) HandHtml.makeHand(x.deal("E"))
            else ""
        
        val htmlCenter = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        //val htmlCenter = s"""   <td><img src="${compass}" height="64" width="64"></td>"""
        
        s"    <td>$htmlWest</td> <td>$htmlCenter</td> <td>$htmlEast</td>"
    }

    def render(x: BsDeal): String = {
        s"""<div class="w3-card w3-border" style="float: left; padding: 10px; margin-bottom: 10px; background-color: #ddf1f1;">
           |<table class="bs-deal">
           |${makeHandNS(x, "N")}
           |  <tr>
           |${makeHandEW(x)}
           |  </tr>
           |${makeHandNS(x, "S")}
           |</table>
           |</div><p style="clear: both;"></p>
           |""".stripMargin
    }
}
