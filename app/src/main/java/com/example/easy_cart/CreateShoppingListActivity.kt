package com.example.easy_cart

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easy_cart.databinding.ActivityCreateShoppingListBinding
import java.util.Calendar
import java.util.UUID

class CreateShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateShoppingListBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private lateinit var dateEditText: EditText
    private lateinit var priorityRadioGroup: RadioGroup
    private lateinit var alreadyPurchased: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ShoppingListDatabaseHelper(this)

        // Set up the Toolbar as ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable the back button
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Initialize views
        dateEditText = findViewById(R.id.dateEditText)
        priorityRadioGroup = findViewById(R.id.priorityRadioGroup)
        alreadyPurchased = findViewById(R.id.purchaseCheckBox)

        // Date picker dialog
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val shoppingDate = binding.dateEditText.text.toString()

            // Validate inputs
            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (content.isEmpty()) {
                Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (shoppingDate.isEmpty()) {
                Toast.makeText(this, "Date cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get selected priority
            val selectedPriorityId = priorityRadioGroup.checkedRadioButtonId
            if (selectedPriorityId == -1) {
                Toast.makeText(this, "Please select a priority level", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedPriority = findViewById<RadioButton>(selectedPriorityId).text.toString()
            val isPurchased = alreadyPurchased.isChecked;
            val deleted = false
            val favorite = false
            val id = UUID.randomUUID().toString()
            val shoppingList = ShoppingList(id, title, content, shoppingDate, deleted, favorite, selectedPriority,
                isPurchased
            )
            db.insertList(shoppingList)
            finish()
            Toast.makeText(this, "Shopping List Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                dateEditText.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Handle the back button press in the Toolbar
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Closes the activity and goes back to MainActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
