package com.example.easy_cart

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easy_cart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private var startTime: Long = 0 // Track splash start time

    override fun onCreate(savedInstanceState: Bundle?) {

        // Installs Splash Screen and extend its duration
        val splashScreen = installSplashScreen()

        startTime = System.currentTimeMillis() // Record start time

        splashScreen.setKeepOnScreenCondition {
            // Keeps the splash screen visible for at least 1 seconds
            System.currentTimeMillis() < startTime + 1000
        }


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

        binding.trashButton.setOnClickListener {
            val intent = Intent(this, TrashList::class.java)
            startActivity(intent)
        }

        binding.favourite.setOnClickListener{
            val intent = Intent(this, Fovourite::class.java)
            startActivity(intent)
        }

        binding.exportAsPdf.setOnClickListener{
            shoppingListAdapter.exportToPDF();
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                shoppingListAdapter.filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.searchBar.compoundDrawablesRelative[2] // Get drawableEnd
                if (drawableEnd != null && event.rawX >= (binding.searchBar.right - drawableEnd.bounds.width() - 20)) {
                    shoppingListAdapter.toggleSortOrder()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }


    override fun onResume() {
        super.onResume()
        shoppingListAdapter.refreshData(db.getAllLists())
    }

}
