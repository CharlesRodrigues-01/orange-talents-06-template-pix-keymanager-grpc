package br.com.zupacademy.registra.validation

import br.com.zupacademy.registra.request.NovaChavePix
import io.micronaut.validation.validator.constraints.ConstraintValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "chave Pix inválida (\${validatedValue.tipo})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePix> {

    override fun isValid(
        value: NovaChavePix?,
        annotationMetadata: io.micronaut.core.annotation.AnnotationValue<ValidPixKey>,
        context: io.micronaut.validation.validator.constraints.ConstraintValidatorContext
    ): Boolean {
        if (value?.tipo == null) {
            return false
        }
        return value.tipo.valida(value.chave)
    }
}
