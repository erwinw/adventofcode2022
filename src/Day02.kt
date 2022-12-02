@file:Suppress("MagicNumber")

private const val DAY = "02"

private const val PART1_CHECK = 15
private const val PART2_CHECK = 12

private enum class RockPaperScissors(val score: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3),
    ;
}

private val rock = RockPaperScissors.ROCK
private val paper = RockPaperScissors.PAPER
private val scissors = RockPaperScissors.SCISSORS

const val SCORE_YOU_WIN = 6
const val SCORE_YOU_DRAW = 3
const val SCORE_YOU_LOOSE = 0

private enum class RequiredResult {
    SHOULD_WIN,
    SHOULD_DRAW,
    SHOULD_LOOSE,
}
private val shouldWin = RequiredResult.SHOULD_WIN
private val shouldDraw = RequiredResult.SHOULD_DRAW
private val shouldLoose = RequiredResult.SHOULD_LOOSE


private val themToRPS = mapOf(
    'A' to RockPaperScissors.ROCK,
    'B' to RockPaperScissors.PAPER,
    'C' to RockPaperScissors.SCISSORS,
)
private val usToRPS = mapOf(
    'X' to RockPaperScissors.ROCK,
    'Y' to RockPaperScissors.PAPER,
    'Z' to RockPaperScissors.SCISSORS,
)
private val charToRequiredResult = mapOf(
    'X' to shouldLoose,
    'Y' to shouldDraw,
    'Z' to shouldWin,
)

private val scoreGame: Map<RockPaperScissors, Map<RockPaperScissors, Int>> =
    mapOf(
        rock to mapOf(
            rock to SCORE_YOU_DRAW,
            paper to SCORE_YOU_WIN,
            scissors to SCORE_YOU_LOOSE,
        ),
        paper to mapOf(
            rock to SCORE_YOU_LOOSE,
            paper to SCORE_YOU_DRAW,
            scissors to SCORE_YOU_WIN,
        ),
        scissors to mapOf(
            rock to SCORE_YOU_WIN,
            paper to SCORE_YOU_LOOSE,
            scissors to SCORE_YOU_DRAW,
        ),
    )

private val calculateMove: Map<RockPaperScissors, Map<RequiredResult, RockPaperScissors>> =
    mapOf(
        rock to mapOf(
            shouldLoose to scissors,
            shouldDraw to rock,
            shouldWin to paper,
        ),
        paper to mapOf(
            shouldLoose to rock,
            shouldDraw to paper,
            shouldWin to scissors,
        ),
        scissors to mapOf(
            shouldLoose to paper,
            shouldDraw to scissors,
            shouldWin to rock,
        ),
    )

fun main() {
    fun part1(input: List<String>) =
        input.sumOf {
            val them = themToRPS[it[0]]!!
            val us = usToRPS[it[2]]!!
            us.score + scoreGame[them]!![us]!!
        }

    fun part2(input: List<String>): Int =
        input.sumOf {
            val them = themToRPS[it[0]]!!
            val required = charToRequiredResult[it[2]]!!
            val us = calculateMove[them]!![required]!!
            us.score + scoreGame[them]!![us]!!
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput).also { println("Part1 test output: $it") } == PART1_CHECK)
    check(part2(testInput).also { println("Part2 test output: $it") } == PART2_CHECK)

    val input = readInput("Day$DAY")
    println("Part1 final output: ${part1(input)}")
    println("Part2 final output: ${part2(input)}")
}
