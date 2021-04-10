package sne.bs.renderers.pdf

import sne.bs.parser.BsCode
import sne.bs.renderers.RenderUtils.encodeHtmlChars
import sne.bs.renderers.pdf.FOConstants._

object CodeFo {
    def render(x: BsCode): String = {
        val text = x.value.map(_.value).mkString("\n")
        s"""$fo_code_s
           |<![CDATA[$text]]>
           |$fo_block_e
           |""".stripMargin
    }

    private val fo_code_s =
        """<fo:block font-size="10pt"
          |          font-family="Courier New"
          |          line-height="10pt"
          |          text-align="start"
          |          space-before.optimum="10pt"
          |          space-after.optimum="10pt"
          |          white-space-collapse="false"
          |          linefeed-treatment="preserve"
          |          white-space-treatment="preserve"
          |          wrap-option="no-wrap">"""   // linefeed-treatment="preserve" => no newline
}
