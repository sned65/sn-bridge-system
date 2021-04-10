package sne.bs.parser

import fastparse._, NoWhitespace._

//noinspection TypeAnnotation
object Grammar {
    def makeParser[_: P]: P[BsDoc] = {
        P(top_elem.rep(1) ~ End).map(BsDoc)
    }

    ////////////////////////////
    // General purpose parsers
    ////////////////////////////

    def ws0[_: P] = P( CharsWhileIn(" \t\r\n", 0) )
    def ws1[_: P] = P( CharsWhileIn(" \t\r\n", 1) )
    def sp0[_: P] = P( CharsWhileIn(" \t", 0) )
    def sp1[_: P] = P( CharsWhileIn(" \t", 1) )
    def num[_: P] = P( CharIn("0-9").rep(1) )

    
    ////////////////////////////////////
    // M A R K D O W N   P A R S E R S
    ////////////////////////////////////

    def toBlockEnd[_: P] = P(CharsWhile(_ != '}').!)

    // Note: EOL must be always LF.
    def empty[_: P] = P(sp0 ~ "\n").map(_ => BsString(""))
    def non_empty_line[_: P] = P(CharsWhile(_ != '\n').! ~ "\n").map(BsString)

    /**
     * Possible start of special lines (list, bridge element, ...)
     */
    def sosl[_: P] = P(
        ul_start | ol_start | code_start | hdr_start | citation_start | hr |
          table_def_start | "}" | bridge_tags
    )
    def par_line[_: P] = P(!sosl ~ CharsWhile(_ != '\n').! ~ "\n").map(BsString)
    def any_line[_: P] = P(non_empty_line | empty)

    def paragraph[_: P] = P(par_line.rep(1) ~ ("\n".rep(1) | &(sosl) | End))
    def par[_: P] = P(paragraph).map(BsPar)
    def par0[_: P] = P(paragraph).map(BsPar0)

    def hdr_start[_: P] = P("#")
    def hdr[_: P] =
        P(hdr_start.rep(min = 1, max=6).! ~ " ".rep ~ non_empty_line)
          .map(x => BsHdr(x._1.length, x._2.value))
          
    def citation_start[_: P] = P(">")
    def citation[_: P] = P((citation_start ~ any_line).rep(1)).map(BsCitation)
    
    def code_start[_: P] = P(" ".rep(exactly=4) | "\t")
    def code[_: P] = P((code_start ~ any_line).rep(1)).map(BsCode)
    
    def hr[_: P] = P((("*"~sp0) | ("-"~sp0) | ("_"~sp0)).rep(3) ~ empty).!.map(_ => BsHRule())

    // LIST
    
    def li[_: P] = P(non_empty_line ~ (" ".rep(min=2,max=4) ~ non_empty_line | empty).rep).map {
        case (head, tail) => head +: tail
    }.map(BsListItem)

    def ul_start[_: P] = P("- " | "* " | "+ ")
    def ul[_: P] = P(
          (ul_start ~ li).rep(1)
    ).map(BsUnorderedList)

    def ol_start[_: P] = P(num ~ ". ")
    def ol[_: P] = P(
          (ol_start ~ li).rep(1)
    ).map(BsOrderedList)
    
    // TABLE

    def table_def_chars[_: P] = P( CharsWhileIn("|:\\- ", 1).! )
    def table_def_start[_: P] = P("|".? ~ sp0 ~ ":".!.? ~ "-".rep(3))
    def table_def[_: P] = P(
        table_def_start ~ table_def_chars ~ empty
    ).map(s => BsTableDef(s._1.getOrElse("") + "---" + s._2))
    def table_header[_: P] = P(!table_def ~ non_empty_line)
    def table[_: P] = P(
        table_header.? ~ table_def ~/ non_empty_line.rep(1) ~ ("\n".rep(0) | &(sosl) | End)
    ).map(t => BsTable(t._2, t._1, t._3))

    
    ////////////////////////////////
    // B R I D G E   P A R S E R S
    ////////////////////////////////
    
    def bridge_tags[_: P] = P("\\hand" | "\\deal" | "\\bdn" | "\\bids" | "\\arg")

    def small[_: P] = P(CharIn("2-9").!)
    def honour[_: P] = P(CharIn("AKQJTakqjt").!)
    def rank[_: P] = P(small | honour)
    def suit[_: P] = P(("\\c" | "\\d" | "\\h" | "\\s" | "♣" | "♦" | "♥" | "♠").!)
    def suit_backslash[_: P] = P("\\" ~ &("c" | "d" | "h" | "s"))
    def player[_: P] = P(("N" | "E" | "S" | "W").!)
    
    def hand_body[_: P] = P((rank | ".".!).rep(min=3, max=16)).map(_.mkString)
    def hand[_: P] = P(
        ws0 ~ "\\hand" ~ ws0 ~ "{" ~/ ws0 ~ hand_body ~ ws0 ~ "}" ~
          (ws0 ~ "{" ~ ws0 ~ CharsWhile(_ != '}').! ~ "}").?
    ).map{case (h, t) => BsHand(h, t)}
    
    def hand_with_player[_: P] = P(player ~ ":" ~ hand_body)
    def deal[_: P] = P(
        ws0 ~ "\\deal" ~ ws0 ~ "{" ~/ ws0 ~ hand_with_player ~ (ws1 ~ hand_with_player).rep(min=0, max=3) ~ ws0 ~ "}"
    ).map(d => ((d._1, d._2) +: d._3).toMap).map(BsDeal)

    def bdn_footnote[_: P] = P("\\" ~ num.! ~ ws0 ~ "{" ~ toBlockEnd.! ~ "}").map(fn => BsBdnFootnote(fn._1, fn._2))
    def bdn_text[_: P] = P(CharsWhile(c => !c.isWhitespace && c != '}' && c != '\\'))
    def bdn_bid[_: P] = P((bdn_text | suit).rep(1).!)
    def bdn_elem[_: P] = P(bdn_bid.! ~ sp0 ~ bdn_footnote.? ~ sp0 ~ "\n".!.?)
      .map(fn => BsBdnElem(fn._1, fn._2, fn._3.isDefined))
    def bdn[_: P] = {
        P(ws0 ~ "\\bdn" ~ ws0 ~ "{" ~/ ws0 ~ bdn_elem.rep(1) ~ "}" ).map(BsBdn)
    }

    def bids_row_continuation[_: P] = P(sp1 ~ "|" ~ sp0 ~ any_line)
    def bids_row[_: P] = P(
        sp0 ~ CharsWhile(c => c != '|' && c != '\n').! ~ "|" ~ sp0 ~ any_line ~
          bids_row_continuation.rep
    ).map(r => (r._1.trim, r._2 +: r._3))
    def bids[_: P] = {
        P(ws0 ~ "\\bids" ~ ws0 ~ "{" ~ ws0 ~/ bids_row.rep(1) ~ "}" ).map(BsBids)
    }
    
    def arg_elem[_: P] = {
        hr | ul | ol | citation | code | hand | deal | bdn | bids | empty | par
    }
    def arg[_: P] = P(
        ws0 ~ "\\arg" ~ ws0 ~ "{" ~ ws0 ~/ toBlockEnd.! ~ "}" ~ ws0 ~ "{" ~ ws0 ~/
          arg_elem.rep(1)
          ~ ws0 ~ "}" ~ ws0).map(x => BsArg(x._1, x._2))

    ////////////////////////////
    // A L L   P A R S E R S
    ////////////////////////////

    def top_elem[_: P] = {
        hdr | hr | ul | ol | citation | code | table | arg | hand | deal | bdn | bids | empty | par
    }
}
