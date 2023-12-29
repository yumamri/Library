package com.bordeauxuniversity.library

import DBBook
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bordeauxuniversity.library.ui.theme.LibraryTheme

class MainActivity : ComponentActivity() {
    private lateinit var dbBook: DBBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbBook = DBBook(this)
        setContent {
            LibraryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Library("World :D ", modifier = Modifier, dbBook)
                }
            }
        }
    }

}

@Composable
fun Library(name: String, modifier: Modifier = Modifier, dbBook: DBBook) {
    var showSearchScreen by remember { mutableStateOf(false) }
    var showLaunchScreen by remember { mutableStateOf(true) }
    var book: Book? by remember { mutableStateOf(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        if (showLaunchScreen && !showSearchScreen) {
            LaunchScreen(
                onSearchClick = {
                    title, context ->
                    book = searchBookFromDatabase(title, dbBook, context)
                    showSearchScreen = true
                    showLaunchScreen = false
                },
                onAddClick = {
                    title, isbn, context ->
                    showSearchScreen = false
                    showLaunchScreen = true
                    insertBookToDatabase(title, isbn, dbBook, context)
                })
        }
        if (!showLaunchScreen && showSearchScreen) {
            book?.let {
                SearchScreen(
                isbn = it.isbn,
                title = it.title,
                onBackClick = {
                    showSearchScreen = false
                    showLaunchScreen = true
                }
            ) }
        }
    }

}

// Function to show a toast message
fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

// Function to search a book
fun searchBookFromDatabase(title: String, dbBook: DBBook, context: Context): Book? {
    val book = dbBook.getBookByTitle(title)
    if (book != null) {
        // Successfully found the book
        showToast("Book found!", context)
    } else {
        // Failed to find the book
        showToast("Book not found", context)
    }
    Log.d("BOOK", book.toString())
    return book
}

// Function to insert a book
fun insertBookToDatabase(title: String, isbn: String, dbBook: DBBook, context: Context) {
    val newBook = Book(id = 0, title = title, isbn = isbn)
    val insertedRowId = dbBook.insertBook(newBook)

    if (insertedRowId != -1L) {
        // Successfully inserted the book
        showToast("Book added to database!", context)
    } else {
        // Failed to insert the book
        showToast("Failed to add book to database", context)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun LaunchScreen(onSearchClick: (String, Context) -> Unit, onAddClick: (String, String, Context) -> Unit) {
    val rainbowColors: List<Color> = listOf(Color.Green, Color.Blue)
    var textIsbn by remember { mutableStateOf("") }
    var textTitle by remember { mutableStateOf("") }
    var textSearch by remember { mutableStateOf("") }
    var context = LocalContext.current
    val brush = remember {
        Brush.linearGradient(
            colors = rainbowColors
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add a book",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "ISBN"
        )
        OutlinedTextField(
            value = textIsbn,
            onValueChange = {
                // Limit the text to 10 characters
                if (it.length <= 10) {
                    textIsbn = it
                }
            },
            label = { Text("Type the ISBN") },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.padding(end = 16.dp) // Adjust as needed
        )
        Text(
            text = "${textIsbn.length}/10",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Right,
            ),
        )
        Text(
            text = "Title"
        )
        OutlinedTextField(
            value = textTitle,
            onValueChange = { textTitle = it },
            label = { Text("Type the title") },
            textStyle = TextStyle(brush = brush),
            singleLine = true
        )
        Button(onClick = { onAddClick(textTitle, textIsbn, context) }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add book")
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            thickness = 2.dp,
            color = Color.Black
        )
        Text(
            text = "Search a book",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Title to search"
        )
        OutlinedTextField(
            value = textSearch,
            onValueChange = { textSearch = it },
            label = { Text("Search book with title") },
            textStyle = TextStyle(brush = brush),
            singleLine = true
        )
        Button(onClick = { onSearchClick(textSearch, context) }) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Search book")
        }
    }
}

@Composable
fun SearchScreen(isbn: String, title: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ISBN",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = isbn
        )
        Text(
            text = "Title",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = title
        )
        Button(onClick = { onBackClick() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Back")
        }
    }
}
