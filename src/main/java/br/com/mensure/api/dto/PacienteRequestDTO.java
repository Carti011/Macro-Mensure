package br.com.mensure.api.dto;

import br.com.mensure.domain.enums.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PacienteRequestDTO {

    @NotBlank(message = "O nome completo não pode ser vazio.")
    @Size(max = 150, message = "O nome completo deve ter no máximo 150 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "O CPF não pode ser vazio.")
    @Size(min = 11, max = 11, message = "O CPF deve ter exatamente 11 caracteres.")
    private String cpf;

    @NotNull(message = "A data de nascimento não pode ser nula.")
    @Past(message = "A data de nascimento deve ser no passado.")
    private LocalDate dataNascimento;

    @NotNull(message = "O gênero não pode ser nulo.")
    private Genero genero;
}