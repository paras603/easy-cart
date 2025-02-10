package com.example.easy_cart

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

class UpdateShoppingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateShoppingListBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private var shoppingListId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("UpdateShoppingListActivity", "Activity Created")
        binding = ActivityUpdateShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ShoppingListDatabaseHelper(this)

        shoppingListId = intent.getIntExtra("list_id", -1)

        Log.d("UpdateShoppingListActivity", "Received ID: $shoppingListId")
        if (shoppingListId == -1){
            finish()
            return
        }

        val sList = db.getListByID(shoppingListId)
        binding.updateTitleEditText.setText(sList.title)
        binding.updateContentEditText.setText(sList.content)

        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newContent = binding.updateContentEditText.text.toString()
            val updatedShoppingList = ShoppingList(shoppingListId, newTitle, newContent)
            db.updateLists(updatedShoppingList)
            finish()
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }
    }
}