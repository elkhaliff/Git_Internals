import java.math.RoundingMode

fun main() {
    val bd = readLine()!!.toBigDecimal()
    val newScale = readLine()!!.toInt()
    println(bd.setScale(newScale, RoundingMode.HALF_DOWN))
}