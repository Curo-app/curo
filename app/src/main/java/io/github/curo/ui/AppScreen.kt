package io.github.curo.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import io.github.curo.viewmodels.CalendarViewModel
import io.github.curo.viewmodels.CollectionPatchViewModel
import io.github.curo.viewmodels.CollectionViewModel
import io.github.curo.data.FABMenuItem
import io.github.curo.viewmodels.FeedViewModel
import io.github.curo.data.NotePreview
import io.github.curo.viewmodels.NotePatchViewModel
import io.github.curo.data.Route
import io.github.curo.data.Screen
import io.github.curo.database.entities.CollectionInfo
import io.github.curo.viewmodels.SearchViewModel
import io.github.curo.viewmodels.ShareScreenViewModel
import io.github.curo.ui.base.AboutUs
import io.github.curo.ui.base.FABAddMenu
import io.github.curo.ui.base.FABButtonState
import io.github.curo.ui.base.Feed
import io.github.curo.ui.base.NavigationBottomBar
import io.github.curo.ui.base.NoteEditMenu
import io.github.curo.ui.base.NoteOptionsScreen
import io.github.curo.ui.base.SearchTopAppBar
import io.github.curo.ui.base.Settings
import io.github.curo.ui.base.ShareNote
import io.github.curo.ui.base.SideMenu
import io.github.curo.ui.base.fabAnimationProperties
import io.github.curo.ui.base.fabBackgroundModifier
import io.github.curo.ui.screens.CalendarScreen
import io.github.curo.ui.screens.Collections
import io.github.curo.ui.screens.DayNotes
import io.github.curo.ui.screens.EditCollectionScreen
import io.github.curo.ui.screens.SearchView
import io.github.curo.ui.screens.capitalizeFirstLetter
import io.github.curo.ui.screens.rememberCuroCalendarState
import io.github.curo.viewmodels.NoteViewModel
import io.github.curo.viewmodels.RealCollectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

