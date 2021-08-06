package br.com.zupacademy.shared.exception.interceptor

import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Type(ExceptionHandlerInterceptor::class)
@Around
annotation class ErrorHandler
