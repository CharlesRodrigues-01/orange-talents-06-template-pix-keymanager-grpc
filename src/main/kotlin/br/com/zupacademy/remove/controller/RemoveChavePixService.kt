package br.com.zupacademy.remove.controller

import br.com.zupacademy.external.BancoCentralClient
import br.com.zupacademy.external.request.DeletaChavePixRequest
import br.com.zupacademy.registra.repository.ChavePixRepository
import br.com.zupacademy.shared.exception.ChavePixNaoEncontradaException
import br.com.zupacademy.shared.validation.ValidUUID
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChavePixService(@Inject val repository: ChavePixRepository,
                            @Inject val bancoCentralClient: BancoCentralClient
) {

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

        val bcbRequest = DeletaChavePixRequest(chave.chave)

        val bcbResponse = bancoCentralClient.deletaPix(key = chave.chave, request = bcbRequest)
        if (bcbResponse.status != HttpStatus.OK){
            throw IllegalStateException("Erro ao remover chave Pix do Banco Central")
        }
    }
}