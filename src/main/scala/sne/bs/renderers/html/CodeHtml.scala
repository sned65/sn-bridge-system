package sne.bs.renderers.html

import sne.bs.parser.BsCode
import sne.bs.renderers.RenderUtils.encodeHtmlChars

object CodeHtml {
    def render(x: BsCode): String = {
        val lines = x.value.map(s => encodeHtmlChars(s.value))
        s"""<pre>
           |${lines.mkString("\n")}
           |</pre>
           |""".stripMargin
    }
}
