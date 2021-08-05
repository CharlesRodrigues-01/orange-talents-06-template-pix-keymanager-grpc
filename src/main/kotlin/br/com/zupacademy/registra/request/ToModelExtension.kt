package br.com.zupacademy.registra.request

import br.com.zupacademy.RegistraChavePixRequest
import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
import br.com.zupacademy.registra.model.TipoChave
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(RegistraChavePixRequest::class.java)

fun RegistraChavePixRequest.toModel() : NovaChavePix {

    logger.info("Convertendo dados de requisição para a classe DTO da chave Pix")

    return NovaChavePix(
        clientId = clientId,
        tipo = when (this.tipoDeContaChave) {
            TipoDeChave.UNKNOWN_CHAVE -> null
            else -> TipoChave.valueOf(tipoDeContaChave.name)
        },
        chave = chave,
        tipoDeConta = when (this.tipoDeConta) {
            TipoDeConta.UNKNOWN_CONTA -> null
            else -> TipoDeConta.valueOf(tipoDeConta.name)
        }
    )
}