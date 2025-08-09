package com.jcapps.weatherapp.arch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface Interactors<ACTION : Any, RESULT : Any> {
    
    fun initResults(): Flow<RESULT> = emptyFlow()
    
    fun actionToResult(action: ACTION): Flow<RESULT>
}