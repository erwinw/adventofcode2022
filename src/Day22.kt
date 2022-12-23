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
    val direction: D22Direction,
)

private interface Action {
    fun act(status: Status, map: Map): Status
    fun act2(status: Status, map: Map): Status
}

private class TurnRight : Action {
    override fun toString() = "R"
    override fun act(status: Status, map: Map): Status {
        val directions = D22Direction.values().toList()
        val directionIndex = (directions.indexOf(status.direction) + 1) % directions.size
        return status.copy(
            direction = directions[directionIndex]
        )
    }

    override fun act2(status: Status, map: Map): Status {
        val directions = D22Direction.values().toList()
        val directionIndex = (directions.indexOf(status.direction) + 1) % directions.size
        return status.copy(
            direction = directions[directionIndex]
        )
    }
}

private class TurnLeft : Action {
    override fun toString() = "L"
    override fun act(status: Status, map: Map): Status {
        val directions = D22Direction.values().toList()
        val directionIndex = (directions.indexOf(status.direction) + directions.size - 1) % directions.size
        return status.copy(
            direction = directions[directionIndex]
        )
    }

    override fun act2(status: Status, map: Map): Status {
        val directions = D22Direction.values().toList()
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
        repeat(steps) { step ->
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
                            D22Direction.NORTH -> {
                                // find LAST existent
                                val lastExistentY = map.indexOfLast {
                                    it[targetStatus.x] != Square.NON_EXISTENT
                                }
                                Pair(targetStatus.x, lastExistentY)
                            }

                            D22Direction.EAST -> {
                                // find FIRST existent
                                val firstExistentX = map[targetStatus.y].indexOfFirst {
                                    it != Square.NON_EXISTENT
                                }
                                Pair(firstExistentX, targetStatus.y)
                            }

                            D22Direction.SOUTH -> {
                                val firstExistentY = map.indexOfFirst {
                                    it[targetStatus.x] != Square.NON_EXISTENT
                                }
                                Pair(targetStatus.x, firstExistentY)
                            }

                            D22Direction.WEST -> {
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

    override fun act2(status: Status, map: Map): Status {
        val height = map.size
        val width = map.first().size
        val direction = status.direction
        var newStatus = status
        repeat(steps) { step ->
            println("Walking $step: ${newStatus.x} x ${newStatus.y}")
            var targetStatus = lookAhead(newStatus, direction.dx, direction.dy, map).also {
                println("LookedA: $it")
            }
            when (map[targetStatus.y][targetStatus.x]) {
                Square.OPEN -> newStatus = targetStatus
                Square.WALLED -> return newStatus

                Square.NON_EXISTENT -> {
                    val dx = direction.dx
                    val dy = direction.dy
                    targetStatus = lookAhead(targetStatus, dx, dy, map).also {
                        println("LookedB: $it")
                    }
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

    private fun lookAhead(status: Status, dx: Int, dy: Int, map: Map): Status {
        println("Look ahead ${status}, dx/dy $dx x $dy")
        val width = map.first().size
        val height = map.size
        val tileWidth = width / 4
        var resultX = status.x + dx
        var resultY = status.y + dy
        if (resultX < 0) {
            // left of area 2 -> bottom of area 6!
            val tileOffset = resultY % tileWidth
            return Status(width - tileOffset, height - 1, D22Direction.NORTH)
        }
        if (resultY < 0) {
            // top of area 1 -> top of area 2
            val tileOffset = resultX % tileWidth
            return Status(
                tileWidth - tileOffset - 1,
                tileWidth,
                D22Direction.SOUTH
            )
        }
        if (resultX >= width) {
            // right of 6 -> right of 1
            val tileOffset = resultY % tileWidth
            return Status(
                tileWidth - tileOffset - 1,
                2 * tileWidth - 1,
                D22Direction.WEST
            )
        }
        if (resultY >= height) {
            val tileOffset = resultX % tileWidth
            return if (resultX < 2 * tileWidth) {
                // down of 5 -> bottom of 2
                Status(
                    tileWidth - 1 - tileOffset,
                    2 * tileWidth - 1,
                    D22Direction.NORTH
                )
            } else {
                // down of 6 -> left of 2
                Status(
                    0,
                    2 * tileWidth - 1 - tileOffset,
                    D22Direction.EAST
                )
            }
        }
        println("Observed: ${map[resultY][resultX]}")
        if (map[resultY][resultX] != Square.NON_EXISTENT) {
            return Status(
                resultX,
                resultY,
                status.direction
            )
        }
        return when {
            // west of 1 -> 3 going south
            status.direction == D22Direction.WEST && resultX < 2 * tileWidth && resultY < tileWidth -> {
                val tileOffset = resultY % tileWidth
                Status(
                    tileWidth + tileOffset,
                    tileWidth,
                    D22Direction.SOUTH,
                )
            }

            //  north of 3 -> 1 going east
            status.direction == D22Direction.NORTH && resultX in tileWidth until 2 * tileWidth && resultY < tileWidth -> {
                val tileOffset = resultY % tileWidth
                Status(
                    2 * tileWidth,
                    tileOffset,
                    D22Direction.EAST
                )
            }

            // east of 1 -> 6 going west
            status.direction == D22Direction.EAST && resultX >= 2 * tileWidth && resultY < tileWidth -> {
                val tileOffset = resultY % tileWidth
                Status(
                    width - 1,
                    height - tileOffset - 1,
                    D22Direction.WEST
                )
            }

            // east of 4 -> 6 going south
            status.direction == D22Direction.EAST && resultX >= 2 * tileWidth && resultY in tileWidth until 2 * tileWidth -> {
                val tileOffset = resultY % tileWidth
                Status(
                    width - tileOffset,
                    2 * tileWidth - 1,
                    D22Direction.SOUTH
                )
            }

            // south of 2 -> 5 going north
            status.direction == D22Direction.SOUTH && resultX < tileWidth -> {
                val tileOffset = resultX % tileWidth
                Status(
                    2 * tileWidth - 1 - tileOffset,
                    height - 1,
                    D22Direction.NORTH
                )
            }

            // South of 3 -> 5 going east
            status.direction == D22Direction.SOUTH && resultX in tileWidth until 2 * tileWidth -> {
                val tileOffset = resultX % tileWidth
                Status(
                    2 * tileWidth - 1,
                    height - tileOffset - 1,
                    D22Direction.EAST
                )
            }

            // West of 5 -> 3 going north
            status.direction == D22Direction.WEST && resultY >= 2 * tileWidth -> {
                val tileOffset = resultY % tileWidth
                Status(
                    2 * tileWidth - 1 - tileOffset,
                    2 * tileWidth - 1,
                    D22Direction.NORTH
                )
            }

            else -> throw IllegalArgumentException("UNexpected status ${status.x} => ${status.y} $resultX x $resultX, ${status.direction} [$tileWidth]")
        }
    }
}

private enum class D22Direction(val facing: Int, val dx: Int, val dy: Int) {
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

        var status = Status(
            map.first().indexOfFirst { it == Square.OPEN },
            0,
            D22Direction.EAST
        )
        actions.forEach { action ->
            // println("Actioning $action")
            status = action.act(status, map)

            // println("> $action --> $status")
        }

        return (status.x + 1) * 4 + (status.y + 1) * 1000 + status.direction.facing
    }

    fun part2(input: List<String>): Int {
        val emptyLineIndex = input.indexOf("")
        val map = parseMap(input.take(emptyLineIndex - 1))
        val actions = parseActions(input[emptyLineIndex + 1])

        var status = Status(
            map.first().indexOfFirst { it == Square.OPEN },
            0,
            D22Direction.EAST
        )
        actions.forEach { action ->
            println("Actioning $action")
            status = action.act2(status, map)

            println("> $action --> $status")
        }

        return (status.x + 1) * 4 + (status.y + 1) * 1000 + status.direction.facing
    }

    println("Day $DAY")
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput).also { println("Part1 output: $it") } == PART1_CHECK)
    check(part2(testInput).also { println("Part2 output: $it") } == PART2_CHECK)

    val input = readInput("Day$DAY")
    println("Part1 final output: ${part1(input)}")
    // println("Part2 final output: ${part2(input)}")
}
