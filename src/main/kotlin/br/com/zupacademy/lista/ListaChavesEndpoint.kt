package br.com.zupacademy.lista

import br.com.zupacademy.*
import br.com.zupacademy.registra.repository.ChavePixRepository
import br.com.zupacademy.shared.exception.interceptor.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ListaChavesEndpoint(@Inject val repository: ChavePixRepository) :
    KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceImplBase() {

    override fun lista(request: ListaChavePixRequest, responseObserver: StreamObserver<ListaChavePixResponse>) {

        if (request.clientId.isNullOrBlank()) {
            throw IllegalArgumentException("ClientId não pode ser nulo ou vazio")
        }

        val clientId = UUID.fromString(request.clientId)
        val chaves = repository.findAllByClientId(clientId).map {
            ListaChavePixResponse.ChavePix.newBuilder()
                .setPixId(it.id.toString())
                .setTipo(TipoDeChave.valueOf(it.tipo.name))
                .setChave(it.chave)
                .setTipoDeConta(TipoDeConta.valueOf(it.tipoDeConta.name))
                .setCriadaEm(it.criadaEm.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .build()
        }

        responseObserver.onNext(ListaChavePixResponse
            .newBuilder()
            .setClientId(clientId.toString())
            .addAllChaves(chaves)
            .build())
        responseObserver.onCompleted()
    }
}