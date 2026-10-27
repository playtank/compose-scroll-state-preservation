package com.example.composescrolldemo.presentation.recomposition

/**
 * MVI Contract for the Recomposition Demo screen.
 *
 * The intentional flaw: [RecompositionUiState] bundles two completely unrelated
 * pieces of state together — [products] and [clickCounter]. Because the entire
 * state object is passed down to each product item composable, every increment
 * of [clickCounter] invalidates every item's parameter equality check and forces
 * ALL items to recompose, even though none of their product data changed.
 *
 * This is the "before" state — a deliberate anti-pattern to make the problem
 * visible in Layout Inspector's recomposition counts.
 */
data class RecompositionUiState(
    val products: List<DemoProduct> = emptyList(),
    /**
     * High-frequency counter that has nothing to do with products.
     * Changing this field alone will cause every item composable that
     * receives the whole [RecompositionUiState] to recompose.
     */
    val clickCounter: Int = 0,
)

/**
 * A synthetic product used purely as list content for this demo.
 */
data class DemoProduct(
    val id: Int,
    val name: String,
    val price: Double,
    val isFavorite: Boolean = false,
)

sealed interface RecompositionUiIntent {
    /** User tapped the "Increment counter" button. */
    data object IncrementCounter : RecompositionUiIntent

    /** User toggled the favourite icon on a product. */
    data class ToggleFavorite(val productId: Int) : RecompositionUiIntent
}
