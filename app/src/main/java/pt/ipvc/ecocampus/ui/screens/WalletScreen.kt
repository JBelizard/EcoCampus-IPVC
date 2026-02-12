package pt.ipvc.ecocampus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipvc.ecocampus.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(viewModel: MainViewModel) {
    // Subscrevo os StateFlows. Garante que o ecrã atualiza assim que o saldo muda na BD.
    val wallet by viewModel.wallet.collectAsState()
    val orders by viewModel.orders.collectAsState()

    // Estado local apenas para o input de carregamento (buffer antes de enviar à BD).
    var amount by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- UI DO CARTÃO ---
        // Usei um Box com Gradient para replicar o visual de um cartão físico "Premium".
        Card(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Text("Cartão IPVC", color = Color.White, style = MaterialTheme.typography.titleMedium)

                    // O saldo atualiza-se automaticamente aqui graças ao .collectAsState()
                    Text(
                        text = String.format("%.2f €", wallet?.balance ?: 0.0),
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("**** **** **** 1234", color = Color.White.copy(alpha = 0.8f))
                        Text("VALID 12/28", color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- INPUT DE CARREGAMENTO ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Valor a Carregar (€)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    // Parse seguro do input. Só chamo o ViewModel se o valor for numérico válido.
                    val value = amount.toDoubleOrNull()
                    if (value != null) {
                        viewModel.addBalance(value)
                        amount = "" // Limpo o campo após o sucesso
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Carregar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Histórico de Movimentos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // --- LISTA DE MOVIMENTOS ---
        // LazyColumn é essencial aqui para performance (reciclagem de views) à medida que o histórico cresce.
        LazyColumn {
            items(orders) { order -> // 'items' itera sobre a lista de encomendas vinda do Room
                ListItem(
                    headlineContent = { Text(order.itemName, fontWeight = FontWeight.Bold) },
                    // Formatação de timestamp (Long) para data legível
                    supportingContent = { Text(SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(order.date))) },
                    trailingContent = {
                        Text("-${order.price}€", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}


