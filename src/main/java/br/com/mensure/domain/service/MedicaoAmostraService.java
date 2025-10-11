package br.com.mensure.domain.service;

import br.com.mensure.api.dto.MedicaoAmostraRequestDTO;
import br.com.mensure.api.dto.MedicaoAmostraResponseDTO;
import br.com.mensure.domain.entity.MedicaoAmostra;
import br.com.mensure.domain.repository.MedicaoAmostraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicaoAmostraService {

    @Autowired
    private MedicaoAmostraRepository repository;

    @Transactional // Garante que o metodo execute como uma única transação no banco de dados.
    public MedicaoAmostraResponseDTO create(MedicaoAmostraRequestDTO requestDTO) {
        // Verificar se já existe uma amostra com o mesmo código.
        if (repository.findByCodigoAmostra(requestDTO.getCodigoAmostra()).isPresent()) {
            // Lançar uma exceção
            throw new IllegalArgumentException("Já existe uma medição com o código de amostra informado.");
        }

        // Converter o DTO de requisição para a nossa Entidade.
        MedicaoAmostra novaMedicao = toEntity(requestDTO);

        // Usar o repositório para salvar a nova entidade no banco de dados.
        MedicaoAmostra medicaoSalva = repository.save(novaMedicao);

        // Converter a Entidade salva para o DTO de resposta.
        return toResponseDTO(medicaoSalva);
    }

    // Métodos de Mapeamento (Mappers)
    private MedicaoAmostra toEntity(MedicaoAmostraRequestDTO dto) {
        MedicaoAmostra entity = new MedicaoAmostra();
        entity.setCodigoAmostra(dto.getCodigoAmostra());
        entity.setVolume(dto.getVolume());
        entity.setComprimento(dto.getComprimento());
        entity.setLargura(dto.getLargura());
        entity.setAltura(dto.getAltura());
        entity.setObservacoes(dto.getObservacoes());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    private MedicaoAmostraResponseDTO toResponseDTO(MedicaoAmostra entity) {
        MedicaoAmostraResponseDTO dto = new MedicaoAmostraResponseDTO();
        dto.setId(entity.getId());
        dto.setCodigoAmostra(entity.getCodigoAmostra());
        dto.setVolume(entity.getVolume());
        dto.setComprimento(entity.getComprimento());
        dto.setLargura(entity.getLargura());
        dto.setAltura(entity.getAltura());
        dto.setObservacoes(entity.getObservacoes());
        dto.setStatus(entity.getStatus());
        dto.setDataRegistro(entity.getDataRegistro());
        return dto;
    }
}