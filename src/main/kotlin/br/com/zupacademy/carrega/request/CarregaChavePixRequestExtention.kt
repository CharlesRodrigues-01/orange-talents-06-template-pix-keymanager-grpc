package br.com.zupacademy.carrega.request

import br.com.zupacademy.CarregaChavePixRequest
import br.com.zupacademy.CarregaChavePixRequest.FiltroCase.*
import br.com.zupacademy.carrega.filter.Filtro
import io.micronaut.validation.validator.Validator
import javax.validation.ConstraintViolationException

fun CarregaChavePixRequest.toModel(validator: Validator): Filtro {

    val filtro = when(filtroCase){
        PIXID -> pixId.let {
            Filtro.PorPixId(clientId = it.clientId, pixId = it.pixId)
        }
        CHAVE -> Filtro.PorChave(chave)
        FILTRO_NOT_SET -> Filtro.Invalido()
    }

    val violations = validator.validate(filtro)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations)
    }

    return filtro
}