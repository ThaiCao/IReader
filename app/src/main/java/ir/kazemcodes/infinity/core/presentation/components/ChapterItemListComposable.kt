package ir.kazemcodes.infinity.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import ir.kazemcodes.infinity.core.domain.models.Chapter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChapterListItemComposable(modifier : Modifier = Modifier,chapter : Chapter,goTo : () -> Unit) {
    ListItem(
        modifier = modifier.clickable {
            goTo()
        },
        text = {                             Text(
            text =chapter.title,
            color = if (chapter.haveBeenRead) MaterialTheme.colors.onBackground.copy(
                alpha = .4f) else MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            overflow = TextOverflow.Ellipsis,
        ) },
        trailing = {
            Icon(
                imageVector = Icons.Default.PublishedWithChanges,
                contentDescription = "Cached",
                tint = if (chapter.content.joinToString(" , ").length > 10) MaterialTheme.colors.onBackground else MaterialTheme.colors.background,
            )
        },
        secondaryText = {
            Text(
                text = chapter.dateUploaded ?: "",
                fontStyle = FontStyle.Italic,
                color = if (chapter.haveBeenRead) MaterialTheme.colors.onBackground.copy(
                    alpha = .4f) else MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.caption
            )
        }

    )
}