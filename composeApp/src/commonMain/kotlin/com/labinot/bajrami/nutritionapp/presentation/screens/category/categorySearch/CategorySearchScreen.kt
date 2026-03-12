package com.labinot.bajrami.nutritionapp.presentation.screens.category.categorySearch

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.labinot.bajrami.nutritionapp.domain.helpers.Alpha
import com.labinot.bajrami.nutritionapp.domain.helpers.BebasNeueFont
import com.labinot.bajrami.nutritionapp.domain.helpers.BorderIdle
import com.labinot.bajrami.nutritionapp.domain.helpers.DisplayResult
import com.labinot.bajrami.nutritionapp.domain.helpers.FontSize
import com.labinot.bajrami.nutritionapp.domain.helpers.IconPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.Resources
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceDarker
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.presentation.component.InfoCard
import com.labinot.bajrami.nutritionapp.presentation.component.LoadingCard
import com.labinot.bajrami.nutritionapp.presentation.component.ProductCard

import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategorySearch(
    navigator: AppNavigator,
    viewModel: CategorySearchViewModel,
    categoryTitle: String)
{


    val products = viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val windowInfo = LocalWindowInfo.current
    val screenWidthPx = windowInfo.containerSize.width

    var isSearchActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }


    val maxSearchWidth = screenWidthPx.dp - 24.dp

    val searchWidthFraction by animateFloatAsState(
        targetValue = if (isSearchActive) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.7f, // Jelly-like bounce
            stiffness = 120f     // Smooth speed
        ),
        label = "SearchWidth"
    )

    val noRipple = remember { MutableInteractionSource() }

    val topSpace = 80.dp


    Column(modifier = Modifier.fillMaxSize()
        .background(Surface),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally)
    {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topSpace)
                .padding(horizontal = 4.dp)
        ) {


            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 2.dp)
                    .graphicsLayer {
                        val s = 1f - searchWidthFraction
                        scaleX = s
                        scaleY = s
                        alpha = s
                    }
                    .wrapContentWidth()
                    .height(60.dp)
                    .clickable(
                        interactionSource = noRipple,
                        indication = null
                    ) { /* Action */ },
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.popBack() }) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back Arrow",
                            tint = IconPrimary,
                            modifier = Modifier.requiredSize(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(7.dp))


                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = categoryTitle,
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary,
                        maxLines = 1
                    )
                }
            }

            // Search Bar (Right)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(60.dp),
                contentAlignment = Alignment.CenterEnd
            ){

                Box(
                    modifier = Modifier
                        .width(maxSearchWidth * searchWidthFraction)
                        .height(50.dp)
                        .padding(horizontal = 10.dp)
                        .border(
                            width = 1.dp,
                            color = BorderIdle,
                            shape = RoundedCornerShape(size = 25.dp)
                        )
                        .clip(RoundedCornerShape(25.dp))
                        .background(SurfaceDarker)
                )


                // Text Field
                if (searchWidthFraction > 0.1f) {

                    Row(
                        modifier = Modifier
                            .width(maxSearchWidth * searchWidthFraction)
                            .height(50.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = viewModel::updateSearchQuery,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                                    .focusRequester(focusRequester)
                                    .alpha(searchWidthFraction.coerceIn(0f, 1f)),
                                textStyle = TextStyle(color = IconPrimary,
                                    fontSize = FontSize.MEDIUM,
                                    lineHeight = FontSize.REGULAR * 1.3,),
                                cursorBrush = SolidColor(IconPrimary),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            modifier = Modifier
                                                .alpha(Alpha.HALF),
                                            text = "Search...",
                                            fontSize = FontSize.REGULAR,
                                            lineHeight = FontSize.REGULAR * 1.3,
                                            color = TextPrimary,
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                        // Spacer to prevent text overlapping button area
                        Spacer(modifier = Modifier.width(60.dp))
                    }
                }

                LaunchedEffect(isSearchActive) {
                    if (isSearchActive) {
                        delay(100)
                        focusRequester.requestFocus()
                    }
                }

                // Search/Close Icon Button
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable(interactionSource = noRipple, indication = null) {

                            if(searchQuery.isNotBlank()){
                                viewModel.updateSearchQuery("")
                            }else{

                                isSearchActive = !isSearchActive
                                if (!isSearchActive) viewModel.updateSearchQuery("")

                            }


                        },
                    contentAlignment = Alignment.Center
                ) {
                    // We use AnimatedContent with Scale+Fade to prevent visual overlapping
                    AnimatedContent(
                        targetState = isSearchActive,
                        transitionSpec = {
                            (scaleIn(animationSpec = tween(300)) + fadeIn(
                                animationSpec = tween(
                                    300
                                )
                            ))
                                .togetherWith(
                                    scaleOut(animationSpec = tween(300)) + fadeOut(
                                        animationSpec = tween(
                                            300
                                        )
                                    )
                                )
                        },
                        label = "IconAnim"
                    ) { active ->
                        Icon(
                            painter = painterResource(if (active) Resources.Icon.Close else Resources.Icon.Search),
                            contentDescription = null,
                            tint = IconPrimary,
                            modifier = if(active)Modifier.requiredSize(18.dp).alpha(Alpha.HALF) else Modifier.requiredSize(28.dp)
                        )
                    }
                }




            }



        }


        products.value.DisplayResult(
            modifier = Modifier,
            onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
            onSuccess = { lastProducts ->
                AnimatedContent(
                    targetState = lastProducts
                ) { products ->
                    if (products.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(all = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = lastProducts,
                                key = { it.id }
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        navigator.navigateToDetails(id = product.id)
                                    }
                                )
                            }
                        }
                    } else {
                        InfoCard(
                            image = Resources.Image.Cat,
                            title = "Oops!",
                            subtitle = "Products not found."
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