val bottomMenu = listOf(
    BottomNavigationScreen.Collections,
    BottomNavigationScreen.Feed,
    BottomNavigationScreen.Calendar,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    feedViewModel: FeedViewModel,
    noteViewModel: NoteViewModel,
    notePatchViewModel: NotePatchViewModel,
    realCollectionViewModel: RealCollectionViewModel,
    collectionPatchViewModel: CollectionPatchViewModel,
    calendarViewModel: CalendarViewModel,
    collectionViewModel: CollectionViewModel,
    searchViewModel: SearchViewModel
) {
    val mainNavController = rememberNavController()
    val bottomNavigationNavController = rememberNavController()
    val currentSideMenuItem by mainNavController.currentBackStackEntryAsState()

    val scope = rememberCoroutineScope()
//    val feedViewModel = remember { FeedViewModel() }

//    val collectionViewModel = remember { CollectionViewModel() }
//    val calendarViewModel = remember { CalendarViewModel() }
//    val notePatchViewModel = remember { NotePatchViewModel() }
//    val collectionPatchViewModel = remember { CollectionPatchViewModel() }
//    val searchViewModel = remember { SearchViewModel() }
    val shareScreenViewModel = remember { ShareScreenViewModel() }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    SideMenu(
        drawerState = drawerState,
        onItemClick = { screen ->
            scope.launch { drawerState.close() }
            navigateSameScreen(mainNavController, screen)
        },
        content = {
            shareScreenViewModel.link?.let {
                ShareNote(
                    viewModel = shareScreenViewModel,
                    onDone = { shareScreenViewModel.link = null },
                    onDismiss = { shareScreenViewModel.link = null },
                )
            }
            NavHost(
                navController = mainNavController,
                startDestination = BottomNavigationScreen.route
            ) {
                composable(BottomNavigationScreen.route) {
                    FABScreen(
                        drawerState = drawerState,
                        mainNavHost = mainNavController,
                        bottomBarNavHost = bottomNavigationNavController,
                        scope = scope,
                        feedViewModel = feedViewModel,
                        collectionViewModel = collectionViewModel,
                        collectionPatchViewModel = collectionPatchViewModel,
                        calendarViewModel = calendarViewModel,
                        notePatchViewModel = notePatchViewModel,
                    )
                }
                composable(Screen.AboutUs.route) {
                    AboutUs(drawerState, scope)
                }
                composable(Screen.Settings.route) {
                    Settings(drawerState, scope)
                }
                dayNotesScreen(
                    viewModel = calendarViewModel,
                    shareScreenViewModel = shareScreenViewModel,
                    mainNavController = mainNavController,
                )
                collectionEditScreen(
                    feedViewModel = feedViewModel,
                    shareScreenViewModel = shareScreenViewModel,
                    collectionPatchViewModel = collectionPatchViewModel,
                    notePatchViewModel = notePatchViewModel,
                    collectionViewModel = realCollectionViewModel,
                    mainNavController = mainNavController,
                )
                noteEditScreen(
                    noteViewModel = noteViewModel,
                    shareScreenViewModel = shareScreenViewModel,
                    collectionPatchViewModel = collectionPatchViewModel,
                    collectionViewModel = collectionViewModel,
                    notePatchViewModel = notePatchViewModel,
                    mainNavController = mainNavController
                )
                noteOptionsScreen(
                    notePatchViewModel = notePatchViewModel,
                    mainNavController = mainNavController,
                    collectionViewModel = collectionViewModel
                )
                searchScreen(
                    mainNavController = mainNavController,
                    searchViewModel = searchViewModel,
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
                mainNavController.navigate(Screen.EditCollection.route + '/' + collectionName.collectionId)
            }
        )
    }
}

private fun NavGraphBuilder.collectionEditScreen(
    feedViewModel: FeedViewModel,
    shareScreenViewModel: ShareScreenViewModel,
    collectionPatchViewModel: CollectionPatchViewModel,
    notePatchViewModel: NotePatchViewModel,
    collectionViewModel: RealCollectionViewModel,
    mainNavController: NavHostController,
) {
    composable(
        route = Screen.EditCollection.route + "/{collectionName}",
        arguments = listOf(navArgument("collectionName", builder = { type = NavType.LongType }))
    ) {
        val coroutineScope = rememberCoroutineScope()

        it.arguments?.getLong("collectionName")?.let { id ->
            LaunchedEffect(id) {
                val collection = collectionViewModel.find(id).firstOrNull()
                if (collection != null) {
                    collectionPatchViewModel.setCollection(collection)
                } else {
                    Log.v("error", "collection $id not found")
                    mainNavController.popBackStack()
                }
            }
        }

        EditCollectionScreen(
            viewModel = collectionPatchViewModel,
            onNoteClick = { note ->
                mainNavController.navigate(Screen.EditNote.route + '/' + note.id)
            },
            onCollectionClick = { collectionId ->
                mainNavController.navigate(Screen.EditCollection.route + '/' + collectionId.collectionId)
            },
            onAddNote = {
                coroutineScope.launch {
                    val noteId = notePatchViewModel.insertInCollection(collectionPatchViewModel.id)
                    notePatchViewModel.newCollection = CollectionInfo(collectionPatchViewModel.id, collectionPatchViewModel.name)
                    mainNavController.navigate(Screen.EditNote.route + "/$noteId")
                }
            },
            onDeleteCollection = { collection ->
                mainNavController.popBackStack()
                coroutineScope.launch {
                    collectionPatchViewModel.delete(collection.collectionId)
                    collectionPatchViewModel.clear()
                }
            },
            onShareCollection = {
                shareScreenViewModel.link = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            },
            onBackToMenu = { mainNavController.popBackStack() },
            onSaveCollection = { _ ->
                mainNavController.popBackStack()
                coroutineScope.launch {
                    collectionPatchViewModel.updateCollection()
                    collectionPatchViewModel.clear()
                }
//                collectionViewModel.update(collection)

//                The only usage of feedViewModel addCollection
//                feedViewModel.addCollection(collection)
            }
        )
    }
}

private fun NavGraphBuilder.dayNotesScreen(
    viewModel: CalendarViewModel,
    shareScreenViewModel: ShareScreenViewModel,
    mainNavController: NavHostController,
) {
    composable(
        route = Screen.DayNotes.route + "/{day}",
        arguments = listOf(navArgument("day", builder = { type = NavType.StringType }))
    ) {
        it.arguments?.getString("day")?.let { day ->
            LaunchedEffect(day) {
                viewModel.setDay(LocalDate.parse(day))
            }
        }

        DayNotes(
            viewModel = viewModel,
            onNoteClick = { note ->
                mainNavController
                    .navigate(Screen.EditNote.route + '/' + note.id)
            },
            onCollectionClick = { collectionName ->
                mainNavController
                    .navigate(Screen.EditCollection.route + '/' + collectionName.collectionId)
            },
            onShareClick = {
                shareScreenViewModel.link = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            },
            onBackToMenuClick = { mainNavController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.noteEditScreen(
    noteViewModel: NoteViewModel,
    shareScreenViewModel: ShareScreenViewModel,
    collectionPatchViewModel: CollectionPatchViewModel,
    collectionViewModel: CollectionViewModel,
    notePatchViewModel: NotePatchViewModel,
    mainNavController: NavHostController
) {
    composable(
        route = Screen.EditNote.route + "/{noteId}",
        arguments = listOf(navArgument("noteId", builder = { type = NavType.LongType }))
    ) {
        val coroutineScope = rememberCoroutineScope()

        it.arguments?.getLong("noteId")?.let { id ->
            LaunchedEffect(id) {
                val note = noteViewModel.find(id).firstOrNull()
                if (note != null) {
                    notePatchViewModel.set(note)
                } else {
                    Log.e("error", "unknown note id $id")
                    mainNavController.popBackStack()
                }
            }
        }
        NoteEditMenu(
            note = notePatchViewModel,
            onSaveNote = { _ ->
                mainNavController.popBackStack()
                coroutineScope.launch {
                    notePatchViewModel.updateNote()
                    notePatchViewModel.clear()
                }
            },
            onDiscardNote = {
                mainNavController.popBackStack()
                notePatchViewModel.clear()
            },
            onShareNote = {
                shareScreenViewModel.link = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            },
            onPropertiesClick = { id ->
                mainNavController.navigate(Screen.NoteOptions.route + '/' + id)
            },
            onDeleteNote = { id ->
                mainNavController.popBackStack()
                coroutineScope.launch {
                    notePatchViewModel.delete(id)
                    notePatchViewModel.clear()
                }
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
    mainNavHost: NavHostController,
    bottomBarNavHost: NavHostController,
    scope: CoroutineScope,
    feedViewModel: FeedViewModel,
    collectionViewModel: CollectionViewModel,
    collectionPatchViewModel: CollectionPatchViewModel,
    notePatchViewModel: NotePatchViewModel,
    calendarViewModel: CalendarViewModel,
) {
    var fabButtonState: FABButtonState by remember { mutableStateOf(FABButtonState.Closed) }
    val coroutineScope = rememberCoroutineScope()

    FloatingActionButtonMenu(
        onSearchClick = { query ->
            mainNavHost.navigate(Screen.SearchResult.route + "?query=$query")
        },
        onFABMenuSelect = { menu ->
            // Чтобы не делать два разных экрана для создания и изменения заметки будем считать,
            // что изменение заметки с id = -1 это ее создание
            when (menu) {
                Screen.EditCollection -> {
                    coroutineScope.launch {
                        val newId = collectionPatchViewModel.insertEmpty()
                        mainNavHost.navigate(menu.route + "/$newId")
                    }
                }
                Screen.EditNote -> {
                    coroutineScope.launch {
                        val newId = notePatchViewModel.insertNote()
                        mainNavHost.navigate(menu.route + "/$newId")
                    }
                }
            }
        },
        onCollectionClick = { collectionName ->
            mainNavHost.navigate(Screen.EditCollection.route + '/' + collectionName.collectionId)
        },
        drawerState = drawerState,
        scope = scope,
        bottomBarNavHost = bottomBarNavHost,
        mainNavHost = mainNavHost,
        feedViewModel = feedViewModel,
        collectionViewModel = collectionViewModel,
        calendarViewModel = calendarViewModel,
        onFABMenuAct = { fabButtonState = fabButtonState.act() },
        fabButtonState = fabButtonState,
        onNoteClick = { note ->
            mainNavHost.navigate(Screen.EditNote.route + '/' + note.id)
        },
        onCollectionFilter = {
            calendarViewModel.updateOnFilters()
        },
        onDayClick = {
            calendarViewModel.setDay(it)
            mainNavHost.navigate(Screen.DayNotes.route + '/' + it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloatingActionButtonMenu(
    onSearchClick: (String) -> Unit,
    onCollectionClick: (CollectionInfo) -> Unit,
    onNoteClick: (NotePreview) -> Unit,
    onFABMenuSelect: (FABMenuItem) -> Unit,
    onFABMenuAct: () -> Unit,
    onCollectionFilter: (CollectionInfo) -> Unit,
    onDayClick: (LocalDate) -> Unit,
    fabButtonState: FABButtonState,
    drawerState: DrawerState,
    scope: CoroutineScope,
    bottomBarNavHost: NavHostController,
    mainNavHost: NavHostController,
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
            onSearchClick = onSearchClick,
            scope = scope,
            drawerState = drawerState,
            bottomBarNavHost = bottomBarNavHost,
            mainNavHost = mainNavHost,
            onCollectionClick = onCollectionClick,
            onNoteClick = onNoteClick,
            onCollectionFilter = onCollectionFilter,
            onDayClick = onDayClick,
            feedViewModel = feedViewModel,
            collectionViewModel = collectionViewModel,
            calendarViewModel = calendarViewModel,
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
    bottomBarNavHost: NavHostController,
    mainNavHost: NavHostController,
    onCollectionClick: (CollectionInfo) -> Unit,
    onNoteClick: (NotePreview) -> Unit,
    onCollectionFilter: (CollectionInfo) -> Unit,
    onDayClick: (LocalDate) -> Unit,
    feedViewModel: FeedViewModel,
    collectionViewModel: CollectionViewModel,
    calendarViewModel: CalendarViewModel,
) {
    var searchText by remember { mutableStateOf("") }
    val calendarState = rememberCuroCalendarState()

    val currentBottomMenuItem by bottomBarNavHost.currentBackStackEntryAsState()
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
                        val visibleMonth = calendarState.firstVisibleMonth.yearMonth
                        val monthName =
                            visibleMonth.month.name.lowercase().capitalizeFirstLetter()
                        val year = visibleMonth.year
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
                    navigateSameScreen(bottomBarNavHost, screen)
                },
                selected = currentRoute,
            )
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = bottomBarNavHost,
            startDestination = BottomNavigationScreen.Feed.route
        ) {
            navFeedScreen(mainNavHost, onCollectionClick, feedViewModel)
            navCollectionsScreen(collectionViewModel, onCollectionClick, onNoteClick)
            navCalendarScreen(calendarViewModel, calendarState, onCollectionFilter, onDayClick)
        }
    }
}

private fun NavGraphBuilder.navCalendarScreen(
    calendarViewModel: CalendarViewModel,
    calendarState: CalendarState,
    onCollectionClick: (CollectionInfo) -> Unit,
    onDayClick: (LocalDate) -> Unit,
) {
    composable(BottomNavigationScreen.Calendar.route) {
        CalendarScreen(
            calendarViewModel = calendarViewModel,
            calendarState = calendarState,
            onCollectionClick = onCollectionClick,
            onDayClick = onDayClick,
        )
    }
}

private fun NavGraphBuilder.navCollectionsScreen(
    collectionViewModel: CollectionViewModel,
    onCollectionClick: (CollectionInfo) -> Unit,
    onNoteClick: (NotePreview) -> Unit,
) = composable(BottomNavigationScreen.Collections.route) {
    Collections(
        viewModel = collectionViewModel,
        onCollectionClick = onCollectionClick,
        onNoteClick = onNoteClick,
    )
}

private fun NavGraphBuilder.navFeedScreen(
    mainNavHost: NavHostController,
    onCollectionClick: (CollectionInfo) -> Unit,
    feedViewModel: FeedViewModel,
) = composable(BottomNavigationScreen.Feed.route) {
    Feed(
        onNoteClick = { note ->
            mainNavHost.navigate(Screen.EditNote.route + '/' + note.id)
        },
        onCollectionClick = onCollectionClick,
        viewModel = feedViewModel
    )
}

private fun navigateSameScreen(
    navHostController: NavHostController,
    realScreen: Route
) {
    navHostController.navigate(realScreen.route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navHostController.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    AppScreen()
//}
