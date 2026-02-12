package pt.ipvc.ecocampus.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pt.ipvc.ecocampus.data.local.AppDatabase
import pt.ipvc.ecocampus.data.remote.RetrofitClient
import pt.ipvc.ecocampus.data.repository.AppRepository
import pt.ipvc.ecocampus.ui.auth.LoginScreen
import pt.ipvc.ecocampus.ui.auth.RegisterScreen
import pt.ipvc.ecocampus.ui.auth.WelcomeScreen
import pt.ipvc.ecocampus.ui.screens.*
import pt.ipvc.ecocampus.ui.viewmodel.AuthViewModel
import pt.ipvc.ecocampus.ui.viewmodel.AuthViewModelFactory
import pt.ipvc.ecocampus.ui.viewmodel.MainViewModel
import pt.ipvc.ecocampus.ui.viewmodel.MainViewModelFactory

@Composable
fun AppNavHost(database: AppDatabase) {
    val navController = rememberNavController()

    // Faço aqui a Injeção de Dependências Manual.
    // Instancio o Repositório no topo da árvore para garantir que ele é partilhado
    // entre o AuthViewModel e o MainViewModel (Single Source of Truth).
    val repository = AppRepository(database.userDao(), RetrofitClient.api)
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository))
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))

    // --- NAVHOST PRINCIPAL (Raiz) ---
    // Este nível gere apenas a troca entre "Ecrãs de Autenticação" e "Aplicação Principal".
    NavHost(navController = navController, startDestination = "welcome") {

        composable("welcome") {
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    mainViewModel.loadUserData() // Prepara os dados antes de entrar
                    // Uso o 'popUpTo' inclusive para limpar a pilha.
                    // Assim, se o utilizador clicar em "Voltar" dentro da app, não regressa ao Login.
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    mainViewModel.loadUserData()
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // --- ENTRADA NA APP ---
        composable("home") {
            MainScreenWithBottomBar(mainViewModel) {
                // Callback de Logout: Se sair, volto ao início e limpo tudo
                navController.navigate("welcome") {
                    popUpTo(0)
                }
            }
        }
    }
}

@Composable
fun MainScreenWithBottomBar(viewModel: MainViewModel, onLogout: () -> Unit) {
    // Crio um segundo NavController (Nested Navigation).
    // Isto é necessário para que a BottomBar navegue entre tabs SEM sair do Scaffold principal.
    val nestedNavController = rememberNavController()

    // Sistema reativo de feedback: Ocupo-me de mostrar Snackbars baseadas no estado do ViewModel.
    val toastMessage by viewModel.toastMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                // Observo a rota atual para saber qual ícone pintar de "selecionado"
                val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route

                // Definição manual dos itens da navegação
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Restaurant, null) },
                    label = { Text("Cantina") },
                    selected = currentDestination == "cantina",
                    onClick = { nestedNavController.navigate("cantina") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Coffee, null) },
                    label = { Text("Bar") },
                    selected = currentDestination == "bar",
                    onClick = { nestedNavController.navigate("bar") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountBalanceWallet, null) },
                    label = { Text("Carteira") },
                    selected = currentDestination == "wallet",
                    onClick = { nestedNavController.navigate("wallet") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Map, null) },
                    label = { Text("Mapa") },
                    selected = currentDestination == "map",
                    onClick = { nestedNavController.navigate("map") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil") },
                    selected = currentDestination == "profile",
                    onClick = { nestedNavController.navigate("profile") }
                )
            }
        }
    ) { innerPadding ->
        // --- NAVHOST SECUNDÁRIO (Conteúdo das Tabs) ---
        NavHost(
            navController = nestedNavController,
            startDestination = "cantina",
            modifier = Modifier.padding(innerPadding) // Respeito o espaço da BottomBar
        ) {
            composable("cantina") { CantinaScreen(viewModel) }
            composable("bar") { BarScreen(viewModel) }
            composable("wallet") { WalletScreen(viewModel) }
            composable("map") { MapScreen() }
            composable("profile") { ProfileScreen(viewModel, onLogout) }
        }
    }
}

