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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Declaração das variáveis de estado local para controlar os inputs.
    // O 'remember' é essencial para manter os dados durante a recomposição da UI.
    var name by remember { mutableStateOf("") }
    var studentNum by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observo o StateFlow do ViewModel para reagir a mudanças no processo de registo.
    val state by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // Utilizo o LaunchedEffect para gerir Side Effects (Navegação e Toasts).
    // Isto garante que o código só corre quando o 'state' muda, evitando múltiplos Toasts.
    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                viewModel.resetState() // Limpo o estado para evitar loops de navegação
                onRegisterSuccess()
            }
            is AuthState.Error -> {
                // Feedback visual imediato caso o registo falhe (ex: email duplicado)
                Toast.makeText(context, (state as AuthState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    // Container principal organizado em coluna, com alinhamento central.
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Criar Conta", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        // Campos de input com binding bidirecional (value + onValueChange)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome Completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = studentNum,
            onValueChange = { studentNum = it },
            label = { Text("Nº Aluno") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Institucional") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de password com transformação visual para segurança (ocultar caracteres)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Palavra-passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botão de ação que invoca a lógica de negócio no ViewModel
        Button(
            onClick = { viewModel.register(name, studentNum, email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("REGISTAR", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Opção de navegação para utilizadores que já possuem conta
        Text(
            text = "Já tens conta? Faz login.",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}

