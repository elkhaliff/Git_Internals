package gitinternals

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GitCommit(private val hashData: MutableList<String>, private val isLog: Boolean = false): GitObject {
    lateinit var tree: String
    private val parents = mutableListOf<String>()
    private var author = mutableListOf<String>()
    private var committer = mutableListOf<String>()
    private var message = ""

    override fun pars(): MutableList<String>? {
        for (i in 0 until hashData.size) {
            val currLine = hashData[i].split(' ').toMutableList()
            when (currLine[0]) {
                "tree" -> tree = hashData[i].substringAfter(' ')
                "parent" -> parents.add(hashData[i].substringAfter(' '))
                "author" -> author = currLine
                "committer" -> committer = currLine
                else -> message += hashData[i] + "\n"
            }
        }
        message = message.trim()

        /*return if (parents.size > 1)
            parents[1]
        else
            if (parents.size > 0) parents[0]
        else */
        return if (parents.size > 0) parents  else null
    }

    private fun parsProp(prop: MutableList<String>, orig: Boolean): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val instant = Instant.ofEpochSecond(prop[3].toLong())

        val date = LocalDateTime.ofInstant(instant, ZoneId.of(prop[4]))

        return "${prop[1]} ${prop[2].substring(1 until prop[2].lastIndex)} " +
                (if (orig) "original " else "commit ") +
                "timestamp: ${formatter.format(date)} " + ZoneId.of(prop[4]).toString()
    }

    override fun toString(): String {
        return (if(!isLog) {
                    "*COMMIT*\n" +
                    "tree: $tree\n" +
                    (if (parents.size > 0) "parents: ${parents.joinToString(" | ")}\n" else "")}
                else "") +
                (if(!isLog) "author: ${parsProp(author, true)}\n" else "") +
                (if(!isLog) "committer: " else "") + "${parsProp(committer, false)}\n" +
                (if(!isLog) "commit message:\n" else "") + message
    }
}