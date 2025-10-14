package br.com.mensure.domain.service;

import br.com.mensure.api.dto.PacienteRequestDTO;
import br.com.mensure.api.dto.PacienteResponseDTO;
import br.com.mensure.api.exception.ResourceNotFoundException;
import br.com.mensure.domain.entity.Paciente;
import br.com.mensure.domain.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    @Transactional
    public PacienteResponseDTO create(PacienteRequestDTO requestDTO) {
        if (repository.findByCpf(requestDTO.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um paciente cadastrado com o CPF informado.");
        }
        Paciente novoPaciente = toEntity(requestDTO);
        Paciente pacienteSalvo = repository.save(novoPaciente);
        return toResponseDTO(pacienteSalvo);
    }

    public List<PacienteResponseDTO> findAll() {
        List<Paciente> pacientes = repository.findAll(Sort.by(Sort.Direction.ASC, "nomeCompleto"));
        return pacientes.stream().map(this::toResponseDTO).toList();
    }

    public PacienteResponseDTO findById(Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado para o ID: " + id));
        return toResponseDTO(paciente);
    }

    @Transactional
    public PacienteResponseDTO update(Long id, PacienteRequestDTO requestDTO) {
        Paciente pacienteExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado para o ID: " + id));

        repository.findByCpf(requestDTO.getCpf()).ifPresent(pacienteEncontrado -> {
            if (!pacienteEncontrado.getId().equals(id)) {
                throw new IllegalArgumentException("O CPF informado já está em uso por outro paciente.");
            }
        });

        updateEntityFromDTO(pacienteExistente, requestDTO);
        Paciente pacienteAtualizado = repository.save(pacienteExistente);
        return toResponseDTO(pacienteAtualizado);
    }

    @Transactional
    public void delete(Long id) {
        Paciente pacienteExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado para o ID: " + id));
        repository.delete(pacienteExistente);
    }


    // --- MÉTODOS DE MAPEAMENTO (MAPPERS) ---

    private Paciente toEntity(PacienteRequestDTO dto) {
        Paciente entity = new Paciente();
        entity.setNomeCompleto(dto.getNomeCompleto());
        entity.setCpf(dto.getCpf());
        entity.setDataNascimento(dto.getDataNascimento());
        entity.setGenero(dto.getGenero());
        return entity;
    }

    private PacienteResponseDTO toResponseDTO(Paciente entity) {
        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setCpf(entity.getCpf());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setGenero(entity.getGenero());
        return dto;
    }

    private void updateEntityFromDTO(Paciente entity, PacienteRequestDTO dto) {
        entity.setNomeCompleto(dto.getNomeCompleto());
        entity.setCpf(dto.getCpf());
        entity.setDataNascimento(dto.getDataNascimento());
        entity.setGenero(dto.getGenero());
    }
}