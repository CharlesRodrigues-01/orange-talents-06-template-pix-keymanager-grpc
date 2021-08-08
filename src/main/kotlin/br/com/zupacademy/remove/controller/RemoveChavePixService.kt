package br.com.zupacademy.remove.controller

import br.com.zupacademy.registra.repository.ChavePixRepository
import br.com.zupacademy.shared.validation.ValidUUID
import br.com.zupacademy.shared.exception.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChavePixService(@Inject val repository: ChavePixRepository) {

    private val logger = LoggerFactory.getLogger(RemoveChavePixService::class.java)

    @Transactional
    fun remove(@NotBlank @ValidUUID(message = "cliente ID com formato inválido!") clientId: String?,
               @NotBlank @ValidUUID(message = "Pix ID com formato inválido!") pixId: String?){

        logger.info("Recebendo dados após validação primária")

        val uuidPixId = UUID.fromString(pixId)
        val uuidClientId = UUID.fromString(clientId)

        logger.info("Buscando se chave existe no sistema")

        val chave = repository.findByIdAndClientId(uuidPixId, uuidClientId)
            .orElseThrow{ ChavePixNaoEncontradaException("Chave Pix não encontrada ou não pertence ao cliente") }

        logger.info("Chave Pix existe, início da deleção")

        repository.deleteById(uuidPixId)

    }
}