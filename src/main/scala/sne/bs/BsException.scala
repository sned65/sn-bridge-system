package sne.bs

class BsException(msg: String, t: Throwable, val verbose: Boolean = false) extends Exception(msg, t) {

    override def getMessage: String = {
        s"${getClass.getSimpleName}: " + {
            if (msg == null || msg.isEmpty) {
                if (t == null) ""
                else s"${t.getClass.getSimpleName}: ${t.getMessage}"
            }
            else msg
        }
    }
}

object BsException {
    def apply(msg: String): BsException = new BsException(msg, null)
    def apply(t: Throwable, verbose: Boolean = false): BsException = new BsException(null, t, verbose)
}
