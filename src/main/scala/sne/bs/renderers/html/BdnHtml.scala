package sne.bs.renderers.html

import sne.bs.parser.{BsBdn, BsBdnElem, BsBdnFootnote}

object BdnHtml {

    private def renderIndex(fnIdx: Option[BsBdnFootnote]): String =
        fnIdx.map(fn => s"<sup>${fn.label}</sup>").getOrElse("")

    private def renderBid(be: BsBdnElem): String =
        s"<td>${InlineHtml.render(be.bid)}${renderIndex(be.footnote)}</td>"

    private def renderRound(bids: Seq[BsBdnElem]): String = {
        s"  <tr>${bids.map(renderBid).mkString(" ")}</tr>"
    }

    private def renderFootnoteRow(fn: BsBdnFootnote): String = {
        s"  <tr><td>&nbsp;<sup>${fn.label}</sup></td><td>${InlineHtml.render(fn.text)}</td></tr>"
    }

    def render(bdn: BsBdn): String = {
        
        val bidsTable =
            s"""<br/>
               |<table class="bidding">
               |${bdn.splitByRounds.map(renderRound).mkString("\n")}
               |</table>
               |""".stripMargin

        val footnotes = bdn.value.filter(_.footnote.isDefined).map(_.footnote.get)
        val footnoteTable =
            if (footnotes.isEmpty) ""
            else
                s"""<br/>
                   |<table>
                   |${footnotes.map(renderFootnoteRow).mkString("\n")}
                   |</table>
                   |""".stripMargin

        bidsTable + footnoteTable
    }
}
