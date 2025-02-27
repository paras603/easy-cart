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

class ShoppingListAdapter(private var shoppingLists: List<ShoppingList>, context: Context) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private  val db: ShoppingListDatabaseHelper = ShoppingListDatabaseHelper(context)

    class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.shoppingTitleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateListButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val favButton: ImageView = itemView.findViewById(R.id.favButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shopping_item, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun getItemCount(): Int = shoppingLists.size

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val sList = shoppingLists[position]
        holder.titleTextView.text = sList.title
        holder.contentTextView.text = sList.content

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateShoppingListActivity::class.java).apply {
                putExtra("shopping_list_id", sList.id)
            }
            Log.d("intent_value", sList.toString())
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.moveToTrash(sList.id)
            refreshData(db.getAllLists())
            Toast.makeText(holder.itemView.context, "List Deleted", Toast.LENGTH_SHORT).show()
        }


        holder.favButton.setOnClickListener {
//            holder.favButton.isSelected = !holder.favButton.isSelected
            db.moveToFavorite(sList.id);
            Toast.makeText(holder.itemView.context, "Added to Favourite List", Toast.LENGTH_SHORT).show()
        }


    }

    fun refreshData(newShoppingLists: List<ShoppingList>){
        shoppingLists = newShoppingLists
        notifyDataSetChanged()
    }

}