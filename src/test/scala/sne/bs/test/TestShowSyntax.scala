package sne.bs.test

import org.scalatest.funspec.AnyFunSpec
import sne.bs.Main

class TestShowSyntax extends AnyFunSpec {

    it("Test Show Syntax in browser") {
        Main.main(Array("--verbose=1", "--help-bs-syntax"))
    }
}
