package com.labinot.bajrami.nutritionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.labinot.bajrami.nutritionapp.domain.helpers.utils.PreferenceRepository
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.permissionUtil
import kotlin.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true,
            )
        )

        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()

        setContent {
            App()
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent.data ?: return

        val isSuccess = uri.getQueryParameter("success")
        val isCancelled = uri.getQueryParameter("cancel")
        val token = uri.getQueryParameter("token")



        PreferenceRepository.savePayPalData(
            isSuccess = isSuccess?.toBooleanStrictOrNull(),
            error = if (isCancelled == "null") null
            else "Payment has been canceled.",
            token = token
        )

    }


}


@Preview
@Composable
fun AppAndroidPreview() {

        App()


}