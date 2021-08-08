package br.com.zupacademy.remove.controller

import br.com.zupacademy.*
import br.com.zupacademy.registra.model.ChavePix
import br.com.zupacademy.registra.model.ContaAssociada
import br.com.zupacademy.registra.repository.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotNull

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
    @Inject val repository: ChavePixRepository,
    @Inject val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub
){

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setUp(){
        CHAVE_EXISTENTE = repository.save(chavePix(
            tipo = TipoDeChave.EMAIL,
            chave = "teste@zup.com.br",
            clientId = UUID.randomUUID()
        ))
    }

    @AfterEach
    fun cleanUp(){
        repository.deleteAll()
    }

    @Test
    fun `deve remover chave Pix existente`(){

        // cenário

        // ação
        val response = grpcClient.remove(RemoveChavePixRequest
            .newBuilder()
            .setClientId(CHAVE_EXISTENTE.clientId.toString())
            .setPixId(CHAVE_EXISTENTE.id.toString())
            .build())

        // validação
        assertEquals(CHAVE_EXISTENTE.id.toString(), response.pixId)
        assertEquals(CHAVE_EXISTENTE.clientId.toString(), response.clientId)
        assertFalse(repository.existsById(CHAVE_EXISTENTE.id))
    }

    @Test
    fun `nao deve remover chave Pix quando esta nao existir`(){

        // cenário
        val pixIdNaoExistente = UUID.randomUUID().toString()

        // ação
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.remove(
                RemoveChavePixRequest
                    .newBuilder()
                    .setPixId(pixIdNaoExistente)
                    .setClientId(CHAVE_EXISTENTE.clientId.toString())
                    .build()
            )
        }

        // validação

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada ou não pertence ao cliente", status.description)
        }
    }

    @Test
    fun `nao deve remover chave Pix quando pertencer a outro cliente`(){

        // cenário
        val outroCliente = UUID.randomUUID().toString()

        // ação
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.remove(
                RemoveChavePixRequest
                    .newBuilder()
                    .setPixId(CHAVE_EXISTENTE.id.toString())
                    .setClientId(outroCliente)
                    .build()
            )
        }

        // validação
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada ou não pertence ao cliente", status.description)
        }

    }

    @Factory
    class Clients {

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveGrpcServiceGrpc
        .KeyManagerRemoveGrpcServiceBlockingStub? {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chavePix(
        clientId: UUID = UUID.randomUUID(),
        tipo: TipoDeChave,
        chave: String = UUID.randomUUID().toString()
    ): ChavePix {
        return ChavePix(clientId = clientId,
            tipo = tipo,
            chave = chave,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = "UNIBANCO ITAU SA",
                nomeTitular = "Alguém a ser testado",
                cpfTitular = "00000000000",
                agencia = "0001",
                numeroDaConta = "291900")
        )
    }

}