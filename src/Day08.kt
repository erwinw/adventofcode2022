@file:Suppress("MagicNumber")

private const val DAY = "08"

private const val PART1_CHECK = 21
private const val PART2_CHECK = 8

private const val CODE_ZERO = '0'.code

private operator fun List<String>.get(y: Int, x: Int): Byte =
    (this[y][x].code - CODE_ZERO).toByte()

private fun buildScenicScore(input: List<String>, height: Int, width: Int, treeY: Int, treeX: Int): Int {
    val houseHeight = input[treeY, treeX]

    // to the left
    var toLeft = 0
    for (x in (treeX - 1) downTo 0) {
        val treeHeight = input[treeY, x]
        toLeft += 1
        if (treeHeight >= houseHeight) {
            break
        }
    }

    // to the right
    var toRight = 0
    for (x in treeX + 1 until height) {
        val treeHeight = input[treeY, x]
        toRight += 1
        if (treeHeight >= houseHeight) {
            break
        }
    }

    // up
    var toUp = 0
    for (y in (treeY - 1) downTo 0) {
        val treeHeight = input[y, treeX]
        toUp += 1
        if (treeHeight >= houseHeight) {
            break
        }
    }

    // down
    var toDown = 0
    for (y in treeY + 1 until height) {
        val treeHeight = input[y, treeX]
        toDown += 1
        if (treeHeight >= houseHeight) {
            break
        }
    }

    return toLeft * toRight * toUp * toDown
}

fun main() {
    fun part1(input: List<String>): Int {
        val width = input.first().length
        val height = input.size
        val visibles = List(height) {
            MutableList(width) { false }
        }
        // left to right
        for (y in 0 until height) {
            var maxHeight: Byte = -1
            for (x in 0 until width) {
                val tree = input[y, x]
                if (tree > maxHeight) {
                    visibles[y][x] = true
                    maxHeight = tree
                    if (maxHeight == 9.toByte()) {
                        break
                    }
                }
            }
        }
        // right to left
        for (y in 0 until height) {
            var maxHeight: Byte = -1
            for (x in width - 1 downTo 0) {
                val tree = input[y, x]
                if (tree > maxHeight) {
                    visibles[y][x] = true
                    maxHeight = tree
                    if (maxHeight == 9.toByte()) {
                        break
                    }
                }
            }
        }
        // top to bottom
        for (x in 0 until width) {
            var maxHeight: Byte = -1
            for (y in 0 until height) {
                val tree = input[y, x]
                if (tree > maxHeight) {
                    visibles[y][x] = true
                    maxHeight = tree
                    if (maxHeight == 9.toByte()) {
                        break
                    }
                }
            }
        }
        // bottom to top
        for (x in 0 until width) {
            var maxHeight: Byte = -1
            for (y in height - 1 downTo 0) {
                val tree = input[y, x]
                if (tree > maxHeight) {
                    visibles[y][x] = true
                    maxHeight = tree
                    if (maxHeight == 9.toByte()) {
                        break
                    }
                }
            }
        }

        return visibles.sumOf { row ->
            row.count { it }
        }
    }

    fun part2(input: List<String>): Int {
        val width = input.first().length
        val height = input.size
        val scenicScores = List(height) {
            MutableList(width) { -1 }
        }
        for (y in 0 until height) {
            for (x in 0 until width) {
                scenicScores[y][x] = buildScenicScore(input, height, width, y, x)
            }
        }

        return scenicScores.maxOf(Iterable<Int>::max)
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
