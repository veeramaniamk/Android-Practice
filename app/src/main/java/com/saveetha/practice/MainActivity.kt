package com.saveetha.practice

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.saveetha.practice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "messages_channel"
        const val NOTIF_ID = 1001
        const val REQUEST_NOTIFICATIONS_CODE = 1000
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        WindowCompat.setDecorFitsSystemWindows(window, false)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutContainer) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply padding to avoid cutting content
            view.setPadding(0, systemBars.top, 0,systemBars.bottom)
            insets
        }

    }


  /*
  private fun createConversationShortcut(): String {

        binding.btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13+
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // Request notification permission
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS_CODE)
                    return@setOnClickListener
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Recreate the channel before checking to refresh bubble capability
                createNotificationChannel()

//                if (!isBubbleAllowed()) {
//                    Toast.makeText(
//                        this,
//                        "Please enable bubble permission for this app",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    requestBubblePermission()
//                } else {
                sendBubbleNotification("Chat Bubble", "Click to open bubble chat")
//                }
            } else {
                Toast.makeText(
                    this,
                    "Bubbles require Android 11 or above",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val shortcutId = "chat_veer"

        // Intent that opens when tapping the shortcut or bubble
        val intent = Intent(this, BubbleNotificationActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("chatName", "Veer")
        }

        val shortcut = ShortcutInfoCompat.Builder(this, shortcutId)
            .setShortLabel("Veer")
            .setLongLabel("Chat with Veer")
            .setIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher))
            .setIntent(intent)
            .setLongLived(false) // important: needed for conversations/bubbles
            .build()

        // Publish the dynamic shortcut
//        ShortcutManagerCompat.pushDynamicShortcut(this, shortcut)

        return shortcutId
    }

    private fun requestBubblePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
            val appBubbleSettingIntent = Intent(Settings.ACTION_APP_NOTIFICATION_BUBBLE_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            startActivity(appBubbleSettingIntent)
        } else {
            // Below Android 11, just show info (bubbles aren't supported)
            Toast.makeText(this, "Bubbles are supported only on Android 11 and above", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATIONS_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled! Try the button again.", Toast.LENGTH_SHORT).show()
                // Optionally re-check bubbles now
            } else {
                Toast.makeText(this, "Notifications permission denied. Bubbles require this.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isBubbleAllowed(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (!nm.areBubblesAllowed()) return false

            val channel = nm.getNotificationChannel(CHANNEL_ID)
            if (channel == null) return false

            // Some devices require you to re-create the channel after enabling
            if (!channel.canBubble()) {
                // Recreate the channel to ensure it supports bubbles
                createNotificationChannel()
                val updated = nm.getNotificationChannel(CHANNEL_ID)
                return updated?.canBubble() == true
            }

            return true
        }
        return false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Messages"
            val channelDesc = "Channel for messages and bubbles"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDesc
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    setAllowBubbles(true)
                }
            }

            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun sendBubbleNotification(title: String, text: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val shortcutId = createConversationShortcut()

        val bubbleIntent = Intent(this, BubbleNotificationActivity::class.java).apply {
            putExtra("chatName", "Veer")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val bubblePendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT

        val bubblePendingIntent = PendingIntent.getActivity(
            this,
            0,
            bubbleIntent,
            bubblePendingIntentFlags
        )

        val bubbleIcon = IconCompat.createWithResource(this, R.mipmap.ic_launcher)
        val bubbleMetadata = NotificationCompat.BubbleMetadata.Builder(bubblePendingIntent, bubbleIcon)
            .setDesiredHeight(600)
            .setAutoExpandBubble(true)
            .setSuppressNotification(true)
            .build()

        val contentIntent = PendingIntent.getActivity(
            this,
            1,
            Intent(this, BubbleNotificationActivity::class.java),
            bubblePendingIntentFlags
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_arrow_circle_down_24)
            .setContentTitle(title)
            .setContentText(text)
            .setShortcutId(shortcutId) // ✅ link to the conversation shortcut
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setBubbleMetadata(bubbleMetadata)
            .setStyle(NotificationCompat.MessagingStyle("You")
                .setConversationTitle("Chat with Veer")
                .addMessage(text, System.currentTimeMillis(), "Veer"))

        notificationManager.notify(NOTIF_ID, builder.build())
    }*/


}
