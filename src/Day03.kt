@file:Suppress("MagicNumber")

private const val DAY = "03"

private const val PART1_CHECK = 157
private const val PART2_CHECK = 70

fun String.toCompartments(): List<String> =
    (length / 2).let {
        listOf(take(it), drop(it))
    }

fun itemPriority(item: Char): Int =
    when (item) {
        in 'a'..'z' -> item.code - 'a'.code + 1
        in 'A'..'Z' -> item.code - 'A'.code + 27
        else -> throw IllegalArgumentException("Item '${item}' out of range")
    }

fun main() {
    fun part1(input: List<String>): Int =
        input.sumOf { ruckSack ->
            ruckSack.toCompartments()
                .map(String::toSet)
                .reduce(Iterable<Char>::intersect)
                .sumOf(::itemPriority)
        }

    fun part2(input: List<String>): Int =
        input.chunked(3)
            .sumOf { rucksacks ->
                rucksacks.map(String::toSet)
                    .reduce(Iterable<Char>::intersect)
                    .sumOf(::itemPriority)
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
