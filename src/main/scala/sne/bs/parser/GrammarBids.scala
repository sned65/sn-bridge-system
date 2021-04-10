package sne.bs.parser

import fastparse._, NoWhitespace._
import sne.bs.parser.Grammar._

object GrammarBids {
    def makeParser[_: P] = {

        val ans = P(bids_elem.rep(1) ~ End)
        //println(s"GrammarBids.makeParser. index=${ans.index} success=${ans.isSuccess}")
        ans
    }

    // All parsers

    def bids_elem[_: P] = {
        val ans = ul | ol | bids | empty | par0
        //println(s"elem. index=${ans.index} success=${ans.isSuccess}")
        ans
    }
}
