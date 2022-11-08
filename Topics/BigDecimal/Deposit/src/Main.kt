import java.math.BigDecimal
import java.math.RoundingMode

fun main() {
    val (startingAmount, yearlyPercent) = Array(2) { readLine()!!.toBigDecimal() }
    val years = readLine()!!.toInt()
    val finalAmount = startingAmount * (BigDecimal.ONE + yearlyPercent.setScale(2) / 100.toBigDecimal()).pow(years)
    println("Amount of money in the account: ${finalAmount.setScale(2, RoundingMode.CEILING)}")
}
