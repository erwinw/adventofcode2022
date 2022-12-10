@file:Suppress("MagicNumber")

private const val DAY = "10"

private const val PART1_CHECK = 13140
private const val PART2_CHECK = 146

fun main() {
    fun part1(input: List<String>): Int {
        var registerX = 1
        var reported = 0
        var cycles = 1

        fun report(round: Int) {
            if ((round + 20) % 40 == 0) {
                println("! $round: $registerX\n")
                reported += round * registerX
            }
        }
        input.forEach { instruction ->
            when (instruction.take(4)) {
                "noop" -> {
                    cycles += 1
                    report(cycles)
                }

                "addx" -> {
                    val value = instruction.drop(5).toInt()
                    cycles += 1
                    report(cycles)
                    registerX += value
                    cycles += 1
                    report(cycles)
                }
            }
        }
        return reported
    }

    fun part2(input: List<String>): Int {
        val screenSize = 240
        var registerX = 1
        var cycles = 0
        val screen = MutableList(screenSize) { '_' }

        fun printScreen() {
            screen.joinToString("")
                // .trim()
                .chunked(40).forEach {
                    println(it)
                }
            println("")
        }

        fun report() {
            val index = cycles % screenSize
            val char = if ((index % 40) in (registerX - 1)..(registerX + 1)) '#' else ' '
            screen[index] = char
        }
        report()
        input.forEach { instruction ->
            when (instruction.take(4)) {
                "noop" -> {
                    cycles += 1
                    report()
                }

                "addx" -> {
                    val value = instruction.drop(5).toInt()
                    cycles += 1
                    report()
                    registerX += value
                    cycles += 1
                    report()
                }
            }
        }
        printScreen()
        return input.size
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
