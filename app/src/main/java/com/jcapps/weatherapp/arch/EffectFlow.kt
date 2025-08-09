package com.jcapps.weatherapp.arch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@JvmInline
value class EffectFlow<EFFECT : Any>(
    val flow: SharedFlow<EFFECT>
)

@Composable
fun <EFFECT : Any> WatchEffectFlow(
    effectFlow: EffectFlow<EFFECT>,
    onEffect: suspend (EFFECT) -> Unit
) {
    LaunchedEffect(effectFlow) {
        effectFlow.flow.collectLatest { effect ->
            onEffect(effect)
        }
    }
}