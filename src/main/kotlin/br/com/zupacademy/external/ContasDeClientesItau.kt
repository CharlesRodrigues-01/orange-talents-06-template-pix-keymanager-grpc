package br.com.zupacademy.external

import br.com.zupacademy.registra.response.DadosContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.contas.url}")
interface ContasDeClientesItau {

    @Get("/api/v1/clientes/{clientId}/contas{?tipo}",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON])
    fun buscaContaPorTipo(@PathVariable("clientId") clientId: String,
                          @QueryValue("tipo") tipo: String) : HttpResponse<DadosContaResponse>

}