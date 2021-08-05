package br.com.zupacademy.registra.response

import br.com.zupacademy.registra.model.ContaAssociada
import org.slf4j.LoggerFactory

data class DadosContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    private val logger = LoggerFactory.getLogger(DadosContaResponse::class.java)

    fun toModel() : ContaAssociada {
        logger.info("Convertendo dados de resposta do cliente externo para conta associada a chave pix")

        return ContaAssociada(
            instituicao = this.instituicao.nome,
            nomeTitular = this.titular.nome,
            cpfTitular = this.titular.cpf,
            agencia = this.agencia,
            numeroDaConta = this.numero
        )
    }
}

data class TitularResponse(val nome: String, val cpf: String){}

data class InstituicaoResponse(val nome: String, val ispb: String){}