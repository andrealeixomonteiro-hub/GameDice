package com.example.gamedice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// ==========================================
// 1. ESTADO DO JOGO (MODEL)
// ==========================================
data class GameState(
    val jogadorAtivo: Int = 1,
    val valorDado: Int = 1,
    val numerosJogador1: Set<Int> = emptySet(),
    val numerosJogador2: Set<Int> = emptySet(),
    val jogoTerminado: Boolean = false,
    val vencedor: String = ""
)

// ==========================================
// 2. CONTROLADOR (GAME MANAGER / VIEWMODEL LOGIC)
// ==========================================
class GameManager {
    var uiState = MutableStateFlow(GameState())
        private set

    fun lancarDado() {
        if (uiState.value.jogoTerminado) return
        val novoValor = (1..6).random()

        uiState.update { estadoAtual ->
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

// ==========================================
// 3. ATIVIDADE PRINCIPAL
// ==========================================
class MainActivity : ComponentActivity() {
    private val gameManager = GameManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Utilização estável do collectAsState() nativo
                    val state by gameManager.uiState.collectAsState()
                    GameScreen(
                        state = state,
                        onLancar = { gameManager.lancarDado() },
                        onReiniciar = { gameManager.reiniciarJogo() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. INTERFACE GRÁFICA PRINCIPAL
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
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp),
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
            ProgressoVisual(
                nome = "Jogador 1",
                conquistados = state.numerosJogador1,
                isAtivo = state.jogadorAtivo == 1 && !state.jogoTerminado
            )
            ProgressoVisual(
                nome = "Jogador 2",
                conquistados = state.numerosJogador2,
                isAtivo = state.jogadorAtivo == 2 && !state.jogoTerminado
            )
        }

        // Cartão Dinâmico que detalha os números em falta
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Faltam ao J1:", fontSize = 14.sp, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (faltamJogador1.isEmpty()) "🎉 Pronto!" else faltamJogador1.sorted().joinToString(", "),
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (faltamJogador1.isEmpty()) Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Faltam ao J2:", fontSize = 14.sp, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (faltamJogador2.isEmpty()) "🎉 Pronto!" else faltamJogador2.sorted().joinToString(", "),
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (faltamJogador2.isEmpty()) Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                }
            }
        }

        // Elemento Visual do Dado Central
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(24.dp))
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.valorDado.toString(),
                fontSize = 80.sp,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Botão de Ação Condicional (Lançar vs Reiniciar)
        if (state.jogoTerminado) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🏆 ${state.vencedor} Ganhou!", fontSize = 26.sp, style = MaterialTheme.typography.titleLarge, color = Color(0xFF4CAF50))
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onReiniciar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Jogar Novamente", fontSize = 18.sp)
                }
            }
        } else {
            Button(
                onClick = onLancar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lançar Dado 🎲", fontSize = 20.sp)
            }
        }
    }
}

// ==========================================
// 5. COMPONENTE DE PROGRESSO ATÓMICO
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
                        .background(color = if (obtido) Color(0xFF4CAF50) else Color.LightGray, shape = CircleShape)
                        .border(1.5.dp, Color.Gray, CircleShape),
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

// Preview para renderização estática no Android Studio
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameScreenPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GameScreen(state = GameState(), onLancar = {}, onReiniciar = {})
        }
    }
}