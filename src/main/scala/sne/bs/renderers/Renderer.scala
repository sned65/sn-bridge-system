package sne.bs.renderers

import os.Path
import sne.bs.BsException

import java.io.OutputStream
import java.nio.file.StandardOpenOption._
import scala.util.Using

trait Renderer {
    
    val kind: String

    def write(out: OutputStream): Unit

    def write(outDir: Path, filename: String): Unit = {
        val res =
            Using(os.write.outputStream(
                target = outDir / filename,
                createFolders = true,
                openOptions = Seq(WRITE, CREATE, TRUNCATE_EXISTING)
            )) { out => write(out) }

        if (res.isFailure) {
            val t = res.failed.get
            if (t.isInstanceOf[BsException]) throw t
            else throw BsException(t)
        }
        else println(s"$kind: $filename is written into $outDir")
    }
}
