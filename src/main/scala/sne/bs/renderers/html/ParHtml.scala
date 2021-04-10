package sne.bs.renderers.html

import sne.bs.parser.{BsPar, BsPar0}

object ParHtml {
    def render(par: BsPar): String = {
        s"""
           |<p>
           |${InlineHtml.render(par.singleLine)}
           |</p>
           |""".stripMargin
    }

    def renderMargin0(par0: BsPar0): String = {
        val par = BsPar(par0.value)
        s"""
           |<p style="margin-top: 0; margin-bottom: 0;">
           |${InlineHtml.render(par.singleLine)}
           |</p>
           |""".stripMargin
    }
}
