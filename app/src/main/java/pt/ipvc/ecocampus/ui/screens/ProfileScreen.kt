package pt.ipvc.ecocampus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ipvc.ecocampus.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    // Observo o StateFlow do utilizador. Assim que o ViewModel carregar os dados da BD,
    // esta variável atualiza-se e a UI redesenha automaticamente (Reatividade).
    val user by viewModel.user.collectAsState()

    // Controlo o estado da UI localmente: Modo de Visualização vs. Modo de Edição.
    var isEditing by remember { mutableStateOf(false) }

    // Criei variáveis de estado separadas para o formulário de edição.
    // Isto é crucial para não alterar os dados reais do 'user' enquanto estou a escrever (Buffer).
    // Só altero o 'user' real quando clicar em "Guardar".
    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editNum by remember { mutableStateOf("") }

    // Uso o LaunchedEffect para sincronizar os dados iniciais.
    // Sempre que o objeto 'user' mudar (ex: carregou da BD), preencho os campos de edição
    // para o utilizador não ter de escrever tudo do zero.
    LaunchedEffect(user) {
        user?.let {
            editName = it.name
            editEmail = it.email
            editNum = it.studentNumber
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("O teu Perfil", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        // Lógica de Renderização Condicional:
        // Se estiver a editar, mostro Inputs. Se não, mostro Texto estático.
        // Optei por fazer isto no mesmo ecrã para evitar criar fragmentos desnecessários.
        if (isEditing) {
            // --- MODO EDIÇÃO ---
            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = editNum,
                onValueChange = { editNum = it },
                label = { Text("Nº Aluno") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = editEmail,
                onValueChange = { editEmail = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                // Envio os dados novos para o ViewModel persistir na Room Database.
                // Ao gravar, o 'user' lá em cima atualiza-se sozinho graças ao Flow.
                viewModel.updateUserProfile(editName, editEmail, editNum)
                isEditing = false
            }) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Alterações")
            }

        } else {
            // --- MODO LEITURA ---
            // Uso o operador Elvis (?:) para garantir que a UI não crasha se o user for null
            ProfileItem("Nome", user?.name ?: "...")
            ProfileItem("Nº Aluno", user?.studentNumber ?: "...")
            ProfileItem("Email", user?.email ?: "...")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { isEditing = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Edit, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Dados")
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Empurra o botão de logout para o fundo

        // Botão de Logout com destaque de erro (Vermelho)
        Button(
            onClick = {
                viewModel.logout() // Limpo a sessão e as SharedPreferences
                onLogout() // Navego para fora (Login Screen)
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Terminar Sessão")
        }
    }
}

// Extraí este componente visual para manter o código do ecrã principal limpo e legível.
@Composable
fun ProfileItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    }
}

