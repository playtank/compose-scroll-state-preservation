package com.example.composescrolldemo.presentation.hotellist

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composescrolldemo.di.hotelListViewModelFactory
import com.example.composescrolldemo.domain.model.Hotel
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Hotel list screen — demonstrates the LazyColumn scroll-preservation fix.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE BUG (without the fix)
 * ─────────────────────────
 * `rememberLazyListState()` stores state inside the Composable's remembered
 * scope.  Navigation pops the Composable off the tree when going to Detail,
 * then re-creates it from zero on Back.  The remembered LazyListState is
 * gone, so the list jumps back to item 0.
 *
 * THE FIX (implemented below)
 * ────────────────────────────
 * 1. We create `LazyListState` with the initial scroll values taken from the
 *    ViewModel's UiState (`firstVisibleItemIndex`, `firstVisibleItemScrollOffset`).
 *    Because the ViewModel survives navigation, these values are non-zero when
 *    the user returns from the Detail screen.
 *
 * 2. A `LaunchedEffect` observes the live LazyListState via `snapshotFlow` and
 *    fires `OnScrollPositionChanged` every time the user scrolls, keeping the
 *    ViewModel in sync.
 *
 * 3. On Back navigation the screen re-enters step 1: the ViewModel still holds
 *    the last saved position → `LazyListState` is seeded with it → list appears
 *    at exactly the right spot.
 * ═══════════════════════════════════════════════════════════════════════════
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelListScreen(
    onNavigateToDetail: (hotelId: Int) -> Unit,
    // The ViewModel is scoped to the NavBackStackEntry by the Compose
    // Navigation runtime — it is NOT recreated on recomposition.
    viewModel: HotelListViewModel = viewModel(factory = hotelListViewModelFactory()),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ── One-shot effects ──────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HotelListUiEffect.NavigateToDetail -> onNavigateToDetail(effect.hotelId)
                is HotelListUiEffect.ShowMessage -> { /* In a real app: show snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotels", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error!!,
                        onRetry = { viewModel.processIntent(HotelListUiIntent.Refresh) },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    HotelList(
                        hotels = uiState.hotels,
                        // ── FIX step 1: seed LazyListState from ViewModel ──────
                        // These come from the ViewModel, which persists them across
                        // navigation.  On first load they are both 0 (default).
                        // On re-entry after Back they hold the last saved position.
                        initialFirstVisibleItemIndex = uiState.firstVisibleItemIndex,
                        initialFirstVisibleItemScrollOffset = uiState.firstVisibleItemScrollOffset,
                        onScrollPositionChanged = { index, offset ->
                            // ── FIX step 2: push scroll changes back to ViewModel ──
                            viewModel.processIntent(
                                HotelListUiIntent.OnScrollPositionChanged(index, offset)
                            )
                        },
                        onHotelClick = { hotel ->
                            viewModel.processIntent(HotelListUiIntent.OnHotelClicked(hotel.id))
                        },
                    )
                }
            }
        }
    }
}

// ─────────────────────────── Sub-composables ─────────────────────────────────

@Composable
private fun HotelList(
    hotels: List<Hotel>,
    initialFirstVisibleItemIndex: Int,
    initialFirstVisibleItemScrollOffset: Int,
    onScrollPositionChanged: (index: Int, offset: Int) -> Unit,
    onHotelClick: (Hotel) -> Unit,
    modifier: Modifier = Modifier,
) {
    // ── FIX step 1 (continued): pass persisted values to LazyListState ────────
    //
    // WITHOUT the fix you would write:
    //   val listState = rememberLazyListState()
    // That always starts at (0, 0).
    //
    // WITH the fix:
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset,
    )

    // ── FIX step 2: observe scroll position and sync to ViewModel ─────────────
    //
    // `snapshotFlow` turns Compose snapshot state into a regular Kotlin Flow.
    // `distinctUntilChanged` prevents redundant dispatches when the values
    // haven't actually changed (e.g., during fling deceleration at rest).
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                onScrollPositionChanged(index, offset)
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp,
        ),
    ) {
        items(
            items = hotels,
            key = { it.id }, // stable keys help Compose skip unnecessary recompositions
        ) { hotel ->
            HotelCard(hotel = hotel, onClick = { onHotelClick(hotel) })
        }
    }
}

@Composable
private fun HotelCard(
    hotel: Hotel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            // ── Image placeholder ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "🏨",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ── Text content ──────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = hotel.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Rating row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "%.1f".format(hotel.rating),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${hotel.reviewCount} reviews)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$${hotel.pricePerNight} / night",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onRetry) { Text("Retry") }
    }
}
