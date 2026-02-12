package pt.ipvc.ecocampus.data.repository

import android.util.Log
import pt.ipvc.ecocampus.data.local.UserDao
import pt.ipvc.ecocampus.data.model.*
import pt.ipvc.ecocampus.data.remote.ApiService

class AppRepository(private val userDao: UserDao, private val apiService: ApiService) {

    // Definição dos URLs diretos para os meus JSONs alojados no GitHub Gist
    private val URL_CANTINA = "https://gist.githubusercontent.com/JBelizard/ab8fb9f51a5a6cc8c7c19b131d74045a/raw/f06ad218abedeb85ea872002f694f18756787910/menu.json"
    private val URL_BAR = "https://gist.githubusercontent.com/JBelizard/4c2295142d2487b9a7d73d6ba31d6e10/raw/0c0f1fdf928d18460408d5e602a923eeaaf92ac3/products.json"

    // --- Gestão de Utilizadores (Auth) ---

    // Métodos de leitura para verificar login ou carregar perfil
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun getUserById(id: Int) = userDao.getUserById(id)

    // Regista um novo utilizador e cria automaticamente uma carteira com saldo 0
    suspend fun registerUser(user: UserEntity): Long {
        val userId = userDao.insertUser(user)
        userDao.insertWallet(WalletEntity(userId = userId.toInt(), balance = 0.0))
        return userId
    }

    // Atualiza os dados do utilizador na BD local
    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    // --- Gestão da Carteira e Saldo ---

    suspend fun getWallet(userId: Int) = userDao.getWallet(userId)

    // Adiciona saldo à carteira do utilizador (Saldo atual + Novo valor)
    suspend fun addBalance(userId: Int, amount: Double) {
        val current = userDao.getWallet(userId)?.balance ?: 0.0
        userDao.updateBalance(userId, current + amount)
    }

    // --- Lógica de Transação (Core) ---

    // Processa a compra: verifica saldo, desconta o valor e guarda no histórico
    suspend fun processPurchase(userId: Int, item: String, price: Double, type: String): Boolean {
        val wallet = userDao.getWallet(userId) ?: return false

        // Verificação de saldo antes da transação
        if (wallet.balance >= price) {
            // 1. Atualiza o saldo (débito)
            userDao.updateBalance(userId, wallet.balance - price)
            // 2. Insere o registo na tabela de encomendas
            userDao.insertOrder(OrderEntity(userId = userId, itemName = item, price = price, type = type))
            return true // Sucesso
        }
        return false // Falha: Saldo insuficiente
    }

    // Obtém o histórico de movimentos ordenado por data
    suspend fun getHistory(userId: Int) = userDao.getUserOrders(userId)

    // --- Dados Remotos (API) ---

    // Obtém a ementa via Retrofit. Inclui tratamento de erro para não crashar sem net.
    suspend fun getMenu(): RemoteMenu {
        return try {
            apiService.getDailyMenu(URL_CANTINA)
        } catch (e: Exception) {
            Log.e("Repo", "Erro ao buscar ementa: ${e.message}")
            // Retorna objeto vazio/erro para a UI lidar com a falha graciosamente
            RemoteMenu(
                RemoteDish("Erro rede", "Sem dados", 0.0),
                RemoteDish("Erro rede", "Sem dados", 0.0),
                RemoteDish("Erro rede", "Sem dados", 0.0),
                RemoteDish("Erro rede", "Sem dados", 0.0)
            )
        }
    }

    // Obtém a lista de produtos do Bar
    suspend fun getBarProducts(): List<RemoteProduct> {
        return try {
            apiService.getBarProducts(URL_BAR)
        } catch (e: Exception) {
            Log.e("Repo", "Erro ao buscar produtos: ${e.message}")
            emptyList()
        }
    }
}

