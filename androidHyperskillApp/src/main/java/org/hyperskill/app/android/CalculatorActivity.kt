package org.hyperskill.app.android

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.hyperskill.app.calculator.*
import org.hyperskill.app.calculator.impl.ExpressionImpl

class CalculatorActivity : AppCompatActivity() {
    private var curExpression = ExpressionImpl()

    private val buttonsClickExpressionChange = mapOf(
        R.id.buttonDot to { curExpression.addToken(Dot) },
        R.id.button0 to { curExpression.addToken(Digit.ZERO) },
        R.id.button1 to { curExpression.addToken(Digit.ONE) },
        R.id.button2 to { curExpression.addToken(Digit.TWO) },
        R.id.button3 to { curExpression.addToken(Digit.THREE) },
        R.id.button4 to { curExpression.addToken(Digit.FOUR) },
        R.id.button5 to { curExpression.addToken(Digit.FIVE) },
        R.id.button6 to { curExpression.addToken(Digit.SIX) },
        R.id.button7 to { curExpression.addToken(Digit.SEVEN) },
        R.id.button8 to { curExpression.addToken(Digit.EIGHT) },
        R.id.button9 to { curExpression.addToken(Digit.NINE) },

        R.id.buttonMinus to { curExpression.addToken(Operator.MINUS) },
        R.id.buttonPlus to { curExpression.addToken(Operator.PLUS) },
        R.id.buttonDivision to { curExpression.addToken(Operator.DIVISION_CHAR) },
        R.id.buttonMultiply to { curExpression.addToken(Operator.MULTIPLY_CHAR) },
        R.id.buttonPow to { curExpression.addToken(Operator.POW_CHAR) },

        R.id.buttonClear to { curExpression.clear() },
        R.id.buttonDelete to { curExpression.deleteToken() },

        R.id.buttonLeftBrace to { curExpression.addToken(Brace.LEFT) },
        R.id.buttonRightBrace to { curExpression.addToken(Brace.LEFT) },
        R.id.buttonLeftArrow to { curExpression.shiftCursorLeft() },
        R.id.buttonRightArrow to { curExpression.shiftCursorRight() },
    )

    private val buttonsToken = mapOf(
        R.id.buttonDot to Dot,

        R.id.buttonMinus to Operator.MINUS,
        R.id.buttonPlus to Operator.PLUS,
        R.id.buttonDivision to Operator.DIVISION_CHAR,
        R.id.buttonMultiply to Operator.MULTIPLY_CHAR,
        R.id.buttonPow to Operator.POW_CHAR,
    )

    private lateinit var resultView: TextView
    private lateinit var currentExpressionEditor: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        resultView = findViewById<Button>(R.id.ResultView)
        buttonsClickExpressionChange.forEach {
            findViewById<Button>(it.key).setOnClickListener{v ->
                it.value()
                val res = curExpression.calculate()
                if (res == null)
                    resultView.clearComposingText()
                else
                    resultView.text = res.toString()
            }
        }
//        findViewById<Button>(R.id.buttonClear).setOnClickListener{
//            if ()
//        }
        buttonsToken.forEach{
            findViewById<Button>(it.key).text = it.value.toString()
        }

        /* TODO connect R.id.Result, R.id.ActualExpression and R.id.ExpressionsHistory
         * TODO with CalculateCore, for updating them after changes in core state
         */
    }
}