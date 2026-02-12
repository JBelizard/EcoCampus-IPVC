package pt.ipvc.ecocampus.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Um Singleton (object), ou seja, existe apenas uma vez na memória da app
object RetrofitClient {

    // Eu defino o endereço base onde estão os ficheiros JSON (GitHub)
    private const val BASE_URL = "https://gist.githubusercontent.com/"

    // Eu crio a API apenas quando for necessária (by lazy) para poupar bateria e memória
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Eu aponto para o GitHub
            .addConverterFactory(GsonConverterFactory.create()) // Eu traduzo JSON para Kotlin automaticamente
            .build()
            .create(ApiService::class.java) // Eu gero o código final da interface
    }
}