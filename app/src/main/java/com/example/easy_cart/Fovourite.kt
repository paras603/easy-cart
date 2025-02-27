package com.example.easy_cart

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easy_cart.databinding.ActivityFovouriteBinding

class Fovourite : AppCompatActivity() {
    private lateinit var binding: ActivityFovouriteBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private lateinit var trashListAdapter: FavouriteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFovouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = ShoppingListDatabaseHelper(this)
        trashListAdapter = FavouriteListAdapter(db.getAllFavoriteList(), this)

        binding.favoriteList.layoutManager = LinearLayoutManager(this)
        binding.favoriteList.adapter = trashListAdapter

        // Navigate back to MainActivity
        binding.showShoppingList.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}