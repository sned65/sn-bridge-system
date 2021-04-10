package sne.bs.parser

import fastparse._, NoWhitespace._
import sne.bs.parser.Grammar._

object GrammarList {
    def makeParser[_: P] = {
        P(list_elem.rep(1) ~ End)
    }

    // All parsers

    def list_elem[_: P] = {
        ul | ol | citation | code | hand | deal | bdn | bids | par0 | empty
    }
}
