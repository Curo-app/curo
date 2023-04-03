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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kizitonwose.calendar.compose.CalendarState
import io.github.curo.data.BottomNavigationScreen
import io.github.curo.data.CalendarViewModel
import io.github.curo.data.CollectionName
import io.github.curo.data.CollectionPatchViewModel
import io.github.curo.data.CollectionViewModel
import io.github.curo.data.FABMenuItem
import io.github.curo.data.FeedViewModel
import io.github.curo.data.Note
import io.github.curo.data.NotePatchViewModel
import io.github.curo.data.Route
import io.github.curo.data.Screen
import io.github.curo.data.SearchViewModel
import io.github.curo.ui.base.AboutUs
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
import io.github.curo.ui.screens.Collections
import io.github.curo.ui.screens.EditCollectionScreen
import io.github.curo.ui.screens.SearchView
import io.github.curo.ui.screens.capitalizeFirstLetter
import io.github.curo.ui.screens.rememberCuroCalendarState
import io.github.curo.ui.screens.rememberFirstMostVisibleMonth
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
    val feedViewModel = remember { FeedViewModel() }

    val collectionViewModel = remember { CollectionViewModel() }
    val calendarViewModel = remember { CalendarViewModel() }
    val notePatchViewModel = remember { NotePatchViewModel() }
    val collectionPatchViewModel = remember { CollectionPatchViewModel() }
    val searchViewModel = remember { SearchViewModel() }

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
                        feedViewModel = feedViewModel,
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
                collectionEditScreen(
                    feedViewModel,
                    collectionPatchViewModel,
                    notePatchViewModel,
                    collectionViewModel,
                    mainNavController,
                )
                noteEditScreen(
                    feedViewModel,
                    collectionPatchViewModel,
                    collectionViewModel,
                    notePatchViewModel,
                    mainNavController
                )
                noteOptionsScreen(notePatchViewModel, mainNavController, collectionViewModel)
                searchScreen(
                    mainNavController,
                    searchViewModel,
                )
            }
        },
        selected = currentSideMenuItem?.destination?.route
    )
}

