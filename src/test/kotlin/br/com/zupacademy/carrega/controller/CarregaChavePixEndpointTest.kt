package br.com.zupacademy.carrega.controller

import br.com.zupacademy.CarregaChavePixRequest
import br.com.zupacademy.CarregaChavePixRequest.FiltroPorPixId
import br.com.zupacademy.CarregaChavePixRequest.newBuilder
import br.com.zupacademy.KeyManagerCarregaGrpcServiceGrpc
import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeChave.*
import br.com.zupacademy.TipoDeConta
import br.com.zupacademy.external.BancoCentralClient
import br.com.zupacademy.external.model.BankAccount
import br.com.zupacademy.external.model.Owner
import br.com.zupacademy.external.model.PixKeyType
import br.com.zupacademy.external.response.DetalhesChavePixResponse
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
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CarregaChavePixEndpointTest(
    @Inject val repository: ChavePixRepository,
    @Inject val grpcClient: KeyManagerCarregaGrpcServiceGrpc.KeyManagerCarregaGrpcServiceBlockingStub
){

    @Inject
    lateinit var bancoCentralClient: BancoCentralClient

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
        repository.save(chave(tipo = EMAIL, chave = "teste@zup.com.br", clientId = CLIENT_ID))
        repository.save(chave(tipo = CPF, chave = "00000000000", clientId = UUID.randomUUID()))
        repository.save(chave(tipo = ALEATORIA, chave = "qualquerCoisa", clientId = CLIENT_ID))
        repository.save(chave(tipo = CELULAR, chave = "+551912345678", clientId = CLIENT_ID))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve carregar chave por pixId e clientId`(){

        // cenário
        val chaveExistente = repository.findByChave("+551912345678").get()

        // ação
        val response = grpcClient.carrega(
            newBuilder()
            .setPixId(FiltroPorPixId.newBuilder()
                .setPixId(chaveExistente.id.toString())
                .setClientId(chaveExistente.clientId.toString())
                .build())
            .build())

        // validação
        with(response){
            assertEquals(chaveExistente.id.toString(), this.pixId)
            assertEquals(chaveExistente.clientId.toString(), this.clientId)
            assertEquals(chaveExistente.tipo.name, this.chave.tipo.name)
            assertEquals(chaveExistente.chave, this.chave.chave)
        }
    }

    @Test
    fun `nao deve carregar uma chave por pixId e clientId quando filtro invalido`(){

        // cenário

        // açõa
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest
                .newBuilder()
                .setPixId(FiltroPorPixId
                    .newBuilder()
                    .setPixId("")
                    .setClientId("")
                    .build())
                .build())
        }

        // validação
        with(response) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `nao deve carregar uma chave por pixId e clientId quando registro nao existir`(){

        // cenário
        val pixIdNaoExistente = UUID.randomUUID().toString()
        val clienteNaoExistente = UUID.randomUUID().toString()

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest
                .newBuilder()
                .setPixId(FiltroPorPixId
                    .newBuilder()
                    .setPixId(pixIdNaoExistente)
                    .setClientId(clienteNaoExistente)
                    .build())
                .build())
        }

        // validação
        with(response){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada", status.description)
        }
    }

    @Test
    fun `deve carregar chave por valor quando registro existir localmente`(){

        // cenário
        val chaveExistente = repository.findByChave("teste@zup.com.br").get()

        // ação
        val response = grpcClient.carrega(CarregaChavePixRequest
                .newBuilder()
                .setChave("teste@zup.com.br")
                .build()
        )

        // validação
        with(response){
            assertEquals(chaveExistente.id.toString(), this.pixId)
            assertEquals(chaveExistente.clientId.toString(), this.clientId)
            assertEquals(chaveExistente.tipo.name, this.chave.tipo.name)
            assertEquals(chaveExistente.chave, this.chave.chave)
        }
    }

    @Test
    fun `deve retornar chave por seu valor quando registro só existir no BCB`(){

        // cenário
        val bcbResponse = detalhesChavePixResponse()
        `when`(bancoCentralClient.buscaPorPix(key ="outroTeste@zup.com.br"))
            .thenReturn(HttpResponse.ok(detalhesChavePixResponse()))

        // ação
        val response = grpcClient.carrega(CarregaChavePixRequest
            .newBuilder()
            .setChave("outroTeste@zup.com.br")
            .build())

        // validação
        with(response){
            assertEquals("", this.pixId)
            assertEquals("", this.clientId)
            assertEquals(bcbResponse.keyType.name, this.chave.tipo.name)
            assertEquals(bcbResponse.key, this.chave.chave)
        }
    }

    @Test
    fun `nao deve carregar chave por valor quando esta nao existir localmente e nem no BCB`(){

        // cenário
        `when`(bancoCentralClient.buscaPorPix(key ="chaveQueNaoExiste@zup.com.br"))
            .thenReturn(HttpResponse.notFound())

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest
                .newBuilder()
                .setChave("chaveQueNaoExiste@zup.com.br")
                .build())
        }

        // validação
        with(response){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave Pix não encontrada", status.description)
        }
    }

    @Test
    fun `nao deve carregar chave por valor quando filtro invalido`(){

        // cenário

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest
                .newBuilder()
                .setChave("")
                .build())
        }

        // validação
        with(response){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("chave: must not be blank", status.description)
        }
    }

    @Test
    fun `nao deve carregar chave quando filtro for inválido`(){

        // cenário

        // ação
        val response = assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest
                .newBuilder()
                .build())
        }

        // validação
        with(response){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave Pix Inválida ou não informada", status.description)
        }
    }

    @MockBean(BancoCentralClient::class)
    fun bancoCentralClient(): BancoCentralClient? {
        return Mockito.mock(BancoCentralClient::class.java)
    }

    @Factory
    class Clients {

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerCarregaGrpcServiceGrpc
        .KeyManagerCarregaGrpcServiceBlockingStub? {
            return KeyManagerCarregaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun detalhesChavePixResponse(): DetalhesChavePixResponse {
        return DetalhesChavePixResponse(
            keyType = PixKeyType.CPF,
            key = "outroTeste@zup.com.br",
            bankAccount = BankAccount(
                participant = ispb,
                branch = agencia,
                accountNumber = numeroConta,
                accountType = BankAccount.AccountType.by(TipoDeConta.CONTA_CORRENTE)),
            owner = Owner(
                type = Owner.OwnerType.NATURAL_PERSON,
                name = nomeTitular,
                taxIdNumber = cpf),
            cratedAt = LocalDateTime.now())
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