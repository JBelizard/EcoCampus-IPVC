package pt.ipvc.ecocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import pt.ipvc.ecocampus.data.SessionManager
import pt.ipvc.ecocampus.data.local.AppDatabase
import pt.ipvc.ecocampus.ui.AppNavHost
import pt.ipvc.ecocampus.ui.theme.EcoCampusIPVCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializar o SessionManager (CRUCIAL PARA O LOGIN NÃO CRASHAR)
        SessionManager.init(applicationContext)

        // 2. Inicializar a Base de Dados Room
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ecocampus-db"
        )
            .fallbackToDestructiveMigration() // Isto previne crashes se mudares a BD: apaga e cria nova
            .build()

        setContent {
            EcoCampusIPVCTheme {
                // Passamos a base de dados para a navegação
                AppNavHost(database = db)
            }
        }
    }
}

