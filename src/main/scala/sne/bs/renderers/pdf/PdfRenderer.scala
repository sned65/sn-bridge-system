package sne.bs.renderers.pdf

import sne.bs.PdfConfig
import sne.bs.parser._
import sne.bs.renderers.Renderer

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, OutputStream}

class PdfRenderer(title: String, subtitle: String, bsDoc: BsDoc, pdfConfig: PdfConfig) extends Renderer {

    override val kind: String = "PDF"

    val foRenderer = new FoRenderer(title, subtitle, bsDoc, pdfConfig)
    def write(out: OutputStream): Unit = PdfRenderer.write(foRenderer, out, pdfConfig)
}

object PdfRenderer {

    def write(foRenderer: FoRenderer, out: OutputStream, pdfConfig: PdfConfig): Unit = {
        val fo_out = new ByteArrayOutputStream()
        foRenderer.write(fo_out)
        val inp = new ByteArrayInputStream(fo_out.toByteArray)
        FO2PDFConverter.convertFO2PDF(inp, out, pdfConfig)
    }
}