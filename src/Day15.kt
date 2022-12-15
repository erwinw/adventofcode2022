@file:Suppress("MagicNumber")

import kotlin.math.abs

private const val DAY = "15"

private const val PART1_CHECK = 26
private const val PART2_CHECK = 56000011L

fun main() {
    data class Coordinate(
        val x: Int,
        val y: Int,
    ) {
        fun distance(other: Coordinate): Int =
            abs(x - other.x) + abs(y - other.y)
    }

    data class SensorBeacon(
        val sensor: Coordinate,
        val beacon: Coordinate,
        val distance: Int = sensor.distance(beacon),
    )

    val parseRegexp = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
    fun parseSensorBeacons(input: List<String>): List<SensorBeacon> =
        @Suppress("DestructuringDeclarationWithTooManyEntries")
        input.map { line ->
            val (sx, sy, bx, by) = parseRegexp.matchEntire(line)!!.groupValues.drop(1).map(String::toInt)
            SensorBeacon(Coordinate(sx, sy), Coordinate(bx, by))
        }

    fun part1(input: List<String>, targetY: Int): Int {
        val sensorBeacons = parseSensorBeacons(input)
        val minX = sensorBeacons.minOf { (sensor, beacon) ->
            sensor.x - sensor.distance(beacon)
        } - 100
        val maxX = sensorBeacons.maxOf { (sensor, beacon) ->
            sensor.x + sensor.distance(beacon)
        } + 100

        val cannotContainCount = (minX..maxX).count { x ->
            sensorBeacons.any { (sensor, beacon, seeDistance) ->
                if (beacon.y == targetY && beacon.x == x) {
                    return@count false
                }
                val atDistance = sensor.distance(Coordinate(x, targetY))
                val canSee = atDistance <= seeDistance
                // if (canSee) {
                //     println("X: $x, Sensor $sensor (beacon $beacon, distance $seeDistance), $atDistance")
                // }
                canSee
            }
        }
        println("CannotContainCount: $cannotContainCount")

        return cannotContainCount
    }

    fun subtractLor(parts: List<IntRange>, operand: IntRange): List<IntRange> = buildList {
        parts.forEach { part ->
            when {
                // no overlap
                operand.last < part.first ||
                    operand.first > part.last -> {
                    add(part)
                }

                // complete overlap
                operand.first <= part.first && operand.last >= part.last -> {
                    // skip
                }

                else -> {
                    // head
                    if (part.first < operand.first) {
                        add(part.first until operand.first)
                    }
                    // tail
                    if (part.last > operand.last) {
                        add(operand.last + 1..part.last)
                    }
                }
            }
        }
    }

    fun part2(input: List<String>, maxDx: Int): Long {
        val sensorBeacons = parseSensorBeacons(input)

        fun getCandidate(y: Int): Int? {
            // For each row we create a range of candidates, then subtract the range of each sensor for that row
            var candidates = listOf(0..maxDx)
            sensorBeacons.forEach { (sensor, _, distance) ->
                val dx = distance - abs(sensor.y - y)
                if (dx < 0) {
                    return@forEach
                }
                val sensorRange = (sensor.x - dx)..(sensor.x + dx)
                candidates = subtractLor(candidates, sensorRange)
                if (candidates.isEmpty()) {
                    return null
                }
            }
            return candidates.firstOrNull()?.first
        }

        for (y in 0..maxDx) {
            getCandidate(y)?.let { x ->
                return x.toLong() * 4_000_000 + y
            }
        }

        return 0
    }

    println("Day $DAY")
    val testInput = readInput("Day${DAY}_test")
    check(part1(testInput, 10).also { println("Part1 output: $it") } == PART1_CHECK)
    check(part2(testInput, 20).also { println("Part2 output: $it") } == PART2_CHECK)

    val input = readInput("Day$DAY")
    println("Part1 final output: ${part1(input, 2_000_000)}")
    println("Part2 final output: ${part2(input, 4_000_000)}")
}
