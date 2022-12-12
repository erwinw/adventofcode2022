@file:Suppress("MagicNumber")

private const val DAY = "12"

private const val PART1_CHECK = 31
private const val PART2_CHECK = 29

private data class Cell(
    val elevation: Int,
    val x: Int,
    val y: Int,
    var previous: Cell? = null,
)

private fun part1(input: List<String>): Int {
    lateinit var start: Cell
    lateinit var end: Cell
    val grid = input.mapIndexed { indexY, row ->
        row.mapIndexed { indexX, c ->
            val elevation =
                when (c) {
                    'S' -> 0
                    'E' -> 25
                    else -> c - 'a'
                }
            val cell = Cell(elevation, indexX, indexY)
            when (c) {
                'S' -> start = cell
                'E' -> end = cell
            }
            cell
        }
    }
    val maxY = grid.size - 1
    val maxX = grid.first().size - 1

    // use OSPF starting from the start
    val toEvaluate = mutableListOf(start)
    while (toEvaluate.isNotEmpty()) {
        val currentRound = toEvaluate.toList()
        toEvaluate.removeIf { true }
        currentRound.forEach { currentCell ->
            val accessibleElevation = 0..currentCell.elevation + 1
            val candidates =
                listOfNotNull(
                    grid.takeIf { currentCell.y > 0 }?.get(currentCell.y - 1)?.get(currentCell.x),
                    grid.takeIf { currentCell.y < maxY }?.get(currentCell.y + 1)?.get(currentCell.x),
                    grid.takeIf { currentCell.x > 0 }?.get(currentCell.y)?.get(currentCell.x - 1),
                    grid.takeIf { currentCell.x < maxX }?.get(currentCell.y)?.get(currentCell.x + 1),
                )
                    .filter { it.previous == null && it.elevation in accessibleElevation }
            candidates.forEach { it.previous = currentCell }
            toEvaluate.addAll(candidates)
        }
    }

    // backtrack until we reach start
    var steps = 0
    var current = end
    while (current != start) {
        steps += 1
        current = current.previous ?: throw IllegalArgumentException("Current $current lacks previous")
    }

    return steps
}

private fun part2(input: List<String>): Int {
    val starts = mutableListOf<Cell>()
    lateinit var end: Cell
    val grid = input.mapIndexed { indexY, row ->
        row.mapIndexed { indexX, c ->
            val elevation =
                when (c) {
                    'S' -> 0
                    'E' -> 25
                    else -> c - 'a'
                }
            val cell = Cell(elevation, indexX, indexY)
            when {
                c == 'E' -> end = cell
                elevation == 0 -> starts.add(cell)
            }
            cell
        }
    }
    val maxY = grid.size - 1
    val maxX = grid.first().size - 1
    // use OSPF, but now we start from *any* starting point with elevation 0
    val toEvaluate = starts.toMutableList()
    while (toEvaluate.isNotEmpty()) {
        val currentRound = toEvaluate.toList()
        toEvaluate.removeIf { true }
        currentRound.forEach { currentCell ->
            val accessibleElevation = 0..currentCell.elevation + 1
            val candidates =
                listOfNotNull(
                    grid.takeIf { currentCell.y > 0 }?.get(currentCell.y - 1)?.get(currentCell.x),
                    grid.takeIf { currentCell.y < maxY }?.get(currentCell.y + 1)?.get(currentCell.x),
                    grid.takeIf { currentCell.x > 0 }?.get(currentCell.y)?.get(currentCell.x - 1),
                    grid.takeIf { currentCell.x < maxX }?.get(currentCell.y)?.get(currentCell.x + 1),
                )
                    .filter { it.previous == null && it.elevation in accessibleElevation }
            candidates.forEach { it.previous = currentCell }
            toEvaluate.addAll(candidates)
        }
    }


    // backtrack until we reach *any* point with elevation zero
    var steps = 0
    var current = end
    while (current.elevation > 0) {
        steps += 1
        current = current.previous ?: throw IllegalArgumentException("Current $current lacks previous")
    }

    return steps
}

fun main() {
    println("Day $DAY")
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput).also { println("Part1 output: $it") } == PART1_CHECK)
    check(part2(testInput).also { println("Part2 output: $it") } == PART2_CHECK)

    val input = readInput("Day$DAY")
    println("Part1 final output: ${part1(input)}")
    println("Part2 final output: ${part2(input)}")
}
