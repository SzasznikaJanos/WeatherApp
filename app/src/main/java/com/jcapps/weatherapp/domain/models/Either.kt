package com.jcapps.weatherapp.domain.models


sealed interface Either<out L, out R> {
    data class Error<L>(val value: L) : Either<L, Nothing>
    data class Success<R>(val value: R) : Either<Nothing, R>
}

sealed interface DomainError {
    data object NetworkError : DomainError
    data object ApiKeyError : DomainError
    data object CityNotFound : DomainError
    data class ServerError(val code: Int, val message: String) : DomainError
    data class UnknownError(val throwable: Throwable) : DomainError
    data object InvalidInput : DomainError
    data object TimeoutError : DomainError
}

inline fun <L, R> Either<L, R>.onFailure(onError: L.() -> Unit): Either<L, R> {
    if (this is Either.Error) onError(value)
    return this
}

inline fun <L, R> Either<L, R>.onSuccess(onSuccess: R.() -> Unit): Either<L, R> {
    if (this is Either.Success) onSuccess(value)
    return this
}