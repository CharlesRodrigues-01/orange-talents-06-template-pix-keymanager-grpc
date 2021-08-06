package br.com.zupacademy.shared.exception

import br.com.zupacademy.shared.exception.interceptor.ExceptionHandler
import br.com.zupacademy.shared.exception.interceptor.ExceptionHandler.*
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixExistenteExceptionHandler : ExceptionHandler<ChavePixExistenteException> {

    override fun handle(e: Exception): StatusWithDetails {
        return StatusWithDetails(Status.ALREADY_EXISTS.withDescription(e.message).withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixExistenteException
    }
}