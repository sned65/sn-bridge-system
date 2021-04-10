package sne.bs.renderers.pdf

import sne.bs.parser.TOCEntry
import sne.bs.renderers.RenderUtils._
import sne.bs.renderers.pdf.FOConstants._

import scala.util.matching.Regex

object InlineFo {

    def render(s: String): String = {
        applyFuns(s,
            encodeHtmlChars,
            autoLink,
            lineBreak,
            escape,
            suits,
            emphasis,
            references
        )
    }

    def limitedRender(s: String): String = {
        applyFuns(s,
            suits
        )
    }

    def autoLink(s: String): String =
        autoLinkPattern.replaceAllIn(s, """<fo:basic-link external-destination="$1">$1</fo:basic-link>""")

    def lineBreak(s: String): String = {
        val s_br = s.replace("&lt;br&gt;", fo_line_break)
        if (s_br.endsWith("  ")) s_br + fo_line_break
        else s_br
    }

    def suits(s: String): String = {
        s
          .replace("\\c", "♣")
          .replace("\\s", "♠")
          .replace("♦", """<fo:character character="♦" color="red"/>""")
          .replace("\\d", """<fo:character character="♦" color="red"/>""")
          .replace("♥", """<fo:character character="♥" color="red"/>""")
          .replace("\\h", """<fo:character character="♥" color="red"/>""")
    }
    
    def insertSuitSymbols(s: String): String = {
        s
          .replace("\\c", "♣")
          .replace("\\s", "♠")
          .replace("\\d", "♦")
          .replace("\\h", "♥")
    }

    
    private val anchorPattern: Regex = "(<fo:basic-link external-destination=.+?</fo:basic-link>)".r
    /**
     * The same as `emphasis0()` but ignores anchors `<a>`
     * because anchors can contain underscores.
     */
    private def emphasis(s: String): String = {
        val encoded = ul_anchor_encode(s, anchorPattern)
        ul_decode(emphasis0(encoded))
    }

    private def emphasis0(s: String): String = {
        applyFuns(s,
            strong1, strong2, em1, em2, strike
        )
    }

    private def strong1(s: String): String = strongPattern1.replaceAllIn(s, fo_inline_bold_text_s + "$1" + fo_inline_e)
    private def strong2(s: String): String = strongPattern2.replaceAllIn(s, fo_inline_bold_text_s + "$1" + fo_inline_e)
    private def em1(s: String): String = emPattern1.replaceAllIn(s, fo_inline_italic_text_s + "$1" + fo_inline_e)
    private def em2(s: String): String = emPattern2.replaceAllIn(s, fo_inline_italic_yellow_text_s + "$1" + fo_inline_e)
    private def strike(s: String): String = strikePattern.replaceAllIn(s, fo_inline_strike_text_s + "$1" + fo_inline_e)

    private def references(s: String): String = {

        def replacer(m: Regex.Match): String = {
            val ur = m.group(1)
            val uRef = TOCEntry.userReference(ur)
            val text =
                if (m.group(2).isEmpty) ur
                else m.group(2)
            s"""<fo:basic-link internal-destination="$uRef">$text</fo:basic-link>"""
        }

        refUserPattern.replaceAllIn(s, replacer _)
    }
}
