package gitinternals

import java.io.FileInputStream

data class GitFileInfo(val fis: FileInputStream, val type: String, val hashData: MutableList<String>)
