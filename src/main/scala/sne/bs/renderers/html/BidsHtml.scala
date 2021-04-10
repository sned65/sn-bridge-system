package sne.bs.renderers.html

import sne.bs.parser.{BsBids, BsElem, BsString}

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

object BidsHtml {
    
    private def makeRow(row: (String, Seq[BsElem])): String = {
        val (bid, descLines) = row
        val desc = HtmlRenderer.elemsToString(descLines)
        s"""<tr>
           |  <td>${InlineHtml.render(bid)}</td>
           |  <td>$desc</td>
           |</tr>
           |""".stripMargin
    }
    
    def render(b: BsBids): String = {
        s"""<br/>
           |<table class="bids">
           |  ${b.parsedDescription.map(makeRow).mkString("\n")}
           |</table>
           |""".stripMargin
    }

}
