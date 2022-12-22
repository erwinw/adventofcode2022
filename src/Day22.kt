@file:Suppress("MagicNumber")

private const val DAY = "22"

private const val PART1_CHECK = 6032
private const val PART2_CHECK = 14

private typealias Map = List<List<Square>>

private enum class Square {
    OPEN,
    WALLED,
    NON_EXISTENT,
}

private data class Status(
    val x: Int,
    val y: Int,
    val direction: Direction,
)

private interface Action {
    fun act(status: Status, map: Map): Status
}

private class TurnRight : Action {
    override fun toString() = "R"
    override fun act(status: Status, map: Map): Status {
        val directions = Direction.values().toList()
        val directionIndex = (directions.indexOf(status.direction) + 1) % directions.size
        return status.copy(
            direction = directions[directionIndex]
        )
    }
}

private class TurnLeft : Action {
    override fun toString() = "L"
    override fun act(status: Status, map: Map): Status {
        val directions = Direction.values().toList()
        val directionIndex = (directions.indexOf(status.direction) + directions.size - 1) % directions.size
        return status.copy(
            direction = directions[directionIndex]
        )
    }
}

private class Walk(val steps: Int) : Action {
    override fun toString() = "W($steps)"
    override fun act(status: Status, map: Map): Status {
        val height = map.size
        val width = map.first().size
        val direction = status.direction
        var newStatus = status
        repeat(steps) {step ->
            // println("Walking $step: ${newStatus.x} x ${newStatus.y}")
            var targetStatus = newStatus.copy(
                x = (newStatus.x + direction.dx + width) % width,
                y = (newStatus.y + direction.dy + height) % height,
            )
            when (map[targetStatus.y][targetStatus.x]) {
                Square.OPEN -> newStatus = targetStatus
                Square.WALLED -> return newStatus

                Square.NON_EXISTENT -> {
                    val (tx, ty) =
                        when (direction) {
                            Direction.NORTH -> {
                                // find LAST existent
                                val lastExistentY = map.indexOfLast {
                                    it[targetStatus.x] != Square.NON_EXISTENT
                                }
                                Pair(targetStatus.x, lastExistentY)
                            }

                            Direction.EAST -> {
                                // find FIRST existent
                                val firstExistentX = map[targetStatus.y].indexOfFirst {
                                    it != Square.NON_EXISTENT
                                }
                                Pair(firstExistentX, targetStatus.y)
                            }

                            Direction.SOUTH -> {
                                val firstExistentY = map.indexOfFirst {
                                    it[targetStatus.x] != Square.NON_EXISTENT
                                }
                                Pair(targetStatus.x, firstExistentY)
                            }

                            Direction.WEST -> {
                                // find LAST existent
                                val lastExistentX = map[targetStatus.y].indexOfLast {
                                    it != Square.NON_EXISTENT
                                }
                                Pair(lastExistentX, targetStatus.y)
                            }
                        }
                    targetStatus = targetStatus.copy(x = tx, y = ty)
                    when (map[targetStatus.y][targetStatus.x]) {
                        Square.WALLED -> return newStatus
                        Square.OPEN -> newStatus = targetStatus
                        else -> throw IllegalArgumentException("Target should be existent")
                    }
                }
            }
        }

        return newStatus
    }
}

private enum class Direction(val facing: Int, val dx: Int, val dy: Int) {
    NORTH(facing = 3, dx = 0, dy = -1),
    EAST(facing = 0, dx = 1, dy = 0),
    SOUTH(facing = 1, dx = 0, dy = 1),
    WEST(facing = 2, dx = -1, dy = 0),
}

fun main() {
    fun parseMap(input: List<String>): Map {
        val width = input.maxOf { it.length }
        return input.map { line ->
            val row = MutableList<Square>(width) { Square.NON_EXISTENT }
            line.forEachIndexed { index, c ->
                when (c) {
                    ' ' -> { /* nothing to do */
                    }

                    '.' -> row[index] = Square.OPEN
                    '#' -> row[index] = Square.WALLED
                    else -> throw IllegalArgumentException("Unexpected map character '$c'")
                }
            }

            row.toList()
        }
    }

    fun parseActions(input: String): List<Action> {
        val stepsDivider = Regex("\\d+|L|R")
        return buildList {
            var matchResult: MatchResult? = stepsDivider.find(input)
            while (matchResult != null) {
                add(
                    when (matchResult.value) {
                        "L" -> TurnLeft()
                        "R" -> TurnRight()
                        else -> Walk(matchResult.value.toInt())
                    }
                )
                matchResult = matchResult.next()
            }
        }
    }

    fun part1(input: List<String>): Int {
        val emptyLineIndex = input.indexOf("")
        val map = parseMap(input.take(emptyLineIndex - 1))
        val actions = parseActions(input[emptyLineIndex + 1])

        map.forEach { row ->
            val display = row.joinToString("") { square ->
                when (square) {
                    Square.OPEN -> "."
                    Square.WALLED -> "#"
                    Square.NON_EXISTENT -> " "
                }
            }
            println(display)
        }

        var status = Status(
            map.first().indexOfFirst { it == Square.OPEN },
            0,
            Direction.EAST
        )
        actions.forEach { action ->
            // println("Actioning $action")
            status = action.act(status, map)

            // println("> $action --> $status")
        }

        return (status.x+1) * 4 + (status.y+1) * 1000 + status.direction.facing
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    println("Day $DAY")
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput).also { println("Part1 output: $it") } == PART1_CHECK)
    check(part2(testInput).also { println("Part2 output: $it") } == PART2_CHECK)

    val input = readInput("Day$DAY")
    println("Part1 final output: ${part1(input)}")
    // println("Part2 final output: ${part2(input)}")
}
