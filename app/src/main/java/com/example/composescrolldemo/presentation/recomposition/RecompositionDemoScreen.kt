package com.example.composescrolldemo.presentation.recomposition

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val TAG = "RecompositionDemo"

// ─────────────────────────────────────────────────────────────────────────────
// Screen (nav entry-point — receives ViewModel from the nav graph so the
// ViewModel is scoped to the back-stack entry and survives navigating away/back)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Entry-point composable wired into the nav graph.
 *
 * The ViewModel is injected by the nav graph (see [AppNavGraph]) rather than
 * created here, which ensures it is scoped to the back-stack entry and its
 * [clickCounter] is never reset when the user navigates away and returns.
 */
@Composable
fun RecompositionDemoScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecompositionViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecompositionDemoContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onIntent = viewModel::processIntent,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Content (pure UI — no ViewModel dependency)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Stateless content layer. Accepts the whole [RecompositionUiState] and propagates
 * it all the way down to [ProductItem] — this is the deliberate flaw.
 *
 * **Why this causes unnecessary recompositions:**
 * [RecompositionUiState] is a data class, so Compose uses structural equality to
 * decide whether to skip recomposition. When [clickCounter] changes, a new
 * [RecompositionUiState] instance is created (even if [products] is identical).
 * Because [ProductItem] receives the entire state object as a parameter, Compose
 * sees a new, non-equal argument and recomposes **every single item** in the list,
 * even though nothing relevant to those items changed.
 *
 * The red badge on each card shows the running recomposition count for that item.
 * Tap "Increment counter" once and every visible badge increments simultaneously.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecompositionDemoContent(
    uiState: RecompositionUiState,
    onNavigateBack: () -> Unit,
    onIntent: (RecompositionUiIntent) -> Unit,
) {
    Log.d(TAG, "RecompositionDemoContent recomposed — counter=${uiState.clickCounter}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomposition Demo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            CounterSection(
                counter = uiState.clickCounter,
                onIncrement = { onIntent(RecompositionUiIntent.IncrementCounter) },
            )

            // ⚠️ FLAW: the entire `uiState` is passed into each item.
            // Every counter increment produces a new state object → equality fails
            // → all 30 items recompose even though no product data changed.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = uiState.products,
                    key = { it.id },
                ) { product ->
                    ProductItem(
                        uiState = uiState,
                        productId = product.id,
                        onToggleFavorite = {
                            onIntent(RecompositionUiIntent.ToggleFavorite(product.id))
                        },
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CounterSection(
    counter: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "⚠️ Unrelated state trigger",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = "This button only changes clickCounter — nothing to do with " +
                    "products. Watch every item's recomposition badge increment anyway.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onIncrement) {
                    Text("Increment counter")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Clicks: $counter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}

/**
 * Product item composable with a visible recomposition counter badge.
 *
 * **The anti-pattern lives here**: this composable receives the entire
 * [RecompositionUiState] instead of only the three fields it actually needs
 * (`name`, `price`, `isFavorite`). Any change to any field in that state object —
 * including the completely unrelated [clickCounter] — produces a new instance,
 * fails the equality check, and forces this composable to recompose.
 *
 * The red badge in the top-right corner shows the running recomposition count.
 * It uses a plain [Int] ref (not a `MutableState`) so reading or writing it does
 * NOT itself trigger a recomposition. [SideEffect] increments the ref and then
 * reads the latest value purely for display — the Text showing the count is
 * re-read because the composable is already recomposing for another reason
 * (the uiState parameter changed), not because the ref changed.
 */
@Composable
private fun ProductItem(
    uiState: RecompositionUiState,    // ⚠️ whole state — the anti-pattern
    productId: Int,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val product = uiState.products.firstOrNull { it.id == productId } ?: return

    // A plain wrapper around a raw Int — Compose does NOT observe this for
    // recomposition. Writing to it can never schedule another recomposition,
    // so there is no feedback loop.
    val recompositionCount = remember { intArrayOf(0) }

    // SideEffect runs after every successful recomposition of this composable.
    // It writes to the non-observable ref and logs — no state change, no loop.
    SideEffect {
        recompositionCount[0]++
        Log.d(TAG, "ProductItem recomposed — id=${product.id}, count=${recompositionCount[0]}")
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "$%.2f".format(product.price),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = if (product.isFavorite) "Remove from favourites"
                        else "Add to favourites",
                        tint = if (product.isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Badge reads recompositionCount[0] directly. It refreshes because the
        // whole ProductItem is recomposing (due to the uiState parameter changing),
        // not because the count value itself is observable state.
        Text(
            text = "recomposed ×${recompositionCount[0]}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onError,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.error)
                .padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}
