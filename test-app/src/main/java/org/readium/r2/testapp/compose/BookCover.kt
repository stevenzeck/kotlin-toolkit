package org.readium.r2.testapp.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import org.readium.r2.testapp.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookCover(
    modifier: Modifier = Modifier,
    title: String?,
    //TODO clean these two up once bookshelf is working
    coverImage: File? = null,
    coverImageHref: String? = null,
    onItemSelected: () -> Unit,
    onItemLongSelected: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .height(200.dp)
            .width(120.dp)
            .combinedClickable(
                onClick = { onItemSelected() },
                onLongClickLabel = stringResource(id = R.string.delete),
                onLongClick = {
                    if (onItemLongSelected != null) {
                        onItemLongSelected()
                    }
                }
            )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverImage ?: coverImageHref)
                    .build(),
                placeholder = painterResource(R.drawable.cover),
                contentDescription = stringResource(R.string.cover_image),
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )
            Box(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .fillMaxWidth(),
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        modifier = modifier.padding(16.dp),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun BookCoverPreview() {
    BookCover(
        title = "Moby Dick",
        coverImage = File("https://test.opds.io/assets/moby/small.jpg"),
        onItemSelected = {}
    ) {}
}