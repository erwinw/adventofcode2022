@file:Suppress("MagicNumber")

private const val DAY = "20"

private const val PART1_CHECK = 3
private const val PART2_CHECK = 7

fun main() {
    fun <T> MutableList<T>.insertAt(position: Int, element: T) {
        val size = size
        add(this[size - 1])
        for (i in size downTo (position + 1)) {
            this[i] = this[i - 1]
        }
        this[position] = element
    }

    fun <T> MutableList<T>.swapLeft(position: Int, swapCount: Int) {
        var targetPosition = (position - swapCount + size) % size
        val subject = removeAt(position)
        println("!Left:A $targetPosition")
        if (targetPosition > position) {
            targetPosition -= 1
        }
        if (targetPosition < 0) {
            targetPosition += size
        }
        if (targetPosition == 0) {
            targetPosition = size
        }
        println("!Left:B $targetPosition")
        insertAt(targetPosition, subject)
        println("Swap left $swapCount")
    }

    fun <T> MutableList<T>.swapRight(position: Int, swapCount: Int) {
        var targetPosition = position + swapCount
        val subject = removeAt(position)
        println("!Right:A $targetPosition")
        while (targetPosition > size) {
            targetPosition -= size
        }
        println("!Right:B $targetPosition")
        insertAt(targetPosition, subject)
        println("Swap right $swapCount")
    }

    fun <T> MutableList<T>.swap(position: Int, swapCount: Int) {
        when {
            swapCount < 0 -> swapLeft(position, -swapCount)
            swapCount > 0 -> swapRight(position, swapCount)
            else -> {
                //
            }
        }
    }

    fun part1(input: List<String>): Int {
        val toRemix = input.map(String::toInt)
        val result = toRemix.toMutableList()
        toRemix.forEach { subject ->
            val initialPosition = result.indexOf(subject)
            println("Move '$subject' from '$initialPosition' to '$subject'")
            result.swap(initialPosition, subject)

            println(">> ${result.joinToString()}\n")
        }
        val pos0 = result.indexOf(0)
        val val1000 = result[(pos0 + 1000) % result.size]
        val val2000 = result[(pos0 + 2000) % result.size]
        val val3000 = result[(pos0 + 3000) % result.size]
        return val1000 + val2000 + val3000
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
    println("Part2 final output: ${part2(input)}")
}
