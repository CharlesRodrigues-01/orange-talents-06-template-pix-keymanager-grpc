package br.com.zupacademy.remove.controller

import br.com.zupacademy.KeyManagerRemoveGrpcServiceGrpc
import br.com.zupacademy.RemoveChavePixRequest
import br.com.zupacademy.RemoveChavePixResponse
import br.com.zupacademy.registra.controller.RegistraChaveEndpoint
import br.com.zupacademy.shared.exception.interceptor.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChaveEndpoint(@Inject private val service: RemoveChavePixService) :
    KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(RegistraChaveEndpoint::class.java)

    override fun remove(request: RemoveChavePixRequest, responseObserver: StreamObserver<RemoveChavePixResponse>) {

        logger.info("Iniciando remoção")
        service.remove(clientId = request.clientId, pixId = request.pixId)

        logger.info("Montando resposta após remoção")
        responseObserver.onNext(
            RemoveChavePixResponse
            .newBuilder()
            .setClientId(request.clientId)
            .setPixId(request.pixId)
            .build())
        responseObserver.onCompleted()
    }
}