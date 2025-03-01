package com.example.easy_cart

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class ShoppingReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ShoppingReminderReceiver", "BroadcastReceiver triggered!") // ✅ Log to check if receiver is called

        val channelId = "shopping_list_channel"

        // Create a notification channel for Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Shopping List Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for shopping list reminders"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("ShoppingReminderReceiver", "Notification channel created.")
        }

        // Create the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Ensure this icon exists
            .setContentTitle("Shopping List Reminder")
            .setContentText("It's time for your shopping list!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically dismiss the notification when clicked
            .build()

        // Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)

        Log.d("ShoppingReminderReceiver", "Notification sent.")
    }

}
