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
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import androidx.core.content.FileProvider
import java.io.FileOutputStream
import java.io.IOException
import androidx.recyclerview.widget.RecyclerView

class ShoppingListAdapter(private var shoppingLists: List<ShoppingList>, private val context: Context) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    private  val db: ShoppingListDatabaseHelper = ShoppingListDatabaseHelper(context)
    private var isAscending = true  // Track sorting order

    class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.shoppingTitleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateListButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val copyButton: ImageView = itemView.findViewById(R.id.copyButton)
        val favButton: ImageView = itemView.findViewById(R.id.favButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shopping_item, parent, false)
        return ShoppingListViewHolder(view)
    }

    fun exportToPDF () {

        if (shoppingLists.isEmpty()) {
            Toast.makeText(context, "No shopping lists to export!", Toast.LENGTH_SHORT).show()
            return
        }
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageWidth = 595 // A4 size width in pixels
        val pageHeight = 842 // A4 size height in pixels

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        var yPos = 50f

        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Shopping List", 200f, yPos, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        yPos += 40f

        for (shoppingList in shoppingLists) {
            if (yPos > pageHeight - 50) { // Start new page if space runs out
                pdfDocument.finishPage(page)
                val newPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
                val newPage = pdfDocument.startPage(newPageInfo)
                canvas.drawText("Shopping List (Continued)", 200f, 50f, paint)
                yPos = 90f
            }
            canvas.drawText("• ${shoppingList.title}: ${shoppingList.content}", 50f, yPos, paint)
            yPos += 30f
        }

        pdfDocument.finishPage(page)

        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "EasyCart")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "ShoppingList.pdf")
        try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
            openPDF(file);
            Toast.makeText(context, "PDF Exported: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }

    override fun getItemCount(): Int = shoppingLists.size

    private fun openPDF (file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No PDF viewer found!", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val sList = shoppingLists[position]
        holder.titleTextView.text = sList.title
        holder.contentTextView.text = sList.content



        // Set priority dynamically
        if (sList.priority == "High") {
            holder.priorityTextView.text = "High"
//            holder.priorityIcon.setImageResource(R.drawable.baseline_notification_important_24) // Red icon
        } else {
            holder.priorityTextView.text = "Low"
//            holder.priorityIcon.setImageResource(R.drawable.baseline_priority_low_24) // Grey icon
        }

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

        holder.copyButton.setOnClickListener{
            val title = sList.title + "Copy"
            val content = sList.content
            val shoppingDate = sList.shoppingDate
            val deleted = false;
            val favorite = false;
            val priority = sList.priority
            val shoppingList = ShoppingList(0, title, content, shoppingDate, deleted, favorite, priority)
            db.insertList(shoppingList)
            refreshData(db.getAllLists())
            Toast.makeText(holder.itemView.context, "List Duplicated", Toast.LENGTH_SHORT).show()
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

    fun filterList(query: String) {
        val filteredList = db.getAllLists().filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
        this.refreshData(filteredList)
    }

    fun toggleSortOrder() {
        isAscending = !isAscending
        val sortedList = if (isAscending) {
            shoppingLists.sortedBy { it.title }
        } else {
            shoppingLists.sortedByDescending { it.title }
        }
        refreshData(sortedList)
    }


}