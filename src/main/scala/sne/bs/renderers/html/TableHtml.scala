package sne.bs.renderers.html

import sne.bs.parser.BsTable

object TableHtml {
    
    private def makeCell(cell: String, cellTag: String, alignment: Int): String = {
        import BsTable._
        val align = alignment match {
            case ALIGN_LEFT => " class=\"bs-table-align-left\""
            case ALIGN_RIGHT => " class=\"bs-table-align-right\""
            case ALIGN_CENTER => " class=\"bs-table-align-center\""
            case _ => ""
        }
        s"  <$cellTag$align>${InlineHtml.render(cell)}</$cellTag>"
    }
    
    private def makeRow(row: Vector[(String, Int)], cellTag: String): String = {
        s"""<tr>
           |${row.map(c => makeCell(c._1, cellTag, c._2)).mkString(" ")}
           |</tr>
           |""".stripMargin
    }
    
    def render(t: BsTable): String = {
        s"""
           |<table class="bs-table">
           |  ${t.headerCells.map(_ zip t.columnAlignment).map(r => makeRow(row = r, cellTag = "th")).getOrElse("")}
           |  ${t.cells.map(r => r zip t.columnAlignment).map(r => makeRow(row = r, cellTag = "td")).mkString("\n")}
           |</table>
           |""".stripMargin
    }

}
