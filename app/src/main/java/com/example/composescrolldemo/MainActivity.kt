package com.example.composescrolldemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.composescrolldemo.navigation.AppNavGraph
import com.example.composescrolldemo.ui.theme.ComposeScrollDemoTheme

/**
 * Single-activity host.
 *
 * All navigation is managed by [AppNavGraph]; this class only wires up the
 * theme and the edge-to-edge window flags.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ComposeScrollDemoTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
