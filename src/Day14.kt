@file:Suppress("MagicNumber")

import kotlin.math.sign

private const val DAY = "14"

private const val PART1_CHECK = 24
private const val PART2_CHECK = 93

private enum class Rego {
    START,
    ROCK,
    SAND
}

private fun printGrid(grid: List<List<Rego?>>) {
    val withInit = grid.map { it.toMutableList() }
    withInit[0][500] = Rego.START

    val indents = withInit.map { line ->
        line.indexOfFirst { it != null }
    }.filterNot { it < 0 }
    val indent = indents.min()

    val outdents = withInit.map { line ->
        line.indexOfLast { it != null }
    }.filterNot { it < 0 }
    val outdent = 699 - outdents.max()

    withInit.forEach { line ->
        line.drop(indent).dropLast(outdent).forEach { item ->
            when (item) {
                null -> print(".")
                Rego.START -> print("+")
                Rego.ROCK -> print("#")
                Rego.SAND -> print("o")
            }
        }
        println("")
    }
}

private fun fillWithSand(grid: List<MutableList<Rego?>>): Int {
    val maxY = grid.size - 1

    var settledCount = 0
    var settled: Boolean
    var sy: Int
    do {
        sy = 0
        var sx = 500
        settled = false

        while (!settled && sy < maxY) {
            // println("Sand? $sx x $sy")
            when {
                grid[sy + 1][sx] == null -> {
                    sy += 1
                }

                grid[sy + 1][sx - 1] == null -> {
                    sy += 1
                    sx -= 1
                }

                grid[sy + 1][sx + 1] == null -> {
                    sy += 1
                    sx += 1
                }

                else -> settled = true
            }
        }
        // println("Grain done: $settled")

        if (settled) {
            settledCount += 1
            grid[sy][sx] = Rego.SAND
        }
        // printGrid(grid)
        // println("\n--------\n")
    } while (settled && sy>0)

    return settledCount
}

fun main() {
    fun part1(input: List<String>): Int {
        var grid = List(200) { MutableList<Rego?>(700) { null } }

        input.forEach { line ->
            val steps = line.split(" -> ")
                .map { tuple ->
                    val (x, y) = tuple.split(',').map(String::toInt)
                    Pair(x, y)
                }.toMutableList()
            var (cx, cy) = steps.removeFirst()
            grid[cy][cx] = Rego.ROCK
            steps.forEach { (x, y) ->
                val dx = sign(x.toDouble() - cx).toInt()
                val dy = sign(y.toDouble() - cy).toInt()
                while (cx != x || cy != y) {
                    cx += dx
                    cy += dy
                    grid[cy][cx] = Rego.ROCK
                }

            }
        }
        grid = grid
            .dropLastWhile { line -> line.all { it == null } }

        return fillWithSand(grid)
    }

    fun part2(input: List<String>): Int {
        var grid = List(200) { MutableList<Rego?>(700) { null } }

        input.forEach { line ->
            val steps = line.split(" -> ")
                .map { tuple ->
                    val (x, y) = tuple.split(',').map(String::toInt)
                    Pair(x, y)
                }.toMutableList()
            var (cx, cy) = steps.removeFirst()
            grid[cy][cx] = Rego.ROCK
            steps.forEach { (x, y) ->
                val dx = sign(x.toDouble() - cx).toInt()
                val dy = sign(y.toDouble() - cy).toInt()
                while (cx != x || cy != y) {
                    cx += dx
                    cy += dy
                    grid[cy][cx] = Rego.ROCK
                }

            }
        }
        // put in the floor
        val bottom = grid.indexOfLast { line -> line.any { it != null } }
        println("bottom! $bottom")

        val indents = grid.map { line ->
            line.indexOfFirst { it != null }
        }.filterNot { it < 0 }
        val indent = indents.min()

        val outdents = grid.map { line ->
            line.indexOfLast { it != null }
        }.filterNot { it < 0 }
        val outdent = outdents.max()

        for (x in (indent + 1 - bottom)..(outdent+bottom)) {
            grid[bottom + 2][x] = Rego.ROCK
        }

        grid = grid
            .dropLastWhile { line -> line.all { it == null } }
        printGrid(grid)

        return fillWithSand(grid)
        // return input.size
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
