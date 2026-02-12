package pt.ipvc.ecocampus.data.model

// Eu defino a estrutura do prato detalhado (como está no teu JSON)
data class RemoteDish(
    val name: String,
    val ingredients: String,
    val price: Double
)

// Eu atualizo a ementa para usar estes objetos em vez de texto simples
data class RemoteMenu(
    val soup: RemoteDish,
    val meat: RemoteDish,
    val fish: RemoteDish,
    val diet: RemoteDish
)

// O Produto do Bar mantém-se igual
data class RemoteProduct(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val image: String? = null
)

