package br.com.zupacademy.registra.model

import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {

    UNKNOWN_CHAVE {
        override fun valida(chave: String?): Boolean {
            return false
        }
    },

    CPF{
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()){
                return false
            }
            if (!chave.matches("[0-9]+".toRegex())){
                return false
            }
            return CPFValidator().run {
                initialize(null)
                isValid(chave, null) }
        }
    },

    CELULAR{
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()){
                return false
            }
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },

    EMAIL{

        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()){
                return false
            }

        return chave.matches(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
                .toRegex())
        }
    },

    ALEATORIA{
        override fun valida(chave: String?) = chave.isNullOrBlank()
    };

    abstract fun valida(chave: String?): Boolean
}