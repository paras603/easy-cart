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

class TrashListAdapter(private var shoppingLists: List<ShoppingList>, context: Context) :
    RecyclerView.Adapter<TrashListAdapter.TrashListViewHolder>() { // ✅ Use TrashListViewHolder

    private val db: ShoppingListDatabaseHelper = ShoppingListDatabaseHelper(context)

    class TrashListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { // ✅ Correct ViewHolder
        val titleTextView: TextView = itemView.findViewById(R.id.shoppingTitleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val restoreButton: ImageView = itemView.findViewById(R.id.restoreButton) // Use restore instead of update
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashListViewHolder { // ✅ Correct ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trash_item, parent, false)
        return TrashListViewHolder(view)
    }

    override fun getItemCount(): Int = shoppingLists.size

    override fun onBindViewHolder(holder: TrashListViewHolder, position: Int) { // ✅ Correct ViewHolder
        val sList = shoppingLists[position]
        holder.titleTextView.text = sList.title
        holder.contentTextView.text = sList.content

        holder.restoreButton.setOnClickListener {
            db.restoreFromTrash(sList.id) // Restore item
            refreshData(db.getAllTrashLists()) // Refresh data
            Toast.makeText(holder.itemView.context, "List Restored", Toast.LENGTH_SHORT).show()
        }

        holder.deleteButton.setOnClickListener {
            db.deleteList(sList.id) // Delete permanently
            refreshData(db.getAllTrashLists())
            Toast.makeText(holder.itemView.context, "List Deleted Permanently", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newShoppingLists: List<ShoppingList>) {
        shoppingLists = newShoppingLists
        notifyDataSetChanged()
    }
}
