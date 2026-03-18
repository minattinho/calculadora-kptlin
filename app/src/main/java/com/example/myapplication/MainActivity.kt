package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Usando o tema básico para garantir que carregue
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212) // Fundo cinza muito escuro
                ) {
                    CalculadoraApp()
                }
            }
        }
    }
}

@Composable
fun CalculadoraApp() {
    var display by remember { mutableStateOf("0") }
    var num1 by remember { mutableStateOf<Double?>(null) }
    var operacao by remember { mutableStateOf<String?>(null) }
    var novoNumero by remember { mutableStateOf(false) }

    fun clique(botao: String) {
        when {
            botao == "AC" -> {
                display = "0"
                num1 = null
                operacao = null
                novoNumero = false
            }
            botao in "0123456789." -> {
                if (novoNumero || display == "0") {
                    display = if (botao == ".") "0." else botao
                    novoNumero = false
                } else {
                    if (botao == "." && display.contains(".")) return
                    display += botao
                }
            }
            botao == "=" -> {
                val n1 = num1
                val op = operacao
                if (n1 != null && op != null) {
                    val n2 = display.toDoubleOrNull() ?: 0.0
                    val res = when (op) {
                        "+" -> n1 + n2
                        "-" -> n1 - n2
                        "*" -> n1 * n2
                        "/" -> if (n2 != 0.0) n1 / n2 else Double.NaN
                        else -> n2
                    }
                    display = if (res.isNaN()) "Erro" else {
                        if (res % 1 == 0.0) res.toLong().toString() else res.toString()
                    }
                    num1 = null
                    operacao = null
                    novoNumero = true
                }
            }
            else -> { // Operadores
                num1 = display.toDoubleOrNull()
                operacao = botao
                novoNumero = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Display - Forçado a ser visível com peso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = display,
                fontSize = 70.sp,
                color = Color.White,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grade de Botões
        val layout = listOf(
            listOf("AC", "", "", "/"),
            listOf("7", "8", "9", "*"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        layout.forEach { linha ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                linha.forEach { item ->
                    if (item.isNotEmpty()) {
                        Button(
                            onClick = { clique(item) },
                            modifier = Modifier
                                .weight(if (item == "0") 2f else 1f)
                                .height(80.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (item) {
                                    "/", "*", "-", "+", "=" -> Color(0xFFFF9F0A)
                                    "AC" -> Color(0xFFA5A5A5)
                                    else -> Color(0xFF333333)
                                }
                            ),
                            shape = CircleShape
                        ) {
                            Text(
                                text = item,
                                fontSize = 28.sp,
                                color = if (item == "AC") Color.Black else Color.White
                            )
                        }
                    } else if (item == "") {
                        // Espaço vazio para alinhar AC à esquerda e operadores à direita
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
