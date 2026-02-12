package pt.ipvc.ecocampus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ipvc.ecocampus.ui.viewmodel.MainViewModel
import pt.ipvc.ecocampus.utils.ShakeDetector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarScreen(viewModel: MainViewModel) {
    // Eu recebo a lista de produtos do teu Gist
    val products by viewModel.products.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val context = LocalContext.current

    // Eu controlo o popup do desconto
    var showDiscountDialog by remember { mutableStateOf(false) }

    // Eu ativo o sensor para detetar abanões
    DisposableEffect(Unit) {
        val sensor = ShakeDetector(context)
        sensor.start { showDiscountDialog = true } // Se abanar, mostro dialog
        onDispose { sensor.stop() }
    }

    // Eu mostro a mensagem de parabéns se o sensor disparar
    if (showDiscountDialog) {
        AlertDialog(
            onDismissRequest = { showDiscountDialog = false },
            title = { Text("🎉 Agita & Ganha!") },
            text = { Text("Detetámos um abanão! Tens 10% de desconto no próximo café.") },
            confirmButton = { Button(onClick = { showDiscountDialog = false }) { Text("Aproveitar") } }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bar & Cafetaria", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text("${String.format("%.2f", wallet?.balance ?: 0.0)}€", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Text("Agita o telemóvel para promoções!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(16.dp))

        // Eu listo os produtos numa grelha
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                // Eu crio o cartão do produto clicável
                Card(
                    onClick = { viewModel.buyItem(product.name, product.price, "BAR") },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("☕", style = MaterialTheme.typography.displayMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(product.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${product.price}€", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

