@file:Suppress("MagicNumber")

private const val DAY = "11"

private val PART1_CHECK = 10605L
private val PART2_CHECK = 2713310158L

private data class Monkey(
    val id: Int,
    var items: MutableList<Long>,
    val operation: (Long) -> Long,
    val divisor: Long,
    val trueTargetMonkey: Int,
    val falseTargetMonkey: Int,
    var inspectedCount: Long = 0L,
) {
    fun inspectAndThrowAll(monkeys: List<Monkey>, relief: Int, commonDivisor: Long = 1) {
        inspectedCount += items.size
        // println("  Items: ${items.size}")
        items.removeAll { item ->
            true.also {
                val inspectionResult = operation(item)
                val postRelief = if (relief != 1) {
                    inspectionResult / relief
                } else {
                    inspectionResult % commonDivisor
                }
                val targetMonkeyId =
                    if (postRelief % divisor == 0L) trueTargetMonkey else falseTargetMonkey
                // println("  Item: $item -> $inspectionResult -> $postRelief; thrown to $targetMonkeyId")
                val targetMonkey = monkeys[targetMonkeyId]
                targetMonkey.items.add(postRelief)
            }
        }
    }
}

private fun parseOperation(opString: String): (Long) -> Long =
    when {
        opString == "* old" -> ({ it * it })
        opString.first() == '*' -> {
            val operand = opString.drop(2).toLong()
            ({ it * operand })
        }

        opString.first() == '+' -> {
            val operand = opString.drop(2).toLong()
            ({ it + operand })
        }

        else -> throw IllegalArgumentException("Unsupported opString: $opString")
    }

private fun parseMonkey(input: List<String>) =
    Monkey(
        id = input[0].drop(7).dropLast(1).toInt(),
        items = input[1].drop(18).split(", ").map(String::toLong).toMutableList(),
        operation = parseOperation(input[2].drop(23)),
        divisor = input[3].drop(21).toLong(),
        trueTargetMonkey = input[4].drop(29).toInt(),
        falseTargetMonkey = input[5].drop(30).toInt(),
    )

fun main() {
    fun part1(input: List<String>): Long {
        val monkeys = input.chunked(7).map(::parseMonkey)
        val commonDivisor = monkeys.map { it.divisor }.reduce { acc, i -> acc * i }
        repeat(20) { round ->
            println("\nStarting round $round\n========")
            monkeys.forEach { monkey ->
                // println("Monkey ${monkey.id}:")
                monkey.inspectAndThrowAll(monkeys, 3)
            }

            println("After round ${round + 1}, the monkeys are holding items with these worry levels:")
            monkeys.forEach { println("Monkey ${it.id}: ${it.items.joinToString()}") }
            println("")
        }
        monkeys.forEach { println("Monkey ${it.id} inspected items ${it.inspectedCount} times.") }
        return monkeys.map { it.inspectedCount }.sortedDescending().take(2).reduce { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.chunked(7).map(::parseMonkey)
        val commonDivisor = monkeys.map { it.divisor }.reduce { acc, i -> acc * i }
        val printIf = listOf(
            0,
            19,
            999,
            1999,
            2999,
            3999,
            4999,
            5999,
            6999,
            7999,
            8999,
            9999,
        )
        repeat(10_000) { round ->
            // if (round % 100 == 0) {
            //     println("Round: $round")
            // }
            // println("\nStarting round $round\n========")
            monkeys.forEach { monkey ->
                // println("Monkey ${monkey.id}:")
                monkey.inspectAndThrowAll(monkeys, 1, commonDivisor)
            }

            if (round in printIf) {
                println("")
                println("After round ${round + 1}, the monkeys are holding items with these worry levels:")
                monkeys.forEach { println("Monkey ${it.id}: ${it.items.joinToString()}") }
                println("")

                monkeys.forEach { println("Monkey ${it.id} inspected items ${it.inspectedCount} times.") }
                println("")
            }
        }
        monkeys.forEach { println("Monkey ${it.id} inspected items ${it.inspectedCount} times.") }
        return monkeys.map { it.inspectedCount }.sortedDescending().take(2).reduce { acc, i -> acc * i }
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
