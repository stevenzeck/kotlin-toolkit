package org.readium.r2.testapp.compose.bookshelf

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import org.readium.r2.testapp.bookshelf.BookshelfViewModel
import org.readium.r2.testapp.compose.BookCover
import org.readium.r2.testapp.data.model.Book

@Composable
internal fun BookshelfScreen(
    modifier: Modifier = Modifier,
    viewModel: BookshelfViewModel = viewModel(),
    onOpenBook: (bookId: Long) -> Unit
) {
    Loading()
//    val listState = rememberLazyGridState()
//    val uiState by viewModel.uiState.collectAsState()
//    val context = LocalContext.current
//    val openDialog = remember { mutableStateOf(false) }
//
//    when (uiState) {
//        is BookshelfViewModel.BookshelfUiState.HasBooks -> BookshelfList(
//            context = context,
//            listState = listState,
//            books = (uiState as BookshelfViewModel.BookshelfUiState.HasBooks).books,
//            openBook = { book ->
//
//            },
//            onItemLongSelected = {
//                openDialog.value = true
//            },
//        )
//        else -> Loading()
//    }
//
//    if (openDialog.value) {
//        AlertDialog(
//            onDismissRequest = { openDialog.value = false },
//            title = { Text(text = stringResource(id = R.string.confirm_delete_book_title)) },
//            text = { Text(text = stringResource(id = R.string.confirm_delete_book_text)) },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        openDialog.value = false
//                    }
//                ) {
//                    Text(stringResource(id = R.string.delete))
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        openDialog.value = false
//                    }
//                ) {
//                    Text(text = stringResource(id = R.string.cancel))
//                }
//            },
//            containerColor = MaterialTheme.colorScheme.secondaryContainer,
//            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
//            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
//        )
//    }
}

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
                onItemSelected = { openBook(books[index]) }
            ) { onItemLongSelected(books[index]) }
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Text(modifier = modifier.fillMaxSize(), text = "Loading...")
}
