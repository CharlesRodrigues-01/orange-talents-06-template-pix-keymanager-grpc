package br.com.zupacademy.external.model

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
) {
    enum class OwnerType{
        NATURAL_PERSON,
        LEGAL_PERSON
    }
}