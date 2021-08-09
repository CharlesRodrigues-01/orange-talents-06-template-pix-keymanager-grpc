package br.com.zupacademy.external.model

import br.com.zupacademy.TipoDeConta

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
) {
    enum class AccountType{
        CACC,
        SVGS;

        companion object{
            fun by(domainType: TipoDeConta) : AccountType{
                return when (domainType){
                    TipoDeConta.CONTA_CORRENTE -> CACC
                    else -> SVGS
                }
            }
        }
    }
}