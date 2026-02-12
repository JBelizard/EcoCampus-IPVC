package pt.ipvc.ecocampus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ipvc.ecocampus.data.model.RemoteDish
import pt.ipvc.ecocampus.ui.viewmodel.MainViewModel

@Composable
fun CantinaScreen(viewModel: MainViewModel) {
    // Subscrevo os StateFlows do ViewModel.
    // Isto garante que a UI é reativa: assim que a API devolver o menu
    // ou o saldo for atualizado na BD, este ecrã redesenha-se sozinho.
    val menu by viewModel.menu.collectAsState()
    val wallet by viewModel.wallet.collectAsState()

    // Estado local efémero para controlar o Modal de detalhes.
    // Uso 'remember' para manter a seleção durante recomposições.
    var selectedDish by remember { mutableStateOf<RemoteDish?>(null) }

    // --- LÓGICA DO POPUP (ALERTA) ---
    // O AlertDialog só é injetado na árvore de composição se existir um prato selecionado.
    if (selectedDish != null) {
        val dish = selectedDish!! // Force unwrap seguro pois verifiquei null acima
        AlertDialog(
            onDismissRequest = { selectedDish = null }, // Fecha o popup ao clicar fora
            icon = { Icon(Icons.Default.Info, contentDescription = null) },
            title = { Text(dish.name, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Ingredientes:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(dish.ingredients)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Preço:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text("${dish.price}€", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Delega a transação para o ViewModel (Business Logic)
                        // A UI não deve saber como processar pagamentos, apenas pedir.
                        viewModel.buyItem(dish.name, dish.price, "CANTINA")
                        selectedDish = null // Fecha o popup após a compra
                    }
                ) {
                    Icon(Icons.Default.ShoppingCart, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comprar")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDish = null }) {
                    Text("Fechar")
                }
            }
        )
    }

    // --- CONSTRUÇÃO DO ECRÃ ---
    // Optei por um Column com Scroll simples em vez de LazyColumn,
    // pois o conteúdo é fixo e pequeno (cabeçalho + 4 pratos).
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabeçalho: Título à esquerda, Saldo à direita
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ementa do Dia", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            // Surface para destacar visualmente o saldo
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = MaterialTheme.shapes.medium
            ) {
                // Formatação da moeda para 2 casas decimais
                Text(
                    text = "${String.format("%.2f", wallet?.balance ?: 0.0)}€",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text("Toca num prato para ver detalhes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(24.dp))

        // Renderização Condicional:
        // Se o menu já chegou da API, mostro os cartões. Se não, Loading.
        if (menu != null) {
            val m = menu!!
            // Reutilizo o componente DishCard para manter o código DRY (Don't Repeat Yourself)
            DishCard("Sopa", m.soup) { selectedDish = m.soup }
            DishCard("Carne", m.meat) { selectedDish = m.meat }
            DishCard("Peixe", m.fish) { selectedDish = m.fish }
            DishCard("Dieta", m.diet) { selectedDish = m.diet }

        } else {
            // Estado de carregamento centralizado
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

// Extraí este componente para isolar a lógica visual do cartão.
// Recebe uma lambda (onClick) para passar o evento de clique para o pai (Hoisting).
@Composable
fun DishCard(type: String, dish: RemoteDish, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Peso 1f para o texto ocupar o espaço disponível e empurrar o preço para a ponta
            Column(modifier = Modifier.weight(1f)) {
                Text(type, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(dish.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            }

            // Destaque visual do preço dentro do cartão
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "${dish.price}€",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

