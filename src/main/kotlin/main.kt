enum class Bracket(val open: Char, val close: Char) {
    ROUND('(', ')'),
    SQUARE('[', ']'),
    CURLY('{', '}');

    companion object {
        fun sequence(prop: String): List<Bracket> =
            prop.toCharArray().map { char ->
                when (char.toLowerCase()) {
                    'r' -> ROUND
                    's' -> SQUARE
                    'c' -> CURLY
                    else -> error("Invalid bracket type '$char', use 'r', 's' or 'c'")
                }
            }
    }
}

data class Options(
    val placeBySides: Boolean,
    val placeInMiddle: Boolean,
    val bracketSequence: List<Bracket>
) {
    companion object {
        fun read(args: Array<String>): Pair<Options, List<String>> {
            var placeBySides = false;
            var placeInMiddle = false;
            var bracketSequence = listOf(Bracket.ROUND)

            val data = ArrayList<String>()

            for (arg in args) {
                if (arg.startsWith("--")) {
                    try {
                        val (option, value) = arg.split('=', limit = 2)
                        when (option) {
                            "--by-sides" -> placeBySides = (value == "yes")
                            "--in-middle" -> placeInMiddle = (value == "yes")
                            "--sequence" -> bracketSequence = Bracket.sequence(value)
                            else -> error("Unknown option '$option'")
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        error("Option syntax is '--<option>=<value>', not '$arg'")
                    }
                } else {
                    data.add(arg)
                }
            }

            return Pair(Options(placeBySides, placeInMiddle, bracketSequence), data)
        }
    }
}

infix fun Boolean.implies(value: Int): Int {
    return if (this) value else 0
}

fun StringBuilder.appendIf(flag: Boolean, value: Any): StringBuilder =
    if (flag) append(value) else this

fun String.transform(options: Options): String {
    val defaultBracketsCount = length + length % 2 - 2
    val bracketsCount = length + defaultBracketsCount +
            (options.placeBySides implies 2) +
            ((length % 2 == 0 && options.placeInMiddle) implies 2)

    var currentBracketNumber = 0
    fun nextBracket(): Char {
        return options.bracketSequence[currentBracketNumber].open.also {
            currentBracketNumber++
            if (currentBracketNumber == options.bracketSequence.size) {
                currentBracketNumber = 0
            }
        }
    }
    fun previousBracket(): Char {
        currentBracketNumber--;
        if (currentBracketNumber == -1) {
            currentBracketNumber = options.bracketSequence.size - 1
        }
        return options.bracketSequence[currentBracketNumber].close
    }

    val wordLength = length
    return buildString(bracketsCount) {
        if (options.placeBySides) {
            append(nextBracket())
        }
        this@transform.forEachIndexed { index, char ->
            val marker = 2 * index
            when {
                marker < defaultBracketsCount -> append(char, nextBracket())
                marker == wordLength - 2 -> append(char).appendIf(options.placeInMiddle, nextBracket())
                marker == wordLength -> appendIf(options.placeInMiddle, previousBracket()).append(char)
                marker >= 2 * wordLength - defaultBracketsCount -> append(previousBracket(), char)
                else -> append(char)
            }
        }
        if (options.placeBySides) {
            append(previousBracket())
        }
    }
}

fun main(args: Array<String>) {
    val (options, data) = Options.read(args)
    data.forEach { word ->
        println(word.transform(options))
    }
}