package com.example.travelmate.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelmate.ui.viewmodel.*

@Composable
fun getViewModelFactory(): ViewModelFactory {
    val context = LocalContext.current
    return ViewModelFactory.create(context)
}

@Composable
inline fun <reified T : ViewModel> travelMateViewModel(): T {
    val factory = getViewModelFactory()
    return viewModel(factory = factory)
}

