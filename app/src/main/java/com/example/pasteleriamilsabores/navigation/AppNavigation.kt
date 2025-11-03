package com.example.pasteleriamilsabores.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pasteleriamilsabores.R
import com.example.pasteleriamilsabores.ui.screens.CartScreen
import com.example.pasteleriamilsabores.ui.screens.LoginScreen
import com.example.pasteleriamilsabores.ui.screens.ProductListScreen
import com.example.pasteleriamilsabores.ui.screens.ProfileScreen
import com.example.pasteleriamilsabores.ui.screens.RegisterScreen
import com.example.pasteleriamilsabores.viewmodel.CartViewModel
import com.example.pasteleriamilsabores.viewmodel.LoginViewModel
import com.example.pasteleriamilsabores.viewmodel.ProductListViewModel
import kotlinx.coroutines.launch

// --- Definición de todas las rutas de la app ---
sealed class Destinations(
    val route: String,
    val title: Int? = null,
    val icon: ImageVector? = null
) {
    data object Login : Destinations("login")
    data object Register : Destinations("register")
    data object Home : Destinations("home", R.string.screen_title_home, Icons.Default.Home)
    data object Products : Destinations("products", R.string.screen_title_products, Icons.AutoMirrored.Filled.List)
    data object Profile : Destinations("profile", R.string.screen_title_profile, Icons.Default.Person)
    data object Cart : Destinations("cart", R.string.screen_title_cart, Icons.Default.ShoppingCart)
    // Ruta "fantasma" para el drawer
    data object Logout : Destinations("logout")
}

// --- Navegación principal de la App ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val productListViewModel: ProductListViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CartViewModel(loginViewModel) as T
        }
    })

    NavHost(navController = navController, startDestination = Destinations.Login.route) {
        composable(Destinations.Login.route) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Destinations.Register.route) }
            )
        }
        composable(Destinations.Register.route) {
            RegisterScreen(
                loginViewModel = loginViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }
        composable(Destinations.Home.route) {
            MainScaffold(
                navController = navController,
                loginViewModel = loginViewModel,
                cartViewModel = cartViewModel,
                productListViewModel = productListViewModel
            )
        }
    }
}

// --- Estructura principal (Scaffold) con Drawer y Bottom Bar ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    cartViewModel: CartViewModel,
    productListViewModel: ProductListViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val cartItemCount by cartViewModel.cartItems.collectAsState().value.size.let { mutableStateOf(it) }

    // Lista de items para el Bottom Nav Bar
    val navItems = listOf(
        Destinations.Home,
        Destinations.Products,
        Destinations.Cart
    )
    
    val innerNavController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(innerNavController, cartItemCount, onLogout = {
                scope.launch { drawerState.close() }
                loginViewModel.logout()
                navController.navigate(Destinations.Login.route) {
                    popUpTo(Destinations.Home.route) { inclusive = true }
                }
            })
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = { TopBarActions(innerNavController, cartItemCount) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = innerNavController, items = navItems, cartItemCount = cartItemCount)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavHost(navController = innerNavController, startDestination = Destinations.Home.route) {
                    composable(Destinations.Home.route) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text("¡Bienvenido a Pasteleria Mil Sabores!", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
                        }
                    }
                    composable(Destinations.Products.route) {
                        ProductListScreen(
                            viewModel = productListViewModel,
                            cartViewModel = cartViewModel
                        )
                    }
                    composable(Destinations.Cart.route) {
                        CartScreen(
                            cartViewModel = cartViewModel,
                            onBackPress = { innerNavController.popBackStack() }
                        )
                    }
                    composable(Destinations.Profile.route) {
                        ProfileScreen(loginViewModel = loginViewModel)
                    }
                }
            }
        }
    }
}

// --- Acciones del Top App Bar ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.TopBarActions(navController: NavController, cartItemCount: Int) {
    BadgedBox(
        badge = {
            if (cartItemCount > 0) {
                Badge { Text("$cartItemCount") }
            }
        }
    ) {
        IconButton(onClick = { navController.navigate(Destinations.Cart.route) }) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
        }
    }
    Spacer(modifier = Modifier.width(8.dp))
}

// --- Bottom Navigation Bar ---
@Composable
fun BottomNavigationBar(navController: NavController, items: List<Destinations>, cartItemCount: Int) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        items.forEach { screen ->
            val selected = currentDestination.isRouteInHierarchy(screen.route)
            NavigationBarItem(
                icon = {
                    if (screen.route == Destinations.Cart.route) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge { Text("$cartItemCount") }
                                }
                            }
                        ) {
                            Icon(screen.icon!!, contentDescription = null)
                        }
                    } else {
                        Icon(screen.icon!!, contentDescription = null)
                    }
                },
                label = { Text(stringResource(id = screen.title!!)) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// --- Contenido del Navigation Drawer ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(navController: NavController, cartItemCount: Int, onLogout: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val drawerItems = listOf(
        Destinations.Home,
        Destinations.Products,
        Destinations.Profile,
        Destinations.Cart
    )

    ModalDrawerSheet {
        Spacer(Modifier.padding(vertical = 16.dp))
        drawerItems.forEach { screen ->
            val selected = currentDestination.isRouteInHierarchy(screen.route)
            NavigationDrawerItem(
                icon = {
                    if (screen.route == Destinations.Cart.route) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge { Text("$cartItemCount") }
                                }
                            }
                        ) {
                            Icon(screen.icon!!, contentDescription = null)
                        }
                    } else {
                        Icon(screen.icon!!, contentDescription = null)
                    }
                },
                label = { Text(stringResource(id = screen.title!!)) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
            label = { Text("Cerrar Sesión") },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        Spacer(Modifier.padding(vertical = 16.dp))
    }
}

// Helper para saber si la ruta actual está en la jerarquía
fun NavDestination?.isRouteInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
