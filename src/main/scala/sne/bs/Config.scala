package sne.bs

import os.Path
import scopt.OParser
import sne.bs.renderers.html.HtmlRenderer

import java.awt.Desktop
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class PdfConfig(
                      pdfArgFlag: Boolean = true,
                      pdfFontDir: Option[String] = None,
                      pdfParamsFile: Option[String] = None,
                      verbose: Int = 0  // duplicated for convenience
                    ) {

    override def toString: String = {
        s"""PdfConfig(
           |    pdfArgFlag    = $pdfArgFlag
           |    pdfFontDir    = ${pdfFontDir.getOrElse("")}
           |    pdfParamsFile = ${pdfParamsFile.getOrElse("")}
           |    verbose       = $verbose
           |)""".stripMargin
    }
}

case class Config(
                   bsFile: File = new File("."),
                   titleOpt: Option[String] = None,
                   subtitleOpt: Option[String] = None,
                   htmlFlag: Boolean = false,
                   pdfFlag: Boolean = false,
                   pdfConfig: PdfConfig = PdfConfig(),
                   foFlag: Boolean = false,
                   outDir: Path = os.pwd,
                   verbose: Int = 0
                 ) {

    val basename: String = bsFile.getName.stripSuffix(".bs")
    val htmlFilename: String = basename + ".html"
    val pdfFilename: String = basename + ".pdf"
    val foFilename: String = basename + ".fo"

    val title: String = titleOpt.getOrElse(basename)
    val subtitle: String = subtitleOpt.getOrElse(Config.today)

    override def toString: String = {
        s"""Config(
           |    bsFile        = $bsFile
           |    title         = ${titleOpt.getOrElse("")}
           |    subtitle      = ${subtitleOpt.getOrElse("")}
           |    htmlFlag      = $htmlFlag
           |    pdfFlag       = $pdfFlag
           |    $pdfConfig
           |    foFlag        = $foFlag
           |    outDir        = $outDir
           |    verbose       = $verbose
           |)""".stripMargin
    }
}

object Config {

    val specificationTitle: String = Option(getClass.getPackage.getSpecificationTitle).getOrElse("BridgeSystem")
    val specificationVersion: String = Option(getClass.getPackage.getSpecificationVersion).getOrElse("DEV")

    private val builder = OParser.builder[Config]

    // OParser does not support processing of two "helps".
    // That's why this option is treated separately.
    private val HELP_BS_SYNTAX = "help-bs-syntax"
    private val HELP_BS_SYNTAX_DIR = "bs-syntax"

    private val parser = {
        import builder._
        OParser.sequence(
            programName(specificationTitle),
            head(specificationTitle, specificationVersion),
            help("help").text("prints this usage text"),
            opt[Unit](HELP_BS_SYNTAX)
              .text("complete description of the bs-syntax"),
            arg[File]("<bs-file>")
              .action((x, c) => c.copy(bsFile = x))
              .text("bs-file")
              .validate { f =>
                  if (f.canRead) success
                  else failure(s"Can not read bs-file ${f.getPath}")
              },
            opt[String]('t', "title")
              .action((t, c) => c.copy(titleOpt = Some(t)))
              .text("title. The default is bs-file name."),
            opt[String]('s', "subtitle")
              .action((t, c) => c.copy(subtitleOpt = Some(t)))
              .text("subtitle. The default is current date."),
            opt[Unit]("pdf")
              .action((_, c) => c.copy(pdfFlag = true))
              .text("generate pdf-file"),
            opt[Unit]("pdf-short")
              .action((_, c) =>
                  c.copy(pdfFlag = true, pdfConfig = c.pdfConfig.copy(pdfArgFlag = false))
              )
              .text("generate pdf-file without \\arg{} blocks"),
            opt[String]("pdf-font-dir")
              .action((x, c) =>
                  c.copy(pdfConfig = c.pdfConfig.copy(pdfFontDir = Some(x)))
              )
              .text("register all the fonts found in a directory and all of its sub directories"),
            // todo
            opt[String]("pdf-params")
              .action((x, c) =>
                  c.copy(pdfConfig = c.pdfConfig.copy(pdfParamsFile = Some(x)))
              )
              .hidden()
              .text("file with fine-tuning parameters for generating a pdf file"),
            opt[Unit]("html")
              .action((_, c) => c.copy(htmlFlag = true))
              .text("generate html-file"),
            opt[Unit]("fo")
              .action((_, c) => c.copy(foFlag = true))
              .hidden()
              .text("generate fo-file"),
            opt[Unit]("fo-short")
              .action((_, c) =>
                  c.copy(foFlag = true, pdfConfig = c.pdfConfig.copy(pdfArgFlag = false))
              )
              .hidden()
              .text("generate fo-file without \\arg{} blocks"),
            opt[Int]("verbose")
              .action((x, c) => c.copy(pdfConfig = c.pdfConfig.copy(verbose = x)))
              .action((x, c) => c.copy(verbose = x))
              .hidden()
              .text("verbosity level"),
            opt[String]('o', "out")
              .action((x, c) => c.copy(outDir = Path(x, base = os.pwd)))
              .text("output directory. Default is current working directory."),
        )
    }

    def parseArgs(args: Array[String]): Option[Config] = {
        val bsSyntax = s"--$HELP_BS_SYNTAX"
        if (args.contains(bsSyntax)) { browseBsSyntax(); sys.exit() }
        else OParser.parse(parser, args, Config())
    }

    /**
     * Generates bs-syntax web page and opens the
     * default browser to the generated web page.
     */
    def browseBsSyntax(): Unit = {
        val desktop =
            if (Desktop.isDesktopSupported) {
                val dt = Desktop.getDesktop
                if (dt.isSupported(Desktop.Action.BROWSE)) Some(dt)
                else None
            }
            else None

        val syntaxUri = HtmlRenderer.createResources(os.pwd/HELP_BS_SYNTAX_DIR)
        desktop.foreach(_.browse(syntaxUri))
    }

    def today: String = {
        DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())
    }
}