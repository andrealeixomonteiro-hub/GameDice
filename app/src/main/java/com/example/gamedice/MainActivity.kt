package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

// Estado que guarda o progresso dos dois jogadores e o valor do dado
data class GameState(
    val jogadorAtivo: Int = 1,
    val valorDado: Int = 1,
    val numerosJogador1: Set<Int> = emptySet(),
    val numerosJogador2: Set<Int> = emptySet(),
    val jogoTerminado: Boolean = false,
    val vencedor: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // A interface gráfica será injetada aqui nos próximos commits
                }
            }
        }
    }
}