package pt.ipvc.ecocampus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.ipvc.ecocampus.data.SessionManager
import pt.ipvc.ecocampus.data.model.OrderEntity
import pt.ipvc.ecocampus.data.model.RemoteMenu
import pt.ipvc.ecocampus.data.model.RemoteProduct
import pt.ipvc.ecocampus.data.model.UserEntity
import pt.ipvc.ecocampus.data.model.WalletEntity
import pt.ipvc.ecocampus.data.repository.AppRepository

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    // Padrão de Encapsulamento:
    // Uso '_private' mutável para alterar dados internamente e exponho 'public' imutável para a UI.
    // Isto protege o estado de alterações indevidas vindas da View.
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user = _user.asStateFlow()

    private val _wallet = MutableStateFlow<WalletEntity?>(null)
    val wallet = _wallet.asStateFlow()

    private val _menu = MutableStateFlow<RemoteMenu?>(null)
    val menu = _menu.asStateFlow()

    private val _products = MutableStateFlow<List<RemoteProduct>>(emptyList())
    val products = _products.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders = _orders.asStateFlow()

    // Sistema de eventos únicos (One-off events) para feedback ao utilizador (Snackbars/Toasts).
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    init {
        // Inicialização centralizada: carrego todos os dados assim que o ViewModel é instanciado.
        loadUserData()
        loadMenu()
        loadProducts()
    }

    // Limpeza do estado da mensagem após exibição para evitar repetições na recomposição.
    fun clearToast() { _toastMessage.value = null }

    // Agrega o carregamento de todos os dados locais (Room) relacionados com a sessão atual.
    fun loadUserData() {
        viewModelScope.launch {
            val userId = SessionManager.getCurrentUserId() ?: return@launch

            // Atualizo os fluxos individualmente. A UI reage apenas ao que mudar.
            _wallet.value = repository.getWallet(userId)
            _orders.value = repository.getHistory(userId)
            _user.value = repository.getUserById(userId)
        }
    }

    // Lógica de atualização de perfil com persistência na BD local.
    fun updateUserProfile(name: String, email: String, studentNum: String) {
        viewModelScope.launch {
            val currentUser = _user.value ?: return@launch

            // Utilizo o método .copy() das data classes para imutabilidade segura.
            val updatedUser = currentUser.copy(
                name = name,
                email = email,
                studentNumber = studentNum
            )

            repository.updateUser(updatedUser)
            _user.value = updatedUser // Atualizo o estado local imediatamente
            _toastMessage.value = "Perfil atualizado com sucesso!"
        }
    }

    // Chamadas assíncronas ao Repositório para obter dados externos (Retrofit).
    private fun loadMenu() {
        viewModelScope.launch { _menu.value = repository.getMenu() }
    }

    private fun loadProducts() {
        viewModelScope.launch { _products.value = repository.getBarProducts() }
    }

    // Operação de carregamento de saldo.
    fun addBalance(amount: Double) {
        viewModelScope.launch {
            val userId = SessionManager.getCurrentUserId() ?: return@launch
            repository.addBalance(userId, amount)

            loadUserData() // Forço o refresh dos dados para atualizar a UI com o novo saldo.
            _toastMessage.value = "Carregamento de $amount€ efetuado!"
        }
    }

    // Lógica transacional: A decisão de sucesso/falha é delegada ao Repositório (Regra de Negócio).
    fun buyItem(itemName: String, price: Double, type: String) {
        viewModelScope.launch {
            val userId = SessionManager.getCurrentUserId() ?: return@launch

            val success = repository.processPurchase(userId, itemName, price, type)

            if (success) {
                loadUserData() // Atualiza saldo e histórico
                _toastMessage.value = "Compra realizada: $itemName"
            } else {
                _toastMessage.value = "Saldo Insuficiente!"
            }
        }
    }

    fun logout() {
        SessionManager.logout()
    }
}

// Factory necessária para injetar dependências (Repository) no ViewModel.
class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

