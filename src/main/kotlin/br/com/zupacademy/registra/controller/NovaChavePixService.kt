package br.com.zupacademy.registra.controller

import br.com.zupacademy.registra.exception.ChavePixExistenteException
import br.com.zupacademy.registra.externo.ContasDeClientesItau
import br.com.zupacademy.registra.model.ChavePix
import br.com.zupacademy.registra.repository.ChavePixRepository
import br.com.zupacademy.registra.request.NovaChavePix
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(@Inject val repository: ChavePixRepository,
                          @Inject val itauClient: ContasDeClientesItau
) {

    private val logger = LoggerFactory.getLogger(NovaChavePixService::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePix) : ChavePix  {

        logger.info("Verifica se a chave existe no sistema")
        if (repository.existsByChave(novaChave.chave)) {

            logger.warn("Chave existente, lançando exceção")
            throw ChavePixExistenteException("Esta chave Pix ja existe")
        }

        logger.info("Buscando os dados da conta no ERP do Itaú")
        val response = itauClient.buscaContaPorTipo(novaChave.clientId!!, novaChave.tipoDeConta!!.name)
        val conta = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado!")

        logger.info("salvando no banco de dados")
        val chave = novaChave.toModel(conta)
        repository.save(chave)

        return chave
    }
}