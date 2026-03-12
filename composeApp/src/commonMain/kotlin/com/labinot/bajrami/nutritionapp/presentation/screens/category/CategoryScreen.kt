package com.labinot.bajrami.nutritionapp.presentation.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.models.ProductCategory
import com.labinot.bajrami.nutritionapp.presentation.component.CategoryCard

@Composable
fun CategoryScreen(navigator: AppNavigator, ){

    Column(modifier = Modifier.fillMaxSize()
        .background(Surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top)
    {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProductCategory.entries.forEach { category ->
                CategoryCard(
                    category = category,
                    onClick = {

                         navigator.navigateToCategorySearch(category = category.title)

                    }
                )
            }
        }




    }




}