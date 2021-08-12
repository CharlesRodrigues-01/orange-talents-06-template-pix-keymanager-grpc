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

    @field:NotNull(message ="Dados inválidos")
    @Column(nullable = false)
    val clientId: UUID,

    @field:NotNull(message ="Dados inválidos")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipo: TipoDeChave,

    @field:NotBlank(message ="Dados inválidos")
    @Column(unique = true, nullable = false)
    var chave: String,

    @field:NotNull(message ="Dados inválidos")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeConta: TipoDeConta,

    @field:NotNull(message ="Dados inválidos")
    @Embedded
    val conta: ContaAssociada
) {
    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()

    fun pertenceAoCliente(clientId: UUID) = this.clientId.equals(clientId)

    fun isAleatoria(): Boolean {
        return tipo == TipoDeChave.ALEATORIA
    }

    fun atualiza(chave: String): Boolean{
        if (isAleatoria()){
            this.chave = chave
            return true
        }
        return false
    }
}