private fun NavGraphBuilder.searchScreen(
    mainNavController: NavHostController,
    searchViewModel: SearchViewModel,
) {
    composable(
        route = Screen.SearchResult.route + "?query={query}",
        arguments = listOf(
            navArgument("query",
                builder = {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        )
    ) {
        val query = it.arguments?.getString("query").orEmpty()
        LaunchedEffect(query) {
            searchViewModel.query = query
        }

        SearchView(
            onSearchTextChanged = { /* Query updated on SearchView, so do nothing */ },
            onSearchKeyboardClick = { /* Query updated on SearchView, so do nothing */ },
            onLeadingIconClick = {
                mainNavController.popBackStack()
            },
            searchViewModel = searchViewModel,
            onNoteClick = { note ->
                mainNavController.navigate(Screen.EditNote.route + '/' + note.id)
            },
            onCollectionClick = { collectionName ->
                mainNavController.navigate(Screen.EditCollection.route + '/' + collectionName.name)
            }
        )
    }
}

private fun NavGraphBuilder.collectionEditScreen(
    feedViewModel: FeedViewModel,
    collectionPatchViewModel: CollectionPatchViewModel,
    notePatchViewModel: NotePatchViewModel,
    collectionViewModel: CollectionViewModel,
    mainNavController: NavHostController,
) {
    composable(
        route = Screen.EditCollection.route + "/{collectionName}",
        arguments = listOf(navArgument("collectionName", builder = { type = NavType.StringType }))
    ) {
        it.arguments?.getString("collectionName")?.let { name ->
            LaunchedEffect(name) {
                collectionPatchViewModel.set(CollectionName(name.ifEmpty { "New collection" }))
            }
        }

        EditCollectionScreen(
            viewModel = collectionPatchViewModel,
            onNoteClick = { note ->
                mainNavController
                    .navigate(Screen.EditNote.route + '/' + note.id)
            },
            onCollectionClick = { collectionName ->
                mainNavController
                    .navigate(Screen.EditCollection.route + '/' + collectionName.name)
            },
            onAddNoteClick = {
                mainNavController
                    .navigate(Screen.EditNote.route + '/' + NEW_ENTITY_ID)
                notePatchViewModel.newCollection = CollectionName(collectionPatchViewModel.name)
            },
            onDeleteCollectionClick = { collection ->
                collectionViewModel.delete(collection)
                mainNavController.popBackStack()
            },
            onShareCollectionClick = { /* TODO: sharing screen */ },
            onBackToMenuClick = { mainNavController.popBackStack() },
            onSaveClick = { collection ->
                collectionViewModel.update(collection)
                feedViewModel.addCollection(collection)
                mainNavController.popBackStack()
            }
        )
    }
}

private fun NavGraphBuilder.noteEditScreen(
    feedViewModel: FeedViewModel,
    collectionPatchViewModel: CollectionPatchViewModel,
    collectionViewModel: CollectionViewModel,
    notePatchViewModel: NotePatchViewModel,
    mainNavController: NavHostController
) {
    composable(
        route = Screen.EditNote.route + "/{noteId}",
        arguments = listOf(navArgument("noteId", builder = { type = NavType.IntType }))
    ) {
        it.arguments?.getInt("noteId")?.let { id ->
            LaunchedEffect(id) {
                val note = feedViewModel.items.find { note ->
                    note.id == id
                }
                note?.let { item ->
                    notePatchViewModel.set(item)
                } ?: notePatchViewModel.empty(feedViewModel.items.maxOf(Note::id).inc())
            }
        }
        NoteEditMenu(
            note = notePatchViewModel,
            onSaveNote = { note ->
                if (notePatchViewModel.newCollection != null) {
                    collectionPatchViewModel.items.add(note)
                }
                feedViewModel.update(note)
                collectionViewModel.addNote(note)
                mainNavController.popBackStack()
            },
            onDiscardNote = {
                notePatchViewModel.empty(-1)
                mainNavController.popBackStack()
            },
            onShareNote = { /* TODO: sharing screen */ },
            onPropertiesClick = { id ->
                mainNavController.navigate(Screen.NoteOptions.route + '/' + id)
            },
            onDeleteNote = { id ->
                feedViewModel.delete(id)
                mainNavController.popBackStack()
            },
        )
    }
}

private fun NavGraphBuilder.noteOptionsScreen(
    notePatchViewModel: NotePatchViewModel,
    mainNavController: NavHostController,
    collectionViewModel: CollectionViewModel,
) {
    composable(
        route = Screen.NoteOptions.route + "/{noteId}",
        arguments = listOf(navArgument("noteId", builder = { type = NavType.IntType }))
    ) {
        NoteOptionsScreen(
            note = notePatchViewModel,
            collectionViewModel = collectionViewModel,
            onReturn = { mainNavController.popBackStack() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FABScreen(
    drawerState: DrawerState,
    outerNavHost: NavHostController,
    innerNavHost: NavHostController,
    scope: CoroutineScope,
    feedViewModel: FeedViewModel,
    collectionViewModel: CollectionViewModel,
    calendarViewModel: CalendarViewModel,
) {
    var fabButtonState: FABButtonState by remember { mutableStateOf(FABButtonState.Closed) }

    FloatingActionButtonMenu(
        onSearchClick = { query ->
            outerNavHost.navigate(Screen.SearchResult.route + "?query=$query")
        },
        onFABMenuSelect = { menu ->
            // Чтобы не делать два разных экрана для создания и изменения заметки будем считать,
            // что изменение заметки с id = -1 это ее создание
            when (menu) {
                Screen.EditCollection -> outerNavHost.navigate(menu.route + "/My collection")
                Screen.EditNote -> outerNavHost.navigate(menu.route + "/$NEW_ENTITY_ID")
            }
        },
        onCollectionClick = { collectionName ->
            outerNavHost.navigate(Screen.EditCollection.route + '/' + collectionName.name)
        },
        drawerState = drawerState,
        scope = scope,
        innerNavController = innerNavHost,
        outerNavHost = outerNavHost,
        feedViewModel = feedViewModel,
        collectionViewModel = collectionViewModel,
        calendarViewModel = calendarViewModel,
        onFABMenuAct = { fabButtonState = fabButtonState.act() },
        fabButtonState = fabButtonState,
        onNoteClick = { note ->
            outerNavHost.navigate(Screen.EditNote.route + '/' + note.id)
        },
        onCollectionFilter = {
            calendarViewModel.updateOnFilters()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloatingActionButtonMenu(
    onSearchClick: (String) -> Unit,
    onCollectionClick: (CollectionName) -> Unit,
    onNoteClick: (Note) -> Unit,
    onFABMenuSelect: (FABMenuItem) -> Unit,
    onFABMenuAct: () -> Unit,
    onCollectionFilter: (CollectionName) -> Unit,
    fabButtonState: FABButtonState,
    drawerState: DrawerState,
    scope: CoroutineScope,
    innerNavController: NavHostController,
    outerNavHost: NavHostController,
    feedViewModel: FeedViewModel,
    collectionViewModel: CollectionViewModel,
    calendarViewModel: CalendarViewModel,
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
            onNoteClick,
            onCollectionFilter,
            feedViewModel,
            collectionViewModel,
            calendarViewModel,
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
    onNoteClick: (Note) -> Unit,
    onCollectionFilter: (CollectionName) -> Unit,
    feedViewModel: FeedViewModel,
    collectionViewModel: CollectionViewModel,
    calendarViewModel: CalendarViewModel,
) {
    var searchText by remember { mutableStateOf("") }
    val calendarState = rememberCuroCalendarState()

    val currentBottomMenuItem by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBottomMenuItem?.destination?.route
    Scaffold(
        topBar = {
            SearchTopAppBar(
                onSearchClick = {
                    val query = it.orEmpty()
                    searchText = query
                    onSearchClick(query)
                },
                onMenuClick = { scope.launch { drawerState.open() } },
                content = if (currentRoute == BottomNavigationScreen.Calendar.route) {
                    {
                        val visibleMonth =
                            rememberFirstMostVisibleMonth(calendarState, viewportPercent = 90f)
                        val monthName =
                            visibleMonth.yearMonth.month.name.lowercase().capitalizeFirstLetter()
                        val year = visibleMonth.yearMonth.year
                        Text("$monthName $year")
                    }
                } else {
                    null
                }
            )
        },

        bottomBar = {
            NavigationBottomBar(
                onItemSelected = { screen ->
                    navigateSameScreen(innerNavController, screen)
                },
                selected = currentRoute,
            )
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = innerNavController,
            startDestination = BottomNavigationScreen.Feed.route
        ) {
            navFeedScreen(outerNavHost, onCollectionClick, feedViewModel)
            navCollectionsScreen(collectionViewModel, onCollectionClick, onNoteClick)
            navCalendarScreen(calendarViewModel, calendarState, onCollectionFilter)
        }
    }
}

private fun NavGraphBuilder.navCalendarScreen(
    calendarViewModel: CalendarViewModel,
    calendarState: CalendarState,
    onCollectionClick: (CollectionName) -> Unit,
) {
    composable(BottomNavigationScreen.Calendar.route) {
        CalendarScreen(calendarViewModel, calendarState, onCollectionClick)
    }
}

private fun NavGraphBuilder.navCollectionsScreen(
    collectionViewModel: CollectionViewModel,
    onCollectionClick: (CollectionName) -> Unit,
    onNoteClick: (Note) -> Unit,
) = composable(BottomNavigationScreen.Collections.route) {
    Collections(
        viewModel = collectionViewModel,
        onCollectionClick = onCollectionClick,
        onNoteClick = onNoteClick,
    )
}

private fun NavGraphBuilder.navFeedScreen(
    outerNavHost: NavHostController,
    onCollectionClick: (CollectionName) -> Unit,
    feedViewModel: FeedViewModel,
) = composable(BottomNavigationScreen.Feed.route) {
    Feed(
        onNoteClick = { note ->
            outerNavHost.navigate(Screen.EditNote.route + '/' + note.id)
        },
        onCollectionClick = onCollectionClick,
        viewModel = feedViewModel
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
