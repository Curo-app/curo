package io.github.curo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.curo.data.BottomNavigationScreen
import io.github.curo.data.CalendarViewModel
import io.github.curo.data.CollectionName
import io.github.curo.data.CollectionViewModel
import io.github.curo.data.FABMenuItem
import io.github.curo.data.NoteViewModel
import io.github.curo.data.Route
import io.github.curo.data.Screen
import io.github.curo.ui.base.AboutUs
import io.github.curo.ui.base.Collections
import io.github.curo.ui.base.FABAddMenu
import io.github.curo.ui.base.FABButtonState
import io.github.curo.ui.base.Feed
import io.github.curo.ui.base.NavigationBottomBar
import io.github.curo.ui.base.NoteEditMenu
import io.github.curo.ui.base.NoteOptionsScreen
import io.github.curo.ui.base.SearchTopAppBar
import io.github.curo.ui.base.Settings
import io.github.curo.ui.base.SideMenu
import io.github.curo.ui.base.fabAnimationProperties
import io.github.curo.ui.base.fabBackgroundModifier
import io.github.curo.ui.screens.CalendarScreen
import io.github.curo.utils.NEW_ENTITY_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val bottomMenu = listOf(
    BottomNavigationScreen.Collections,
    BottomNavigationScreen.Feed,
    BottomNavigationScreen.Calendar,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    val mainNavController = rememberNavController()
    val bottomNavigationNavController = rememberNavController()
    val currentSideMenuItem by mainNavController.currentBackStackEntryAsState()

    val scope = rememberCoroutineScope()
    val noteViewModel = remember { NoteViewModel() }
    val collectionViewModel = remember { CollectionViewModel() }
    val calendarViewModel = remember { CalendarViewModel() }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    SideMenu(
        drawerState = drawerState,
        onItemClick = { screen ->
            scope.launch { drawerState.close() }
            navigateSameScreen(mainNavController, screen)
        },
        content = {
            NavHost(
                navController = mainNavController,
                startDestination = BottomNavigationScreen.route
            ) {
                composable(BottomNavigationScreen.route) {
                    FABScreen(
                        drawerState = drawerState,
                        outerNavHost = mainNavController,
                        innerNavHost = bottomNavigationNavController,
                        scope = scope,
                        noteViewModel = noteViewModel,
                        collectionViewModel = collectionViewModel,
                        calendarViewModel = calendarViewModel,
                    )
                }

                composable(Screen.AboutUs.route) {
                    AboutUs(drawerState, scope)
                }
                composable(Screen.Settings.route) {
                    Settings(drawerState, scope)
                }

                composable(Screen.EditCollection.route + "/{collectionName}") {
                    /* TODO: EDIT COLLECTION SCREEN */
                }
                noteEditScreen(noteViewModel, mainNavController)
                noteOptionsScreen(noteViewModel, mainNavController, collectionViewModel)
                composable(Screen.SearchResult.route + "/{query}") {
                    /* TODO: SEARCH RESULT SCREEN */
                }
            }
        },
        selected = currentSideMenuItem?.destination?.route
    )
}

private fun NavGraphBuilder.noteEditScreen(
    noteViewModel: NoteViewModel,
    mainNavController: NavHostController
) {
    composable(
        route = Screen.EditNote.route + "/{noteId}",
        arguments = listOf(navArgument("noteId", builder = { type = NavType.IntType }))
    ) {
        val note by noteViewModel.patchItem.collectAsState(null)
        note?.let {
            NoteEditMenu(
                note = it,
                onSaveNote = {
                    /* TODO: Call view model save */
                    mainNavController.popBackStack()
                },
                onDiscardNote = {
                    mainNavController.popBackStack()
                },
                onShareNote = { /* TODO: sharing screen */ },
                onPropertiesClick = {
                    mainNavController.navigate(Screen.NoteOptions.route + '/' + it.id)
                },
                onDeleteNode = {
                    /* TODO: delete node */
                    mainNavController.popBackStack()
                },
            )
        }
    }
}

