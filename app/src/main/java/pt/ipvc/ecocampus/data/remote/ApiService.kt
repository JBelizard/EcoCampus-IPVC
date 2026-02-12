package pt.ipvc.ecocampus.data.remote

import pt.ipvc.ecocampus.data.model.RemoteMenu
import pt.ipvc.ecocampus.data.model.RemoteProduct
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    // Eu faço um pedido GET para descarregar a Ementa do link
    // (O @Url permite-me usar o link direto do GitHub Gist em vez de um fixo)
    @GET
    suspend fun getDailyMenu(@Url url: String): RemoteMenu

    // Eu faço um pedido GET para descarregar a lista de produtos do Bar
    @GET
    suspend fun getBarProducts(@Url url: String): List<RemoteProduct>
}