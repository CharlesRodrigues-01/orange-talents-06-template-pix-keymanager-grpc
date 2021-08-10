package br.com.zupacademy.carrega.filter

import br.com.zupacademy.CarregaChavePixResponse
import br.com.zupacademy.carrega.response.ChavePixInfo
import br.com.zupacademy.external.BancoCentralClient
import br.com.zupacademy.registra.repository.ChavePixRepository
import br.com.zupacademy.shared.exception.ChavePixNaoEncontradaException
import br.com.zupacademy.shared.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.lang.IllegalArgumentException
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    abstract fun filtra(repository: ChavePixRepository, bancoCentralClient: BancoCentralClient): ChavePixInfo

    @Introspected
    data class PorPixId(
        @field:NotBlank @field:ValidUUID val clientId: String,
        @field:NotBlank @field:ValidUUID val pixId: String
    ): Filtro() {
        fun pixIdAsUuid() = UUID.fromString(pixId)
        fun clientIdAsUuid() = UUID.fromString(clientId)

        override fun filtra(repository: ChavePixRepository, bancoCentralClient: BancoCentralClient): ChavePixInfo {
            return repository.findById(pixIdAsUuid())
                .filter { it.pertenceAoCliente(clientIdAsUuid()) }
                .map(ChavePixInfo::of)
                .orElseThrow { ChavePixNaoEncontradaException("Chave Pix não encontrada") }
        }
    }

    @Introspected
    data class PorChave(
        @field:NotBlank @field:Size(max = 77) val chave: String
    ): Filtro() {
        override fun filtra(repository: ChavePixRepository, bancoCentralClient: BancoCentralClient): ChavePixInfo {
            return repository.findByChave(chave)
                .map(ChavePixInfo::of)
                .orElseGet {
                    val response = bancoCentralClient.buscaPorPix(chave)
                    when(response.status) {
                        HttpStatus.OK -> response.body()?.toModel()
                        else -> throw ChavePixNaoEncontradaException("Chave Pix não encontrada")
                    }
                }

        }
    }

    @Introspected
    class Invalido(): Filtro() {
        override fun filtra(repository: ChavePixRepository, bancoCentralClient: BancoCentralClient): ChavePixInfo {
            throw IllegalArgumentException("Chave Pix Inválida ou não informada")
        }
    }
}