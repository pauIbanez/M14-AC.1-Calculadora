package com.example.ac1calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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


/*
 *
 *
*/

class MainActivity : ComponentActivity() {


    var number by mutableStateOf("0")
    var operation by mutableStateOf("")
    var resetNumber by mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AC1CalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        operationBox(operation)
                        valueBox(number)
                        calculatorButtons({ label -> processToken(label) })
                        // CalculatorButtons(
                        //     onButtonClick = { label ->
                        //         when (label) {
                        //             "C" -> {
                        //                 currentOperation = "0"
                        //                 currentNumber = "0"
                        //                 lastOperation = ""
                        //                 startNewNumber = false

                        //             }
                        //             "=" -> {
                        //                 currentNumber = CalculateResult(currentOperation + currentNumber)
                        //                 startNewNumber = true
                        //                 currentOperation += currentNumber
                        //             }
                        //             else -> ParseValue(label)
                        //         }
                        //     }
                        // )
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
    }

    fun onEqual() {

        calculateResult(operation + number)
    }

    fun onToken(token: String) {
        if (token.first().isDigit()) onDigit(token)
        else onOperator(token)
    }

    fun onDigit(token: String) {
        if (resetNumber) {
            resetNumber = false
            number = token
            return
        }
        addDigit(token)
    }


    fun onOperator(operator: String) {
        if (hasPendingOperation() && resetNumber) {
            replaceOperator(operator)
        } else if (hasPendingOperation()) {
            number = calculateResult(operation + number)
            operation = number + operator
        } else {
            operation = number + operator
        }
        resetNumber = true
    }

    fun hasPendingOperation(): Boolean {
        return operation.isNotEmpty() && !operation.last().isDigit()
    }

    fun replaceOperator(token: String) {
        operation = operation.dropLast(1) + token
    }

    fun addDigit(token: String) {
        var lastToken = number.takeLastWhile { it.isDigit() || it == '.' }
        if (lastToken == "0") number = number.dropLast(1) + token
        else number += token
    }

    fun calculateResult(value: String): String {

        var numbers = mutableListOf<Double>()
        var operators = mutableListOf<Char>()
        var current = ""


        for (c in value) {
            if (c.isDigit() || c == '.') {
                current += c
            } else {
                numbers.add(current.toDoubleOrNull() ?: return "Error")
                operators.add(c)
                current = ""
            }
        }
        numbers.add(current.toDoubleOrNull() ?: return "Error")

        var i = 0
        while (i < operators.size) {
            val op = operators[i]
            if (op == '×' || op == '÷') {
                val a = numbers[i]
                val b = numbers[i + 1]
                if (op == '÷' && b == 0.0) return "Error"
                numbers[i] = if (op == '×') a * b else a / b
                numbers.removeAt(i + 1)
                operators.removeAt(i)
            } else {
                i++
            }
        }

        var result = numbers[0]
        for (j in operators.indices) {
            when (operators[j]) {
                '+' -> result += numbers[j + 1]
                '−' -> result -= numbers[j + 1]
            }
        }

        return if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()

    }

}


@Composable
fun valueBox(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 48.sp,
        color = Color.Black,
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
        color = Color.Gray,
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
            containerColor = if (isDigit) Color.LightGray else Color(0xFFFF9800),
            contentColor = if (isDigit) Color.Black else Color.White
        ),
        modifier = modifier
    ) {
        Text(text = label, fontSize = 28.sp)
    }
}

// @Preview(showBackground = true)
// @Composable
// fun PlainTextBoxPreview() {
//     AC1CalculatorTheme {
//         PlainTextBox(text = "0")
//     }
// }
