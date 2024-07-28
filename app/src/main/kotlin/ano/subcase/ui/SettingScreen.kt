package ano.subcase.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ano.subcase.BuildConfig
import ano.subcase.R

@Composable
fun SettingScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { SettingTopBar(navController) },
    ) {
        Column(
            modifier = Modifier.padding(it)
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        ) {

            MoreSpan()
            Spacer(modifier = Modifier.height(10.dp))
            VersionSpan()
        }
    }
}

@Preview
@Composable
fun MoreSpan() {
    val urlHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        urlHandler.openUri("https://t.me/subcase_group")
                    }
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Telegram 频道")

                Icon(
                    Icons.Outlined.KeyboardArrowRight,
                    contentDescription = "加入 Telegram 频道",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun VersionSpan() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(id = R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")",
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "关于",
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "返回",
                )
            }
        },
        modifier = Modifier.systemBarsPadding(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        )
    )
}