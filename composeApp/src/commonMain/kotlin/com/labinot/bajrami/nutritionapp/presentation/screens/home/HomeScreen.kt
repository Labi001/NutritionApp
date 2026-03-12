package com.labinot.bajrami.nutritionapp.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.labinot.bajrami.nutritionapp.domain.helpers.Alpha
import com.labinot.bajrami.nutritionapp.domain.helpers.Black
import com.labinot.bajrami.nutritionapp.domain.helpers.DisplayResult
import com.labinot.bajrami.nutritionapp.domain.helpers.FontSize
import com.labinot.bajrami.nutritionapp.domain.helpers.Resources
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.presentation.component.InfoCard
import com.labinot.bajrami.nutritionapp.presentation.component.LoadingCard
import com.labinot.bajrami.nutritionapp.presentation.component.MainProductCard
import com.labinot.bajrami.nutritionapp.presentation.component.ProductCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(navigator: AppNavigator){

    val viewModel = koinViewModel<HomeViewModel>()
    val products by viewModel.products.collectAsState()
    val listState = rememberLazyListState()

    val centeredIndex: Int? by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                val itemCenter = item.offset + item.size / 2
                kotlin.math.abs(itemCenter - viewportCenter)
            }?.index
        }
    }

    Column(modifier = Modifier.fillMaxSize()
        .background(Surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top)
    {

        products.DisplayResult(
            onLoading = {LoadingCard(modifier = Modifier.fillMaxSize())},

            onSuccess = { productList ->

                AnimatedContent(
                    targetState = productList.distinctBy { it.id }
                ) {  products ->

                    if(products.isNotEmpty()){

                        Column(modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top) {


                            LazyRow(
                                state = listState,
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                itemsIndexed(
                                    items = products
                                        .filter { it.isNew }
                                        .sortedBy { it.createdAt }
                                        .take(6),
                                    key = { index, item -> item.id }
                                ){ index, product ->

                                    val isLarge = index == centeredIndex
                                    val animatedScale by animateFloatAsState(
                                        targetValue = if (isLarge) 1f else 0.8f,
                                        animationSpec = tween(300)
                                    )

                                    MainProductCard(
                                        modifier = Modifier
                                            .scale(animatedScale)
                                            .height(300.dp)
                                            .fillParentMaxWidth(0.6f),
                                        product = product,
                                        isLarge = isLarge,
                                        onClick = {
                                            navigator.navigateToDetails(product.id)
                                        }
                                    )



                                }



                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(Alpha.HALF),
                                text = "Discounted Products",
                                fontSize = FontSize.EXTRA_REGULAR,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = products
                                        .filter { it.isDiscounted }
                                        .sortedBy { it.createdAt }
                                        .take(3),
                                    key = { it.id }
                                ) { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = {
                                            navigator.navigateToDetails(product.id)
                                        }
                                    )
                                }
                            }




                        }


                    } else{

                        InfoCard(
                            image = Resources.Image.Cat,
                            title = "Nothing here",
                            subtitle = "Empty product list."
                        )

                    }



                }

            },
            onError = { message ->
                InfoCard(
                    image = Resources.Image.Cat,
                    title = "Oops!",
                    subtitle = message
                )
            }
        )





    }




}