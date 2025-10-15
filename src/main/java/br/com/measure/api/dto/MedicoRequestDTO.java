package br.com.measure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicoRequestDTO {

    @NotBlank(message = "O nome completo não pode ser vazio.")
    @Size(max = 150, message = "O nome completo deve ter no máximo 150 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "O CRM não pode ser vazio.")
    @Size(max = 20, message = "O CRM deve ter no máximo 20 caracteres.")
    private String crm;
}