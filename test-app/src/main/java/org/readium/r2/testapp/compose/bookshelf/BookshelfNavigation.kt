package org.readium.r2.testapp.compose.bookshelf

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.readium.r2.testapp.compose.Screen

fun NavGraphBuilder.bookshelfScreen(
    onOpenBook: (bookId: Long) -> Unit
) {
    composable(route = Screen.BottomNav.Bookshelf.route) {
        BookshelfScreen(
            onOpenBook = onOpenBook
        )
    }
}
