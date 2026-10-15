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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ac1calculator.ui.theme.AC1CalculatorTheme

class MainActivity : ComponentActivity() {


    var currentNumber by mutableStateOf("0")
    var currentOperation by mutableStateOf("0")
    var lastOperation by mutableStateOf("0")
    var isOperationResult by mutableStateOf(false)



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

                        RememberBox(currentOperation)
                        ValueBox(currentNumber)
                        CalculatorButtons(
                            onButtonClick = { label ->
                                when {
                                    label == "C" ->{
                                        currentOperation = "0"
                                        currentNumber = "0"
                                        lastOperation = ""
                                        isOperationResult = false

                                    }
                                    label == "=" -> {
                                        isOperationResult = true
                                        currentNumber = CalculateResult(currentOperation+currentNumber)
                                        currentOperation += currentNumber
                                    }
                                    else ->ParseValue(label)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    fun ParseValue(value: String) {
        if(value[0].isDigit()){
            if(isOperationResult) {
                isOperationResult = false
                currentNumber = value
                return
            }
            DropLeadingZero(value)

        }
        else if(!currentOperation.last().isDigit()) ReplaceOperation(value)
        else if (isOperationResult) {
            currentOperation = currentNumber+value
        }
        else {
            currentOperation = currentNumber+value
        }

    }

    fun DropLeadingZero(token: String) {
        var lastToken = currentNumber.takeLastWhile { it.isDigit() || it == '.' }
        if(lastToken == "0") currentNumber = currentNumber.dropLast(1) + token
        else currentNumber+token
    }

    fun ReplaceOperation(token: String) {
        currentOperation = currentOperation.dropLast(1) + token
    }



    fun CalculateResult(value: String) : String {

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
fun ValueBox(text: String, modifier: Modifier = Modifier) {
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
fun RememberBox(text: String, modifier: Modifier = Modifier) {
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
fun CalculatorButtons(onButtonClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttonRows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    CalculatorButton(
                        label = label,
                        onClick = { onButtonClick(label) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
        CalculatorButton(
            label = "=",
            onClick = { onButtonClick("=") },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        )
    }
}

@Composable
fun CalculatorButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
