package br.com.mensure.domain.service;

import br.com.mensure.api.dto.MedicoRequestDTO;
import br.com.mensure.api.dto.MedicoResponseDTO;
import br.com.mensure.api.exception.ResourceNotFoundException;
import br.com.mensure.domain.entity.Medico;
import br.com.mensure.domain.repository.MedicoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository repository;

    @Transactional
    public MedicoResponseDTO create(MedicoRequestDTO requestDTO) {
        if (repository.findByCrm(requestDTO.getCrm()).isPresent()) {
            throw new IllegalArgumentException("Já existe um médico cadastrado com o CRM informado.");
        }
        Medico novoMedico = toEntity(requestDTO);
        Medico medicoSalvo = repository.save(novoMedico);
        return toResponseDTO(medicoSalvo);
    }

    public List<MedicoResponseDTO> findAll() {
        List<Medico> medicos = repository.findAll(Sort.by(Sort.Direction.ASC, "nomeCompleto"));
        return medicos.stream().map(this::toResponseDTO).toList();
    }

    public MedicoResponseDTO findById(Long id) {
        Medico medico = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado para o ID: " + id));
        return toResponseDTO(medico);
    }

    @Transactional
    public MedicoResponseDTO update(Long id, MedicoRequestDTO requestDTO) {
        Medico medicoExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado para o ID: " + id));

        repository.findByCrm(requestDTO.getCrm()).ifPresent(medicoEncontrado -> {
            if (!medicoEncontrado.getId().equals(id)) {
                throw new IllegalArgumentException("O CRM informado já está em uso por outro médico.");
            }
        });

        updateEntityFromDTO(medicoExistente, requestDTO);
        Medico medicoAtualizado = repository.save(medicoExistente);
        return toResponseDTO(medicoAtualizado);
    }

    @Transactional
    public void delete(Long id) {
        Medico medicoExistente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado para o ID: " + id));

        repository.delete(medicoExistente);
    }


    // --- MÉTODOS DE MAPEAMENTO (MAPPERS) ---

    private Medico toEntity(MedicoRequestDTO dto) {
        Medico entity = new Medico();
        entity.setNomeCompleto(dto.getNomeCompleto());
        entity.setCrm(dto.getCrm());
        return entity;
    }

    private MedicoResponseDTO toResponseDTO(Medico entity) {
        MedicoResponseDTO dto = new MedicoResponseDTO();
        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setCrm(entity.getCrm());
        return dto;
    }

    private void updateEntityFromDTO(Medico entity, MedicoRequestDTO dto) {
        entity.setNomeCompleto(dto.getNomeCompleto());
        entity.setCrm(dto.getCrm());
    }
}