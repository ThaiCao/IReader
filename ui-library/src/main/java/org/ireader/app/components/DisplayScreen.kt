package org.ireader.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.ireader.common_models.DisplayMode
import org.ireader.common_models.LayoutType
import org.ireader.common_models.layouts
import org.ireader.components.text_related.RadioButton
import org.ireader.components.text_related.TextSection
import org.ireader.ui_library.R

@Composable
fun DisplayScreen(
    layoutType: LayoutType,
    onLayoutSelected: (DisplayMode) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TextSection(
            text = stringResource( R.string.display_mode),
            padding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            style =MaterialTheme.typography.bodyMedium,
        )
        layouts.forEach { layout ->
            RadioButton(
                text = stringResource(id = layout.title),
                selected = layoutType == layout.layout,
                onClick = {
                    onLayoutSelected(layout)
                }
            )
        }
    }
}
