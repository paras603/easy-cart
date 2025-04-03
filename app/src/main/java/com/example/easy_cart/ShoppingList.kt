package com.example.easy_cart

data class ShoppingList(
    val id: Int,
    val title: String,
    val content: String,
    val shoppingDate: String,
    val deleted: Boolean,
    val favorite: Boolean,
    val priority: String
)
