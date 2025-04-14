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
import android.media.MediaScannerConnection
import android.os.Environment
import java.io.File
import androidx.core.content.FileProvider
import java.io.FileOutputStream
import java.io.IOException
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

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
        val isPurchase: TextView = itemView.findViewById(R.id.purchaseText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shopping_item, parent, false)
        return ShoppingListViewHolder(view)
    }

    fun exportToPDF() {
        if (shoppingLists.isEmpty()) {
            Toast.makeText(context, "No shopping lists to export!", Toast.LENGTH_SHORT).show()
            return
        }

        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageWidth = 595
        val pageHeight = 842

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var yPos = 60f
        val margin = 40f

        // Document title
        paint.apply {
            textAlign = Paint.Align.CENTER
            textSize = 20f
            isFakeBoldText = true
        }
        canvas.drawText("Shopping Lists", pageWidth / 2f, yPos, paint)
        yPos += 20f

        // Separator line
        paint.strokeWidth = 2f
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, paint)
        yPos += 30f

        // Reset paint for list content
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 14f
        paint.isFakeBoldText = false

        for ((index, shoppingList) in shoppingLists.withIndex()) {

            // Start new page if needed
            if (yPos > pageHeight - 100) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 60f

                // Header for continuation page
                paint.textAlign = Paint.Align.CENTER
                paint.textSize = 18f
                paint.isFakeBoldText = true
                canvas.drawText("Shopping Lists (Continued)", pageWidth / 2f, yPos, paint)
                yPos += 30f
                paint.textAlign = Paint.Align.LEFT
                paint.textSize = 14f
                paint.isFakeBoldText = false
            }

            // List title
            paint.isFakeBoldText = true
            canvas.drawText("${index + 1}. ${shoppingList.title}", margin, yPos, paint)
            yPos += 20f

            // List content
            paint.isFakeBoldText = false
            val lines = shoppingList.content.split("\n")
            for (line in lines) {
                if (yPos > pageHeight - 60) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPos = 60f
                }
                canvas.drawText("   • $line", margin + 20f, yPos, paint)
                yPos += 18f
            }

            yPos += 20f // Space between lists
        }

        pdfDocument.finishPage(page)

        // Save PDF
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "EasyCart")
        if (!directory.exists()) directory.mkdirs()

        val file = File(directory, "ShoppingList.pdf")
        try {
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
            openPDF(file)
            Toast.makeText(context, "PDF Exported: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
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

        if(sList.isPurchased){
            holder.isPurchase.text = "Already Purchased"
        }else{
            holder.isPurchase.text = ""
        }

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateShoppingListActivity::class.java).apply {
                putExtra("shopping_list_id", sList.id.toString())
            }
            Log.d("intent_value", sList.toString())
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.moveToTrash(sList.id.toInt())
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
            val isPurchased = sList.isPurchased
            val id = UUID.randomUUID().toString();
            val shoppingList = ShoppingList(id, title, content, shoppingDate, deleted, favorite, priority, isPurchased)
            db.insertList(shoppingList)
            refreshData(db.getAllLists())
            Toast.makeText(holder.itemView.context, "List Duplicated", Toast.LENGTH_SHORT).show()
        }


        holder.favButton.setOnClickListener {
//            holder.favButton.isSelected = !holder.favButton.isSelected
            db.moveToFavorite(sList.id.toInt());
            Toast.makeText(holder.itemView.context, "Added to Favourite List", Toast.LENGTH_SHORT).show()
        }

        // Set favorite icon initially
        if (sList.favorite) {
            holder.favButton.setImageResource(R.drawable.baseline_favorite_24) // Filled heart
        } else {
            holder.favButton.setImageResource(R.drawable.baseline_favorite_border_24) // Empty heart
        }

        holder.favButton.setOnClickListener {
//            val updatedFavorite = !sList.favorite
            db.moveToFavorite(sList.id.toInt())
            refreshData(db.getAllLists())
//            val message = if (updatedFavorite) "Added to Favourite List" else "Removed from Favourite List"
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