package com.example.easy_cart

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.easy_cart.databinding.ActivityCreateShoppingListBinding
import com.example.easy_cart.databinding.ActivityUpdateShoppingListBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import java.util.Date


class UpdateShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateShoppingListBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private var shoppingListId: Int = -1
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("UpdateShoppingListActivity", "Activity Created")
        binding = ActivityUpdateShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ShoppingListDatabaseHelper(this)

        shoppingListId = intent.getIntExtra("shopping_list_id", -1)
        Log.d("UpdateShoppingListActivity", "Received ID: $shoppingListId")
        if (shoppingListId == -1) {
            Log.d("UpdateShoppingListActivity", "Shopping list id not found")
            finish()
            return
        }

        val sList = db.getListByID(shoppingListId)
        binding.updateTitleEditText.setText(sList.title)
        binding.updateContentEditText.setText(sList.content)
        binding.updateDateEditText.setText(sList.shoppingDate)

        // Open Date Picker when clicking on Date EditText
        binding.updateDateEditText.setOnClickListener {
            showDatePicker()
        }

        // Save Updated Data and schedule reminder
        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()
            val shoppingDate = binding.updateDateEditText.text.toString()

            // Convert shoppingDate to milliseconds (long)
            val shoppingDateInMillis = convertToMillis(shoppingDate)

            // Schedule reminder for the updated shopping date
            scheduleShoppingReminder(this, shoppingDateInMillis)

            // Test the alarm by setting a reminder 1 minute from now
            val testTime = System.currentTimeMillis() + 10000 // 30 sec from now
            scheduleShoppingReminder(this, testTime)

            // Update the shopping list in the database
            val updatedShoppingList = ShoppingList(shoppingListId, newTitle, newContent, shoppingDate, false, false)
            db.updateLists(updatedShoppingList)

            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.updateDateEditText.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    // Convert the date in "yyyy-MM-dd" format to milliseconds
    private fun convertToMillis(dateString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        return date?.time ?: System.currentTimeMillis() // If parsing fails, use current time as fallback
    }

    //code that sets alarm according to user provided date
    // Schedule the shopping reminder at the provided shopping date (in milliseconds)
//    fun scheduleShoppingReminder(context: Context, shoppingDate: Long) {
//        // Check if the app has permission to schedule exact alarms on Android 12 and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val isPermissionGranted = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
//            if (!isPermissionGranted) {
//                // Prompt user to go to settings to grant permission for exact alarms
//                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
//                context.startActivity(intent)
//                return // Don't proceed with scheduling alarm until permission is granted
//            }
//        }
//
//        // Proceed with setting the alarm after permission check
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, ShoppingReminderReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        // Set the alarm to trigger at the shopping date time
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, shoppingDate, pendingIntent)
//    }

    //code to check if reminder works
    // Schedule the shopping reminder at the provided shopping date (in milliseconds)
    private fun scheduleShoppingReminder(context: Context, shoppingDate: Long) {
        // Check if the app has permission to schedule exact alarms on Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                // Notify the user that notifications are disabled
                Log.d("ShoppingReminderReceiver", "Notifications are not enabled.")

                // Optionally, prompt the user to go to settings to enable notifications
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
                return
            }
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val isPermissionGranted = alarmManager.canScheduleExactAlarms()
            if (!isPermissionGranted) {
                // Prompt user to grant permission for exact alarms
                val permissionIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(permissionIntent)
                Log.d("UpdateShoppingListActivity", "Exact alarm permission not granted.")
                return // Do not proceed until permission is granted
            }
        }

        // Proceed with setting the alarm after permission check
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ShoppingReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, shoppingDate, pendingIntent)
        Log.d("UpdateShoppingListActivity", "Alarm scheduled for $shoppingDate")
        val date = Date(shoppingDate)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        Log.d("UpdateShoppingListActivity", "Scheduled time: ${sdf.format(date)}")

    }


}
