package br.com.zupacademy.registra.model

import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(

    @field:NotNull
    @Column(nullable = false)
    val clientId: UUID,

    @field:NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipo: TipoDeChave,

    @field:NotBlank
    @Column(unique = true, nullable = false)
    val chave: String,

    @field:NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeConta: TipoDeConta,

    @field:NotNull
    @Embedded
    val conta: ContaAssociada
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()
}