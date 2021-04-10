package sne.bs.renderers.html

import os.{Path, SubPath}
import sne.bs.BsException
import sne.bs.parser._
import sne.bs.renderers.Renderer

import java.io.{ByteArrayOutputStream, OutputStream}
import java.nio.charset.StandardCharsets

class HtmlRenderer(title: String, subtitle: String, bsDoc: BsDoc) extends Renderer {

    override val kind: String = "HTML"
    
    def write(out: OutputStream): Unit = HtmlRenderer.write(title, subtitle, bsDoc, out)

    override def write(outDir: Path, filename: String): Unit = {
        super.write(outDir, filename)
        HtmlRenderer.createResources(outDir)
    }
}

object HtmlRenderer {

    val resourceDirectory = "."
    val CSS_MAIN = "main.css"
    val CSS_W3 = "w3.css"
    val ICON = "spades-24.png"
    val JS_MAIN = "main.js"
    val HELP_DIR = "help"
    val HELP_BS = "SystemFormatting_v2.html"

    val sp_css_main: SubPath = os.sub/"stylesheets"/CSS_MAIN
    val sp_css_w3: SubPath = os.sub/"stylesheets"/CSS_W3
    val sp_icon: SubPath = os.sub/"images"/ICON
    val sp_js_main: SubPath = os.sub/"javascripts"/JS_MAIN
    val sp_help_bs: SubPath = os.sub/HELP_DIR/HELP_BS

    def copyResource(from: SubPath, outDir: Path): Unit = {
        os.write.over(outDir/from, (os.resource/from).toSource, createFolders = true, truncate = true)
    }

    def copyHelp(outDir: Path): java.net.URI = {
        val out = outDir/sp_help_bs
        os.write.over(out, helpBegin("BS Formatting"), createFolders = true, truncate = true)
        val src = (os.resource/sp_help_bs).toSource
        os.write.append(out, src)
        os.write.append(out, helpEnd)
        out.wrapped.toUri
    }

    def createResources(outDir: Path): java.net.URI = {
        copyResource(sp_css_main, outDir)
        copyResource(sp_css_w3, outDir)
        copyResource(sp_icon, outDir)
        copyResource(sp_js_main, outDir)
        copyHelp(outDir)
    }


    def elemsToString(elems: Seq[BsElem]): String = {
        elems.map((_, new ByteArrayOutputStream())).map {
            case (e, out) => write(e, out); out
        }.map(out => new String(out.toByteArray, StandardCharsets.UTF_8)).mkString("\n")
    }


    private def write(out: OutputStream, s: String): Unit = out.write(s.getBytes(StandardCharsets.UTF_8))

    private def renderTOC(toc: TableOfContents): String = {
        val MARGIN_STEP = 0.5

        val htmlTOC =
            toc.entries.map { e =>
                val margin = MARGIN_STEP * (e.label.level - 1)
                val hdr = s"${e.label.labelString}&nbsp;&nbsp;&nbsp;${InlineHtml.render(e.title)}"
                if (e.label.level == 1) {
                    s"""<a href="#${e.ref}" class="w3-bar-item w3-button" style="font-weight: bold; margin-left: ${margin}cm;">$hdr</a>"""
                }
                else {
                    s"""<a href="#${e.ref}" class="w3-bar-item w3-button" style="padding-top: 0px; padding-bottom: 0px; margin-left: ${margin}cm;">$hdr</a>"""
                }
            }.mkString("", "\n", "<p></p>\n")
        
        val refTitle = """<span class="w3-bar-item" style="font-weight: bold;">References</span>"""
        val urefs = toc.entriesWithUserRef
        val htmlURef =
            if (urefs.isEmpty) ""
            else {
                urefs.map { e =>
                    val hdr = s"${e.label.labelString}&nbsp;&nbsp;&nbsp;${InlineHtml.render(e.title)}"
                    s"""<a href="#${e.ref}" class="w3-bar-item w3-button" style="padding-top: 0px; padding-bottom: 0px;">$hdr</a>"""
                }.mkString(s"\n$refTitle\n", "\n", "<p></p>\n")
            }
        
        htmlTOC + htmlURef
    }

    def write(title: String, subtitle: String, doc: BsDoc, out: OutputStream): Unit = {
        val toc = doc.makeTOC
        val tocHtml = renderTOC(toc)

        var idx_hdr = -1
        var idx_arg = 0
        val enrichedElems = doc.elems.map {
            case _: BsHdr => idx_hdr += 1; BsHdrAsTOCElem(toc(idx_hdr))
            case a: BsArg => idx_arg += 1; BsArgIndexed(a, idx_arg)
            case x => x
        }

        write(out, begin(title, resourceDirectory))
        write(out, sideBar("Содержание", tocHtml))
        write(out, beginPageMainContent(title, subtitle, "Formatting"))

        enrichedElems.foreach(e => write(e, out))

        write(out, endPageMainContent)
        write(out, end(resourceDirectory))
    }

