@file:Suppress("MagicNumber")

import kotlin.math.abs

private const val DAY = "09"

private const val PART1_CHECK = 13
private const val PART2_CHECK = 36

private data class Coordinate(
    var x: Int,
    var y: Int,
)

private fun sign(number: Int): Int =
    when {
        number < 0 -> -1
        number > 0 -> 1
        else -> 0
    }

private fun part1(input: List<String>): Int {
    val head = Coordinate(0, 0)
    val tail = Coordinate(0, 0)
    val seen = mutableSetOf(tail.copy())
    input.forEach { line ->
        val direction = line.first()
        val amount = line.drop(2).toInt()
        val (dx, dy) =
            when (direction) {
                'R' -> Pair(1, 0)
                'D' -> Pair(0, -1)
                'L' -> Pair(-1, 0)
                'U' -> Pair(0, 1)
                else -> throw IllegalArgumentException("Unexpected direction $direction")
            }

        repeat(amount) {
            head.x += dx
            head.y += dy
            val differenceX = abs(head.x - tail.x)
            val differenceY = abs(head.y - tail.y)
            when {
                differenceX < 2 && differenceY < 2 -> { /* nothing to do */
                }

                differenceX == 0 -> {
                    tail.y += dy
                }

                differenceY == 0 -> {
                    tail.x += dx
                }

                else -> {
                    tail.x += sign(head.x - tail.x)
                    tail.y += sign(head.y - tail.y)
                }
            }
            seen.add(tail.copy())
        }
    }
    return seen.size
}

private fun printKnots(knots: List<Coordinate>, isSeen: Boolean = false) {
    val knotsPlusStart = knots + Coordinate(0, 0)
    val maxY = knotsPlusStart.maxOf { it.y }
    val maxX = knotsPlusStart.maxOf { it.x }
    val minY = knotsPlusStart.minOf { it.y }
    val minX = knotsPlusStart.minOf { it.x }
    val width = maxX - minX + 1
    val height = maxY - minY + 1
    val output = List(height) {
        MutableList(width) { '.' }
    }

    fun markCoordinate(coordinate: Coordinate, char: Char) {
        val indexY = coordinate.y - minY
        val indexX = coordinate.x - minX
        output[indexY][indexX] = char
    }
    markCoordinate(Coordinate(0, 0), 's')
    for (knotIndex in knots.size - 1 downTo 1) {
        val char = if (isSeen) '#' else '0' + knotIndex
        markCoordinate(knots[knotIndex], char)
    }
    markCoordinate(knots.first(), 'H'.takeUnless { isSeen } ?: '#')

    output.reversed().forEach { line ->
        println(line.joinToString(" "))
    }
}

private fun part2(input: List<String>): Int {
    val knots = List(10) { Coordinate(0, 0) }
    val head = knots.first()
    val tail = knots.last()
    val seen = mutableSetOf(tail.copy())

    input.forEach { line ->
        val direction = line.first()
        val amount = line.drop(2).toInt()
        val (dx, dy) =
            when (direction) {
                'R' -> Pair(1, 0)
                'D' -> Pair(0, -1)
                'L' -> Pair(-1, 0)
                'U' -> Pair(0, 1)
                else -> throw IllegalArgumentException("Unexpected direction $direction")
            }
        
        repeat(amount) {
            head.x += dx
            head.y += dy
            for (knotIdx in 1..9) {
                // println("KNOT: $knotIdx")
                val predecessor = knots[knotIdx - 1]
                val current = knots[knotIdx]
                val differenceX = abs(predecessor.x - current.x)
                val differenceY = abs(predecessor.y - current.y)
                when {
                    differenceX < 2 && differenceY < 2 -> { /* nothing to do */
                    }

                    differenceX == 0 -> {
                        current.y = predecessor.y - sign(predecessor.y - current.y)
                    }

                    differenceY == 0 -> {
                        current.x = predecessor.x - sign(predecessor.x - current.x)
                    }

                    else -> {
                        current.x += sign(predecessor.x - current.x)
                        current.y += sign(predecessor.y - current.y)
                    }
                }
            }
            seen.add(tail.copy())
        }
    }
    printKnots(seen.toList(), true)

    return seen.size
}

fun main() {
    println("Day $DAY")
    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("Day${DAY}_test_part1")
    check(part1(testInputPart1).also { println("Part1 output: $it") } == PART1_CHECK)
    val testInputPart2 = readInput("Day${DAY}_test_part2")
    check(part2(testInputPart2).also { println("Part2 output: $it") } == PART2_CHECK)

    val input = readInput("Day$DAY")
    println("Part1 final output: ${part1(input)}")
    println("Part2 final output: ${part2(input)}")
}
