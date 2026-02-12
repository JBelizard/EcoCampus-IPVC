package pt.ipvc.ecocampus.data

import android.content.Context
import android.content.SharedPreferences

// Optei por um Singleton (object) para conseguir aceder à sessão em qualquer ecrã da app
object SessionManager {
    // Defino aqui as constantes para não me enganar a escrever as chaves mais tarde
    private const val PREF_NAME = "EcoCampusSession"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private lateinit var preferences: SharedPreferences

    // Preciso de inicializar isto logo no arranque (MainActivity) para ter o Contexto
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Quando faço login, guardo o ID e a flag de "logado".
    // Uso o apply() para gravar em background e não bloquear a interface.
    fun login(userId: Int) {
        preferences.edit().apply {
            putInt(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Limpo o ficheiro de preferências para garantir que o logout é total
    fun logout() {
        preferences.edit().clear().apply()
    }

    // Verifico simples se a chave "is_logged_in" é verdadeira
    // Adicionei a verificação 'isInitialized' para evitar crashes se chamar isto cedo demais
    fun isLoggedIn(): Boolean {
        return if (::preferences.isInitialized) {
            preferences.getBoolean(KEY_IS_LOGGED_IN, false)
        } else {
            false
        }
    }

    // Recupero o ID do utilizador atual para saber de quem é a carteira que vou carregar
    fun getCurrentUserId(): Int? {
        if (!::preferences.isInitialized) return null
        val id = preferences.getInt(KEY_USER_ID, -1)
        return if (id != -1) id else null
    }
}

