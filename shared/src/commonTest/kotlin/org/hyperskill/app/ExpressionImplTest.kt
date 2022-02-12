package org.hyperskill.app

import org.hyperskill.app.calculator.Expression
import org.hyperskill.app.calculator.impl.ExpressionImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ExpressionImplTest: ExpressionTest() {
    override fun createExpression(): Expression {
        return ExpressionImpl()
    }

    @Test
    fun testCalculateNumberWithStartingZeros() {
        assertEquals(1.0, createExpressionFromString("0001").calculate())
    }

    @Test
    fun testFewDotsBetweenDigit() {
        assertNull(createExpressionFromString("1.0.0").calculate())
    }
}