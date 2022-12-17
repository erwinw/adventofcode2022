@file:Suppress("MagicNumber")

import java.util.BitSet

private const val DAY = "17"

private const val PART1_CHECK = 1
private const val PART2_CHECK = 1

fun main() {
    val shaftWidth = 7
    val initialOffset = 3

    class Rock(
        val width: Int,
        val height: Int,
        val rows: List<BitSet>,
    ) {
        fun display(): String =
            rows
                .reversed()
                .joinToString("\n") { row ->
                    buildString {
                        for (x in 0 until width) {
                            append("#".takeIf { row[x] } ?: ".")
                        }
                    }
                }

        fun offsetY(x: Int): Int {
            require(x in 0 until width)

            for (y in 0 until height) {
                if (rows[y][x]) {
                    return y
                }
            }
            throw IllegalArgumentException("Rock has hole in x $x; rock:\n\n${display()}")
        }
    }

    fun buildRock(pattern: String): Rock {
        val lines = pattern.split('\n')
        val height = lines.size
        val width = lines.first().length
        val rows = List(height) { BitSet(width) }
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '#') {
                    rows[height - y - 1].set(x)
                }
            }
        }

        return Rock(width, height, rows)
    }

    val rocks = listOf(
        buildRock(
            """
                ####
            """.trimIndent(),
        ),
        buildRock(
            """
                .#.
                ###
                .#.
            """.trimIndent(),
        ),
        buildRock(
            """
                ..#
                ..#
                ###
            """.trimIndent(),
        ),
        buildRock(
            """
                #
                #
                #
                #
            """.trimIndent(),
        ),
        buildRock(
            """
                ##
                ##
            """.trimIndent()
        )
    )

    fun shaftOffset(shaft: BitSet, height: Int, x: Int): Int {
        // println("x? $x")
        for (y in height downTo 0) {
            // println("y? $y -> (${y * shaftWidth + x}) = ${shaft[y * shaftWidth + x]}")
            if (shaft[y * shaftWidth + x]) {
                return y + 1
            }
        }
        return 0
    }

    fun printShaft(shaft: BitSet, height: Int) {
        for (y in height downTo 0) {
            val line = buildString {
                for (x in 0 until shaftWidth) {
                    val bit = shaft[y * shaftWidth + x]
                    append('#'.takeIf { bit } ?: '_')
                }
            }
            println(line)
        }
        println("~".repeat(shaftWidth))
    }

    fun shaftPlaceRock(shaft: BitSet, rock: Rock, dy: Int, dx: Int) {
        for (y in 0 until rock.height) {
            for (x in 0 until rock.width) {
                if (rock.rows[y][x]) {
                    shaft.set((dy + y) * shaftWidth + dx + x)
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val blowRight = input.first().map {
            it == '>'
        }
        var height = 0
        val shaft = BitSet(shaftWidth * initialOffset)
        var blowIndex = 0
        repeat(3) { rockIndex ->
            val rock = rocks[rockIndex % rocks.size]
            var dx = 2
            var dy = height + 4
            println("Starting rock:\n${rock.display()}\n")

            fun landed() =
                (0 until rock.width).any { x ->
                    val shaftHeight = shaftOffset(shaft, height, x + dx)
                    val rockHeight = rock.offsetY(x) + dy
                    // println("Heights at x $x: rock $rockHeight, shaft: $shaftHeight")
                    shaftHeight >= rockHeight
                    // }.also {
                    //     println("Landed? $it")
                }

            println("dx x dy: $dx x $dy")
            while (!landed()) {
                if (blowRight[blowIndex]) {
                    if (dx + rock.width < shaftWidth) {
                        dx += 1
                        println("Right: $dx")
                    } else {
                        println("Right but nothing: $dx")
                    }
                } else {
                    if (dx > 0) {
                        dx -= 1
                        println("Left: $dx")
                    } else {
                        println("Left but nothing: $dx")
                    }
                }
                blowIndex = (blowIndex + 1) % blowRight.size
                dy -= 1
                println("dx x dy: $dx x $dy")
            }
            println("Placing rock at $dy")

            shaftPlaceRock(shaft, rock, dy, dx)
            height = dy + rock.height
            printShaft(shaft, height)
        }
        println("Height: $height")
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    println("Day $DAY")
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput).also { println("Part1 output: $it") } == PART1_CHECK)
    check(part2(testInput).also { println("Part2 output: $it") } == PART2_CHECK)

    // val input = readInput("Day$DAY")
    // println("Part1 final output: ${part1(input)}")
    // println("Part2 final output: ${part2(input)}")
}
