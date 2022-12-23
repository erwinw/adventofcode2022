@file:Suppress("MagicNumber")

private const val DAY = "23"

private const val PART1_CHECK = 110
private const val PART2_CHECK = 7

private data class D23Coord(
    var x: Int,
    var y: Int,
)

private enum class D23Direction(val move: D23Coord, val scan: List<D23Coord>) {
    NORTH(
        D23Coord(0, -1),
        listOf(D23Coord(-1, -1), D23Coord(0, -1), D23Coord(1, -1))
    ),
    EAST(
        D23Coord(1, 0),
        listOf(D23Coord(1, -1), D23Coord(1, 0), D23Coord(1, 1))
    ),
    SOUTH(
        D23Coord(0, 1),
        listOf(D23Coord(1, 1), D23Coord(0, 1), D23Coord(-1, 1))
    ),
    WEST(
        D23Coord(-1, 0),
        listOf(D23Coord(-1, 1), D23Coord(-1, 0), D23Coord(-1, -1))
    ),
}

fun main() {
    val allAroundOffsets: List<D23Coord> = buildList {
        for (dx in -1..1) {
            for (dy in -1..1) {
                if (dx != 0 || dy != 0) {
                    add(D23Coord(dx, dy))
                }
            }
        }
    }

    data class Elf(
        var position: D23Coord,
        var proposed: D23Coord = D23Coord(0, 0),
    ) {
        lateinit var allAround: Map<D23Coord, Elf?>

        fun matchPosition(x: Int, y: Int) = x == position.x && y == position.y
        infix fun matchProposed(c: D23Coord) = c.x == proposed.x && c.y == proposed.y

        fun noneAround(elves: List<Elf>): Boolean {
            allAround =
                allAroundOffsets
                    .map { coord ->
                        val x = position.x + coord.x
                        val y = position.y + coord.y
                        coord to elves.firstOrNull { it.matchPosition(x, y) }
                    }
                    .filterNot { (_, elf) -> elf == null }
                    .toMap()
            // println("All around $this:\n$allAround\n")
            return allAround.isEmpty()
        }

        fun attempt(direction: D23Direction, elves: List<Elf>): Boolean {
            val anyInSight = direction.scan.firstNotNullOfOrNull { dir ->
                allAround[dir].also {
                    // if (it != null) {
                    //     println("In sight $dir: $it")
                    // }
                }
            } != null
            // println("Elf $this attempt $direction; in sight? $anyInSight")
            if (anyInSight) {
                return false
            }
            proposed = D23Coord(position.x + direction.move.x, position.y + direction.move.y)

            return true
        }
    }

    fun printMap(elves: List<Elf>) {
        val minX = elves.minOf { it.position.x }
        val maxX = elves.maxOf { it.position.x }
        val minY = elves.minOf { it.position.y }
        val maxY = elves.maxOf { it.position.y }
        for (y in minY..maxY) {
            val row = (minX..maxX).map { x ->
                '#'.takeIf { elves.any { it.matchPosition(x, y) } } ?: '.'
            }
            println(row.joinToString(""))
        }
    }

    fun empties(elves: List<Elf>): Int {
        val minX = elves.minOf { it.position.x }
        val maxX = elves.maxOf { it.position.x }
        val minY = elves.minOf { it.position.y }
        val maxY = elves.maxOf { it.position.y }
        var empties = 0
        for (y in minY..maxY) {
            for(x in minX..maxX) {
                if ( elves.none { it.matchPosition(x, y) }) {
                    empties += 1
                }
            }
        }
        return empties
    }

    fun part1(input: List<String>): Int {
        val elves: List<Elf> = buildList {
            input.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    if (c == '#') {
                        add(Elf(D23Coord(x, y)))
                    }
                }
            }
        }

        printMap(elves)

        val directions = listOf(
            D23Direction.NORTH,
            D23Direction.SOUTH,
            D23Direction.WEST,
            D23Direction.EAST,
        )

        repeat(10) { cycle ->
            println("ROUND $cycle")

            val attemptedDirections = (0..3).map { directions[(it + cycle) % 4] }
            println("Attempted directions: $attemptedDirections")

            // propose
            elves.forEach { elf ->
                if (elf.noneAround(elves)) {
                    // println("None around: $elf")
                    elf.proposed = elf.position
                } else {
                    attemptedDirections.firstOrNull { attemptedDirection ->
                        elf.attempt(attemptedDirection, elves).also {
                            // println("Elf $elf attempted $attemptedDirection ?: $it")
                        }
                    }
                }
            }

            // action
            elves.forEach { currentElf ->
                val targetingSpot = elves.count { it matchProposed currentElf.proposed }
                if (targetingSpot == 1) {
                    currentElf.position = currentElf.proposed
                }
                currentElf.proposed = D23Coord(0, 0)
            }

            printMap(elves)
            println()
        }

        return empties(elves)
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
    // too low: 3908
    // too high: 4155
    println("Part2 final output: ${part2(input)}")
}
