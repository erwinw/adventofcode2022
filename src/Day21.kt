@file:Suppress("MagicNumber")

private const val DAY = "21"

private const val PART1_CHECK = 152L
private const val PART2_CHECK = 301L

private object HumanException : Exception("To come from human")

private interface Op {
    fun calculate(monkeys: Map<String, Op>): Long
    fun reverseCalculate(target: Long, monkeys: Map<String, Op>): Long
}

private abstract class BinaryOp(
    val opA: String,
    val opB: String,
) : Op {
    abstract fun reverseHumanA(target: Long, valB: Long, monkeys: Map<String, Op>): Long
    abstract fun reverseHumanB(target: Long, valA: Long, monkeys: Map<String, Op>): Long

    override fun reverseCalculate(target: Long, monkeys: Map<String, Op>): Long {
        val valA = try { calculate(opA, monkeys) } catch (_: HumanException) { null }
        val valB = try { calculate(opB, monkeys) } catch (_: HumanException) { null }
        return when {
            valA == null && valB != null -> reverseHumanA(target, valB, monkeys)
            valA != null && valB == null -> reverseHumanB(target, valA, monkeys)
            else -> throw IllegalArgumentException("Cannot reverse operation")
        }
    }
}

private class HumanOp : Op {
    override fun calculate(monkeys: Map<String, Op>): Long =
        throw HumanException
    override fun reverseCalculate(target: Long, monkeys: Map<String, Op>): Long =
        throw HumanException
}

private class ConstantOp(val value: Long) : Op {
    override fun calculate(monkeys: Map<String, Op>): Long = value
    override fun reverseCalculate(target: Long, monkeys: Map<String, Op>): Long =
        throw IllegalArgumentException("Cannot reverse a constant")
}

private class AddOp(
    opA: String,
    opB: String,
) : BinaryOp(opA, opB) {
    override fun calculate(monkeys: Map<String, Op>): Long =
        calculate(opA, monkeys) + calculate(opB, monkeys)

    override fun reverseHumanA(target: Long, valB: Long, monkeys: Map<String, Op>): Long =
        reverseCalculate(opA, target - valB, monkeys)

    override fun reverseHumanB(target: Long, valA: Long, monkeys: Map<String, Op>): Long =
        reverseCalculate(opB, target - valA, monkeys)
}

private class SubtractOp(
    opA: String,
    opB: String,
) : BinaryOp(opA, opB) {
    override fun calculate(monkeys: Map<String, Op>): Long =
        calculate(opA, monkeys) - calculate(opB, monkeys)

    override fun reverseHumanA(target: Long, valB: Long, monkeys: Map<String, Op>): Long =
        reverseCalculate(opA, valB + target, monkeys)

    override fun reverseHumanB(target: Long, valA: Long, monkeys: Map<String, Op>): Long =
        reverseCalculate(opB, valA - target, monkeys)
}

private class MultiplyOp(
    opA: String,
    opB: String,
) : BinaryOp(opA, opB) {
    override fun calculate(monkeys: Map<String, Op>): Long =
        calculate(opA, monkeys) * calculate(opB, monkeys)

    override fun reverseHumanA(target: Long, valB: Long, monkeys: Map<String, Op>): Long =
        reverseCalculate(opA, target / valB, monkeys)

    override fun reverseHumanB(target: Long, valA: Long, monkeys: Map<String, Op>): Long =
        reverseCalculate(opB, target / valA, monkeys)
}

private class DivideOp(
    opA: String,
    opB: String,
) : BinaryOp(opA, opB) {
    override fun calculate(monkeys: Map<String, Op>): Long =
        calculate(opA, monkeys) / calculate(opB, monkeys)

    override fun reverseHumanA(target: Long, valB: Long, monkeys: Map<String, Op>): Long =
        reverseCalculate(opA, valB * target, monkeys)

    override fun reverseHumanB(target: Long, valA: Long, monkeys: Map<String, Op>): Long =
        reverseCalculate(opB, valA / target, monkeys)
}

private fun calculate(name: String, monkeys: Map<String, Op>): Long =
    monkeys[name]?.calculate(monkeys) ?: throw IllegalArgumentException("Failed to find monkey '$name'")

private fun reverseCalculate(name: String, target: Long, monkeys: Map<String, Op>): Long =
    if (name == "humn") {
        target
    } else {
        monkeys[name]?.reverseCalculate(target, monkeys)
            ?: throw IllegalArgumentException("Failed to find monkey '$name'")
    }

fun main() {
    val digitsRegex = Regex("^\\d+$")
    fun parseMonkeys(input: List<String>): Map<String, Op> =
        input.associate { line ->
            val (name, opString) = line.split(": ", limit = 2)
            if (digitsRegex.matches(opString)) {
                name to ConstantOp(opString.toLong())
            } else {
                val (operandA, operator, operandB) = opString.split(' ')
                val op =
                    when (operator) {
                        "+" -> ::AddOp
                        "-" -> ::SubtractOp
                        "*" -> ::MultiplyOp
                        "/" -> ::DivideOp
                        else -> throw IllegalArgumentException("Unexpected operator '$operator'")
                    }
                name to op(operandA, operandB)
            }
        }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        return calculate("root", monkeys)
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input).toMutableMap()
        monkeys["humn"] = HumanOp()
        val root = monkeys["root"] as BinaryOp
        val rootA = try { calculate(root.opA, monkeys) } catch (_: HumanException) { null }
        val rootB = try { calculate(root.opB, monkeys) } catch (_: HumanException) { null }
        println("RootA: $rootA")
        println("RootB: $rootB")
        return when {
            rootA == null && rootB != null -> reverseCalculate(root.opA, rootB, monkeys)
            rootA != null && rootB == null -> reverseCalculate(root.opB, rootA, monkeys)
            else -> throw IllegalArgumentException("Expected either operand of root to be null")
        }
    }

    println("Day $DAY")
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput).also { println("Part1 output: $it") } == PART1_CHECK)
    check(part2(testInput).also { println("Part2 output: $it") } == PART2_CHECK)

    val input = readInput("Day$DAY")
    // Wrong: 1114691800
    println("Part1 final output: ${part1(input)}")
    println("Part2 final output: ${part2(input)}")
}
