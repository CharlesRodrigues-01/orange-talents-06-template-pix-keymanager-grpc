package br.com.zupacademy.external

import br.com.zupacademy.external.request.CriaChavePixRequest
import br.com.zupacademy.external.request.DeletaChavePixRequest
import br.com.zupacademy.external.response.CriaChavePixBcbResponse
import br.com.zupacademy.external.response.DeletaChavePixResponse
import br.com.zupacademy.external.response.DetalhesChavePixResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.pix.url}")
interface BancoCentralClient {

    @Post("/api/v1/pix/keys",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML],
        )
    fun cadastraPix(@Body request: CriaChavePixRequest): HttpResponse<CriaChavePixBcbResponse>

    @Delete("/api/v1/pix/keys/{key}",
        produces = [MediaType.APPLICATION_XML],
        consumes = [MediaType.APPLICATION_XML]
        )
    fun deletaPix(@PathVariable key: String,
                  @Body request: DeletaChavePixRequest
    ): HttpResponse<DeletaChavePixResponse>

    @Get("/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML])
    fun buscaPorPix(@PathVariable key: String): HttpResponse<DetalhesChavePixResponse>
}

