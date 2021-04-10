package sne.bs.renderers.pdf

import sne.bs.parser.{BsPar, BsPar0}
import sne.bs.renderers.pdf.FOConstants._

object ParFo {

    def render(par: BsPar): String = {
        s"""
           |$fo_normal_text_s
           |${InlineFo.render(par.singleLine)}
           |$fo_block_e
           |""".stripMargin
    }

    def renderMargin0(par0: BsPar0): String = {
        val par = BsPar(par0.value)
        render(par)
    }

}
