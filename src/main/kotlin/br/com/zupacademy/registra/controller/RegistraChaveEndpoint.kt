package br.com.zupacademy.registra.controller

import br.com.zupacademy.KeyManagerRegistraGrpcServiceGrpc
import br.com.zupacademy.RegistraChavePixRequest
import br.com.zupacademy.RegistraChavePixResponse
import br.com.zupacademy.registra.exception.interceptor.ErrorHandler
import br.com.zupacademy.registra.request.toModel
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RegistraChaveEndpoint(@Inject val service: NovaChavePixService) :
    KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(RegistraChaveEndpoint::class.java)

    override fun registra(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val novaChave = request.toModel()
        val chaveCriada = service.registra(novaChave)

        logger.info("Montando resposta")
        responseObserver.onNext(
            RegistraChavePixResponse.newBuilder()
            .setClientId(chaveCriada.clientId.toString())
            .setPixId(chaveCriada.id.toString())
            .build())
        responseObserver.onCompleted()
    }
}