package br.com.zupacademy.external.response

import br.com.zupacademy.TipoDeConta
import br.com.zupacademy.carrega.response.ChavePixInfo
import br.com.zupacademy.external.model.BankAccount
import br.com.zupacademy.external.model.Owner
import br.com.zupacademy.external.model.PixKeyType
import br.com.zupacademy.registra.model.ContaAssociada
import java.time.LocalDateTime

data class DetalhesChavePixResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val cratedAt: LocalDateTime
) {
    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            tipo = keyType.domainType!!,
            chave = this.key,
            tipoDeConta = when (this.bankAccount.accountType){
                BankAccount.AccountType.CACC -> TipoDeConta.CONTA_CORRENTE
                else -> TipoDeConta.CONTA_POUPANCA
            },
            conta = ContaAssociada(
                instituicao = bankAccount.participant,
                nomeTitular = owner.name,
                cpfTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numeroDaConta = bankAccount.accountNumber
            )
        )
    }

}