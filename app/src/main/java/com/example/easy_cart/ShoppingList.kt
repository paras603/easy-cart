package com.example.easy_cart

data class ShoppingList(
    val id: String,
    val title: String,
    val content: String,
    val shoppingDate: String,
    val deleted: Boolean,
    var favorite: Boolean,
    val priority: String,
    val isPurchased: Boolean
)
