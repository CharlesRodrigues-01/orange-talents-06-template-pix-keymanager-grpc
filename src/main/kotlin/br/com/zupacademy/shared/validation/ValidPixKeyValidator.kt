package br.com.zupacademy.shared.validation

import br.com.zupacademy.registra.request.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePix> {
    private val logger = LoggerFactory.getLogger(ValidPixKeyValidator::class.java)

    override fun isValid(
        value: NovaChavePix?,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext
    ): Boolean {
        logger.info("Inicio da validação")

        if (value?.tipo == null) {
            return false
        }
        return value.tipo.valida(value.chave)
    }
}