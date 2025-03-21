package com.example.easy_cart

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.easy_cart.databinding.ActivityCreateShoppingListBinding
import java.util.Calendar

class CreateShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateShoppingListBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private lateinit var dateEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ShoppingListDatabaseHelper(this)

        // Set up the Toolbar as ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable the back button
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.saveButton.setOnClickListener{
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val shoppingDate = binding.dateEditText.text.toString()

            //validate inputs
            if (title.isEmpty()){
                Toast.makeText(this, "Title can not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (content.isEmpty()){
                Toast.makeText(this,"Content can not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (shoppingDate.isEmpty()){
                Toast.makeText(this, "Date can not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deleted = false;
            val favorite = false;
            val shoppingList = ShoppingList(0, title, content, shoppingDate, deleted, favorite)
            db.insertList(shoppingList)
            finish()
            Toast.makeText(this, "Shopping List Saved", Toast.LENGTH_SHORT).show()
        }

        dateEditText = findViewById(R.id.dateEditText)

        dateEditText.setOnClickListener {
            showDatePickerDialog()
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
                // Format selected date and display in EditText
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                dateEditText.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Handle the back button press in the Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Closes the activity and goes back to MainActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
