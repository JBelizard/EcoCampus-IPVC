package pt.ipvc.ecocampus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipvc.ecocampus.data.SessionManager
import pt.ipvc.ecocampus.data.model.UserEntity
import pt.ipvc.ecocampus.data.repository.AppRepository

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    // Utilizo StateFlow para gerir o estado da UI de forma reativa.
    // Garante que a UI reage automaticamente a mudanças (Loading, Sucesso, Erro)
    // e mantém o estado consistente mesmo durante a rotação do ecrã.
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(email: String, pass: String) {
        // O viewModelScope garante que as corrotinas são canceladas se o ViewModel for destruído,
        // evitando memory leaks e operações desnecessárias em background.
        viewModelScope.launch {

            // Validação local imediata para evitar chamadas de IO desnecessárias à base de dados.
            if (email.isBlank() || pass.isBlank()) {
                _authState.value = AuthState.Error("Por favor, preencha o email e a palavra-passe.")
                return@launch
            }

            _authState.value = AuthState.Loading

            try {
                // A lógica de acesso aos dados está delegada ao Repositório para manter o ViewModel limpo.
                val user = repository.getUserByEmail(email)

                // Verificação de credenciais.
                if (user != null && user.passwordHash == pass) {
                    // Persistência da sessão para manter o utilizador logado ao reiniciar a app.
                    SessionManager.login(user.id)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Credenciais inválidas. Verifique o email ou crie conta.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Erro no sistema: ${e.message}")
            }
        }
    }

    fun register(name: String, studentNum: String, email: String, pass: String) {
        viewModelScope.launch {
            // Defesa inicial: Validações de integridade dos dados antes de processar.

            if (name.isBlank() || studentNum.isBlank() || email.isBlank() || pass.isBlank()) {
                _authState.value = AuthState.Error("Todos os campos são obrigatórios!")
                return@launch
            }

            // Garante que o email segue o padrão correto do Android.
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _authState.value = AuthState.Error("Formato de email inválido.")
                return@launch
            }

            if (pass.length < 4) {
                _authState.value = AuthState.Error("A password deve ter pelo menos 4 caracteres.")
                return@launch
            }

            _authState.value = AuthState.Loading

            try {
                // Verifica duplicados para garantir a consistência da base de dados (chave única).
                val existing = repository.getUserByEmail(email)
                if (existing != null) {
                    _authState.value = AuthState.Error("Este email já está registado. Faça login.")
                    return@launch
                }

                val newUser = UserEntity(
                    name = name,
                    email = email,
                    studentNumber = studentNum,
                    passwordHash = pass
                )

                // Regista e obtém o ID gerado automaticamente pelo Room.
                val newId = repository.registerUser(newUser)

                // Login automático após registo para melhorar a experiência do utilizador (UX).
                SessionManager.login(newId.toInt())
                _authState.value = AuthState.Success

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Erro ao criar conta: ${e.message}")
            }
        }
    }

    // Método para limpar o estado (ex: remover mensagens de erro ao navegar).
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

// Sealed Class define uma máquina de estados finita e segura para a autenticação.
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

// Factory necessária para injetar o Repositório no construtor do ViewModel,
// já que o sistema Android padrão não suporta construtores com argumentos sem configuração extra.
class AuthViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

