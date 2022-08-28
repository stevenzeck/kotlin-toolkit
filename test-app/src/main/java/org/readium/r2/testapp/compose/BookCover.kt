package org.readium.r2.testapp.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import org.readium.r2.testapp.R
import java.io.File

@ExperimentalFoundationApi
@Composable
fun BookCover(title: String, coverImage: File, onItemSelected: () -> Unit, onItemLongSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .width(120.dp)
            .combinedClickable(
                onClick = { onItemSelected() },
                onLongClickLabel = stringResource(id = R.string.delete),
                onLongClick = { onItemLongSelected() }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = rememberImagePainter(coverImage),
                contentDescription = stringResource(id = R.string.cover_image),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .fillMaxWidth(),
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun BookCoverPreview() {
    val emptyNavigation: () -> Unit = {}
    BookCover("Moby Dick", File("https://test.opds.io/assets/moby/small.jpg"), emptyNavigation, emptyNavigation)
}