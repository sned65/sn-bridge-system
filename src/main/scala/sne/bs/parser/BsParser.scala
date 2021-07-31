package sne.bs.parser

import fastparse._

import java.io.File

class BsParser(text: String) {

    def parseBS: Either[String, BsDoc] = {
        def parser[_: P] = Grammar.makeParser

        parse(text, parser(_)) match {
            case Parsed.Success(value, _) => Right(value)
            case failure: Parsed.Failure => Left(failure.extra.trace().longMsg)
        }
    }
}

object BsParser {
    def apply(bsFile: File): BsParser = {
        val src = io.Source.fromFile(bsFile, "UTF-8")
        try {
            val allLines = src.getLines().toVector
            //println(s"${lines.zipWithIndex.map(x => "BsParser. " + (x._2+1) +" |"+x._1+"|").mkString("\n")}")
            
            var accept = true
            val allLinesMarked = allLines.collect {
                case ln if ln == "\\ignore" => accept = false; (ln, false)
                case ln if ln == "\\eoi" => accept = true; (ln, false)
                case ln => (ln, accept)
            }
            val lines = allLinesMarked.filter(_._2).map(_._1)

            // Content of input file with any of OS-specific line separators (\r\n, \r, or \n)
            // replaced with UNIX line separator (\n)
            val text = lines.mkString("\n") + "\n"
            new BsParser(text)
        }
        finally {
            src.close()
        }
    }
}