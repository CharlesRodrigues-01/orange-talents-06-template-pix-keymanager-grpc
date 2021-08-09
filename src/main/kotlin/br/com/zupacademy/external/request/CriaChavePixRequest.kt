package br.com.zupacademy.external.request

import br.com.zupacademy.external.model.BankAccount
import br.com.zupacademy.external.model.Owner
import br.com.zupacademy.external.model.PixKeyType
import br.com.zupacademy.registra.model.ChavePix
import br.com.zupacademy.registra.model.ContaAssociada
import org.slf4j.LoggerFactory

data class CriaChavePixRequest(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
){
    companion object{

        private val logger = LoggerFactory.getLogger(CriaChavePixRequest::class.java)

        fun of(chave: ChavePix): CriaChavePixRequest{
            logger.info("Inicio da conversão de ChavePix para CriaChavePixBcbRequest")

            return CriaChavePixRequest(
                keyType = PixKeyType.by(chave.tipo),
                key = chave.chave,
                bankAccount = BankAccount(
                    participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
                    branch = chave.conta.agencia,
                    accountNumber = chave.conta.numeroDaConta,
                    accountType = BankAccount.AccountType.by(chave.tipoDeConta)
                ),
                owner = Owner(
                    type = Owner.OwnerType.NATURAL_PERSON,
                    name = chave.conta.nomeTitular,
                    taxIdNumber = chave.conta.cpfTitular
                )
            )
        }
    }
}