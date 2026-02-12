package pt.ipvc.ecocampus.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pt.ipvc.ecocampus.R

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // Optei por criar este ecrã como "Stateless" (sem estado interno).
    // Ele não sabe nada sobre ViewModels ou Base de Dados, apenas recebe
    // as ações de navegação (callbacks) para manter o código limpo e desacoplado.
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Garante que tudo fica alinhado ao centro
        verticalArrangement = Arrangement.Center // Centralizo o conteúdo verticalmente no ecrã
    ) {
        // Usei um emoji como placeholder temporário.
        // O objetivo é substituir isto por `Image(painter = painterResource(id = R.drawable.logo))`
        // assim que tiver o vetor final do logótipo.
        Text(text = "🎓", style = MaterialTheme.typography.displayLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Apliquei a tipografia do Material Theme para manter consistência visual.
        // Usei a cor primária para destacar a marca "EcoCampus".
        Text(
            text = "EcoCampus IPVC",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "Gestão de Cantinas e Sustentabilidade")

        Spacer(modifier = Modifier.height(48.dp))

        // Botão Principal (Entrar):
        // Dei-lhe destaque com cor sólida (Filled Button) para indicar que é a ação primária.
        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão Secundário (Registar):
        // Escolhi o estilo "Outlined" (apenas contorno) para criar hierarquia visual
        // e não competir a atenção com o botão de Entrar.
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Conta")
        }
    }
}

