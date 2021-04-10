package sne.bs.renderers.html

import sne.bs.BsException
import sne.bs.parser.BsHand

object HandHtml {
    private val suitLabels = Vector(
        "\\s", "\\h", "\\d", "\\c"
    ).map(InlineHtml.suits)

    def render(x: BsHand): String = {
        val title = x.title.map(t => s"<b>$t</b><br/>").getOrElse("")
        s"""<div class="w3-card w3-border" style="float: left; padding: 10px; margin-bottom: 10px; background-color: #ddf1f1;">
           |$title
           |${makeHand(x.hand)}
           |</div><p style="clear: both;"></p>
           |""".stripMargin
    }

    def makeHand(hand: String): String = {
        val suits = hand.split('.').map(_.toUpperCase).map { s =>
            if (s.isEmpty) "&ndash;"
            else s
        }
        if (suits.length != 4) throw BsException(s"Bad hand `$hand`")
        
        (suitLabels zip suits).map { case (l, s) => s"$l $s<br/>" }.mkString("\n")
    }

}
