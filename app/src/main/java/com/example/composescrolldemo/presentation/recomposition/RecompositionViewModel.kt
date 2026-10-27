package com.example.composescrolldemo.presentation.recomposition

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Recomposition Demo screen.
 *
 * Keeps product data and the unrelated [clickCounter] in the same state object
 * so the UI can observe the intentional flaw: a counter change causes every
 * product item — which receives the whole state — to recompose.
 */
class RecompositionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        RecompositionUiState(products = generateProducts()),
    )
    val uiState: StateFlow<RecompositionUiState> = _uiState.asStateFlow()

    fun processIntent(intent: RecompositionUiIntent) {
        when (intent) {
            RecompositionUiIntent.IncrementCounter -> {
                _uiState.update { it.copy(clickCounter = it.clickCounter + 1) }
            }

            is RecompositionUiIntent.ToggleFavorite -> {
                _uiState.update { state ->
                    state.copy(
                        products = state.products.map { product ->
                            if (product.id == intent.productId) {
                                product.copy(isFavorite = !product.isFavorite)
                            } else {
                                product
                            }
                        },
                    )
                }
            }
        }
    }

    private fun generateProducts(): List<DemoProduct> = List(30) { index ->
        DemoProduct(
            id = index + 1,
            name = "Product ${index + 1}",
            price = 9.99 + index * 5.0,
            isFavorite = false,
        )
    }
}
