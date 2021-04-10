package sne.bs.renderers.pdf

import sne.bs.parser.{BsBids, BsElem}
import sne.bs.renderers.pdf.FOConstants._

object BidsFo {
    
    private val bg_odd = "#e0e0e0"
    private val bg_even = "#ffffee"

    private def makeRow(bid: String, descLines: Seq[BsElem], even: Boolean): String = {
        val desc = FoRenderer.elemsToString(descLines)
        val bg =
            if (even) s"""background-color="$bg_even" """
            else      s"""background-color="$bg_odd" """
        s"""<fo:table-row $bg>
           |  ${fo_bids_cell(InlineFo.render(bid), "left")}
           |  ${fo_bids_cell(desc, "justify")}
           |</fo:table-row>
           |""".stripMargin
    }

    def render(b: BsBids): String = {
        val rows =
            b.parsedDescription.zipWithIndex.map{
                case ((b, elems), idx) => makeRow(b, elems, idx%2 == 0)
            }.mkString("\n")
        s"""
           |<fo:table table-layout="fixed" width="100%" margin-bottom="10px" margin-top="10px" border-collapse="separate">
           |    <fo:table-column column-number="1" column-width="20%"/>
           |    <fo:table-column column-number="2" column-width="80%"/>
           |    <fo:table-body>
           |  $rows
           |    </fo:table-body>
           |</fo:table>
           |""".stripMargin
    }
    
    private def fo_bids_cell(s: String, align: String) =
        s"""<fo:table-cell padding-top="1mm" padding-bottom="1mm" padding-right="1em">
           |<fo:block font-size="${textSizeNormal}pt"
           |          font-family="Arial"
           |          line-height="${lineHeightNormal}pt"
           |          space-before.optimum="3pt"
           |          space-after.optimum="3pt"
           |          text-align="$align">$s</fo:block>
           |</fo:table-cell>
           |""".stripMargin
}
