package com.example.easy_cart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easy_cart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private lateinit var shoppingListAdapter: ShoppingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ShoppingListDatabaseHelper(this)
        shoppingListAdapter = ShoppingListAdapter(db.getAllLists(), this)

        binding.shoppingListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.shoppingListRecyclerView.adapter = shoppingListAdapter

        binding.addButton.setOnClickListener{
            val intent = Intent(this, CreateShoppingListActivity::class.java)
            startActivity(intent)
        }

        binding.clearAllButton.setOnClickListener {
            db.deleteAllLists() // Clear all lists
            shoppingListAdapter.refreshData(db.getAllLists()) // Refresh RecyclerView
            Toast.makeText(this, "All lists cleared", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()
        shoppingListAdapter.refreshData(db.getAllLists())
    }

}
