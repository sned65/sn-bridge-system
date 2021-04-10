package sne.bs.renderers.pdf

import sne.bs.{BsException, PdfConfig}
import sne.bs.parser._
import sne.bs.renderers.Renderer
import sne.bs.renderers.pdf.FOConstants._

import java.io.{ByteArrayOutputStream, OutputStream}
import java.nio.charset.StandardCharsets

class FoRenderer(title: String, subtitle: String, bsDoc: BsDoc, pdfConfig: PdfConfig) extends Renderer {

    override val kind: String = "FO"

    private val logLevel =
        pdfConfig.verbose match {
            case x if x >= 2 => java.util.logging.Level.INFO
            case 1 => java.util.logging.Level.WARNING
            case 0 => java.util.logging.Level.SEVERE
        }
    FoRenderer.foLogger.setLevel(logLevel)

    def write(out: OutputStream): Unit = FoRenderer.write(title, subtitle, bsDoc, out, pdfConfig)
}

object FoRenderer {

    private val foLogger = java.util.logging.Logger.getLogger("org.apache.fop.apps.FOUserAgent")
    
    private def write(out: OutputStream, s: String): Unit = out.write(s.getBytes(StandardCharsets.UTF_8))

    def elemsToString(elems: Seq[BsElem]): String = {
        elems.map((_, new ByteArrayOutputStream())).map {
            case (e, out) => write(e, out); out
        }.map(out => new String(out.toByteArray, StandardCharsets.UTF_8)).mkString("\n")
    }

    private def makeBookmarks(toc: TableOfContents): String = {
        
        val bms = toc.entries.map(renderFOBookmark).mkString("  ", "\n  ", "\n")
        s"""<fo:bookmark-tree>
           |$bms
           |</fo:bookmark-tree>
           |""".stripMargin
    }

    private def renderTOC(toc: TableOfContents): String = {
        toc.entries.map(renderTOCEntry).mkString("\n")
    }

    private def renderTOCEntry(tocEntry: TOCEntry): String = {
        val INDENT_STEP = 5
        val start_indent = INDENT_STEP * (tocEntry.level - 1)
        val titleFO = s"${tocEntry.label}   ${InlineFo.suits(tocEntry.title)}"

        s"""
           |<fo:block font-family="Arial" text-align-last="justify" start-indent="${start_indent}mm">
           |  <fo:basic-link internal-destination="${tocEntry.ref}">
           |    $titleFO
           |  </fo:basic-link>
           |  <fo:leader leader-length.minimum="12pt" leader-length.optimum="40pt" leader-length.maximum="100%" leader-pattern="dots">
           |  </fo:leader>
           |  <fo:page-number-citation ref-id="${tocEntry.ref}">
           |  </fo:page-number-citation>
           |</fo:block>
           |""".stripMargin
    }
    
    private def renderTitle(title: String, subtitle: String): String = {
        s"""<fo:block space-after="18pt" background-color="teal" color="white">
           |  <fo:block line-height="${lineHeightTitle}pt"
           |            font-family="Arial" font-size="${textSizeTitle}pt" font-weight="bold"
           |            text-align="center">
           |    $title
           |  </fo:block>
           |  <fo:block line-height="${lineHeightSubtitle}pt"
           |            font-family="Arial" font-size="${textSizeSubtitle}pt" font-style="italic"
           |            text-align="center">
           |    $subtitle
           |  </fo:block>
           |</fo:block>
           |""".stripMargin
    }

    private def renderFOBookmark(tocEntry: TOCEntry): String = {
        // The fo:bookmark-title is a specialized form of the fo:inline
        // with restrictions on the applicable properties and on its content model.
        val titleFO = s"""${tocEntry.label}   ${InlineFo.insertSuitSymbols(tocEntry.title)}"""
        s"""<fo:bookmark internal-destination="${tocEntry.ref}" starting-state="hide">
           |  <fo:bookmark-title>$titleFO</fo:bookmark-title>
           |</fo:bookmark>
           |""".stripMargin
    }

    def write(title: String, subtitle: String, doc: BsDoc, out: OutputStream, pdfConfig: PdfConfig): Unit = {
        val toc = doc.makeTOC
        val tocFO = renderTOC(toc)

        val bookmarks = makeBookmarks(toc)

        var idx_hdr = -1
        val enrichedElems = doc.elems.flatMap {
            case _: BsHdr => idx_hdr += 1; Seq(BsHdrAsTOCElem(toc(idx_hdr)))
            case _: BsArg if !pdfConfig.pdfArgFlag => Seq()
            case x => Seq(x)
        }
        
        write(out, fo_root_s)
        write(out, fo_layout_master_set)
        write(out, bookmarks)
        write(out, fo_page_sequence_s)
        write(out, fo_footer)
        write(out, fo_flow_s)
        write(out, renderTitle(title, subtitle))
        write(out, tocFO)

        enrichedElems.foreach(e => write(e, out))
        
        write(out, fo_end)
        write(out, fo_flow_e)
        write(out, fo_page_sequence_e)
        write(out, fo_root_e)
    }

    def write(e: BsElem, out: OutputStream): Unit = e match {
        case BsString(text) => write(out, text)
        case x: BsPar => write(out, ParFo.render(x))
        case x: BsPar0 => write(out, ParFo.renderMargin0(x))

        case x: BsUnorderedList => write(out, ListFo.renderUL(x))
        case x: BsOrderedList => write(out, ListFo.renderOL(x))

        case BsHdrAsTOCElem(hdr) => write(out, HeaderFo.render(hdr))

        case x: BsCitation => write(out, CitationFo.render(x))
        case x: BsCode => write(out, CodeFo.render(x))
        case _: BsHRule => write(out, HRuleFo.render)

        case x: BsHand => write(out, HandFo.render(x))
        case x: BsDeal => write(out, DealFo.render(x))
        case b: BsBdn => write(out, BdnFo.render(b))
        case b: BsBids => write(out, BidsFo.render(b))

        case a: BsArg => write(out, ArgFo.render(a))

        case t: BsTable => write(out, TableFo.render(t))

        case _: BsListItem =>
            throw BsException(s"FoRenderer. INTERNAL ERROR: BsListItem found on top level")
        case _: BsArgIndexed =>
            throw BsException(s"FoRenderer. INTERNAL ERROR: BsArgIndexed found on top level")
        case _: BsBdnElem | _: BsBdnFootnote =>
            throw BsException(s"FoRenderer. INTERNAL ERROR: internal element of BsBdn found on top level")
        case _: BsHdr =>
            throw BsException(s"FoRenderer. INTERNAL ERROR: internal element of BsHdrAsTOCElem found on top level")

        case x => write(out, ParFo.render(BsPar(Seq(BsString(s"NOT IMPLEMENTED ${x.getClass.getSimpleName}")))))
    }
}