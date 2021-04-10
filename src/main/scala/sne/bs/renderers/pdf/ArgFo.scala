package sne.bs.renderers.pdf

import sne.bs.parser._
import sne.bs.renderers.pdf.FoRenderer.{elemsToString, write}

object ArgFo {
    def render(a: BsArg): String = {
        val elems =
            if (a.title.startsWith("*") || a.title.startsWith("_"))
                BsPar(Seq(BsString(a.title))) +: a.elems
            else
                BsPar(Seq(BsString("*" + a.title + "*"))) +: a.elems

        elemsToString(elems)
    }
}
