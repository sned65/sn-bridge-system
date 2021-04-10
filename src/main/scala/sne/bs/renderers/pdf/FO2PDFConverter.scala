package sne.bs.renderers.pdf

import org.apache.fop.apps.{FopFactory, PageSequenceResults}
import org.apache.xmlgraphics.util.MimeConstants
import sne.bs.PdfConfig

import java.io.{ByteArrayInputStream, File, InputStream, OutputStream}
import java.nio.charset.StandardCharsets
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamSource

object FO2PDFConverter {

    /**
     * Converts an FO stream to a PDF stream using FOP
     *
     * @param inp      the source stream containing the FO data
     * @param out      the target stream containing the PDF data
     * @param pdfConfig FO/PDF configuration parameters
     */
    def convertFO2PDF(inp: InputStream, out: OutputStream, pdfConfig: PdfConfig): Unit = {

        // Construct a FopFactory by specifying a reference to the configuration file
        // (reuse if you plan to render multiple documents!)
        val fop_xconf = pdfConfig.pdfFontDir.map(fopXConf).getOrElse(fopXConfDefault)
        if (pdfConfig.verbose >= 2) println(s"*** fop.xconf ***\n$fop_xconf")
        val is = new ByteArrayInputStream(fop_xconf.getBytes(StandardCharsets.UTF_8))
        val fopFactory = FopFactory.newInstance(new File(".").toURI, is)

        val foUserAgent = fopFactory.newFOUserAgent()

        // Construct fop with desired output format
        val fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out)

        // Setup input stream
        val src = new StreamSource(inp)

        // Setup JAXP using identity transformer
        val factory = TransformerFactory.newInstance()
        val transformer = factory.newTransformer() // identity transformer

        // Resulting SAX events (the generated FO) must be piped through to FOP
        val res = new SAXResult(fop.getDefaultHandler)

        // Start XSLT transformation and FOP processing
        transformer.transform(src, res)

        // Result processing
        val foResults = fop.getResults
        val pageSequences = foResults.getPageSequences

        val it = pageSequences.iterator
        while (it.hasNext)
        {
            val pageSequenceResults: PageSequenceResults = it.next().asInstanceOf[PageSequenceResults]
            //Logger.debug(s"PageSequence ${if (String.valueOf(pageSequenceResults.getID).length > 0) pageSequenceResults.getID else "<no id>"} generated ${pageSequenceResults.getPageCount} pages.")
        }

        println(s"Generated ${foResults.getPageCount} PDF pages in total.")
    }

    /** Default Apache FOP configuration XML
     */
    private val fopXConfDefault: String =
        s"""<fop version="1.0">
           |	<renderers>
           |		<renderer mime="application/pdf">
           |			<fonts>
           |
           |				<!-- automatically detect operating system installed fonts -->
           |				<auto-detect/>
           |
           |			</fonts>
           |		</renderer>
           |	</renderers>
           |</fop>
           |""".stripMargin

    /**
     * Generate Apache FOP configuration XML
     * registering all the fonts found in a specified directory
     * and all of its sub directories to be embedded into the PDF.
     * 
     * @param fontDir a directory with fonts
     * @return FOP configuration XML
     */
    private def fopXConf(fontDir: String): String =
        s"""<fop version="1.0">
           |	<renderers>
           |		<renderer mime="application/pdf">
           |			<fonts>
           |
           |				<!-- automatically detect operating system installed fonts -->
           |				<auto-detect/>
           |
           |				<!-- register all the fonts found in a directory and all of its sub directories -->
           |				<directory recursive="true">$fontDir</directory>
           |			</fonts>
           |		</renderer>
           |	</renderers>
           |</fop>
           |""".stripMargin
}