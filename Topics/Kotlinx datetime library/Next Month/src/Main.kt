import kotlinx.datetime.*
import java.time.Clock
import java.time.Instant

fun nextMonth(date: String): String {
    val instant = Instant.parse(date)
    val instant1 = Instant.toInstant("2023-01-01T20:00:00.1234Z")
    val period: DateTimePeriod = DateTimePeriod(months = 1)

    return instant.plus(period, TimeZone.UTC).toString()
}

fun main() {
    val date = readln()
    println(nextMonth(date))
}