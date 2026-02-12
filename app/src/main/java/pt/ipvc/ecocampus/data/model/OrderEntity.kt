package pt.ipvc.ecocampus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//Tabela "orders" e seus dados

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val itemName: String,
    val price: Double,
    val date: Long = System.currentTimeMillis(),
    val type: String // "CANTINA" ou "BAR"
)

