package pt.ipvc.ecocampus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pt.ipvc.ecocampus.data.model.OrderEntity
import pt.ipvc.ecocampus.data.model.UserEntity
import pt.ipvc.ecocampus.data.model.WalletEntity

@Dao
interface UserDao {
    // --- Autenticação e Perfil ---

    // Eu procuro na tabela de utilizadores se existe alguém com este email (para o Login)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Eu busco os dados completos do utilizador pelo seu ID único
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    // Eu insiro um novo registo de utilizador e devolvo o ID gerado
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    // Eu atualizo os dados do utilizador (ex: mudar nome ou email no Perfil)
    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Carteira (Wallet) ---

    // Eu vou buscar a carteira associada a um utilizador específico
    @Query("SELECT * FROM wallet WHERE userId = :userId")
    suspend fun getWallet(userId: Int): WalletEntity?

    // Eu crio uma carteira nova (com saldo 0) quando o utilizador se regista
    @Insert
    suspend fun insertWallet(wallet: WalletEntity)

    // Eu altero diretamente o saldo da carteira (para carregar ou descontar compras)
    @Query("UPDATE wallet SET balance = :newBalance WHERE userId = :userId")
    suspend fun updateBalance(userId: Int, newBalance: Double)

    // --- Histórico de Encomendas (Orders) ---

    // Eu guardo o registo de uma compra feita (para ficar no histórico)
    @Insert
    suspend fun insertOrder(order: OrderEntity)

    // Eu listo todas as compras desse utilizador, ordenadas da mais recente para a mais antiga
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY date DESC")
    suspend fun getUserOrders(userId: Int): List<OrderEntity>
}

