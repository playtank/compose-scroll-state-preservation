package com.example.composescrolldemo.presentation.hoteldetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composescrolldemo.di.hotelDetailViewModelFactory
import com.example.composescrolldemo.domain.model.Hotel

/**
 * Hotel detail screen.
 *
 * Receives [hotelId] as a nav argument (passed from the NavGraph) and
 * delegates to [HotelDetailViewModel] to load the data.
 *
 * Pressing Back emits [HotelDetailUiIntent.NavigateBack] → ViewModel sends
 * [HotelDetailUiEffect.PopBackStack] → [onNavigateBack] lambda → NavController
 * pops the stack → HotelListScreen re-enters with the ViewModel's preserved
 * scroll position.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HotelDetailScreen(
    hotelId: Int,
    onNavigateBack: () -> Unit,
    viewModel: HotelDetailViewModel = viewModel(
        // key ensures a separate ViewModel instance per hotelId
        key = "hotel_detail_$hotelId",
        factory = hotelDetailViewModelFactory(hotelId),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // One-shot navigation effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                HotelDetailUiEffect.PopBackStack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.hotel?.name ?: "Hotel Detail",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.processIntent(HotelDetailUiIntent.NavigateBack) },
                    ) {
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

                uiState.hotel == null -> {
                    Text(
                        text = "Hotel not found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                else -> {
                    HotelDetail(hotel = uiState.hotel!!)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HotelDetail(
    hotel: Hotel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        // ── Hero image placeholder ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "🏨", style = MaterialTheme.typography.displayLarge)
        }

        // ── Name + location ───────────────────────────────────────────────────
        Text(
            text = hotel.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "📍 ${hotel.location}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider()

        // ── Rating ────────────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "%.1f".format(hotel.rating),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "· ${hotel.reviewCount} reviews",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // ── Price ─────────────────────────────────────────────────────────────
        Text(
            text = "$${hotel.pricePerNight}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "per night",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        HorizontalDivider()

        // ── Amenities ─────────────────────────────────────────────────────────
        Text(
            text = "Amenities",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            hotel.amenities.forEach { amenity ->
                AssistChip(
                    onClick = {},
                    label = { Text(amenity) },
                )
            }
        }

        HorizontalDivider()

        // ── Description placeholder ───────────────────────────────────────────
        Text(
            text = "About",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Experience world-class hospitality at ${hotel.name}, located in the heart of ${hotel.location}. " +
                "Our property blends modern design with exceptional service, offering guests an unparalleled stay " +
                "with breathtaking views, award-winning dining, and a full-service spa. Whether you're traveling " +
                "for business or leisure, every detail has been thoughtfully curated to exceed your expectations.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Reserve button ────────────────────────────────────────────────────
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(
                text = "Reserve for $${hotel.pricePerNight}/night",
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
