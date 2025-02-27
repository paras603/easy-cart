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

    class FavouriteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.shoppingTitleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val updateButton: ImageView = itemView.findViewById(R.id.updateFavoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteListViewHolder { // ✅ Use correct ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_list, parent, false)
        return FavouriteListViewHolder(view)
    }

    override fun getItemCount(): Int = shoppingLists.size

    override fun onBindViewHolder(holder: FavouriteListViewHolder, position: Int) {
        val sList = shoppingLists[position]
        holder.titleTextView.text = sList.title
        holder.contentTextView.text = sList.content

        holder.deleteButton.setOnClickListener {
            db.moveToTrash(sList.id) // Delete permanently
            refreshData(db.getAllTrashLists())
            Toast.makeText(holder.itemView.context, "List Deleted Permanently", Toast.LENGTH_SHORT).show()
        }

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateShoppingListActivity::class.java).apply {
                putExtra("shopping_list_id", sList.id)
            }
            Log.d("intent_value", sList.toString())
            holder.itemView.context.startActivity(intent)
        }
    }

    fun refreshData(newShoppingLists: List<ShoppingList>) {
        shoppingLists = newShoppingLists
        notifyDataSetChanged()
    }
}
