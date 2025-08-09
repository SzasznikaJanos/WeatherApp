package com.jcapps.weatherapp.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

abstract class FlowViewModel<STATE : Any, ACTION : Any, RESULT : Any, EFFECT : Any>(
    flowContext: CoroutineDispatcher,
    private val interactors: Interactors<ACTION, RESULT>,
    initialState: STATE,
) : ViewModelContract<STATE, ACTION, EFFECT>, ViewModel() {

    private val actionToJobMap = ConcurrentHashMap<String, Job>()
    private val _uiEffectFlow = MutableSharedFlow<EFFECT>(replay = 1)
    override val uiEffectFlow: EffectFlow<EFFECT> = EffectFlow(_uiEffectFlow.asSharedFlow())
    private val _actionResults = MutableSharedFlow<RESULT>()

    override val uiState: StateFlow<STATE> = merge(
        _actionResults.asSharedFlow(),
        interactors.initResults()
    )
        .flowOn(flowContext)
        .scan(initialState, ::handleResult)
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialState)

    override fun processUiAction(action: ACTION) {
        val actionTag = action::class.java.simpleName
        val subsequentBehavior = getSubsequentActionBehavior(action)

        when (subsequentBehavior) {
            SubsequentActionBehavior.SKIP -> {
                if (actionToJobMap.containsKey(actionTag)) return
            }

            SubsequentActionBehavior.CANCEL_AND_START_NEW -> actionToJobMap[actionTag]?.cancel()
            SubsequentActionBehavior.ENQUEUE -> Unit // Allow multiple jobs for the same action tag

        }

        val job = viewModelScope.launch {
            _actionResults.emitAll(interactors.actionToResult(action))
        }

        if (subsequentBehavior != SubsequentActionBehavior.ENQUEUE) actionToJobMap[actionTag] = job
    }

    protected fun emitEffect(effect: EFFECT) {
        viewModelScope.launch { _uiEffectFlow.emit(effect) }
    }

    protected open fun getSubsequentActionBehavior(action: ACTION): SubsequentActionBehavior {
        return SubsequentActionBehavior.CANCEL_AND_START_NEW
    }

    abstract suspend fun handleResult(previous: STATE, result: RESULT): STATE

    override fun onCleared() {
        super.onCleared()
        actionToJobMap.values.forEach { it.cancel() }
        actionToJobMap.clear()
    }
}