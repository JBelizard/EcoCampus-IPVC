package pt.ipvc.ecocampus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//Tabela Carteira e seus dados

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val userId: Int, // Chave Estrangeira (1:1 com User)
    val balance: Double = 0.0
)

