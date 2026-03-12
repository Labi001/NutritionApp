package com.labinot.bajrami.nutritionapp.domain.NavSetup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.labinot.bajrami.nutritionapp.domain.helpers.Resources
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import kotlin.collections.set

interface BottomNavItem {
    val icon: DrawableResource
    val title: String
}

@Serializable
sealed interface NavRoutes : NavKey {

    @Serializable
    data object Auth: NavRoutes

    @Serializable
    data object Main: NavRoutes


    @Serializable
    data object ProductsOverview : NavRoutes

    @Serializable
    data class CategorySearch(val category: String): NavRoutes

    @Serializable
    data object Profile: NavRoutes

    @Serializable
    data object AdminPanel: NavRoutes

    @Serializable
    data class ManageProduct(val id: String? = null): NavRoutes

    @Serializable
    data class Details(val id: String): NavRoutes

    @Serializable
    data class CheckOut(val totalAmount: String): NavRoutes

    @Serializable
    data class PaymentCompleted(
        val isSuccess: Boolean? = null,
        val error: String? = null,
        val token: String? = null
    ): NavRoutes


}


@Serializable
data object Home : NavKey,BottomNavItem {
    override val icon: DrawableResource = Resources.Icon.Home
    override val title: String = "Nutri Sport"
}

@Serializable
data object Cart : NavKey,BottomNavItem {
    override val icon: DrawableResource = Resources.Icon.ShoppingCart
    override val title: String = "Cart"
}

@Serializable
data object Category : NavKey,BottomNavItem {
    override val icon: DrawableResource = Resources.Icon.Categories
    override val title: String = "Category"
}




class TopLevelBackStack<T : NavKey>(private val startKey: T) {

    private var topLevelBackStacks: HashMap<T, SnapshotStateList<T>> = hashMapOf(
        startKey to mutableStateListOf(startKey)
    )

    // 2. The active Tab key (Home, Cart, or Category)
    var topLevelKey by mutableStateOf(startKey)
        private set

    // 3. The REAL backstack that NavDisplay and your Scaffolds watch
    val backStack = mutableStateListOf<T>(startKey)

    // 4. The "Engine" that updates the visible backstack
    private fun updateBackStack() {
        backStack.clear()
        val currentStack = topLevelBackStacks[topLevelKey] ?: emptyList()

        if (topLevelKey == startKey) {
            // If on Home tab, backStack is just the home history
            backStack.addAll(currentStack)
        } else {
            // If on other tabs, prepend the Home stack so 'Back' eventually hits Home
            val startStack = topLevelBackStacks[startKey] ?: emptyList()
            backStack.addAll(startStack + currentStack)
        }
    }

    // --- NAVIGATION FUNCTIONS ---

    fun switchTopLevel(key: T) {
        if (topLevelBackStacks[key] == null) {
            topLevelBackStacks[key] = mutableStateListOf(key)
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T) {
        // Adds screen to the current active tab's history
        topLevelBackStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        val currentStack = topLevelBackStacks[topLevelKey] ?: return

        if (currentStack.size > 1) {
            // Remove screen from current tab history
            currentStack.removeAt(currentStack.size - 1)
        } else if (topLevelKey != startKey) {
            // If tab is at its root, switch back to the Home tab
            topLevelKey = startKey
        }
        updateBackStack()
    }

    fun replaceStack(vararg keys: T) {
        topLevelBackStacks[topLevelKey] = mutableStateListOf(*keys)
        updateBackStack()
    }

    fun resetTo(key: T) {
        topLevelBackStacks.clear()
        topLevelBackStacks[key] = mutableStateListOf(key)
        topLevelKey = key
        updateBackStack()
    }






//    private var topLevelBackStacks: HashMap<T, SnapshotStateList<T>> = hashMapOf(
//        startKey to mutableStateListOf(startKey)
//    )
//
//    var topLevelKey by mutableStateOf(startKey)
//        private set
//
//    val backStack = mutableStateListOf<T>(startKey)
//
//    private fun updateBackStack() {
//        backStack.clear()
//        val currentStack = topLevelBackStacks[topLevelKey] ?: emptyList()
//
//        if (topLevelKey == startKey) {
//            backStack.addAll(currentStack)
//        } else {
//            val startStack = topLevelBackStacks[startKey] ?: emptyList()
//            backStack.addAll(startStack + currentStack)
//        }
//    }
//
//    fun switchTopLevel(key: T) {
//        if (topLevelBackStacks[key] == null) {
//            topLevelBackStacks[key] = mutableStateListOf(key)
//        }
//        topLevelKey = key
//        updateBackStack()
//    }
//
//    fun add(key: T) {
//        topLevelBackStacks[topLevelKey]?.add(key)
//        updateBackStack()
//    }
//
//    fun removeLast() {
//        val currentStack = topLevelBackStacks[topLevelKey] ?: return
//
//        if (currentStack.size > 1) {
//            currentStack.removeLastOrNull()
//        } else if (topLevelKey != startKey) {
//            topLevelKey = startKey
//        }
//        updateBackStack()
//    }
//
//    fun replaceStack(vararg keys: T) {
//        topLevelBackStacks[topLevelKey] = mutableStateListOf(*keys)
//        updateBackStack()
//    }
//
//    fun resetTo(key: T) {
//
//        topLevelBackStacks.clear()
//        topLevelBackStacks[key] = mutableStateListOf(key)
//        topLevelKey = key
//        updateBackStack()
//
//
//    }

}