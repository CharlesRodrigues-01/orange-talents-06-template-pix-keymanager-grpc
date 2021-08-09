package br.com.zupacademy.external.response

import br.com.zupacademy.external.model.BankAccount
import br.com.zupacademy.external.model.Owner
import br.com.zupacademy.external.model.PixKeyType
import java.time.LocalDateTime

data class CriaChavePixBcbResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)