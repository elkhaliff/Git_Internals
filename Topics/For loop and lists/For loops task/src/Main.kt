fun main() {
    val list = MutableList<Int>(readLine()!!.toInt()) { readLine()!!.toInt() }
    val p = readLine()!!.split(' ').toMutableList()
    println(if (list.contains(p[0].toInt()) && list.contains(p[1].toInt())) "YES" else "NO")
}