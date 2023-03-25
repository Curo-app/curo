package io.github.curo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.curo.data.BottomMenu
import io.github.curo.data.CollectionName
import io.github.curo.data.CollectionViewModel
import io.github.curo.data.FABMenu
import io.github.curo.data.MainScreen
import io.github.curo.data.NoteOptionsScreen
import io.github.curo.data.NoteViewModel
import io.github.curo.data.SideMenu
import io.github.curo.ui.base.AboutUsContent
import io.github.curo.ui.base.Collections
import io.github.curo.ui.base.FABAddMenu
import io.github.curo.ui.base.FABButtonState
import io.github.curo.ui.base.Feed
import io.github.curo.ui.base.NavigationBottomBar
import io.github.curo.ui.base.NoteEditMenu
import io.github.curo.ui.base.NoteOptionsScreen
import io.github.curo.ui.base.SearchTopAppBar
import io.github.curo.ui.base.SettingsContent
import io.github.curo.ui.base.SideMenu
import io.github.curo.ui.base.fabAnimationProperties
import io.github.curo.ui.base.fabBackgroundModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

val bottomMenu = listOf(BottomMenu.Collections, BottomMenu.Feed, BottomMenu.Calendar)

@Composable
fun AppScreen(navController: NavHostController = rememberNavController()) {
    val scope = rememberCoroutineScope()
    val noteViewModel = remember { NoteViewModel() }
    val collectionViewModel = remember { CollectionViewModel() }

    NavHost(navController = navController, startDestination = MainScreen.route) {
        navMainScreen(navController, scope, noteViewModel, collectionViewModel)
        navEditNote(noteViewModel, collectionViewModel, navController)
        navEditNoteSettings(noteViewModel, navController)
    }

}

private fun NavGraphBuilder.navEditNoteSettings(
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    composable(
        "${FABMenu.Note.route}/{noteId}",
        arguments = listOf(navArgument("noteId", builder = { type = NavType.IntType }))
    ) {
        val note by noteViewModel.patchItem.collectAsState(null)
        note?.let {
            NoteEditMenu(
                note = it,
                onSaveNote = {
                    /* TODO: Call view model save */
                    navController.popBackStack()
                },
                onDiscardNote = {
                    navController.popBackStack()
                },
                onShareNote = { /* TODO: sharing screen */ }) {
            }
        }
    }
}

private fun NavGraphBuilder.navEditNote(
    noteViewModel: NoteViewModel,
    collectionViewModel: CollectionViewModel,
    navController: NavHostController
) {
    composable(
        "${NoteOptionsScreen.route}/{noteId}",
        arguments = listOf(navArgument("noteId", builder = { type = NavType.IntType }))
    ) {
        val note by noteViewModel.patchItem.collectAsState(null)
        note?.let {
            NoteOptionsScreen(
                note = it,
                collectionViewModel = collectionViewModel,
                onReturn = {
                    /* TODO: Call view model save */
                    navController.popBackStack()
                },
            )
        }
    }
}

private fun NavGraphBuilder.navMainScreen(
    navController: NavHostController,
    scope: CoroutineScope,
    noteViewModel: NoteViewModel,
    collectionViewModel: CollectionViewModel
) {
    composable(MainScreen.route) {
        MainScreen(
            outerNavHost = navController,
            scope = scope,
            noteViewModel = noteViewModel,
            collectionViewModel = collectionViewModel
        )
    }
}

