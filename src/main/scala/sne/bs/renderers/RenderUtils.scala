package sne.bs.renderers

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

object RenderUtils {
    private[renderers] val autoLinkPattern = "\\&lt;(http[://|s://].+?)\\&gt;".r
    private[renderers] val strongPattern1 = "(?U)(?s)\\*\\*([\\w|(].*?)\\*\\*".r
    private[renderers] val strongPattern2 = "(?U)(?s)\\_\\_([\\w|(].*?)\\_\\_".r
    private[renderers] val emPattern1 = "(?U)(?s)\\*([\\w|(].*?)\\*".r
    private[renderers] val emPattern2 = "(?U)(?s)\\_([\\w|(].*?)\\_".r
    private[renderers] val refUserPattern = """\[(\w+)\]\[(.*?)\]""".r
    private[renderers] val strikePattern = "(?U)(?s)~~([\\w|(].*?)~~".r

    def applyFuns(s: String, funs: (String => String)*): String = funs.foldLeft(s)((x, f) => f(x))

    /**
     *  Convert ampersands (&) and angle brackets (< and >) into HTML entities.
     */
    def encodeHtmlChars(s: String): String = {
        s.replace("&", "&amp;")
          .replace("<", "&lt;")
          .replace(">", "&gt;")
    }

    /**
     * It's possible to trigger an ordered list by accident,
     * by writing something like this:
     *
     * 1986. What a great season.
     *
     * In other words, a number-period-space sequence at the beginning of a line.
     * To avoid this, you can backslash-escape the period:
     *
     * 1986\. What a great season.
     *
     * This function removes this backslash.
     */
    def escape(s: String): String = {
        s
          .replace("\\.", ".")
          .replace("\\*", "&#x002A;") // asterisk
    }

    private val ul_replacement = ":u0064:"
    private[renderers] def ul_encode(s: String): String = s.replace("_", ul_replacement)
    private[renderers] def ul_decode(s: String): String = s.replace(ul_replacement, "_")
    private[renderers] def ul_anchor_encode(s: String, anchorPattern: Regex): String = {
        val mapper = (m: Match) => Some(ul_encode(m.group(1)))
        anchorPattern.replaceSomeIn(s, mapper)
    }
}
