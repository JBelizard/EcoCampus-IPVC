package pt.ipvc.ecocampus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

//Tabela User e seus dados

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val studentNumber: String, // Nº de Aluno
    val passwordHash: String   // Simulado
)

