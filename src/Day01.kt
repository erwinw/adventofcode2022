@file:Suppress("MagicNumber")

private const val DAY = "01"
private const val PART1_CHECK = 24000
private const val PART2_CHECK = 45000

fun main() {
    fun part1(input: List<String>): Int {
        var highestElfCalories = 0
        var currentElfCalories = 0

        input.forEach {
            if (it.isEmpty()) {
                currentElfCalories = 0
                return@forEach
            }

            val lineCalories = it.toInt()
            currentElfCalories += lineCalories
            if (currentElfCalories > highestElfCalories) {
                highestElfCalories = currentElfCalories
            }
        }

        return highestElfCalories
    }

    fun part2(input: List<String>): Int {
        val elfCalories: MutableList<Int> = mutableListOf(0)
        var currentElf = 0

        input.forEach {
            if (it.isEmpty()) {
                currentElf += 1
                return@forEach
            }

            val lineCalories = it.toInt()

            if (currentElf >= elfCalories.size) {
                elfCalories.add(0)
            }
            elfCalories[currentElf] = elfCalories[currentElf] + lineCalories
        }

        elfCalories.sortDescending()

        return elfCalories.take(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput) == PART1_CHECK)
    check(part2(testInput) == PART2_CHECK)

    val input = readInput("Day$DAY")
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}
