package sne.bs.renderers.pdf

object FOConstants {

    val nbsp = "&#160;"
    
    def getIntOrElse(name: String, default: Int): Int =
        default
        //if (config.has(FOConstants.prefix+name)) config.get[Int](FOConstants.prefix+name) else default

    val textSizeNormal: Int = getIntOrElse("textSizeNormal", 12)
    val lineHeightNormal: Int = getIntOrElse("lineHeightNormal", 15)

    val textSizeTitle: Int = getIntOrElse("textSizeTitle", 24)
    val lineHeightTitle: Int = getIntOrElse("lineHeightTitle", 45)

    val textSizeSubtitle: Int = getIntOrElse("textSizeSubtitle", 14)
    val lineHeightSubtitle: Int = getIntOrElse("lineHeightSubtitle", 15)

    val textSizeH1: Int = getIntOrElse("textSizeH1", 24)
    val lineHeightH1: Int = getIntOrElse("lineHeightH1", 24)

    val textSizeH2: Int = getIntOrElse("textSizeH2", 20)
    val lineHeightH2: Int = getIntOrElse("lineHeightH2", 20)

    val textSizeH3: Int = getIntOrElse("textSizeH3", 18)
    val lineHeightH3: Int = getIntOrElse("lineHeightH3", 18)

    val textSizeH4: Int = getIntOrElse("textSizeH4", 18)
    val lineHeightH4: Int = getIntOrElse("lineHeightH4", 18)

    val textSizeH5: Int = getIntOrElse("textSizeH5", 16)
    val lineHeightH5: Int = getIntOrElse("lineHeightH5", 16)

    val textSizeH6: Int = getIntOrElse("textSizeH6", 14)
    val lineHeightH6: Int = getIntOrElse("lineHeightH6", 14)

    ///////////////////////////////////////////////////////////////////////////

    val fo_root_s: String =
        """<?xml version="1.0" encoding="utf-8"?>
           |<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
           |""".stripMargin
    val fo_root_e = """</fo:root>"""

    val fo_layout_master_set: String =
        """
           |<fo:layout-master-set>
           |  <!-- fo:layout-master-set defines in its children the page layout:
           |         the pagination and layout specifications
           |         - page-masters: have the role of describing the intended subdivisions
           |                         of a page and the geometry of these subdivisions
           |                         In this case there is only a simple-page-master which defines the
           |                         layout for all pages of the text
           |   -->
           |   <!-- layout information WAS fo:region-body margin-top="2cm" margin-bottom="2cm" -->
           |   <fo:simple-page-master master-name="simple"
           |       page-height="29.7cm"
           |       page-width="21cm"
           |       margin-top="1cm"
           |       margin-bottom="1cm"
           |       margin-left="2.5cm"
           |       margin-right="2.5cm">
           |       <fo:region-body margin-top="1cm" margin-bottom="2cm"/>
           |       <fo:region-before extent="2cm"/>
           |       <fo:region-after extent="1cm"/>
           |   </fo:simple-page-master>
           |</fo:layout-master-set>
           |<!-- end: defines page layout -->
        """.stripMargin

    val fo_footer: String =
        """
           |      <fo:static-content flow-name="xsl-region-after">
           |        <fo:block text-align="center"
           |                  font-size="10pt"
           |                  font-family="Arial"
           |                  line-height="14pt">
           |          <fo:page-number/> / <fo:page-number-citation ref-id="end"/>
           |        </fo:block>
           |      </fo:static-content>
        """.stripMargin

    val fo_end = """<fo:block id="end"/>"""

    val fo_page_sequence_s = """<fo:page-sequence master-reference="simple">"""
    val fo_page_sequence_e = "</fo:page-sequence>"

    val fo_flow_s = """<fo:flow flow-name="xsl-region-body">"""
    val fo_flow_e = """</fo:flow>"""
    
    val fo_block_e = "</fo:block>"
    val fo_inline_e = "</fo:inline>"

    val fo_normal_text_s: String =
        s"""<fo:block font-size="${textSizeNormal}pt"
            |         font-family="Arial"
            |         line-height="${lineHeightNormal}pt"
            |         space-before.optimum="3pt"
            |         space-after.optimum="3pt"
            |         text-align="justify">
        """.stripMargin

    val fo_italic_text_s: String = s"""
      |<fo:block font-size="${textSizeNormal}pt"
      |          font-family="Arial"
      |          font-style="italic"
      |          line-height="${lineHeightNormal}pt"
      |          space-after.optimum="3pt"
      |          text-align="justify">
      |""".stripMargin

    val fo_bold_text_s: String = s"""
      |<fo:block font-size="${textSizeNormal}pt"
      |          font-family="Arial"
      |          font-weight="bold"
      |          line-height="${lineHeightNormal}pt"
      |          space-after.optimum="3pt"
      |          text-align="justify">
      |""".stripMargin

    val fo_inline_bold_text_s: String =
        s"""<fo:inline font-size="${textSizeNormal}pt"
            |          font-family="Arial"
            |          font-weight="bold"
            |          line-height="${lineHeightNormal}pt"
            |          space-after.optimum="3pt"
            |          text-align="justify">
            |""".stripMargin

    // for fo:inline quotes must not be on a separate line
    val fo_inline_italic_text_s: String =
        s"""<fo:inline font-size="${textSizeNormal}pt"
            |          font-family="Arial"
            |          font-style="italic"
            |          line-height="${lineHeightNormal}pt"
            |          space-after.optimum="3pt"
            |          text-align="justify">
            |""".stripMargin

    val fo_inline_italic_yellow_text_s: String =
        s"""<fo:inline font-size="${textSizeNormal}pt"
            |          font-family="Arial"
            |          font-style="italic"
            |          background-color="yellow"
            |          line-height="${lineHeightNormal}pt"
            |          space-after.optimum="3pt"
            |          text-align="justify">
            |""".stripMargin
    
    val fo_inline_strike_text_s: String =
        s"""<fo:inline font-size="${textSizeNormal}pt"
           |           font-family="Arial"
           |           line-height="${lineHeightNormal}pt"
           |           text-decoration="line-through">
           |""".stripMargin

    val fo_hr = """<fo:block text-align-last="justify"><fo:leader leader-pattern="rule" leader-length.maximum="100%"/></fo:block>"""

    val fo_line_break = "<fo:block/>"
    val fo_page_break = """<fo:block page-break-before="always"/>"""

    val fo_page_break_with_rule = """<fo:block page-break-before="always" text-align-last="justify"><fo:leader leader-pattern="rule" leader-length.maximum="100%"/></fo:block>"""
}
