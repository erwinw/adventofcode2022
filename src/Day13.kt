@file:Suppress("MagicNumber")

import kotlin.Comparator as KComparator

private const val DAY = "13"

private const val PART1_CHECK = 13
private const val PART2_CHECK = 140

private enum class Order {
    RIGHT,
    WRONG,
    UNDETERMINED
}

private sealed interface Lol {
    class Number(val value: Int) : Lol {
        override fun toString() = value.toString()
    }

    class MyList(val items: List<Lol>) : Lol {
        override fun toString() = items.joinToString(separator = ",", prefix = "[", postfix = "]")
    }
}

private object Parser {

    private fun demandInput(input: MutableList<Char>, required: Char) {
        require(input.first() == required)
        input.removeFirst()
    }

    private fun parseLolNumber(input: MutableList<Char>): Lol {
        var digits = ""
        while (input.isNotEmpty() && input.first() in '0'..'9') {
            digits += input.removeFirst()
        }
        return Lol.Number(digits.toInt())
    }

    private fun parseLolList(input: MutableList<Char>): Lol {
        val items = mutableListOf<Lol>()

        demandInput(input, '[')

        if (input.isNotEmpty() && input.first() != ']') {
            items += parse(input)
            while (input.isNotEmpty() && input.first() == ',') {
                demandInput(input, ',')
                items += parse(input)
            }
        }

        demandInput(input, ']')

        return Lol.MyList(items.toList())
    }

    fun parse(input: MutableList<Char>): Lol =
        when (input.first()) {
            '[' -> parseLolList(input)
            in '0'..'9' -> parseLolNumber(input)
            else -> throw IllegalArgumentException("Unexpected character '${input.first()}'; expected '[' or digit")
        }
}

private object Comparator {
    fun compareNumbers(
        left: Lol.Number,
        right: Lol.Number,
    ) = when {
        left.value < right.value -> Order.RIGHT
        left.value > right.value -> Order.WRONG
        else -> Order.UNDETERMINED
    }

    fun compareLists(
        left: Lol.MyList,
        right: Lol.MyList,
    ): Order {
        for (index in 0..Int.MAX_VALUE) {
            val leftItem = left.items.getOrNull(index)
            val rightItem = right.items.getOrNull(index)
            if (leftItem == null && rightItem == null) {
                break
            }
            leftItem ?: return Order.RIGHT
            rightItem ?: return Order.WRONG
            val compared = compare(leftItem, rightItem)
            if (compared != Order.UNDETERMINED) {
                return compared
            }
        }
        return Order.UNDETERMINED
    }

    fun compare(
        left: Lol,
        right: Lol,
    ): Order =
        when {
            left is Lol.Number && right is Lol.Number -> compareNumbers(left, right)
            left is Lol.Number && right is Lol.MyList -> compareLists(Lol.MyList(listOf(left)), right)
            left is Lol.MyList && right is Lol.Number -> compareLists(left, Lol.MyList(listOf(right)))
            left is Lol.MyList && right is Lol.MyList -> compareLists(left, right)
            else -> throw IllegalArgumentException("Unexpected unsupported Lol types")
        }
}

private fun part1(input: List<String>): Int {
    val listPairs = input.chunked(3)
        .map { (a, b) ->
            val lolA = Parser.parse(a.toMutableList())
            val lolB = Parser.parse(b.toMutableList())
            Pair(lolA, lolB)
        }
    val correctItems = mutableListOf<Int>()
    listPairs.forEachIndexed { index, (lolLeft, lolRight) ->
        if (Comparator.compare(lolLeft, lolRight) == Order.RIGHT) {
            correctItems += (1 + index)
        }
    }
    return correctItems.sum()
}

private fun part2(input: List<String>): Int {
    val divider2 = Lol.MyList(listOf(Lol.MyList(listOf(Lol.Number(2)))))
    val divider6 = Lol.MyList(listOf(Lol.MyList(listOf(Lol.Number(6)))))
    val packets = input.chunked(3)
        .flatMap { (a, b) ->
            val lolA = Parser.parse(a.toMutableList())
            val lolB = Parser.parse(b.toMutableList())
            listOf(lolA, lolB)
        } +
        divider2 +
        divider6
    val sorted = packets.sortedWith { o1, o2 ->
        when (Comparator.compare(o1, o2)) {
            Order.RIGHT -> -1
            Order.WRONG -> 1
            Order.UNDETERMINED -> 0
        }
    }
    
    val idxDivider2 = sorted.indexOf(divider2) + 1
    val idxDivider6 = sorted.indexOf(divider6) + 1

    return idxDivider2 * idxDivider6
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
