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

// Adiciona esta classe LOGO ABAIXO do GameState e ACIMA da MainActivity:
class GameManager {
    var uiState = kotlinx.coroutines.flow.MutableStateFlow(GameState())
        private set

    fun lancarDado() {
        if (uiState.value.jogoTerminado) return
        val novoValor = (1..6).random()

        uiState.kotlinx.coroutines.flow.update { estadoAtual ->
            if (estadoAtual.jogadorAtivo == 1) {
                val novaColecaoJ1 = estadoAtual.numerosJogador1 + novoValor
                val ganhou = novaColecaoJ1.size == 6
                estadoAtual.copy(
                    valorDado = novoValor,
                    numerosJogador1 = novaColecaoJ1,
                    jogoTerminado = ganhou,
                    vencedor = if (ganhou) "Jogador 1" else "",
                    jogadorAtivo = if (ganhou) 1 else 2
                )
            } else {
                val novaColecaoJ2 = estadoAtual.numerosJogador2 + novoValor
                val ganhou = novaColecaoJ2.size == 6
                estadoAtual.copy(
                    valorDado = novoValor,
                    numerosJogador2 = novaColecaoJ2,
                    jogoTerminado = ganhou,
                    vencedor = if (ganhou) "Jogador 2" else "",
                    jogadorAtivo = if (ganhou) 2 else 1
                )
            }
        }
    }

    fun reiniciarJogo() {
        uiState.value = GameState()
    }
}

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