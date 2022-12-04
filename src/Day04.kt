@file:Suppress("MagicNumber")

private const val DAY = "04"

private const val PART1_CHECK = 2
private const val PART2_CHECK = 4

private operator fun IntRange.contains(other: IntRange): Boolean {
    return (first >= other.first && last <= other.last) ||
        (other.first >= first && other.last <= last)
}

private infix fun IntRange.overlap(other: IntRange): Boolean {
    return other.first in first..last || other.last in first..last ||
            first in other.first..other.last || last in other.first .. other.last
}

fun main() {
    fun part1(input: List<String>): Int =
        input.map { line ->
            line.split(',').map { elf ->
                val (start, stop) = elf.split('-').map { it.toInt() }
                IntRange(start, stop)
            }
        }.count { (elfA, elfB) ->
            elfA in elfB
        }

    fun part2(input: List<String>): Int =
        input.map { line ->
            line.split(',').map { elf ->
                val (start, stop) = elf.split('-').map { it.toInt() }
                IntRange(start, stop)
            }
        }.count { (elfA, elfB) ->
            elfA overlap elfB
        }


    println("Day $DAY")
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput).also { println("Part1 output: $it") } == PART1_CHECK)
    check(part2(testInput).also { println("Part2 output: $it") } == PART2_CHECK)

    val input = readInput("Day$DAY")
    println("Part1 final output: ${part1(input)}")
    println("Part2 final output: ${part2(input)}")
}
