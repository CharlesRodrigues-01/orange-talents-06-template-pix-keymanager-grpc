package br.com.zupacademy.carrega.response

import br.com.zupacademy.CarregaChavePixResponse
import br.com.zupacademy.TipoDeChave
import br.com.zupacademy.TipoDeConta
import com.google.protobuf.Timestamp
import java.time.ZoneId

class CarregaChavePixResponseConverter {

    fun converter(chaveInfo: ChavePixInfo): CarregaChavePixResponse {
        return CarregaChavePixResponse
            .newBuilder()
            .setClientId(chaveInfo.clientId?.toString() ?: "")
            .setPixId(chaveInfo.pixId?.toString() ?: "")
            .setChave(CarregaChavePixResponse
                .ChavePix
                .newBuilder()
                .setTipo(TipoDeChave.valueOf(chaveInfo.tipo.name))
                .setChave(chaveInfo.chave)
                .setConta(CarregaChavePixResponse
                    .ChavePix
                    .ContaInfo
                    .newBuilder()
                    .setTipo(TipoDeConta.valueOf(chaveInfo.tipoDeConta.name))
                    .setInstituicao(chaveInfo.conta.instituicao)
                    .setNomeDoTitular(chaveInfo.conta.nomeTitular)
                    .setCpfDoTitular(chaveInfo.conta.cpfTitular)
                    .setAgencia(chaveInfo.conta.agencia)
                    .setNumeroDaConta(chaveInfo.conta.numeroDaConta)
                    .build())
                .setCriadaEm(chaveInfo.registradaEm.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .build())
            .build()
    }
}