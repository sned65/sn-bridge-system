package sne.bs.renderers.pdf

import sne.bs.parser.{BsListItem, BsOrderedList, BsUnorderedList}
import sne.bs.renderers.pdf.FOConstants._

object ListFo {
    private val fo_li_label_bullet = fo_li_label("&#x2022;")

    private def fo_li_label(label: String): String =
        s"""<fo:list-item-label start-indent="4.5mm" end-indent="label-end()">
           |  <fo:block text-align="right"><fo:inline font-family="Arial">$label</fo:inline></fo:block>
           |</fo:list-item-label>
           |""".stripMargin

    private def renderULItem(item: BsListItem): String = {
        renderItem(fo_li_label_bullet)(item)
    }

    private def renderOLItem(itemNumber: Int, item: BsListItem): String = {
        val label = fo_li_label(s"$itemNumber.")
        renderItem(label)(item)
    }
    
    private def renderItem(label: String)(item: BsListItem): String = {
        item.value.length match {
            case 0 =>
                s"""
                   |  <fo:list-item>
                   |  <fo:list-item-body start-indent="body-start()">
                   |  </fo:list-item-body>
                   |  </fo:list-item>
                   |""".stripMargin
            case 1 =>
                s"""
                   |  <fo:list-item>
                   |    $label<fo:list-item-body start-indent="body-start()">
                   |    $fo_normal_text_s
                   |    ${InlineFo.render(item.value.head.value)}
                   |    $fo_block_e
                   |    </fo:list-item-body>
                   |  </fo:list-item>
                   |""".stripMargin
            case _ =>
                val content = FoRenderer.elemsToString(item.parsedItem)
                s"""
                   |  <fo:list-item>
                   |    $label<fo:list-item-body start-indent="body-start()">
                   |    $content
                   |    </fo:list-item-body>
                   |  </fo:list-item>
                   |""".stripMargin
        }
    }

    def renderUL(list: BsUnorderedList): String = {
        s"""
           |<fo:list-block>
           |${list.value.map(renderULItem).mkString}
           |</fo:list-block>
           |
           |""".stripMargin
    }

    def renderULMargin0(list: BsUnorderedList): String = {
        //todo s"""<ul style="margin-top: 0; margin-bottom: 0;">
        s"""<fo:list-block>
           |${list.value.map(renderULItem).mkString}
           |</fo:list-block>
           |""".stripMargin
    }

    def renderOL(list: BsOrderedList): String = {
        val indexedValues = list.value.zipWithIndex
        s"""<fo:list-block provisional-distance-between-starts="3em">
           |${indexedValues.map(iv => renderOLItem(iv._2+1, iv._1)).mkString}
           |</fo:list-block>
           |""".stripMargin
    }

    def renderOLMargin0(list: BsOrderedList): String = {
        //todo s"""<ol style="margin-top: 0; margin-bottom: 0;">
        s"""<fo:list-block provisional-distance-between-starts="3em">
           |${list.value.map(renderULItem).mkString}
           |</fo:list-block>
           |""".stripMargin
    }

}
