package br.com.zupacademy.carrega.controller

import br.com.zupacademy.CarregaChavePixRequest
import br.com.zupacademy.CarregaChavePixResponse
import br.com.zupacademy.KeyManagerCarregaGrpcServiceGrpc
import br.com.zupacademy.carrega.request.toModel
import br.com.zupacademy.carrega.response.CarregaChavePixResponseConverter
import br.com.zupacademy.external.BancoCentralClient
import br.com.zupacademy.registra.repository.ChavePixRepository
import br.com.zupacademy.shared.exception.interceptor.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.validation.validator.Validator
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class CarregaChavePixEndpoint(@Inject val repository: ChavePixRepository,
                              @Inject val bancoCentralClient: BancoCentralClient,
                              @Inject val validator: Validator
) : KeyManagerCarregaGrpcServiceGrpc.KeyManagerCarregaGrpcServiceImplBase(){

    private val logger = LoggerFactory.getLogger(CarregaChavePixEndpoint::class.java)

    override fun carrega(request: CarregaChavePixRequest, responseObserver: StreamObserver<CarregaChavePixResponse>) {

        val filtro = request.toModel(validator)
        val chaveInfo = filtro.filtra(repository = repository, bancoCentralClient = bancoCentralClient)

        responseObserver.onNext(CarregaChavePixResponseConverter().converter(chaveInfo))
        responseObserver.onCompleted()
    }

}