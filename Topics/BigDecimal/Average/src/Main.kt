import java.math.RoundingMode

fun main() {
    val bd = Array(3) { readLine()!!.toBigDecimal() }
    val round = (bd[0] + bd[1] + bd[2]) / 3.toBigDecimal()
    println(round.setScale(0, RoundingMode.DOWN))
}