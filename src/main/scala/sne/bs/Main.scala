package sne.bs

import sne.bs.parser.BsParser
import sne.bs.renderers.html.HtmlRenderer
import sne.bs.renderers.pdf.{FoRenderer, PdfRenderer}

object Main {
    def main(args: Array[String]): Unit = {

        try {
            run(args)
        }
        catch {
            case e: BsException =>
                println(s"${e.getMessage}")
                if (e.verbose) e.printStackTrace()
                sys.exit(1)
        }
    }

    def run(args: Array[String]): Unit = {

        val config =
            Config.parseArgs(args) match {
                case Some(cfg) => cfg
                case _ =>
                    // arguments are bad, error message will have been displayed
                    throw BsException("Arguments are bad")
            }
        if (config.verbose >= 1) println(s"$config")

        BsParser(config.bsFile).parseBS match {
            case Right(bsDoc) =>
                if (config.htmlFlag) {
                    val renderer = new HtmlRenderer(config.title, config.subtitle, bsDoc)
                    renderer.write(config.outDir, config.htmlFilename)
                }
                if (config.foFlag) {
                    val renderer = new FoRenderer(config.title, config.subtitle, bsDoc, config.pdfConfig)
                    renderer.write(config.outDir, config.foFilename)
                }
                if (config.pdfFlag) {
                    val renderer = new PdfRenderer(config.title, config.subtitle, bsDoc, config.pdfConfig)
                    renderer.write(config.outDir, config.pdfFilename)
                }
            case Left(err) => throw BsException(err)
        }
    }
}
