package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// 1º e 2º COMMIT: ESTADO E LOGICA
// ==========================================
data class GameState(
    val jogadorAtivo: Int = 1,
    val valorDado: Int = 1,
    val numerosJogador1: Set<Int> = emptySet(),
    val numerosJogador2: Set<Int> = emptySet(),
    val jogoTerminado: Boolean = false,
    val vencedor: String = ""
)

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
                    // A interface gráfica será ligada aqui no 6º commit
                }
            }
        }
    }
}

// ==========================================
// 3º COMMIT: PROGRESSO VISUAL
// ==========================================
@Composable
fun ProgressoVisual(nome: String, conquistados: Set<Int>, isAtivo: Boolean) {
    Column {
        Text(
            text = if (isAtivo) "$nome 🎲 (A jogar)" else nome,
            fontSize = 18.sp,
            style = MaterialTheme.typography.titleMedium,
            color = if (isAtivo) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            for (i in 1..6) {
                val obtido = conquistados.contains(i)
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(color = if (obtido) Color(0xFF4CAF50) else Color.LightGray, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = i.toString(),
                        color = if (obtido) Color.White else Color.DarkGray,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

// ==========================================
// 4º e 5º COMMIT: GAMESCREEN COM OS BOTÕES DINÂMICOS
// ==========================================
@Composable
fun GameScreen(
    state: GameState,
    onLancar: () -> Unit,
    onReiniciar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todosOsNumeros = setOf(1, 2, 3, 4, 5, 6)
    val faltamJogador1 = todosOsNumeros - state.numerosJogador1
    val faltamJogador2 = todosOsNumeros - state.numerosJogador2

    Column(
        modifier = modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = if (state.jogoTerminado) "Fim do Jogo!" else "Vez do Jogador ${state.jogadorAtivo}",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge,
            color = if (state.jogoTerminado) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ProgressoVisual(nome = "Jogador 1", conquistados = state.numerosJogador1, isAtivo = state.jogadorAtivo == 1 && !state.jogoTerminado)
            ProgressoVisual(nome = "Jogador 2", conquistados = state.numerosJogador2, isAtivo = state.jogadorAtivo == 2 && !state.jogoTerminado)
        }

        // O Dado Gigante Centralizado
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.valorDado.toString(),
                fontSize = 80.sp,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // [5º COMMIT]: Lógica condicional dos botões de controlo
        if (state.jogoTerminado) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🏆 ${state.vencedor} Ganhou!", fontSize = 26.sp, color = Color(0xFF4CAF50))
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onReiniciar,
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text("Jogar Novamente", fontSize = 18.sp)
                }
            }
        } else {
            Button(
                onClick = onLancar,
                modifier = Modifier.fillMaxWidth().height(65.dp)
            ) {
                Text("Lançar Dado 🎲", fontSize = 20.sp)
            }
        }
    } // <- Esta era a chaveta que faltava para fechar a GameScreen!
}
}