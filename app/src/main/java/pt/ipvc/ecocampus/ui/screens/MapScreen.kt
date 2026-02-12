package pt.ipvc.ecocampus.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    // Coordenadas estáticas para evitar latência de rede desnecessária (carregamento instantâneo).
    val estg = LatLng(41.6932, -8.8464)
    val ese = LatLng(41.7032, -8.8264)
    val esa = LatLng(41.7937, -8.5427)
    val ess = LatLng(41.6970, -8.8210)
    val esce = LatLng(41.9317, -8.6231)

    // Configuro a câmara inicial. O 'remember' é vital para manter a posição se o ecrã rodar.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(estg, 12f) // Foco na ESTG (centro)
    }

    // A biblioteca 'Maps Compose' gere automaticamente o ciclo de vida (Create, Resume, Destroy) do mapa.
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Marcadores leves com info extra (snippet) ao clicar.
        Marker(state = MarkerState(position = estg), title = "ESTG - IPVC", snippet = "Tecnologia e Gestão")
        Marker(state = MarkerState(position = ese), title = "ESE - IPVC", snippet = "Educação")
        Marker(state = MarkerState(position = esa), title = "ESA - IPVC", snippet = "Agrária")
        Marker(state = MarkerState(position = ess), title = "ESS - IPVC", snippet = "Saúde")
        Marker(state = MarkerState(position = esce), title = "ESCE - IPVC", snippet = "Ciências Empresariais")
    }
}

