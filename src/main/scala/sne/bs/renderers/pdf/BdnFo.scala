package sne.bs.renderers.pdf

import sne.bs.parser.{BsBdn, BsBdnElem, BsBdnFootnote}
import sne.bs.renderers.pdf.FOConstants._

object BdnFo {

    private def renderIndex(fnIdx: Option[BsBdnFootnote]): String =
        fnIdx.map(fn => s"""<fo:inline font-size="50%" baseline-shift="super">${fn.label}</fo:inline>""").getOrElse("")

    private val fo_bid_s: String =
        s"""<fo:block font-size="${textSizeNormal}pt"
           |         font-family="Arial"
           |         line-height="${lineHeightNormal}pt"
           |         space-before.optimum="3pt"
           |         space-after.optimum="3pt"
           |         margin-left="0" margin-right="0"
           |         padding-left="0" padding-right="0"
           |         text-align="center">
        """.stripMargin

    private def renderBid(be: BsBdnElem): String =
        s"""<fo:table-cell>$fo_bid_s${InlineFo.render(be.bid)}${renderIndex(be.footnote)}$fo_block_e</fo:table-cell>"""

    private def renderRound(bids: Seq[BsBdnElem]): String = {
        s"  <fo:table-row>${bids.map(renderBid).mkString(" ")}</fo:table-row>"
    }

    private def renderFootnoteRow(fn: BsBdnFootnote): String = {
        s"  <fo:table-row><fo:table-cell>$fo_normal_text_s $nbsp${renderIndex(Some(fn))}</fo:block></fo:table-cell>" +
          s"<fo:table-cell>$fo_normal_text_s${InlineFo.render(fn.text)}</fo:block></fo:table-cell></fo:table-row>"
    }

    def render(bdn: BsBdn): String = {
        val bids = bdn.splitByRounds
        
        def bdnElemLength(b: BsBdnElem): Int = b.bid.length + {if (b.footnote.isDefined) 1 else 0}
        
        val table_width = bids.map(br => br.map(bdnElemLength).sum).max + 4

        val bidsTable =
            s"""<fo:table table-layout="fixed" width="${table_width}em" margin-left="0.5em" margin-right="0.5em" margin-bottom="10px" margin-top="20px" background-color="#ddf1f1" keep-together="always">
               |<fo:table-body>
               |${bids.map(renderRound).mkString("\n")}
               |</fo:table-body>
               |</fo:table>
               |""".stripMargin

        val footnotes = bdn.value.filter(_.footnote.isDefined).map(_.footnote.get)
        val footnoteTable =
            if (footnotes.isEmpty) ""
            else
                s"""<fo:block/>
                   |<fo:table table-layout="fixed" width="100%" margin-bottom="10px" margin-top="10px" border-collapse="separate">
                   |<fo:table-column column-number="1" column-width="3%"/>
                   |<fo:table-column column-number="2" column-width="97%"/>
                   |<fo:table-body>
                   |${footnotes.map(renderFootnoteRow).mkString("\n")}
                   |</fo:table-body>
                   |</fo:table>
                   |""".stripMargin

        bidsTable + footnoteTable
    }

}
