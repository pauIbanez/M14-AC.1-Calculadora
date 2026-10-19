package com.example.ac1calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ac1calculator.ui.theme.AC1CalculatorTheme


class MainActivity : ComponentActivity() {


    var number by mutableStateOf("0")
    var operation by mutableStateOf("")

    var resetNumber = false
    var accumulator: Double? = null
    var pendingOp: Char? = null
    var lastOp: Char? = null
    var lastOperand: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AC1CalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding).background(Color.Black),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        operationBox(operation)
                        valueBox(number)
                        calculatorButtons({ label -> processToken(label) })

                    }
                }
            }
        }
    }


    fun processToken(token: String) {
        when (token) {
            "C" -> reset()
            "=" -> onEqual()
            else -> onToken(token)
        }
    }

    fun reset() {
        number = "0"
        operation = ""

        resetNumber = false
        accumulator = null
        pendingOp = null
        lastOp = null
        lastOperand = null
    }

    fun onEqual() {
        when {
            pendingOp != null -> {
                val rightOperand = number.toDoubleOrNull() ?: return
                val result = apply(accumulator!!, pendingOp!!, rightOperand) ?: return showError()
                operation = format(accumulator!!) + pendingOp + format(rightOperand) + "="
                lastOp = pendingOp
                lastOperand = rightOperand
                pendingOp = null
                accumulator = null
                number = format(result)
            }

            lastOp != null -> {
                val leftOperand = number.toDoubleOrNull() ?: return
                val result = apply(leftOperand, lastOp!!, lastOperand!!) ?: return showError()
                operation = format(leftOperand) + lastOp + format(lastOperand!!) + "="
                number = format(result)
            }

            else -> {
                operation = "$number="
            }
        }
        resetNumber = true
    }

    fun showError() {
        reset()
        number = "Error"
        resetNumber = true
    }

    fun onToken(token: String) {
        if (token.first().isDigit() || token == ".") onDigit(token)
        else onOperator(token)
    }

    fun onDigit(token: String) {
        if (token == ".") {
            when {
                resetNumber -> {
                    if (pendingOp == null) operation = ""
                    number = "0."
                    resetNumber = false
                }

                !number.contains('.') -> number += "."
            }
            return
        }

        when {
            resetNumber && pendingOp == null -> {
                number = token
                operation = ""
                resetNumber = false
            }

            resetNumber -> {
                number = token
                resetNumber = false
            }

            else -> addDigit(token)
        }
    }


    fun onOperator(operator: String) {


        when {
            pendingOp != null && resetNumber -> {
                pendingOp = operator.first()
                operation = number + operator
            }

            pendingOp != null -> {
                val result = apply(accumulator!!, pendingOp!!, number.toDouble()) ?: return showError()
                number = format(result)
                accumulator = result
                pendingOp = operator.first()
                operation = number + operator
            }


            else -> {
                accumulator = number.toDoubleOrNull() ?: return
                pendingOp = operator.first()
                operation = number + operator
            }
        }

        resetNumber = true
        lastOp = null
        lastOperand = null
    }


    fun addDigit(token: String) {
        var lastToken = number.takeLastWhile { it.isDigit() || it == '.' }
        if (lastToken == "0") number = number.dropLast(1) + token
        else number += token
    }

    fun apply(a: Double, op: Char, b: Double): Double? = when (op) {
        '+' -> a + b
        '−' -> a - b
        '×' -> a * b
        '÷' -> if (b == 0.0) null else a / b
        else -> null
    }

    fun format(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()

}


@Composable
fun valueBox(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 48.sp,
        color = Color.White,
        textAlign = TextAlign.End,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    )
}

@Composable
fun operationBox(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 30.sp,
        color = Color.LightGray,
        textAlign = TextAlign.End,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 2.dp)
    )
}


val buttonRows = listOf(
    listOf("7", "8", "9", "÷"),
    listOf("4", "5", "6", "×"),
    listOf("1", "2", "3", "−"),
    listOf("C", "0", ".", "+")
)

@Composable
fun calculatorButtons(onButtonClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttonRows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    calculatorButton(
                        label = label,
                        onClick = { onButtonClick(label) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
        calculatorButton(
            label = "=",
            onClick = { onButtonClick("=") },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        )
    }
}

@Composable
fun calculatorButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isDigit = label.first().isDigit() || label == "."
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isDigit) Color.DarkGray else Color(0xFFFF9800),
            contentColor =  Color.White
        ),
        modifier = modifier
    ) {
        Text(text = label, fontSize = 28.sp)
    }
}

