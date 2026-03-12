package com.labinot.bajrami.nutritionapp.presentation.screens.adminPanel.manageProduct

import ContentWithMessageBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.labinot.bajrami.nutritionapp.domain.NavSetup.AppNavigator
import com.labinot.bajrami.nutritionapp.domain.helpers.BebasNeueFont
import com.labinot.bajrami.nutritionapp.domain.helpers.BorderIdle
import com.labinot.bajrami.nutritionapp.domain.helpers.ButtonPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.DisplayResult
import com.labinot.bajrami.nutritionapp.domain.helpers.FontSize
import com.labinot.bajrami.nutritionapp.domain.helpers.IconPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.RequestState
import com.labinot.bajrami.nutritionapp.domain.helpers.Resources
import com.labinot.bajrami.nutritionapp.domain.helpers.Surface
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceBrand
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceDarker
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceError
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceLighter
import com.labinot.bajrami.nutritionapp.domain.helpers.SurfaceSecondary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextPrimary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextSecondary
import com.labinot.bajrami.nutritionapp.domain.helpers.TextWhite
import com.labinot.bajrami.nutritionapp.domain.helpers.utils.PhotoPicker
import com.labinot.bajrami.nutritionapp.domain.models.ProductCategory
import com.labinot.bajrami.nutritionapp.presentation.component.AlertTextField
import com.labinot.bajrami.nutritionapp.presentation.component.dialogs.CategoriesDialog
import com.labinot.bajrami.nutritionapp.presentation.component.CustomTextField
import com.labinot.bajrami.nutritionapp.presentation.component.ErrorCard
import com.labinot.bajrami.nutritionapp.presentation.component.LoadingCard
import com.labinot.bajrami.nutritionapp.presentation.component.PrimaryButton
import com.labinot.bajrami.nutritionapp.presentation.component.SelectCountryTextField
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(navigator: AppNavigator,
                        viewModel: ManageProductViewModel = koinViewModel(),
                        id: String?)
{

    val messageBarState = rememberMessageBarState()
    var dropdownMenuOpened by remember { mutableStateOf(false) }
    var showCategoriesDialog by remember { mutableStateOf(false) }

    val screenState = viewModel.screenState
    val isFormValid = viewModel.isFormValid
    val thumbnailUploaderState = viewModel.thumbnailUploaderState

    val photoPicker = koinInject<PhotoPicker>()

    photoPicker.InitializePhotoPicker(
        onImageSelect = { file ->

            if(file != null){

                viewModel.uploadProductImage(
                    byteArray = file,
                    onSuccess = { messageBarState.addSuccess("Thumbnail uploaded successfully!") }
                )

            }else {


                messageBarState.addError("No Image Selected")
            }


        }
    )


    AnimatedVisibility(
        visible = screenState.isCategoryDialogOpen
    ) {
        CategoriesDialog(
            categories = screenState.allCategories,
            onDismiss = viewModel::onCategoryDialogDismiss,
            onSelectedCategory = viewModel::onCategorySelected
        )
    }

    ContentWithMessageBar(
        modifier = Modifier
            .systemBarsPadding(),
        contentBackgroundColor = Surface,
        messageBarState = messageBarState,
        errorMaxLines = 2,
        errorContainerColor = SurfaceError,
        errorContentColor = TextWhite,
        successContainerColor = SurfaceBrand,
        successContentColor = TextPrimary
    ) {

        Column(modifier = Modifier.fillMaxSize()
            .background(Surface),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally)
        {

            TopAppBar(modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = if (id == null) "New Product"
                        else "Edit Product",
                        fontFamily = BebasNeueFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {navigator.popBackMainNav()}) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back Arrow icon",
                            tint = IconPrimary
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                ),

                actions = {

                    id.takeIf { it != null }?.let {
                        Box {
                            IconButton(onClick = { dropdownMenuOpened = true }) {
                                Icon(
                                    painter = painterResource(Resources.Icon.VerticalMenu),
                                    contentDescription = "Vertical menu icon",
                                    tint = IconPrimary
                                )
                            }
                            DropdownMenu(
                                containerColor = Surface,
                                expanded = dropdownMenuOpened,
                                onDismissRequest = { dropdownMenuOpened = false }
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            modifier = Modifier.size(25.dp),
                                            painter = painterResource(Resources.Icon.Delete),
                                            contentDescription = "Delete icon",
                                            tint = IconPrimary
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Delete",
                                            color = TextPrimary,
                                            fontSize = FontSize.REGULAR
                                        )
                                    },
                                    onClick = {
                                        dropdownMenuOpened = false
                                        viewModel.deleteProduct(
                                            onSuccess = {navigator.popBackMainNav()} ,
                                            onError = { message -> messageBarState.addError(message) }
                                        )
                                    }
                                )
                            }
                        }
                    }


                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(size = 12.dp))
                        .border(
                            width = 1.dp,
                            color = BorderIdle,
                            shape = RoundedCornerShape(size = 12.dp)
                        )
                        .background(SurfaceLighter)
                        .clickable(
                            enabled = thumbnailUploaderState.isIdle()
                        ) {
                            println("Triggered!")
                            photoPicker.open()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    thumbnailUploaderState.DisplayResult(
                        onIdle = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(Resources.Icon.Plus),
                                contentDescription = "Plus icon",
                                tint = IconPrimary
                            )
                        },
                        onLoading = {
                            LoadingCard(modifier = Modifier.fillMaxSize())
                        },
                        onSuccess = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = ImageRequest.Builder(
                                        LocalPlatformContext.current
                                    ).data(screenState.thumbnail)
                                        .crossfade(enable = true)
                                        .build(),
                                    contentDescription = "Product thumbnail image",
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(size = 6.dp))
                                        .padding(
                                            top = 12.dp,
                                            end = 12.dp
                                        )
                                        .background(ButtonPrimary)
                                        .clickable {
                                            viewModel.deleteProductImage(
                                                onSuccess = { messageBarState.addSuccess("Thumbnail removed successfully.") },
                                                onError = { message ->
                                                    messageBarState.addError(
                                                        message
                                                    )
                                                }
                                            )
                                        }
                                        .padding(all = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        modifier = Modifier.size(14.dp),
                                        painter = painterResource(Resources.Icon.Delete),
                                        contentDescription = "Delete icon"
                                    )
                                }
                            }
                        },
                        onError = { message ->
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ErrorCard(message = message)
                                Spacer(modifier = Modifier.height(12.dp))
                                TextButton(
                                    onClick = {
                                        viewModel.updateThumbnailUploaderState(RequestState.Idle)
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        containerColor = Color.Transparent
                                    )
                                ) {
                                    Text(
                                        text = "Try again",
                                        fontSize = FontSize.SMALL,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    )
                }


                CustomTextField(
                    value = screenState.title,
                    onValueChange =viewModel::updateTitle,
                    placeholder = "Title"
                )
                CustomTextField(
                    modifier = Modifier.height(168.dp),
                    value = screenState.description,
                    onValueChange = viewModel::updateDescription,
                    placeholder = "Description",
                    expanded = true
                )

                SelectCountryTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text =  screenState.selectedCategory?.title ?: "",
                    onClick =viewModel::onCategoryFieldClick,
                    placeholder = "Select Category"
                )

                AnimatedVisibility(
                    visible = screenState.selectedCategory != ProductCategory.Accessories
                ) {
                    Column {
                        CustomTextField(
                            value = "${screenState.weight ?: ""}",
                            onValueChange = { viewModel.updateWeight(it.toIntOrNull() ?: 0) },
                            placeholder = "Weight",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomTextField(
                            value = screenState.flavors,
                            onValueChange = viewModel::updateFlavors,
                            placeholder = "Flavors"
                        )
                    }
                }

                CustomTextField(
                    value = "${screenState.price}",
                    onValueChange = { value ->
                        if (value.isEmpty() || value.toDoubleOrNull() != null) {
                            viewModel.updatePrice(value.toDoubleOrNull() ?: 0.0)
                        }
                    },
                    placeholder = "Price",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 12.dp),
                            text = "New",
                            fontSize = FontSize.REGULAR,
                            color = TextPrimary
                        )
                        Switch(
                            checked = screenState.isNew,
                            onCheckedChange = viewModel::updateNew,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = SurfaceSecondary,
                                uncheckedTrackColor = SurfaceDarker,
                                checkedThumbColor = Surface,
                                uncheckedThumbColor = Surface,
                                checkedBorderColor = SurfaceSecondary,
                                uncheckedBorderColor = SurfaceDarker
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 12.dp),
                            text = "Popular",
                            fontSize = FontSize.REGULAR,
                            color = TextPrimary
                        )
                        Switch(
                            checked = screenState.isPopular,
                            onCheckedChange = viewModel::updatePopular,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = SurfaceSecondary,
                                uncheckedTrackColor = SurfaceDarker,
                                checkedThumbColor = Surface,
                                uncheckedThumbColor = Surface,
                                checkedBorderColor = SurfaceSecondary,
                                uncheckedBorderColor = SurfaceDarker
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 12.dp),
                            text = "Discounted",
                            fontSize = FontSize.REGULAR,
                            color = TextPrimary
                        )
                        Switch(
                            checked = screenState.isDiscounted,
                            onCheckedChange = viewModel::updateDiscounted,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = SurfaceSecondary,
                                uncheckedTrackColor = SurfaceDarker,
                                checkedThumbColor = Surface,
                                uncheckedThumbColor = Surface,
                                checkedBorderColor = SurfaceSecondary,
                                uncheckedBorderColor = SurfaceDarker
                            )
                        )
                    }
                }



                Spacer(modifier = Modifier.height(24.dp))

            }

            PrimaryButton(
                text = if (id == null) "Add new product"
                else "Update",
                icon = if (id == null) Resources.Icon.Plus
                else Resources.Icon.Checkmark,
                enabled = isFormValid,
                onClick = {

                    viewModel.createNewProduct(
                            onSuccess = { messageBarState.addSuccess("Product successfully added!") },
                            onError = { message -> messageBarState.addError(message) }
                        )


                    if (id != null) {
                        viewModel.updateProduct(
                            onSuccess = { messageBarState.addSuccess("Product successfully updated!") },
                            onError = { message -> messageBarState.addError(message) }
                        )
                    } else {
                        viewModel.createNewProduct(
                            onSuccess = { messageBarState.addSuccess("Product successfully added!") },
                            onError = { message -> messageBarState.addError(message) }
                        )
                    }
                }
            )




        }




    }






}