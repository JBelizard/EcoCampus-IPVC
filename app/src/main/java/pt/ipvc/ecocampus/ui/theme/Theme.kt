package pt.ipvc.ecocampus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Vamos forçar o tema claro para garantir o fundo cinza pedido
private val LightColorScheme = lightColorScheme(
    primary = IPVCGreen,
    secondary = IPVCBlue,
    tertiary = IPVCYellow,
    background = BackgroundGray,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onSecondary = SurfaceWhite,
    onTertiary = TextBlack,
    onBackground = TextBlack,
    onSurface = TextBlack
)

@Composable
fun EcoCampusIPVCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desligar cores dinâmicas para usar as nossas
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // Usar sempre o nosso esquema

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

