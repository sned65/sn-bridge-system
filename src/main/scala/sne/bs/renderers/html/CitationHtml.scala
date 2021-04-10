package sne.bs.renderers.html

import sne.bs.parser.BsCitation

object CitationHtml {
    def render(x: BsCitation): String = {
        val lines = x.value.map(_.value).dropWhile(_.trim.isEmpty).map{s =>
            if (s.trim.isEmpty) "</p><p>"
            else InlineHtml.limitedRender(s)
        }
        s"""<blockquote class="w3-panel w3-leftbar w3-light-grey">
           |<p>
           |${lines.mkString("\n")}
           |</p>
           |</blockquote>
           |""".stripMargin
    }
}
