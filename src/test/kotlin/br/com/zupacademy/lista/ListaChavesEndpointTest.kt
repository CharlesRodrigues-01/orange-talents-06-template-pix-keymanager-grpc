package br.com.zupacademy.lista

import br.com.zupacademy.KeyManagerListaGrpcServiceGrpc
import br.com.zupacademy.ListaChavePixRequest
import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class ListaChavesEndpointTest(
    @Inject val grpcClient: KeyManagerListaGrpcServiceGrpc.KeyManagerListaGrpcServiceBlockingStub,
    @Inject val repository: ChavePixRepository){

    companion object {
        val CLIENT_ID = UUID.randomUUID()

        val instituicao = "ITAÚ UNIBANCO S.A."
        val ispb = "60701190"
        val agencia = "0001"
        val numeroConta = "291900"
        val nomeTitular = "Alguém a ser testado"
        val cpf = "00000000000"
    }

    @BeforeEach
    fun setUp(){
        repository.save(chave(tipo = TipoDeChave.EMAIL, chave = "teste@zup.com.br", clientId = CLIENT_ID))
        repository.save(chave(tipo = TipoDeChave.ALEATORIA, chave = "qualquerCoisa", clientId = UUID.randomUUID()))
        repository.save(chave(tipo = TipoDeChave.ALEATORIA, chave = "qualquerOutraCoisa", clientId = CLIENT_ID))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve listar todas as chaves do cliente`(){

        // cenário
        val clientId = CLIENT_ID.toString()

        // ação
        val response = grpcClient.lista(ListaChavePixRequest
            .newBuilder()
            .setClientId(clientId)
            .build())

        // validação
        with(response.chavesList){
            assertThat(this, hasSize(2))
            assertThat( this.map {
                Pair(it.tipo, it.chave)
            }.toList(), containsInAnyOrder(
                Pair(TipoDeChave.ALEATORIA, "qualquerOutraCoisa"),
                Pair(TipoDeChave.EMAIL, "teste@zup.com.br")
            ))
        }
    }

    @Test
    fun `nao deve listar chaves quando cliente nao possuir chaves`(){

        // cenário
        val clienteSemChaves = UUID.randomUUID().toString()

        // ação
        val response = grpcClient.lista(ListaChavePixRequest
            .newBuilder()
            .setClientId(clienteSemChaves)
            .build())

        // validação
        with(response){
            assertEquals(0, this.chavesCount)
        }
    }

    @Test
    fun `nao deve listar chaves quando quando clientId for inválido`(){

        // cenário
        val clientIdInvalido = ""

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.lista(ListaChavePixRequest
                .newBuilder()
                .setClientId(clientIdInvalido)
                .build())
        }

        // validação
        with(response){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("ClientId não pode ser nulo ou vazio", status.description)
        }
    }

    @Factory
    class Clients {

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerListaGrpcServiceGrpc
        .KeyManagerListaGrpcServiceBlockingStub? {
            return KeyManagerListaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chave(tipo: TipoDeChave, chave: String, clientId: UUID): ChavePix {
        return ChavePix(
            tipo = tipo,
            chave = chave,
            clientId = clientId,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = instituicao,
                nomeTitular = nomeTitular,
                cpfTitular = cpf,
                agencia = agencia,
                numeroDaConta = numeroConta
            )
        )
    }
}