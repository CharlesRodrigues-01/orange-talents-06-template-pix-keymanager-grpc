package br.com.zupacademy.carrega.response

import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
import br.com.zupacademy.registra.model.ChavePix
import br.com.zupacademy.registra.model.ContaAssociada
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfo(
    val pixId: UUID? = null,
    val clientId: UUID? = null,
    val tipo: TipoDeChave,
    val chave: String,
    val tipoDeConta: TipoDeConta,
    val conta: ContaAssociada,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {

    companion object{
        fun of(chave: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                pixId = chave.id,
                clientId = chave.clientId,
                tipo = chave.tipo,
                chave = chave.chave,
                tipoDeConta = chave.tipoDeConta,
                conta = chave.conta,
                registradaEm = chave.criadaEm
            )

        }
    }
}