private fun NavGraphBuilder.noteOptionsScreen(
    noteViewModel: NoteViewModel,
    mainNavController: NavHostController,
    collectionViewModel: CollectionViewModel,
) {
    composable(
        route = Screen.NoteOptions.route + "/{noteId}",
        arguments = listOf(navArgument("noteId", builder = { type = NavType.IntType }))
    ) {
        val note by noteViewModel.patchItem.collectAsState(null)
        note?.let {
            NoteOptionsScreen(
                note = it,
                collectionViewModel = collectionViewModel,
                onReturn = {
                    /* TODO: Call view model save */
                    mainNavController.popBackStack()
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FABScreen(
    drawerState: DrawerState,
    outerNavHost: NavHostController,
    innerNavHost: NavHostController,
    scope: CoroutineScope,
    noteViewModel: NoteViewModel,
    collectionViewModel: CollectionViewModel,
    calendarViewModel: CalendarViewModel
) {
    var fabButtonState: FABButtonState by remember { mutableStateOf(FABButtonState.Closed) }

    FloatingActionButtonMenu(
        onSearchClick = { query ->
            outerNavHost.navigate(Screen.SearchResult.route + "/$query")
        },
        onFABMenuSelect = { menu ->
            // Чтобы не делать два разных экрана для создания и изменения заметки будем считать,
            // что изменение заметки с id = -1 это ее создание
            outerNavHost.navigate(menu.route + "/$NEW_ENTITY_ID")
        },
        onCollectionClick = { collectionName ->
            outerNavHost.navigate(Screen.EditCollection.route + "/$collectionName")
        },
        drawerState = drawerState,
        scope = scope,
        innerNavController = innerNavHost,
        outerNavHost = outerNavHost,
        noteViewModel = noteViewModel,
        collectionViewModel = collectionViewModel,
        calendarViewModel = calendarViewModel,
        onFABMenuAct = { fabButtonState = fabButtonState.act() },
        fabButtonState = fabButtonState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloatingActionButtonMenu(
    onSearchClick: (String) -> Unit,
    onCollectionClick: (CollectionName) -> Unit,
    onFABMenuSelect: (FABMenuItem) -> Unit,
    onFABMenuAct: () -> Unit,
    fabButtonState: FABButtonState,
    drawerState: DrawerState,
    scope: CoroutineScope,
    innerNavController: NavHostController,
    outerNavHost: NavHostController,
    noteViewModel: NoteViewModel,
    collectionViewModel: CollectionViewModel,
    calendarViewModel: CalendarViewModel
) {

    val backgroundColor = MaterialTheme.colorScheme.surface
    val transition = updateTransition(
        targetState = fabButtonState.opened(),
        label = "FABAddMenuTransition"
    )
    val fabAnimationProperties = fabAnimationProperties(transition)

    BackHandler(enabled = fabButtonState.opened(), onBack = onFABMenuAct)

    Box(contentAlignment = Alignment.BottomEnd) {
        BottomNavBarScreen(
            onSearchClick,
            scope,
            drawerState,
            innerNavController,
            outerNavHost,
            onCollectionClick,
            noteViewModel,
            collectionViewModel,
            calendarViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomNavBarScreen(
    onSearchClick: (String) -> Unit,
    scope: CoroutineScope,
    drawerState: DrawerState,
    innerNavController: NavHostController,
    outerNavHost: NavHostController,
    onCollectionClick: (CollectionName) -> Unit,
    noteViewModel: NoteViewModel,
    collectionViewModel: CollectionViewModel,
    calendarViewModel: CalendarViewModel
) {
    var searchText by remember { mutableStateOf("") }
    val currentBottomMenuItem by innerNavController.currentBackStackEntryAsState()
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
                selected = currentBottomMenuItem?.destination?.route,
            )
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = innerNavController,
            startDestination = BottomNavigationScreen.Feed.route
        ) {
            navFeedScreen(outerNavHost, onCollectionClick, noteViewModel)
            navCollectionsScreen(collectionViewModel, onCollectionClick)
            navCalendarScreen(calendarViewModel)
        }
    }
}

private fun NavGraphBuilder.navCalendarScreen(
    calendarViewModel: CalendarViewModel
) {
    composable(BottomNavigationScreen.Calendar.route) {
        CalendarScreen(calendarViewModel)
    }
}

private fun NavGraphBuilder.navCollectionsScreen(
    collectionViewModel: CollectionViewModel,
    onCollectionClick: (CollectionName) -> Unit
) = composable(BottomNavigationScreen.Collections.route) {
    Collections(
        viewModel = collectionViewModel,
        onCollectionClick = onCollectionClick,
    )
}

private fun NavGraphBuilder.navFeedScreen(
    outerNavHost: NavHostController,
    onCollectionClick: (CollectionName) -> Unit,
    noteViewModel: NoteViewModel
) = composable(BottomNavigationScreen.Feed.route) {
    Feed(
        onNoteClick = { note ->
            outerNavHost.navigate(Screen.EditNote.route + '/' + note.id)
        },
        onCollectionClick = onCollectionClick,
        viewModel = noteViewModel
    )
}

private fun navigateSameScreen(
    innerNavController: NavHostController,
    realScreen: Route
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
