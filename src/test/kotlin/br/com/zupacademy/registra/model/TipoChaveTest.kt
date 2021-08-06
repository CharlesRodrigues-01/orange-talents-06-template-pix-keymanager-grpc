package br.com.zupacademy.registra.model

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest{

    @Nested
    inner class UNKNOWN_CHAVE{

        @Test
        fun `deve retornar invalida`(){
            with(TipoChave.UNKNOWN_CHAVE){
                assertFalse(valida("qualquer coisa"))
            }
        }
    }

    @Nested
    inner class CPF{

        @Test
        fun `deve ser valido quando cpf for valido`(){
            with(TipoChave.CPF) {
                assertTrue(valida("00000000000"))
            }
        }

        @Test
        fun `nao deve ser valido quando cpf for invalido`(){
            with(TipoChave.CPF){
                assertFalse(valida("000000"))
            }
        }

        @Test
        fun `nao deve ser valido quando cpf nao for passado`(){
            with(TipoChave.CPF){
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido quando cpf possuir letras`(){
            with(TipoChave.CPF){
                assertFalse(valida("00000Aa0000"))
            }
        }
    }

    @Nested
    inner class CELULAR{

        @Test
        fun `deve ser valido quando celular for valido`(){
            with(TipoChave.CELULAR){
                assertTrue(valida("+5585988714077"))
            }
        }

        @Test
        fun `nao deve ser valido quando celular nao for valido`(){
            with(TipoChave.CELULAR) {
                assertFalse(valida("5585988714077"))
                assertFalse(valida("+558598871407a"))
            }
        }

        @Test
        fun `nao deve ser valido quando celular nao for passado`(){
            with(TipoChave.CELULAR){
                assertFalse(valida(""))
                assertFalse(valida(null))
            }
        }

    }

    @Nested
    inner class EMAIL{

        @Test
        fun `deve ser valido se o email for valido`(){
            with(TipoChave.EMAIL){
                assertTrue(valida("teste@zup.com.br"))
            }
        }

        @Test
        fun `nao deve ser valido quando email nao for valido`(){
            with(TipoChave.EMAIL) {
                assertFalse(valida("testezup.com.br"))
                assertFalse(valida("teste@zup.com."))
            }
        }

        @Test
        fun `nao deve ser valido quando email nao for passado`(){
            with(TipoChave.EMAIL){
                assertFalse(valida(""))
                assertFalse(valida(null))
            }
        }
    }

    @Nested
    inner class ALEATORIA{

        @Test
        fun `deve ser valido quando chave for nula ou vazia`(){
            with(TipoChave.ALEATORIA){
                assertTrue(valida(null))
                assertTrue((valida("")))
            }
        }

        @Test
        fun `nao deve ser valido quando a chave tiver um valor`(){
            with(TipoChave.ALEATORIA){
                assertFalse(valida("teste"))
            }
        }
    }

}