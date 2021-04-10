package sne.bs.renderers.html

import sne.bs.parser.{BsListItem, BsOrderedList, BsUnorderedList}

object ListHtml {

    private def renderItem(item: BsListItem): String = {
        item.value.length match {
            case 0 =>
                s"<li></li>\n"
            case 1 =>
                s"  <li>${InlineHtml.render(item.value.head.value)}</li>\n"
            case _ =>
                val content = HtmlRenderer.elemsToString(item.parsedItem)
                s"  <li>\n$content</li>\n"
        }
    }

    def renderUL(list: BsUnorderedList): String = {
        s"""<ul>
           |${list.value.map(renderItem).mkString}
           |</ul>
           |""".stripMargin
    }

    def renderULMargin0(list: BsUnorderedList): String = {
        s"""<ul style="margin-top: 0; margin-bottom: 0;">
           |${list.value.map(renderItem).mkString}
           |</ul>
           |""".stripMargin
    }

    def renderOL(list: BsOrderedList): String = {
        s"""<ol>
           |${list.value.map(renderItem).mkString}
           |</ol>
           |""".stripMargin
    }

    def renderOLMargin0(list: BsOrderedList): String = {
        s"""<ol style="margin-top: 0; margin-bottom: 0;">
           |${list.value.map(renderItem).mkString}
           |</ol>
           |""".stripMargin
    }
}
