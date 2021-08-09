package br.com.zupacademy.external.request

import br.com.zupacademy.registra.model.ContaAssociada

data class DeletaChavePixRequest(
    val key: String,
    val participant: String = ContaAssociada.ITAU_UNIBANCO_ISPB
)