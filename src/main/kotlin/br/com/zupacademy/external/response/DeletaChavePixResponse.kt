package br.com.zupacademy.external.response

import java.time.LocalDateTime

data class DeletaChavePixResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)