    def write(e: BsElem, out: OutputStream): Unit = e match {
        case BsString(text) => write(out, text)
        case x: BsPar => write(out, ParHtml.render(x))
        case x: BsPar0 => write(out, ParHtml.renderMargin0(x))

        case x: BsUnorderedList => write(out, ListHtml.renderUL(x))
        case x: BsOrderedList => write(out, ListHtml.renderOL(x))

        case BsHdrAsTOCElem(hdr) => write(out, HeaderHtml.render(hdr))
            
        case x: BsCitation => write(out, CitationHtml.render(x))
        case x: BsCode => write(out, CodeHtml.render(x))
        case _: BsHRule => write(out, HRuleHtml.render)

        case x: BsHand => write(out, HandHtml.render(x))
        case x: BsDeal => write(out, DealHtml.render(x))

        case b: BsBdn => write(out, BdnHtml.render(b))
        case b: BsBids => write(out, BidsHtml.render(b))
            
        case x: BsArgIndexed => write(out, ArgHtml.render(x))

        case t: BsTable => write(out, TableHtml.render(t))
            
        case _: BsArg =>
            throw BsException(s"INTERNAL ERROR: internal element of BsArgIndexed found on top level")
        case _: BsBdnElem | _: BsBdnFootnote =>
            throw BsException(s"INTERNAL ERROR: internal element of BsBdn found on top level")
        case _: BsHdr =>
            throw BsException(s"INTERNAL ERROR: internal element of BsHdrAsTOCElem found on top level")
        case _: BsListItem =>
            throw BsException(s"INTERNAL ERROR: BsListItem found on top level")

        case x => write(out, ParHtml.render(BsPar(Seq(BsString(s"NOT IMPLEMENTED ${x.getClass.getSimpleName}")))))
    }

    def begin(title: String, dir: String): String =
        s"""<!DOCTYPE html>
           |<html lang="en">
           |    <head>
           |        <title>$title</title>
           |        <meta charset="utf-8">
           |        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
           |
           |        <!-- W3.CSS -->
           |        <link rel="stylesheet" href="$dir/stylesheets/$CSS_W3">
           |
           |		<!-- Font Awesome CDN -->
           |		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.6.3/css/all.css" integrity="sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/" crossorigin="anonymous">
           |		<!--  script src="$dir/javascripts/fontawesome-all.js" type="text/javascript"></script> -->
           |
           |        <link rel="stylesheet" media="screen" href="$dir/stylesheets/$CSS_MAIN">
           |        <link rel="shortcut icon" type="image/png" href="$dir/images/$ICON">
           |
           |    </head>
           |    <body>
           |
           |""".stripMargin

    def end(dir: String): String =
        s"""      <script src="$dir/javascripts/$JS_MAIN" type="text/javascript"></script>
           |    </body>
           |</html>
           |
           |""".stripMargin

    def sideBar(contentTitle: String, content: Any): String =
        s"""<!-- Sidebar -->
           |
           |<div class="w3-sidebar w3-light-grey w3-bar-block bs-sidebar-width" id="contentSidebar">
           |  <button class="w3-bar-item w3-button w3-large"
           |  		onclick="w3_close()">$contentTitle &times;</button>
           |
           |  $content
           |</div>
           |
           |""".stripMargin

    def beginPageMainContent(title: String, subtitle: String,
                             helpTooltip: String): String =
        s"""
           |<!-- Page Main Content -->
           |
           |<div class="bs-content-margin" id="main">
           |
           |  <div class="w3-bar w3-teal w3-padding-16">
           |    <button id="openNav" class="w3-bar-item w3-button w3-teal w3-round-medium w3-large" style="display:none"
           |            onclick="w3_open()">&#9776;</button>
           |    <div class="w3-bar-item">
           |      <span class="w3-xlarge">$title</span> <br/>
           |      <span class="w3-medium">$subtitle</span>
           |    </div>
           |
           |    <div class="bs-has-tooltip w3-right">
           |      <a href="./$HELP_DIR/$HELP_BS"
           |         target="_blank"
           |	     class="w3-bar-item w3-button w3-round-medium">
           |	     <i class="far fa-question-circle"></i></a>
           |	  <span class="bs-tooltip-text-left">$helpTooltip</span>
           |    </div>
           |
           |  </div>
           |
           |  <div class="w3-container">
           |""".stripMargin

    def endPageMainContent: String =
        s"""
           |  </div>
           |
           |</div>
           |
           |<script>
           |function w3_open() {
           |  document.getElementById("main").style.marginLeft = "25%";
           |  document.getElementById("contentSidebar").style.width = "25%";
           |  document.getElementById("contentSidebar").style.display = "block";
           |  document.getElementById("openNav").style.display = 'none';
           |}
           |function w3_close() {
           |  document.getElementById("main").style.marginLeft = "0%";
           |  document.getElementById("contentSidebar").style.display = "none";
           |  document.getElementById("openNav").style.display = "inline-block";
           |}
           |</script>
           |""".stripMargin

    def helpBegin(title: String): String =
        s"""<!DOCTYPE html>
           |<html lang="en">
           |    <head>
           |        <title>$title</title>
           |        <meta charset="utf-8">
           |        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
           |
           |        <!-- W3.CSS -->
           |        <link rel="stylesheet" href="../stylesheets/$CSS_W3">
           |
           |		<!-- Font Awesome CDN -->
           |		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.6.3/css/all.css" integrity="sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/" crossorigin="anonymous">
           |		<!--  script src="../javascripts/fontawesome-all.js" type="text/javascript"></script> -->
           |
           |        <link rel="stylesheet" media="screen" href="../stylesheets/$CSS_MAIN">
           |        <link rel="shortcut icon" type="image/png" href="../images/$ICON">
           |
           |    </head>
           |    <body>
           |      <div class="w3-main w3-container" style="padding-bottom:20px;">
           |""".stripMargin

    def helpEnd: String =
        s"""      </div>
           |      <script src="../javascripts/$JS_MAIN" type="text/javascript"></script>
           |    </body>
           |</html>
           |
           |""".stripMargin
}