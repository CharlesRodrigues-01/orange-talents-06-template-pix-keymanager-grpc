package br.com.zupacademy.registra.repository

import br.com.zupacademy.carrega.response.ChavePixInfo
import br.com.zupacademy.registra.model.ChavePix
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID> {

    fun existsByChave(chave: String?): Boolean

    @Query("SELECT a FROM ChavePix a WHERE a.clientId = :uuidClientId "
            + "AND a.id = :uuidPixId")
    fun findByIdAndClientId(uuidPixId: UUID?, uuidClientId: UUID?): Optional<ChavePix>

    fun findByChave(chave: String): Optional<ChavePix>
    fun findAllByClientId(clientId: UUID): Iterable<ChavePix>
}