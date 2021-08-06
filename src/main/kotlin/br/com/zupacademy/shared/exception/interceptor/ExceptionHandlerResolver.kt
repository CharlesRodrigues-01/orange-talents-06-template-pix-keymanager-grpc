package br.com.zupacademy.shared.exception.interceptor

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerResolver(@Inject private val listaHandlers: List<ExceptionHandler<*>>) {

    private var defaultExceptionHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()

    fun resolve(e: Exception): ExceptionHandler<*> {
        val procuraHandlers: List<ExceptionHandler<*>> = listaHandlers.filter{ it.supports(e) }

        if (procuraHandlers.size > 1) {
            throw IllegalStateException("Muitos handlers suportam a mesma exception")
        }

        return procuraHandlers.firstOrNull() ?: defaultExceptionHandler
    }
}