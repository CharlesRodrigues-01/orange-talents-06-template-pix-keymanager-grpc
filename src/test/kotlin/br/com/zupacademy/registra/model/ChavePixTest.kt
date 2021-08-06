package br.com.zupacademy.registra.model

import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class ChavePixTest{

    companion object{
        val TIPOS_DE_CHAVES_EXCETO_ALEATORIO = TipoDeChave.values().filterNot { it == TipoDeChave.ALEATORIA }
    }

    @Test
    fun `deve validar se uma chave pertence a um cliente`(){

        val clientId = UUID.randomUUID()
        val otherClientId = UUID.randomUUID()

        with(chavePix(
            clientId = clientId,
            tipo = TipoDeChave.ALEATORIA
        )){
            assertTrue(this.pertenceAoCliente(clientId))
            assertFalse(this.pertenceAoCliente(otherClientId))
        }
    }

    @Test
    fun `deve validar se a chave é aleatoria`(){
        with(chavePix(tipo = TipoDeChave.ALEATORIA)){
            assertTrue(isAleatoria())
        }
    }

    @Test
    fun `nao deve ser aleatoria`(){

        val original = "Uma chave qualquer"
        TIPOS_DE_CHAVES_EXCETO_ALEATORIO
            .forEach {
                with(chavePix(tipo = it, chave = original)) {
                    assertFalse(this.atualiza("Uma nova chave"))
                    assertEquals(original, this.chave)
                }
            }
    }

    private fun chavePix(clientId: UUID = UUID.randomUUID(), tipo: TipoDeChave, chave: String = UUID.randomUUID().toString()): ChavePix {
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