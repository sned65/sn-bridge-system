package sne.bs.renderers.pdf

import sne.bs.parser.TOCEntry
import sne.bs.renderers.pdf.FOConstants._

object HeaderFo {

    def render(hdr: TOCEntry): String = {
        def uRefAnchor(ur: String) = s"""<fo:block id="$ur"></fo:block>"""

        val br = if (hdr.level == 1) fo_page_break_with_rule else ""
        
        val ur = hdr.userRef.map(uRefAnchor).getOrElse("")
        
        val hdrText = s"""${hdr.label.labelString}   ${InlineFo.render(hdr.title)}"""
        val header = formatHeader(hdr.level)(hdr.ref, ur + hdrText)
        
        br + header
    }

    private def formatHeader(headerLevel: Int) =
        headerLevel match {
            case 1 => formatH1 _
            case 2 => formatH2 _
            case 3 => formatH3 _
            case 4 => formatH4 _
            case 5 => formatH5 _
            case 6 => formatH6 _
            case _ => formatH6 _
        }

    private def formatH1(id: String, header: String) =
        s"""
            |<fo:block id="$id"
            |          font-size="${textSizeH1}pt"
            |          font-family="Arial"
            |          font-weight="bold"
            |          line-height="${lineHeightH1}pt"
            |          space-after="20pt"
            |          keep-with-next="always">
            |  $header
            |</fo:block>
            |""".stripMargin

    private def formatH2(id: String, header: String) =
        s"""
            |<fo:block id="$id"
            |          font-size="${textSizeH2}pt"
            |          font-family="Arial"
            |          font-weight="bold"
            |          margin-top="0.5cm"
            |          line-height="${lineHeightH2}pt"
            |          space-after="18pt"
            |          keep-with-next="always">
            |  $header
            |</fo:block>
            |""".stripMargin

    private def formatH3(id: String, header: String) =
        s"""
            |<fo:block id="$id"
            |          font-size="${textSizeH3}pt"
            |          font-family="Arial"
            |          font-weight="bold"
            |          margin-top="0.5cm"
            |          line-height="${lineHeightH3}pt"
            |          space-after="14pt"
            |          keep-with-next="always">
            |  $header
            |</fo:block>
            |""".stripMargin

    private def formatH4(id: String, header: String) =
        s"""
            |<fo:block id="$id"
            |          font-size="${textSizeH4}pt"
            |          font-family="Arial"
            |          font-style="italic"
            |          margin-top="0.5cm"
            |          line-height="${lineHeightH4}pt"
            |          space-after="12pt"
            |          text-decoration="underline"
            |          keep-with-next="always">
            |  $header
            |</fo:block>
            |""".stripMargin

    private def formatH5(id: String, header: String) =
        s"""
            |<fo:block id="$id"
            |          font-size="${textSizeH5}pt"
            |          font-family="Arial"
            |          margin-top="0.5cm"
            |          line-height="${lineHeightH5}pt"
            |          space-after="12pt"
            |          text-decoration="underline"
            |          keep-with-next="always">
            |  $header
            |</fo:block>
            |""".stripMargin

    private def formatH6(id: String, header: String) =
        s"""
            |<fo:block id="$id"
            |          font-size="${textSizeH6}pt"
            |          font-family="Arial"
            |          font-style="italic"
            |          margin-top="0.5cm"
            |          line-height="${lineHeightH6}pt"
            |          space-after="12pt"
            |          text-decoration="underline"
            |          keep-with-next="always">
            |  $header
            |</fo:block>
            |""".stripMargin
}
