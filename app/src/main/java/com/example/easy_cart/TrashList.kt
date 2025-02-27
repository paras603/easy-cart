package com.example.easy_cart

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easy_cart.databinding.ActivityTrashListBinding

class TrashList : AppCompatActivity() {
    private lateinit var binding: ActivityTrashListBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private lateinit var trashListAdapter: TrashListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Correct binding inflation
        binding = ActivityTrashListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = ShoppingListDatabaseHelper(this)
        trashListAdapter = TrashListAdapter(db.getAllTrashLists(), this)

        binding.trashRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trashRecyclerView.adapter = trashListAdapter

        // Navigate back to MainActivity
        binding.showShoppingList.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
}
