package br.com.zupacademy.registra.controller

import br.com.zupacademy.KeyManagerRegistraGrpcServiceGrpc
import br.com.zupacademy.KeyManagerRegistraGrpcServiceGrpc.newBlockingStub
import br.com.zupacademy.RegistraChavePixRequest
import br.com.zupacademy.RegistraChavePixResponse.newBuilder
import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
import br.com.zupacademy.registra.exception.interceptor.ExceptionHandlerInterceptor
import br.com.zupacademy.registra.externo.ContasDeClientesItau
import br.com.zupacademy.registra.model.ChavePix
import br.com.zupacademy.registra.model.ContaAssociada
import br.com.zupacademy.registra.model.TipoChave
import br.com.zupacademy.registra.repository.ChavePixRepository
import br.com.zupacademy.registra.request.NovaChavePix
import br.com.zupacademy.registra.response.DadosContaResponse
import br.com.zupacademy.registra.response.InstituicaoResponse
import br.com.zupacademy.registra.response.TitularResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    @Inject val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub,
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ContasDeClientesItau
) {

    companion object {
        val CLIENT_ID = UUID.randomUUID()
    }

    val clientId = UUID.randomUUID()
    val tipoDeConta = TipoDeConta.CONTA_CORRENTE
    val tipo = TipoDeChave.CPF
    val chave = "00000000000"
    val instituicao = "ITAÚ UNIBANCO S.A."
    val ispb = "60701190"
    val agencia = "0001"
    val numeroConta = "291900"
    val nomeTitular = "Alguém a ser testado"
    val cpf = "00000000000"

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar uma nova chave Pix`() {

        val logger = LoggerFactory.getLogger(this::class.java)

        // cenário
        `when`(
            itauClient.buscaContaPorTipo(
                clientId = CLIENT_ID.toString(),
                tipo = "CONTA_CORRENTE"
            )
        )
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))
        // ação
        logger.error("Inicio da ação")

        val response = grpcClient.registra(
            RegistraChavePixRequest
                .newBuilder()
                .setClientId(CLIENT_ID.toString())
                .setTipoDeContaChave(TipoDeChave.EMAIL)
                .setChave("teste@zup.com.br")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        // validação

        with(response) {
            assertEquals(CLIENT_ID.toString(), clientId)
            assertNotNull(pixId)
            assertTrue(repository.existsByChave("teste@zup.com.br"))
        }
    }

    @Test
    fun `nao deve registrar uma nova chave Pix se ja existir uma`() {

        // cenário
        val chave = repository.save(
            ChavePix(
                clientId = clientId,
                tipo = tipo,
                chave = chave,
                tipoDeConta = tipoDeConta,
                ContaAssociada(
                    instituicao = instituicao,
                    nomeTitular = nomeTitular,
                    cpfTitular = cpf,
                    agencia = agencia,
                    numeroDaConta = numeroConta
                )
            )
        )

        //ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest
                    .newBuilder()
                    .setClientId(clientId.toString())
                    .setTipoDeContaChave(TipoDeChave.CPF)
                    .setChave("00000000000")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        //validação
        assertEquals(Status.ALREADY_EXISTS.code, response.status.code)
        assertTrue(response.message!!.contains("Esta chave Pix ja existe"))
    }

    @Test
    fun `nao deve registrar uma nova chave Pix se nao encontrar dados da conta`() {

        //cenário
        `when`(
            itauClient.buscaContaPorTipo(
                clientId = CLIENT_ID.toString(),
                tipo = "CONTA_CORRENTE"
            )
        ).thenReturn(HttpResponse.notFound())
        //ação

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest
                    .newBuilder()
                    .setClientId(CLIENT_ID.toString())
                    .setTipoDeContaChave(TipoDeChave.EMAIL)
                    .setChave("teste@zup.com.br")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        //validação
        with(response) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertTrue(response.message!!.contains("Cliente não encontrado!"))
        }
    }

    @Test
    fun `nao deve registrar uma nova chave Pix com parametros invalidos`(){

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder().build())
        }

        with(response) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertTrue(response.message!!.contains("must not be null"))
        }

    }

    @MockBean(ContasDeClientesItau::class)
    fun itauClient() : ContasDeClientesItau? {
        return Mockito.mock(ContasDeClientesItau::class.java)
    }

    @Factory
    class Clients {

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegistraGrpcServiceGrpc
        .KeyManagerRegistraGrpcServiceBlockingStub? {
            return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun dadosDaContaResponse(): DadosContaResponse{
        return DadosContaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "0001",
            numero = "291900",
            titular = TitularResponse("Alguém a ser testado", "00000000000")
        )
    }
}