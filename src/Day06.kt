@file:Suppress("MagicNumber")

private const val DAY = "06"

private val PART1_CHECK = listOf(7, 5, 6, 10, 11)
private val PART2_CHECK = listOf(19, 23, 23, 29, 26)

private fun lastNOffset(n: Int) = lastN@ { input: String ->
    val lastN = mutableListOf<Char>()
    input.forEachIndexed { index, char ->
        lastN.add(char)
        val uniqueCount = lastN.toSet().size
        if (uniqueCount == n) {
            return@lastN index + 1
        }
        if (lastN.size > n-1) {
            lastN.removeFirst()
        }
    }
    -1
}

fun main() {
    val part1 = lastNOffset(4)
    val part2 = lastNOffset(14)


    println("Day $DAY")
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY}_test")
    println("Part1 output:")
    testInput.zip(PART1_CHECK).forEach { (input, expected) ->
        val actual = part1(input)
        println("Expected: $expected; actual: $actual")
        check(actual == expected)
    }
    println("Part2 output:")
    testInput.zip(PART2_CHECK).forEach { (input, expected) ->
        val actual = part2(input)
        println("Expected: $expected; actual: $actual")
        check(actual == expected)
    }

    val input = readInput("Day$DAY").first()
    println("Part1 final output: ${part1(input)}")
    println("Part2 final output: ${part2(input)}")
}
