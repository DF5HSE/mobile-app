package org.hyperskill.app

import org.hyperskill.app.calculator.*

import kotlin.test.*

internal abstract class ExpressionTest {
    companion object {
        val multiplySymbol = Operator.MULTIPLY_CHAR.toString()
        val divisionSymbol = Operator.DIVISION_CHAR.toString()
        val powerSymbol = Operator.POW_CHAR.toString()
        val bigExpressionString: String =
            // -10^2 + (-(+6 / 2 - 3 * 6)^2)
            "-10${powerSymbol}2+(-(+6${divisionSymbol}2-3${multiplySymbol}6)${powerSymbol}2)"
    }

    abstract fun createExpression(): Expression

    fun createExpressionFromString(exprString: String): Expression {
        val expr = createExpression()
        for (char in exprString)
            expr.addToken(inputTokenFromChar(char) ?: throw RuntimeException("Unsupported token '$char'"))
        return expr
    }
    
    @Test
    fun testCreateExpressionFromString() {
        assertEquals(createExpressionFromString(bigExpressionString).toString(), bigExpressionString)
    }

    @Test
    fun testShiftCursor() {
        val expr: Expression = createExpressionFromString(bigExpressionString)
        assertEquals(bigExpressionString.length, expr.getCursorPosition())
        assertFalse(expr.shiftCursorRight())
        for (i in 1..bigExpressionString.length) {
            assertTrue(expr.shiftCursorLeft())
            assertEquals(bigExpressionString.length - i, expr.getCursorPosition())
        }
        assertFalse(expr.shiftCursorLeft())
        for (i in 1..bigExpressionString.length) {
            assertTrue(expr.shiftCursorRight())
            assertEquals(i, expr.getCursorPosition())
        }
    }

    @Test
    fun testSetCursorPosition() {
        val expr: Expression = createExpressionFromString(bigExpressionString)
        assertEquals(bigExpressionString.length, expr.getCursorPosition())


        assertTrue(expr.setCursorPosition(-1))
        assertEquals(0, expr.getCursorPosition())

        for (i in 1..bigExpressionString.length) {
            assertTrue(expr.setCursorPosition(i))
            assertEquals(i, expr.getCursorPosition())
        }

        assertFalse(expr.setCursorPosition(bigExpressionString.length + 1))
        assertEquals(bigExpressionString.length, expr.getCursorPosition())
    }

    @Test
    fun testAddElementInDifferentPlaces() {
        val expr: Expression = createExpressionFromString("20")
        expr.addToken(Digit.NINE)
        assertEquals("209", expr.toString())

        expr.shiftCursorLeft()
        expr.addToken(Dot)
        assertEquals("20.9", expr.toString())

        expr.apply {
            shiftCursorLeft()
            shiftCursorLeft()
            shiftCursorLeft()
            addToken(Digit.ONE)
        }
        assertEquals("120.9", expr.toString())
    }

    @Test
    fun testDeleteElementInDifferentPlaces() {
        val expr: Expression = createExpressionFromString("1209")
        assertTrue(expr.deleteToken())
        assertEquals("120", expr.toString())

        expr.shiftCursorLeft()
        assertTrue(expr.deleteToken())
        assertEquals("10", expr.toString())

        expr.shiftCursorLeft()
        assertFalse(expr.deleteToken())
        assertEquals("10", expr.toString())
    }

    @Test
    fun testClear() {
        val expr: Expression = createExpressionFromString("12")
        assertTrue(expr.clear())
        assertTrue(expr.toString().isEmpty())
        assertFalse(expr.clear())
    }

    @Test
    fun testCalculateEmptyExpression() {
        assertEquals(0.0, createExpressionFromString("").calculate())
    }

    @Test
    fun testCalculateOneNumber() {
        assertEquals(0.0, createExpressionFromString("0").calculate())
        assertEquals(120.0, createExpressionFromString("120").calculate())
        assertEquals(120.9, createExpressionFromString("120.9").calculate())
    }

    @Test
    fun testCalculateOneUnaryOperation() {
        assertEquals(120.9, createExpressionFromString("+120.9").calculate())
        assertEquals(-120.9, createExpressionFromString("-120.9").calculate())
    }

    @Test
    fun testCalculateOneBinaryOperation() {
        assertEquals(8.0, createExpressionFromString("6+2").calculate())
        assertEquals(4.0, createExpressionFromString("6-2").calculate())
        assertEquals(12.0, createExpressionFromString("6${multiplySymbol}2").calculate())
        assertEquals(3.0, createExpressionFromString("6${divisionSymbol}2").calculate())
        assertEquals(36.0, createExpressionFromString("6${powerSymbol}2").calculate())
    }

    @Test
    fun testCalculateDivisionByZero() {
        assertEquals(FloatType.POSITIVE_INFINITY, createExpressionFromString("6${divisionSymbol}0.0").calculate())
        assertEquals(FloatType.NEGATIVE_INFINITY, createExpressionFromString("-6${divisionSymbol}0.0").calculate())
    }

    @Test
    fun testRightAssociationOfRightAssociatedBinaryOperation() {
        assertEquals(512.0, createExpressionFromString("2${powerSymbol}3${powerSymbol}2").calculate())
    }

    @Test
    fun testPriorityUnaryAndRightAssociative() {
        assertEquals(-4.0, createExpressionFromString("-2${powerSymbol}2").calculate())
    }

    @Test
    fun testPriorityUnaryAndLeftAssociative() {
        assertEquals(0.0, createExpressionFromString("-2+2").calculate())
    }

    @Test
    fun testPriorityBetweenBinaryOperations() {
        assertEquals(65.0, createExpressionFromString("1+16${multiplySymbol}2${powerSymbol}2").calculate())
        assertEquals(-3.0, createExpressionFromString("1-16${divisionSymbol}2${powerSymbol}2").calculate())
    }

    @Test
    fun testBraces() {
        assertEquals(17.0, createExpressionFromString("(1+16)").calculate())
        assertEquals(-17.0, createExpressionFromString("-(1+16)").calculate())
        assertEquals(17.0, createExpressionFromString("(((1)+(16)))").calculate())
        assertEquals(34.0, createExpressionFromString("(1+16)${multiplySymbol}2").calculate())
    }

    @Test
    fun testCalculateExpressionsWithError() {
        assertNull(createExpressionFromString("(").calculate())
        assertNull(createExpressionFromString(")").calculate())
        assertNull(createExpressionFromString("()").calculate())
        assertNull(createExpressionFromString("(-)").calculate())
        assertNull(createExpressionFromString("1(").calculate())
        assertNull(createExpressionFromString("1-").calculate())
        assertNull(createExpressionFromString("-1-").calculate())
        assertNull(createExpressionFromString("(-1)-").calculate())
        assertNull(createExpressionFromString("1++").calculate())
    }

    @Test
    fun testCalculateBigExpression() {
        val expr: Expression = createExpressionFromString(bigExpressionString)
        val res = expr.calculate()
        assertNotNull(res)
        assertEquals(-325.0, res)
    }
}