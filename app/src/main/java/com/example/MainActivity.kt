package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.SunoV6MainScreen
import com.example.ui.SunoViewModel
import com.example.ui.theme.SunoTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SunoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunoTheme {
                SunoV6MainScreen(viewModel = viewModel)
            }
        }
    }
}
