package pt.ipvc.ecocampus.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import pt.ipvc.ecocampus.ui.viewmodel.AuthState
import pt.ipvc.ecocampus.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Mantenho o estado local do formulário. Usei 'remember' para garantir
    // que o texto não se perde quando o ecrã sofre recomposição.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Subscrevo o StateFlow do ViewModel para reagir a mudanças de estado (ex: Loading ou Erro)
    val state by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // O LaunchedEffect é crucial aqui: permite-me disparar ações únicas (Side Effects)
    // como mostrar um Toast ou navegar, sem entrar em loop infinito de renderização.
    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                viewModel.resetState() // Limpo o estado para não voltar aqui ao fazer "Back"
                onLoginSuccess()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (state as AuthState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Estruturei a UI numa Coluna centralizada com padding para garantir boa visualização em vários ecrãs
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bem-vindo!", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        // Campo de Email ligado à variável de estado local
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email Institucional") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Password com transformação visual para segurança (bolinhas)
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Palavra-passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botão de Login com feedback visual:
        // Se estiver a carregar, mostro o spinner; se não, mostro o texto.
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (state is AuthState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("ENTRAR", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navegação simples para o registo caso o user não tenha conta
        Text(
            text = "Ainda não tens conta? Cria aqui.",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable { onNavigateToRegister() }
        )
    }
}

