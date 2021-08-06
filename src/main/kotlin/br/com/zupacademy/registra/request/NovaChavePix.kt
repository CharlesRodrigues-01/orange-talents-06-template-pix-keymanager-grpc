package br.com.zupacademy.registra.request

import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
import br.com.zupacademy.registra.model.ChavePix
import br.com.zupacademy.registra.model.ContaAssociada
import br.com.zupacademy.registra.model.TipoChave
import br.com.zupacademy.shared.validation.ValidPixKey
import br.com.zupacademy.shared.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
class NovaChavePix(
    @ValidUUID
    @field:NotBlank
    val clientId: String?,
    @field:NotNull
    val tipo: TipoChave?,
    @field:Size(max = 77)
    val chave: String,
    @field:NotNull
    val tipoDeConta: TipoDeConta?
) {
    private val logger = LoggerFactory.getLogger(NovaChavePix::class.java)

    fun toModel(conta: ContaAssociada): ChavePix {

        logger.info("Convertendo a classe DTO para a classe de dompinio da chave Pix")

        return ChavePix(
            clientId = UUID.fromString(this.clientId),
            tipo = TipoDeChave.valueOf(this.tipo!!.name),
            chave = if(this.tipo == TipoChave.ALEATORIA) UUID.randomUUID().toString() else this.chave,
            tipoDeConta = TipoDeConta.valueOf(this.tipoDeConta!!.name),
            conta = conta
        )
    }
}