package sne.bs.renderers.html

import sne.bs.parser.TOCEntry

object HeaderHtml {
    def render(hdr: TOCEntry): String = {
        def uRefAnchor(ur: String) = s"""<a id="$ur"></a>"""
        s"""
           |<h${hdr.level}>
           |  <a id="${hdr.ref}"></a>
           |  ${hdr.userRef.map(uRefAnchor).getOrElse("")}
           |  ${hdr.label.labelString}&nbsp;&nbsp;&nbsp;${InlineHtml.render(hdr.title)}
           |</h${hdr.level}>
           |""".stripMargin
    }
}
