package org.readium.r2.testapp.bookshelf

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.readium.r2.testapp.R
import org.readium.r2.testapp.compose.BookCover
import org.readium.r2.testapp.compose.TopBarState
import org.readium.r2.testapp.domain.model.Book
import java.io.File

@ExperimentalFoundationApi
@Composable
fun BookshelfScreen(updateTopBarState: (TopBarState) -> Unit, viewModel: BookshelfViewModel = viewModel()) {
    val listState = rememberLazyGridState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
//    val selectedBook = remember { mutableStateOf(Book()) }
    val activity = LocalContext.current as Activity

    updateTopBarState(
        TopBarState(
            title = stringResource(id = R.string.title_bookshelf),
        )
    )

    when (uiState) {
        is BookshelfViewModel.BookshelfUiState.HasBooks -> BookshelfList(
            context = context,
            listState = listState,
            books = (uiState as BookshelfViewModel.BookshelfUiState.HasBooks).books,
            openBook = { book ->
                viewModel.openBook(book.id!!, activity)
            },
            onItemLongSelected = {
                openDialog.value = true
//                selectedBook.value = it
            },
        )
        else -> Loading()
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = stringResource(id = R.string.confirm_delete_book_title)) },
            text = { Text(text = stringResource(id = R.string.confirm_delete_book_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
//                        viewModel.deleteBook(selectedBook.value)
                        openDialog.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun BookshelfList(
    context: Context,
    listState: LazyGridState,
    books: List<Book>,
    openBook: (Book) -> Unit,
    onItemLongSelected: (Book) -> Unit,
) {
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(books.size) { index ->
            val coverImageFile = File("${context.filesDir?.path}/covers/${books[index].id}.png")
            BookCover(
                title = books[index].title,
                coverImage = coverImageFile,
                onItemSelected = { openBook(books[index]) },
                onItemLongSelected = { onItemLongSelected(books[index]) }
            )
        }
    }
}

@Composable
fun Loading() {
    Text(text = "Loading...")
}