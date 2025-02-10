package com.example.easy_cart

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

class CreateShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateShoppingListBinding
    private lateinit var db: ShoppingListDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ShoppingListDatabaseHelper(this)

        binding.saveButton.setOnClickListener{
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()
            val shoppingList = ShoppingList(0, title, content)
            db.insertList(shoppingList)
            finish()
            Toast.makeText(this, "Shopping List Saved", Toast.LENGTH_SHORT).show()
        }


    }
}
