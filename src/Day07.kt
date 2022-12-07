@file:Suppress("MagicNumber")

private const val DAY = "07"

private const val PART1_CHECK = 95437
private const val PART2_CHECK = 24933642

abstract class FsEntry(
    open val name: String,
    val parent: Directory?,
) {

    abstract fun print(depth: Int = 0)
    abstract val size: Int

    open fun printLnIndented(depth: Int, message: String) {
        println("${"  ".repeat(depth)}$message")
    }

    fun path(): String = listOfNotNull(parent?.path(), name).joinToString("/")
}

class Directory(
    name: String,
    parent: Directory?,
) : FsEntry(
    name,
    parent,
) {
    var entries: MutableList<FsEntry> = mutableListOf()

    override fun print(depth: Int) {
        printLnIndented(depth, "- $name (dir, size=$size)")
        entries.forEach { it.print(depth + 1) }
    }

    override val size: Int
        get() = entries.sumOf { it.size }
}

class File(
    name: String,
    parent: Directory?,
    override val size: Int,
) : FsEntry(
    name,
    parent,
) {
    override fun print(depth: Int) {
        printLnIndented(depth, "- $name (file, size=$size)")
    }
}

private fun parseFs(input: List<String>): Pair<Directory, List<Directory>> {
    val root = Directory("/", null)
    var currentDirectory = root
    val allDirectories = mutableListOf(root)

    val inputStack = input.toMutableList()
    while (inputStack.isNotEmpty()) {
        val line = inputStack.removeFirst()
        when (line.take(5)) {
            "$ cd " -> {
                val targetDirectoryName = line.drop(5)
                currentDirectory =
                    when (targetDirectoryName) {
                        "/" -> root
                        ".." -> currentDirectory.parent
                            ?: throw IllegalArgumentException("Cannot move up from root")

                        else -> {
                            currentDirectory.entries
                                .filterIsInstance<Directory>()
                                .firstOrNull { it.name == targetDirectoryName }
                                ?: throw IllegalAccessException("Target directory '$targetDirectoryName' not found")
                        }
                    }
            }

            "$ ls" -> {
                while (inputStack.isNotEmpty() && inputStack.first()[0] != '$') {
                    // ls output
                    val (sizeType, entryName) = inputStack.removeFirst().split(' ')
                    val newEntry =
                        if (sizeType == "dir") {
                            Directory(entryName, currentDirectory).also {
                                allDirectories.add(it)
                            }
                        } else {
                            File(entryName, currentDirectory, sizeType.toInt())
                        }
                    currentDirectory.entries.add(newEntry)
                }
            }
        }
    }
    return root to allDirectories
}

fun main() {
    fun part1(input: List<String>): Int {
        val (root, allDirectories) = parseFs(input)
        root.print()
        return allDirectories.filter { it.size <= 100_000 }.sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val (root, allDirectories) = parseFs(input)
        val totalSpace = 70_000_000
        val requiredSpace = 30_000_000
        val freeSpace = totalSpace - root.size
        val missingSpace = requiredSpace - freeSpace
        println("Missing space: $missingSpace")

        val toDelete = allDirectories
            .filter { it.size >= missingSpace }
            .minByOrNull { it.size }
        println("To delete: ${toDelete?.size} (${toDelete?.path()})")

        return toDelete?.size ?: 0
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
