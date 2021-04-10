package sne.bs.renderers.html

import sne.bs.parser.{BsArgIndexed, BsElem}

object ArgHtml {
    def render(ai: BsArgIndexed): String = {
        val infoId = s"info${ai.index}"
        val rightBtnId = s"right${ai.index}"
        val downBtnId = s"down${ai.index}"
        val desc = HtmlRenderer.elemsToString(ai.arg.elems)

        s"""<div id="$rightBtnId" class="w3-container w3-hide w3-show">
           |  <button type="button" onclick="toggleDetails('$infoId', '$rightBtnId', '$downBtnId')" class="w3-btn w3-round"><i class="fa fa-arrow-right"></i></button>
           |  ${ai.arg.title}
           |</div>
           |<div id="$downBtnId" class="w3-container w3-hide">
           |  <button type="button" onclick="toggleDetails('$infoId', '$rightBtnId', '$downBtnId')" class="w3-btn w3-round"><i class="fa fa-arrow-down"></i></button>
           |  ${ai.arg.title}
           |</div>
           |<div id="$infoId" class="w3-container w3-card w3-hide">
           |$desc
           |</div>
           |""".stripMargin
    }
}
