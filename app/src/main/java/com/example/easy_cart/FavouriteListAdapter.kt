package com.example.easy_cart

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class FavouriteListAdapter(private var shoppingLists: List<ShoppingList>, context: Context) :
    RecyclerView.Adapter<FavouriteListAdapter.FavouriteListViewHolder>() {

    private val db: ShoppingListDatabaseHelper = ShoppingListDatabaseHelper(context)
    private val appContext = context

    class FavouriteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.shoppingTitleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val updateButton: ImageView = itemView.findViewById(R.id.updateFavoriteButton)
        val favButton: ImageView = itemView.findViewById(R.id.favButton) // ✅ Added fav icon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_list, parent, false)
        return FavouriteListViewHolder(view)
    }

    override fun getItemCount(): Int = shoppingLists.size

    override fun onBindViewHolder(holder: FavouriteListViewHolder, position: Int) {
        val sList = shoppingLists[position]
        holder.titleTextView.text = sList.title
        holder.contentTextView.text = sList.content

        // ✅ Existing: Delete button
        holder.deleteButton.setOnClickListener {
            db.moveToTrash(sList.id.toInt())
            refreshData(db.getAllFavoriteList()) // ✅ Keep showing favorite list
            Toast.makeText(holder.itemView.context, "List Deleted Permanently", Toast.LENGTH_SHORT).show()
        }

        // ✅ Existing: Update button
        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateShoppingListActivity::class.java).apply {
                putExtra("shopping_list_id", sList.id)
            }
            Log.d("intent_value", sList.toString())
            holder.itemView.context.startActivity(intent)
        }

        // ✅ NEW: Favorite button toggle to remove from favorites
        holder.favButton.setOnClickListener {
            sList.favorite = false
            db.updateLists(sList) // Update the DB to set favourite = false

            refreshData(db.getAllFavoriteList()) // Refresh current favorite list
            Toast.makeText(appContext, "Removed from Favorites", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newShoppingLists: List<ShoppingList>) {
        shoppingLists = newShoppingLists
        notifyDataSetChanged()
    }
}
