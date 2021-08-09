package br.com.zupacademy.registra.response

import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
import br.com.zupacademy.registra.model.ContaAssociada

class ChavePixResponse(tipo: TipoDeChave, chave: String, tipoDeConta: TipoDeConta, conta: ContaAssociada) {
    val tipo = tipo
    val chave = chave
    val tipoDeConta = tipoDeConta
    val conta = conta
}
