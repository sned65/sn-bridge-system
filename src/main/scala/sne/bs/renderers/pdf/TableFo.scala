package sne.bs.renderers.pdf

import sne.bs.parser.BsTable
import sne.bs.renderers.pdf.FOConstants._

object TableFo {

    private def makeCell(cell: String, cellTag: String, alignment: Int): String = {
        import BsTable._
        val align = alignment match {
            case ALIGN_LEFT => " left"
            case ALIGN_RIGHT => " right"
            case ALIGN_CENTER => " center"
            case _ => if (cellTag == "th") " center" else " left"
        }
        s"  ${fo_table_cell(InlineFo.render(cell), align, cellTag == "th")}"
    }

    private def fo_table_cell(s: String, align: String, isHeader: Boolean) = {
        val weight =
            if (isHeader) "font-weight=\"bold\""
            else ""
            
        s"""<fo:table-cell padding-top="1mm" padding-bottom="1mm" padding-left="0.5em" padding-right="0.5em"
           |               border-width="thin" border-style="solid">
           |<fo:block font-size="${textSizeNormal}pt" $weight
           |          font-family="Arial"
           |          line-height="${lineHeightNormal}pt"
           |          space-before.optimum="3pt"
           |          space-after.optimum="3pt"
           |          text-align="$align">$s</fo:block>
           |</fo:table-cell>
           |""".stripMargin
    }

    private def makeRow(row: Vector[(String, Int)], cellTag: String): String = {
        s"""<fo:table-row>
           |${row.map(c => makeCell(c._1, cellTag, c._2)).mkString(" ")}
           |</fo:table-row>
           |""".stripMargin
    }

    def render(t: BsTable): String = {
        val columnWidth = 100 / t.nColumns
        val lastColumnWidth = 100 - columnWidth * (t.nColumns - 1)
        val columns = (
          {
            for (i <- 1 until t.nColumns) yield s"""<fo:table-column column-number="$i" column-width="$columnWidth%"/>"""
        } :+ s"""<fo:table-column column-number="${t.nColumns}" column-width="$lastColumnWidth%"/>"""
          ).mkString("\n")
        
        val headerRow = t.headerCells
          .map(_ zip t.columnAlignment)
          .map(r => makeRow(row = r, cellTag = "th"))
          .map { h =>
              s"""<fo:table-header>
                 |  $h
                 |</fo:table-header>""".stripMargin
          }.getOrElse("")
        
        val rows = t.cells
          .map(_ zip t.columnAlignment)
          .map(r => makeRow(row = r, cellTag = "td"))
          .mkString("\n")
        
        s"""
           |<fo:table table-layout="fixed" width="100%" margin-bottom="10px" margin-top="10px"
           |   border-collapse="collapse" border-width="medium" border-style="solid">
           |    $columns
           |  $headerRow
           |    <fo:table-body>
           |  $rows
           |    </fo:table-body>
           |</fo:table>
           |""".stripMargin
    }
}
