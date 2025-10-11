package br.com.mensure.domain.service;

import br.com.mensure.api.dto.MedicaoAmostraRequestDTO;
import br.com.mensure.api.dto.MedicaoAmostraResponseDTO;
import br.com.mensure.api.exception.ResourceNotFoundException;
import br.com.mensure.domain.entity.MedicaoAmostra;
import br.com.mensure.domain.repository.MedicaoAmostraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<MedicaoAmostraResponseDTO> findAll() {
        // Busca todas as entidades do banco.
        List<MedicaoAmostra> medicoes = repository.findAll();

        // Converte a lista de Entidades para uma lista de DTOs de Resposta.
        return medicoes.stream()
                .map(this::toResponseDTO) // Para cada item da lista, chama o metodo de conversão dto.
                .toList(); // Coleta os resultados em uma nova lista.
    }

    public MedicaoAmostraResponseDTO findById(Long id) {
        // Busca uma entidade pelo ID. O findById retorna um Optional.
        // Se não encontrar, lança uma exceção.
        MedicaoAmostra medicao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medição não encontrada para o ID: " + id));
        return toResponseDTO(medicao);
    }

    @Transactional
    public MedicaoAmostraResponseDTO update(Long id, MedicaoAmostraRequestDTO requestDTO) {
        // Busca a medição existente ou lança uma ResourceNotFoundException.
        MedicaoAmostra medicaoExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medição não encontrada para o ID: " + id));

        // Garante que o código da amostra não seja alterado para um que já está em uso por OUTRA medição.
        repository.findByCodigoAmostra(requestDTO.getCodigoAmostra())
                .ifPresent(medicaoEncontrada -> {
                    if (!medicaoEncontrada.getId().equals(id)) {
                        throw new IllegalArgumentException("O código de amostra '" + requestDTO.getCodigoAmostra() + "' já está em uso por outra medição.");
                    }
                });

        // Atualiza os campos da entidade existente com os dados do DTO.
        updateEntityFromDTO(medicaoExistente, requestDTO);

        // Salva a entidade atualizada. O JPA/Hibernate fará um UPDATE
        MedicaoAmostra medicaoAtualizada = repository.save(medicaoExistente);

        // Retorna o DTO de resposta com os dados atualizados.
        return toResponseDTO(medicaoAtualizada);
    }

    // Metodo auxiliar para manter o código de atualização organizado
    private void updateEntityFromDTO(MedicaoAmostra entity, MedicaoAmostraRequestDTO dto) {
        entity.setCodigoAmostra(dto.getCodigoAmostra());
        entity.setVolume(dto.getVolume());
        entity.setComprimento(dto.getComprimento());
        entity.setLargura(dto.getLargura());
        entity.setAltura(dto.getAltura());
        entity.setObservacoes(dto.getObservacoes());
        entity.setStatus(dto.getStatus());
    }
}