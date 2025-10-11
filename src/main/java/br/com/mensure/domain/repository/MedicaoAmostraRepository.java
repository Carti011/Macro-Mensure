package br.com.mensure.domain.repository;

import br.com.mensure.domain.entity.MedicaoAmostra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para a entidade MedicaoAmostra.
 * Estendendo de JpaRepository.
 */
@Repository
public interface MedicaoAmostraRepository extends JpaRepository<MedicaoAmostra, Long> {

    /**
     * Busca uma medição de amostra pelo seu código único.
     *
     * @param codigoAmostra O código único da amostra a ser buscada.
     * @return um Optional contendo a MedicaoAmostra se encontrada, ou um Optional vazio caso contrário.
     */
    Optional<MedicaoAmostra> findByCodigoAmostra(String codigoAmostra);
}