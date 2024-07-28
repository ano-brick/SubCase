package ano.subcase.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ano.subcase.receiver.NotificationReceiver
import ano.subcase.R
import ano.subcase.caseApp
import ano.subcase.service.SubStoreService
import ano.subcase.ui.caseActivity

object NotificationUtil {

    init {
        createNotificationChannel()
    }

    private const val CHANNEL_ID = "SubCaseChannel"

    val intent = Intent(caseApp, NotificationReceiver::class.java).apply {
        action = "YOUR_ACTION"
    }

    private val receiverIntent =
        PendingIntent.getBroadcast(
            caseApp,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    private val contentIntent = PendingIntent.getActivity(
        caseApp,
        0,
        Intent(caseApp, caseActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    private val action = NotificationCompat.Action.Builder(0, "停止服务", receiverIntent).build()

    val notificationBuilder = NotificationCompat.Builder(caseApp, CHANNEL_ID)
        .setContentTitle(caseApp.getText(R.string.app_name))
        .setContentText(caseApp.getText(R.string.notification_content))
        .setSmallIcon(R.drawable.ic_server)
        .addAction(action)
        .setContentIntent(contentIntent)
        .build()

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "常驻通知",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = caseApp.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    fun startNotification(subStoreService: SubStoreService) {
        if (ActivityCompat.checkSelfPermission(
                caseApp,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        subStoreService.startForeground(1, notificationBuilder)
    }

    fun stopNotification() {
        NotificationManagerCompat.from(caseApp).cancel(1)
    }

    fun checkAndRequestPermission() {
        if (ActivityCompat.checkSelfPermission(
                caseApp,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    caseActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}