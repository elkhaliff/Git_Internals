package gitinternals

class GitBlob(private val hashData: MutableList<String>): GitObject {
    private var message = ""

    override fun pars(): MutableList<String>? {
        hashData.forEach { message += it + "\n" }
        return null
    }

    override fun toString(): String {
        return "*BLOB*\n" +
                message
    }
}