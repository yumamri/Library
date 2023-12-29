package com.bordeauxuniversity.library

class Book(val id: Int, val title: String, val isbn: String) {

    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(isbn.matches(Regex("^\\d{10}$"))) { "ISBN must be a 10-digit number" }
    }

    override fun toString(): String {
        return "Book(id=$id, title='$title', isbn='$isbn')"
    }
}
