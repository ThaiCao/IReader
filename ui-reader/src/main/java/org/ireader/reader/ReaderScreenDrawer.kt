package org.ireader.reader

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ireader.common_models.entities.Chapter
import org.ireader.components.components.ChapterListItemComposable
import org.ireader.components.reusable_composable.AppIconButton
import org.ireader.components.reusable_composable.BigSizeTextComposable
import org.ireader.components.text_related.ErrorTextWithEmojis
import org.ireader.core_api.source.Source
import org.ireader.core_ui.ui_components.LazyColumnScrollbar
import org.ireader.reader.viewmodel.ReaderScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReaderScreenDrawer(
    modifier: Modifier = Modifier,
    readerScreenState: ReaderScreenState,
    chapter: Chapter?,
    source: Source,
    onChapter: (chapter: Chapter) -> Unit,
    chapters: List<Chapter>,
    onReverseIcon: () -> Unit,
    drawerScrollState: LazyListState,
    onMap:(LazyListState) -> Unit
) {

    Column(modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
        Spacer(modifier = modifier.height(5.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            BigSizeTextComposable(text = "Content", modifier = Modifier.align(Alignment.Center))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = modifier.fillMaxWidth()) {
                Box {}

                AppIconButton(imageVector = Icons.Filled.Place, title = "", onClick = {
                    onMap(drawerScrollState)
                })
                AppIconButton(imageVector = Icons.Default.Sort,
                    title = "Reverse list icon",
                    onClick = {
                        onReverseIcon()
                    })

            }
        }

        Spacer(modifier = modifier.height(5.dp))
        Divider(modifier = modifier.fillMaxWidth(), thickness = 1.dp)
        LazyColumnScrollbar(listState = drawerScrollState) {
            LazyColumn(modifier = Modifier.fillMaxSize(),
                state = drawerScrollState) {
                items(count = readerScreenState.drawerChapters.value.size) { index ->
                    ChapterListItemComposable(modifier = modifier,
                        chapter =  readerScreenState.drawerChapters.value[index],
                        onItemClick = { onChapter( readerScreenState.drawerChapters.value[index]) },
                        isLastRead = chapter?.id ==  readerScreenState.drawerChapters.value[index].id)
                }
            }
        }
        if (chapters.isEmpty()) {
            ErrorTextWithEmojis(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally),
                error = "There is no book is Library, you can add books in the Explore screen"
            )
        }


    }
}