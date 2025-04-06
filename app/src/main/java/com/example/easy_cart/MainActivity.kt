package com.example.easy_cart

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easy_cart.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: ShoppingListDatabaseHelper
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private var startTime: Long = 0 // Track splash start time
    private lateinit var startTutorial: android.widget.Button

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

        startTutorial = findViewById(R.id.startTutorial)

        // Handle Button Click to Play Video
        startTutorial.setOnClickListener {
            showVideoPopup();
        }
    }

    private fun showVideoPopup() {
        // Create Dialog
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_video_player)

        // Find Views in Dialog
        val playerView = dialog.findViewById<PlayerView>(R.id.playerView)
        val btnClose = dialog.findViewById<Button>(R.id.btnClose)

        // Initialize ExoPlayer
        val player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Load WebM Video from Assets
        val videoUri = Uri.parse("file:///android_asset/tutorial.mp4")
        val mediaItem = MediaItem.fromUri(videoUri)

        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        // Handle Close Button
        btnClose.setOnClickListener {
            player.release() // Stop playback
            dialog.dismiss() // Close popup
        }

        // Show Dialog
        dialog.show()
    }


    override fun onResume() {
        super.onResume()
        shoppingListAdapter.refreshData(db.getAllLists())
    }

}
