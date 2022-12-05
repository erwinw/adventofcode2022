@file:Suppress("MagicNumber")

private const val DAY = "05"

private const val PART1_CHECK = "CMZ"
private const val PART2_CHECK = "MCD"

private typealias Stacks = MutableList<MutableList<Char>>

private fun splitInput(input: List<String>): Pair<List<String>, List<String>> {
    val partA = mutableListOf<String>()
    val partB = mutableListOf<String>()
    val iterator = input.iterator()
    var item = iterator.next()
    while (item.isNotEmpty()) {
        partA.add(item)
        item = iterator.next()
    }
    iterator.forEachRemaining(partB::add)
    return partA to partB
}

private fun Stacks.print() =
    forEachIndexed { index, chars ->
        println(">[$index]: $chars")
    }

private fun parseInitialStacks(input: List<String>): Stacks {
    val stackCount = (input.last().length + 2) / 4
    val stacks = MutableList(stackCount) { mutableListOf<Char>() }
    input.dropLast(1).reversed().forEach { line ->
        for (stack in 0 until stackCount) {
            val charOffset = stack * 4 + 1
            val char = line.getOrNull(charOffset)
            if (char != null && char != ' ') {
                stacks[stack].add(char)
            }
        }
    }

    return stacks
}

private val procedureRegex = Regex("""^move (\d+) from (\d+) to (\d+)$""")
private fun parseProcedure(input: String): Triple<Int, Int, Int> {
    val (a, b, c) = procedureRegex.find(input)!!.groupValues.drop(1).map(String::toInt)
    return Triple(a, b, c)
}

fun main() {
    fun part1(input: List<String>): String {
        val (inputStacks, inputProcedures) = splitInput(input)
        val stacks = parseInitialStacks(inputStacks)

        inputProcedures.map(::parseProcedure)
            .forEach { (count, src, dst) ->
                val moving = stacks[src-1].takeLast(count)
                stacks[src-1] = stacks[src-1].dropLast(count).toMutableList()
                stacks[dst-1].addAll(moving.reversed())
            }

        stacks.print()

        return stacks.map(List<*>::last).joinToString("")
    }

    fun part2(input: List<String>): String {
        val (inputStacks, inputProcedures) = splitInput(input)
        val stacks = parseInitialStacks(inputStacks)

        inputProcedures.map(::parseProcedure)
            .forEach { (count, src, dst) ->
                val moving = stacks[src-1].takeLast(count)
                stacks[src-1] = stacks[src-1].dropLast(count).toMutableList()
                stacks[dst-1].addAll(moving)
            }

        stacks.print()

        return stacks.map(List<*>::last).joinToString("")
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
