package br.com.measure.api.dto;

import br.com.measure.domain.enums.Genero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PacienteResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataNascimento;
    private Genero genero;
}