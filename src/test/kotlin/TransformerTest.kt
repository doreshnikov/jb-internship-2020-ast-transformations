import org.junit.jupiter.api.Assertions.*

internal class TransformerTest {

    @org.junit.jupiter.api.Test
    fun transformEvenWithSides() {
        assertEquals("abcdefgh".transform(Options(
            placeBySides = true,
            placeInMiddle = false,
            listOf(Bracket.CURLY, Bracket.ROUND)
        )), "{a(b{c(de)f}g)h}")
    }

    @org.junit.jupiter.api.Test
    fun transformEvenWithMiddle() {
        assertEquals("abcdefgh".transform(Options(
            placeBySides = false,
            placeInMiddle = true,
            listOf(Bracket.SQUARE, Bracket.CURLY, Bracket.ROUND)
        )), "a[b{c(d[]e)f}g]h")
    }

    @org.junit.jupiter.api.Test
    fun transformOddWithSides() {
        assertEquals("abcdefghi".transform(Options(
            placeBySides = true,
            placeInMiddle = false,
            listOf(Bracket.SQUARE, Bracket.ROUND, Bracket.SQUARE)
        )), "[a(b[c[d(e)f]g]h)i]")
    }

    @org.junit.jupiter.api.Test
    fun transformOddWithMiddle() {
        assertEquals("abcdefghi".transform(Options(
            placeBySides = false,
            placeInMiddle = true,
            listOf(Bracket.ROUND, Bracket.SQUARE, Bracket.CURLY, Bracket.CURLY, Bracket.ROUND)
        )), "a(b[c{d{e}f}g]h)i")
    }

}