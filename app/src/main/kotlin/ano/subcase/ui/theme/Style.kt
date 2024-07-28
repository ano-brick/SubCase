package ano.subcase.ui.theme

import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = Color.White,
    checkedTrackColor = Color(0xFF478ef2),
    checkedBorderColor = Color(0xFF478ef2),
    uncheckedThumbColor = Color.White,
    uncheckedTrackColor = Color(0xFFe8e8e8),
    uncheckedBorderColor = Color(0xFFe8e8e8),
)