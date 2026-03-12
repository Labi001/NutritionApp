package com.labinot.bajrami.nutritionapp.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.labinot.bajrami.nutritionapp.domain.helpers.BorderIdle
import com.labinot.bajrami.nutritionapp.domain.helpers.BorderSecondary
import com.labinot.bajrami.nutritionapp.domain.helpers.FontSize
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextSecondary

@Composable
fun FlavorChip(
    flavor: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(size = 12.dp))
            .clickable { onClick() }
            .background(Surface)
            .border(
                width = 1.dp,
                color = if (isSelected) BorderSecondary else BorderIdle,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(all = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = flavor,
            fontSize = FontSize.SMALL,
            color = if (isSelected) TextSecondary else TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}