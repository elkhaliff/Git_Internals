package gitinternals

import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.zip.InflaterInputStream

fun main() {
    var fileDir = getString("Enter .git directory location:")
    val command = getString("Enter command:")
    val testDir = ""
//    val testDir = "d:\\projects\\kotlin\\Git Internals\\Git Internals\\"
    fileDir = testDir + fileDir
    when (command) {
        "cat-file" -> catFile(getGitFileInfo(fileDir))
        "list-branches" -> listBranches(fileDir)
        "log" -> log(fileDir)
        "commit-tree" -> commitTree(fileDir)
    }
}

fun getGitFileInfo(fileDir: String, hash: String = ""): GitFileInfo {
    val objHash = if (hash != "") hash else getString("Enter git object hash:")
    val fis = gitFile(fileDir, objHash)
    val fileLines = InflaterInputStream(fis).reader().readLines()
    val type = fileLines[0]
        .substringBefore(0.toChar())
        .split(" ")[0]
        .uppercase(Locale.getDefault())
    val hashData = fileLines.toMutableList()
    hashData[0] = hashData[0].substringAfter(0.toChar())

    return GitFileInfo(gitFile(fileDir, objHash), type, hashData)
}

fun catFile(fileInfo: GitFileInfo, isLog: Boolean = false): MutableList<String>? {
    val gitObject = when (fileInfo.type) {
        "TREE" -> GitTree(fileInfo.fis)
        "BLOB" -> GitBlob(fileInfo.hashData)
        "COMMIT" -> GitCommit(fileInfo.hashData, isLog)
        else -> null
    }
    val hashes = gitObject!!.pars()

    println(gitObject)
    return hashes
}

fun listBranches(fileDir: String) {
    val headBranchName = File("$fileDir/HEAD").readText().split("/").last().trim()
    val fileList = (File("$fileDir/refs/heads").list()?.sorted())

    if (fileList == null) println("Branches are missing.") else {
        for (file in fileList) println((if (file == headBranchName) "* " else "  ") + file)
    }
}

fun log(fileDir: String) {
    val branch = getString("Enter branch name:")
    var hash = File("$fileDir/refs/heads/$branch").readText().trim()
    var doIt = true

    while (doIt) {
        println("Commit: $hash")
        val hashes = catFile(getGitFileInfo(fileDir, hash), true)
        hash = if (hashes != null) {
            if (hashes.size > 1) {
                println("\nCommit: ${hashes[1]} (merged)")
                catFile(getGitFileInfo(fileDir, hashes[1]), true)
                hashes[0]
            } else
                hashes[0]
        } else
            ""
        if (hash == "") doIt = false else println()
    }
}

fun commitTree(fileDir: String, hash: String = "", file: String = "") {
    var gitHash = hash

    if (gitHash == "") {
        val fileInfo = getGitFileInfo(fileDir, getString("Enter commit-hash:"))
        if (fileInfo.type == "COMMIT") {
            val gitObject = GitCommit(fileInfo.hashData)
            gitObject.pars()
            gitHash = gitObject.tree
        }
    }

    val getTree = GitTree(gitFile(fileDir, gitHash))
    getTree.pars()
    val fileMap = getTree.getMap()

    for ((name, hash2) in fileMap) {
        val fileName = if (file == "") name else "$file/$name"

        if (getGitFileInfo(fileDir, hash2).type == "TREE") commitTree(fileDir, hash2, fileName)
//        if (catFile(fileDir, hash2, head = true) == "TREE") commitTree(fileDir, hash2, fileName)
        else println(fileName)
    }
}

fun gitFile(fileDir: String, objHash: String) =
    FileInputStream("$fileDir\\objects\\${objHash.substring(0, 2)}\\${objHash.substring(2)}")

fun getString(vText: String): String {
    println(vText)
    return readLine()!!
}