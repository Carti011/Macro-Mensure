package br.com.measure.domain.service;

import br.com.measure.api.dto.MedicaoAmostraRequestDTO;
import br.com.measure.api.dto.MedicaoAmostraResponseDTO;
import br.com.measure.api.exception.ResourceNotFoundException;
import br.com.measure.domain.entity.MedicaoAmostra;
import br.com.measure.domain.entity.Medico;
import br.com.measure.domain.entity.Paciente;
import br.com.measure.domain.repository.MedicaoAmostraRepository;
import br.com.measure.domain.repository.MedicoRepository;
import br.com.measure.domain.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class MedicaoAmostraService {

    @Autowired
    private MedicaoAmostraRepository repository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

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
        // Busca a entidade Paciente pelo ID recebido no DTO.
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado para o ID: " + dto.getPacienteId()));

        MedicaoAmostra entity = new MedicaoAmostra();

        // Associa o paciente encontrado à nova medição.
        entity.setPaciente(paciente);

        if (dto.getMedicoId() != null) {
            Medico medico = medicoRepository.findById(dto.getMedicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado para o ID: " + dto.getMedicoId()));
            entity.setMedico(medico);
        }

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

        // Verifica se o paciente não é nulo antes de tentar pegar o nome
        if (entity.getPaciente() != null) {
            dto.setPacienteId(entity.getPaciente().getId());
            dto.setPacienteNome(entity.getPaciente().getNomeCompleto());
        }

        // --- LÓGICA PARA O MÉDICO ---
        if (entity.getMedico() != null) {
            dto.setMedicoId(entity.getMedico().getId());
            dto.setMedicoNome(entity.getMedico().getNomeCompleto());
        }
        return dto;
    }

    public List<MedicaoAmostraResponseDTO> findAll(String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ?
                Sort.by(sortField).descending() :
                Sort.by(sortField).ascending();

        // Busca todas as entidades do banco, aplicando a ordenação.
        List<MedicaoAmostra> medicoes = repository.findAll(sort);

        return medicoes.stream()
                .map(this::toResponseDTO)
                .toList();
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
        // Busca a nova entidade Paciente (caso o usuário tenha alterado no formulário).
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado para o ID: " + dto.getPacienteId()));

        // Associa o novo paciente à medição existente.
        entity.setPaciente(paciente);

        if (dto.getMedicoId() != null) {
            Medico medico = medicoRepository.findById(dto.getMedicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado para o ID: " + dto.getMedicoId()));
            entity.setMedico(medico);
        } else {
            entity.setMedico(null); // Permite desassociar um médico
        }

        entity.setCodigoAmostra(dto.getCodigoAmostra());
        entity.setVolume(dto.getVolume());
        entity.setComprimento(dto.getComprimento());
        entity.setLargura(dto.getLargura());
        entity.setAltura(dto.getAltura());
        entity.setObservacoes(dto.getObservacoes());
        entity.setStatus(dto.getStatus());
    }

    @Transactional
    public void delete(Long id) {
        // Busca a medição para garantir que ela existe antes de tentar deletar.
        // Se não existir, o .orElseThrow() cuidará de lançar nossa exceção 404.
        MedicaoAmostra medicaoExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medição não encontrada para o ID: " + id));

        // Se a medição foi encontrada, o repositório a deleta.
        repository.delete(medicaoExistente);
    }
}