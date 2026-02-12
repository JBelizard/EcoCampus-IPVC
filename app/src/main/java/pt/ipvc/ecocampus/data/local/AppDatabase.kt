package pt.ipvc.ecocampus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import pt.ipvc.ecocampus.data.model.OrderEntity
import pt.ipvc.ecocampus.data.model.UserEntity
import pt.ipvc.ecocampus.data.model.WalletEntity

// Eu defino a configuração principal da Base de Dados Local (Room).
// Eu listo quais as tabelas (Entidades) que o telemóvel vai guardar.
@Database(
    entities = [UserEntity::class, WalletEntity::class, OrderEntity::class], // Eu tenho 3 tabelas
    version = 1, // Eu estou na versão 1 (se mudares as tabelas, sobe para 2)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Eu forneço a "porta de entrada" para os comandos SQL.
    // O Repositório usa esta função para conseguir ler e escrever dados.
    abstract fun userDao(): UserDao
}