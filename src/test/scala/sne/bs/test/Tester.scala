package sne.bs.test

import org.scalatest.funspec.AnyFunSpec
import os.SubPath
import sne.bs.BsException

class Tester extends AnyFunSpec  {

    ignore("--help") {
        sne.bs.Main.main(Array(
            "--help"
        ))
    }

    val bsTestDir: SubPath = os.sub/"src"/"test"/"resources"/"bs"
    os.list(os.pwd/bsTestDir)
      .filter(_.baseName.startsWith("90"))
      .foreach { f =>
        it(s"${f.baseName}") {
            sne.bs.Main.run(Array(
                "--fo",
                "--pdf",
                "--html",
                "--verbose=1",
                s"$f",
                "-o", s"D:\\Bridge\\soft_my\\tmp\\${f.baseName}"
            ))
        }
    }

    val bsErrTestDir: SubPath = os.sub/"src"/"test"/"resources"/"bs_err"
    os.list(os.pwd/bsErrTestDir)
      .filter(_.baseName.startsWith("xx"))
      .foreach { f =>
        it(s"${f.baseName}") {
            val e = intercept[BsException] {
                sne.bs.Main.run(Array(
                    "--html",
                    s"$f",
                    "-o", s"D:\\Bridge\\soft_my\\tmp\\err_${f.baseName}"
                ))
            }
            println(s"${f.baseName}: ${e.getMessage}")
            //e.printStackTrace()
        }
    }

}