@Composable
private fun MainScreen(
    outerNavHost: NavHostController,
    scope: CoroutineScope,
    noteViewModel: NoteViewModel,
    collectionViewModel: CollectionViewModel
) {
    val innerNavController = rememberNavController()
    var isBottomBarVisible by remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var fabButtonState: FABButtonState by remember { mutableStateOf(FABButtonState.Closed) }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenu(onItemClick = { screen ->
                scope.launch { drawerState.close() }
                isBottomBarVisible = screen == SideMenu.HomeScreen || screen is BottomMenu
                fabButtonState = if (isBottomBarVisible) {
                    FABButtonState.Closed
                } else {
                    FABButtonState.Hidden
                }
                navigateSameScreen(innerNavController, screen)
            })
        },
        content = {
            FloatingActionButtonMenu(
                onSearchClick = { /* TODO: search screen */ },
                onFABMenuSelect = { menu ->
                    outerNavHost.navigate("${menu.route}/${Random.nextInt()}") {
                        popUpTo(innerNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
                onCollectionClick = { /* TODO: collection screen */ },
                drawerState = drawerState,
                scope = scope,
                innerNavController = innerNavController,
                isBottomBarVisible = isBottomBarVisible,
                outerNavHost = outerNavHost,
                noteViewModel = noteViewModel,
                collectionViewModel = collectionViewModel,
                onFABMenuAct = { fabButtonState = fabButtonState.act() },
                fabButtonState = fabButtonState,
            )
        }
    )
}

@Composable
private fun FloatingActionButtonMenu(
    onSearchClick: (String) -> Unit,
    onCollectionClick: (CollectionName) -> Unit,
    onFABMenuSelect: (FABMenu) -> Unit,
    onFABMenuAct: () -> Unit,
    fabButtonState: FABButtonState,
    drawerState: DrawerState,
    scope: CoroutineScope,
    innerNavController: NavHostController,
    isBottomBarVisible: Boolean,
    outerNavHost: NavHostController,
    noteViewModel: NoteViewModel,
    collectionViewModel: CollectionViewModel,
) {

    val backgroundColor = MaterialTheme.colorScheme.surface
    val transition = updateTransition(
        targetState = fabButtonState.opened(),
        label = "FABAddMenuTransition"
    )
    val fabAnimationProperties = fabAnimationProperties(transition)

    BackHandler(enabled = fabButtonState.opened(), onBack = onFABMenuAct)

    Box(contentAlignment = Alignment.BottomEnd) {
        MainScreen(
            onSearchClick,
            scope,
            drawerState,
            innerNavController,
            isBottomBarVisible,
            outerNavHost,
            onCollectionClick,
            noteViewModel,
            collectionViewModel
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .fabBackgroundModifier(
                    isOpen = fabButtonState == FABButtonState.Opened,
                    onClose = onFABMenuAct,
                ),
            onDraw = { drawRect(backgroundColor, alpha = fabAnimationProperties.backgroundAlpha) }
        )
        FABAddMenu(
            fabButtonState = fabButtonState,
            onToggle = onFABMenuAct,
            onClose = { onFABMenuAct(); onFABMenuSelect(it) },
            properties = fabAnimationProperties,
        )
    }
}

@Composable
private fun MainScreen(
    onSearchClick: (String) -> Unit,
    scope: CoroutineScope,
    drawerState: DrawerState,
    innerNavController: NavHostController,
    isBottomBarVisible: Boolean,
    outerNavHost: NavHostController,
    onCollectionClick: (CollectionName) -> Unit,
    noteViewModel: NoteViewModel,
    collectionViewModel: CollectionViewModel
) {
    var searchText by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            SearchTopAppBar(
                onSearchClick = { searchText = it; onSearchClick(it) },
                onMenuClick = { scope.launch { drawerState.open() } }
            )
        },

        bottomBar = {
            NavigationBottomBar(
                onItemSelected = { screen ->
                    navigateSameScreen(innerNavController, screen)
                },
                visible = isBottomBarVisible,
            )
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = innerNavController,
            startDestination = BottomMenu.Feed.route
        ) {
            navFeedScreen(outerNavHost, innerNavController, onCollectionClick, noteViewModel)
            navCollectionsScreen(collectionViewModel, onCollectionClick)
            navCalendarScreen()
            navAboutUsScreen()
            navSettingsScreen()
        }
    }
}

private fun NavGraphBuilder.navSettingsScreen() =
    composable(SideMenu.SettingsScreen.route) {
        SettingsContent()
    }


private fun NavGraphBuilder.navAboutUsScreen() =
    composable(SideMenu.AboutUsScreen.route) {
        AboutUsContent()
    }


private fun NavGraphBuilder.navCalendarScreen() =
    composable(BottomMenu.Calendar.route) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Calendar",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

private fun NavGraphBuilder.navCollectionsScreen(
    collectionViewModel: CollectionViewModel,
    onCollectionClick: (CollectionName) -> Unit
) = composable(BottomMenu.Collections.route) {
    Collections(
        viewModel = collectionViewModel,
        onCollectionClick = onCollectionClick,
    )
}

private fun NavGraphBuilder.navFeedScreen(
    outerNavHost: NavHostController,
    innerNavController: NavHostController,
    onCollectionClick: (CollectionName) -> Unit,
    noteViewModel: NoteViewModel
) = composable(BottomMenu.Feed.route) {
    Feed(
        onNoteClick = {
            outerNavHost.navigate("${FABMenu.Note.route}/${Random.nextInt()}") {
                popUpTo(innerNavController.graph.findStartDestination().id) {
                    saveState = true
                }
            }
        },
        onCollectionClick = onCollectionClick,
        viewModel = noteViewModel
    )
}

private fun navigateSameScreen(
    innerNavController: NavHostController,
    realScreen: MainScreen
) {
    innerNavController.navigate(realScreen.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(innerNavController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppScreen()
}
