package sne.bs.test

import org.scalatest.funspec.AnyFunSpec
import sne.bs.BsException
import sne.bs.parser.TOCLabel

class TestTOC extends AnyFunSpec {

    ignore("Test TOC label") {
        it("empty toc") {
            val toc0 = TOCLabel(List.empty)
            assert(toc0.next(1).labelString == "1")
        }
       it("same level") {
            val toc = TOCLabel(List(1,2,3))
            assert(toc.next(3).labelString == "1.2.4")
        }
        it("next level") {
            val toc = TOCLabel(List(1,2,3))
            assert(toc.next(4).labelString == "1.2.3.1")
        }
        it("prev level") {
            val toc = TOCLabel(List(1,2,3))
            assert(toc.next(2).labelString == "1.3")
        }
        it("skip level error") {
            val toc = TOCLabel(List(1,2,3))
            assertThrows[BsException](toc.next(5))
        }
    }
}
