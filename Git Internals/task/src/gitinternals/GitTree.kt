package gitinternals

import java.io.FileInputStream
import java.util.*
import java.util.zip.InflaterInputStream

class GitTree(private val fis: FileInputStream): GitObject {
    private var message = ""
    private val fileHash = mutableMapOf<String, String>()

    override fun pars(): MutableList<String>? {
        val allBytes = InflaterInputStream(fis).readAllBytes()
        var fileName = ""
        var count = 0
        var shaCount = 0
        var isHash = false
        var hexHash = ""

        for (byte in allBytes) {
            if (isHash) {
                when (count) {
                    0 -> {
                        if (getChar(byte) == ' ') count++
                        message += "${getChar(byte)}"
                    }
                    1 -> if (getChar(byte) == 0.toChar()) count++ else fileName += "${getChar(byte)}"
                    2 -> {
                        shaCount++
                        val addHex = String.format("%02X", byte).lowercase(Locale.getDefault())
                        hexHash += addHex
                        message += addHex
                        if (shaCount == 20) {
                            fileHash[fileName] = hexHash
                            hexHash = ""
                            message += " $fileName\n"
                            fileName = ""
                            count = 0
                            shaCount = 0
                        }
                    }
                }
            }
            if (!isHash && getChar(byte) == 0.toChar()) isHash = true
        }
        message.trim()
        fis.close()
        return null
    }

    override fun toString(): String {
        return "*TREE*\n" +
                message
    }

    private fun getChar(byte: Byte) = byte.toInt().toChar()

    fun getMap(): MutableMap<String, String> {
        return fileHash
    }
}