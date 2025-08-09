package com.jcapps.weatherapp.arch

import kotlinx.coroutines.flow.StateFlow

interface ViewModelContract<STATE : Any, ACTION : Any, EFFECT : Any> {
    
    val uiState: StateFlow<STATE>
    
    val uiEffectFlow: EffectFlow<EFFECT>
    
    fun processUiAction(action: ACTION)
}