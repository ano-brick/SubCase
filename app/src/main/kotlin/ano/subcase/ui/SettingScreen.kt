package ano.subcase.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ano.subcase.BuildConfig
import ano.subcase.CaseStatus
import ano.subcase.R
import ano.subcase.ui.theme.Blue
import ano.subcase.ui.theme.switchColors
import ano.subcase.util.ConfigStore
import ano.subcase.util.SubStore

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    val hapic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Setting") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                //  modifier = Modifier.heightIn(max = 56.dp),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FrontEndCard()
            Spacer(modifier = Modifier.padding(10.dp))
            BackEndCard()
            HintSpan()
            Spacer(modifier = Modifier.padding(10.dp))
            AllowLanSpan()
            if (CaseStatus.isAllowLan.value) {
                AllowLanHint()
            }
            Spacer(modifier = Modifier.padding(10.dp))
            //OpenSubStore(mViewModel)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 10.dp).clickable{
                            hapic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            SubStore.checkLatestVersion(showToast = true)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text("检测SubStore更新")
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))
            FooterSpan()
        }
    }
}

@Composable
fun FrontEndCard() {
    val clipboardManager = LocalClipboardManager.current
    val urlHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            stringResource(R.string.frontend),
            fontSize = 14.sp
        )
    }

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
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.address),
                )

                val host: String
                if (CaseStatus.isAllowLan.value) {
                    host = CaseStatus.lanIP.value
                } else {
                    host = "127.0.0.1"
                }

                Text(
                    "http://${host}:8080",
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString("http://${host}:8080"))
                    },
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(start = 10.dp),
                color = Color.LightGray,
                thickness = 0.5.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.version),
                )

                Text(
                    SubStore.localFrontendVersion,
                    modifier = Modifier.clickable {
                        urlHandler.openUri("https://github.com/sub-store-org/Sub-Store-Front-End/releases")
                    },
                    color = Blue
                )
            }

        }
    }
}

@Composable
fun BackEndCard() {
    val haptic = LocalHapticFeedback.current
    val urlHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        Text(
            stringResource(R.string.backend),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )
    }

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
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.address),
                )

                val clipboardManager = LocalClipboardManager.current

                val host: String = if (CaseStatus.isAllowLan.value) {
                    CaseStatus.lanIP.value
                } else {
                    "127.0.0.1"
                }

                Text(
                    "http://${host}:8081",
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString("http://${host}:8081"))
                    },
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(start = 10.dp),
                color = Color.LightGray,
                thickness = 0.5.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(R.string.version),
                )

                Text(
                    SubStore.localBackendVersion,
                    modifier = Modifier.clickable {
                        urlHandler.openUri("https://github.com/sub-store-org/Sub-Store/releases")
                    },
                    color = Blue
                )
            }
        }
    }
}

@Composable
fun AllowLanSpan() {
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.allow_lan),
            )

            Switch(
                checked = CaseStatus.isAllowLan.value,
                onCheckedChange = {
                    haptic.performHapticFeedback(
                        HapticFeedbackType.TextHandleMove
                    )

                    CaseStatus.isAllowLan.value = it
                    ConfigStore.isAllowLan = it
                },
                colors = switchColors(),
                modifier = Modifier.scale(0.9f)
            )
        }
    }
}

@Composable
fun HintSpan() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 5.dp),
        text = "你可以点击backend地址,来快速复制",
        color = Color(0xFF909399),
        fontSize = 14.sp
    )
}

@Composable
fun AllowLanHint() {

    if (CaseStatus.isWifi.value) {
        return
    }

    val text = "当前不是WIFI环境,无法获取准确的局域网IP!"

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 5.dp),
        text = text,
        color = Color(0xFFfaad14),
        fontSize = 14.sp
    )
}

@Composable
fun OpenSubStore(mViewModel: MainViewModel) {

    val host: String
    if (mViewModel.allowLan) {
        host = CaseStatus.lanIP.value
    } else {
        host = "127.0.0.1"
    }

    val urlHandler = LocalUriHandler.current

    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 5.dp),
        content = {
            Text("打开SubStore")
        },
        onClick = {
            urlHandler.openUri("http://${host}:8080/subs?api=http://${host}:8081")
        },
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color.White,
            containerColor = Blue
        )
    )
}

@Preview
@Composable
fun FooterSpan() {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "v" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")",
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        )

        IconButton(
            onClick = {
                uriHandler.openUri("https://github.com/angus-cx/SubCase")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_github),
                contentDescription = "github",
                tint = Color.Gray,
            )
        }
    }
}

