package ano.subcase.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF121212),
    //onPrimary = Color(0xFFF4F4F4),
    //primaryVariant = Color(0xFF1E1E1E),

    secondary = PurpleGrey80,
    onSecondary = Color(0xFFF4F4F4),
    //secondaryVariant = PurpleGrey40,

    background = Color(0xFFF4F4F4),
    //onBackground = Color(0xFFF4F4F4),

    surface = Color.White,
    // onSurface = Color.Black,

    error = Color(0xFFD32F2F),
    onError = Color(0xFFD32F2F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF121212),
    onPrimary = Color(0xFFF4F4F4),
    primaryContainer = Color(0xFF1E1E1E),

    //secondaryVariant = PurpleGrey40,

    background = Color(0xFF121212),
    onBackground = Color(0xFFF4F4F4),

    surface = Color(0xFF202020),
    onSurface = Color.White,

    error = Color(0xFFD32F2F),
    onError = Color(0xFFD32F2F),
)


@Composable
fun SubCaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {

        val systemUiController = rememberSystemUiController()
        val useDarkIcons = !isSystemInDarkTheme()

        DisposableEffect(systemUiController, useDarkIcons) {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
            onDispose {}
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = shapes
    )
}