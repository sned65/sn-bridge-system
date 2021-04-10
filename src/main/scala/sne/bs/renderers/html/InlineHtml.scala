package sne.bs.renderers.html

import sne.bs.parser.TOCEntry
import sne.bs.renderers.RenderUtils._

import scala.util.matching.Regex

object InlineHtml {

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

    /**
     *  Avoid cross-site scripting (XSS) attack replacing <script> tag.
     */
    def xss(s: String): String = s.replace("<script", "&lt;script")

    def autoLink(s: String): String =
        autoLinkPattern.replaceAllIn(s, """<a href="$1" target="_blank">$1</a>""")

    def lineBreak(s: String): String = {
        val s_br = s.replace("&lt;br&gt;", "<br/>")
        if (s_br.endsWith("  ")) s_br + "<br/>"
        else s_br
    }

    def suits(s: String): String = s
      .replace("\\c", "♣")
      .replace("\\s", "♠")
      .replace("♦", """<span style="color: red">♦&#xfe0e;</span>""")
      .replace("\\d", """<span style="color: red">♦&#xfe0e;</span>""")
      .replace("♥", """<span style="color: red">♥&#xfe0e;</span>""")
      .replace("\\h", """<span style="color: red">♥&#xfe0e;</span>""")

    private val anchorPattern: Regex = "(<a href=.+?</a>)".r
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

    private def strong1(s: String): String = strongPattern1.replaceAllIn(s, "<strong>$1</strong>")
    private def strong2(s: String): String = strongPattern2.replaceAllIn(s, "<strong>$1</strong>")
    private def em1(s: String): String = emPattern1.replaceAllIn(s, "<em>$1</em>")
    private def em2(s: String): String = emPattern2.replaceAllIn(s, "<em class=\"yellow-bg\">$1</em>")
    private def strike(s: String): String = strikePattern.replaceAllIn(s, "<s>$1</s>")

    private def references(s: String): String = {
        
        def replacer(m: Regex.Match): String = {
            val ur = m.group(1)
            val uRef = TOCEntry.userReference(ur)
            val text =
                if (m.group(2).isEmpty) ur
                else m.group(2)
            s"""<a href="#$uRef">$text</a>"""
        }
        
        refUserPattern.replaceAllIn(s, replacer _)
    }
}
