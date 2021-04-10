package sne.bs.renderers.pdf

import sne.bs.parser.BsCitation
import sne.bs.renderers.pdf.FOConstants._

object CitationFo {
    def render(x: BsCitation): String = {
        val lines = x.value.map(_.value).dropWhile(_.trim.isEmpty).map{s =>
            if (s.trim.isEmpty) s"$fo_block_e\n$fo_normal_text_s"
            else InlineFo.limitedRender(s)
        }
        s"""$fo_blockquote_s
           |$fo_normal_text_s
           |${lines.mkString("\n")}
           |$fo_block_e
           |$fo_block_e
           |""".stripMargin
    }

    private val fo_blockquote_s =
        s"""<fo:block font-size="${textSizeNormal}pt"
           |          font-family="Arial" font-style="italic"
           |          line-height="${lineHeightNormal}pt"
           |          space-after.optimum="3pt"
           |          background-color="#ddf1f1"
           |          margin-top="5mm" margin-bottom="5mm" margin-left="5mm"
           |          padding-left="1em"
           |          border-left-width="2mm" border-left-color="gray" border-left-style="solid"
           |          text-align="justify">
           |""".stripMargin
}
