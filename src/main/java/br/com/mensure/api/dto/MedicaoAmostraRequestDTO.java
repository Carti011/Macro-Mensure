package br.com.mensure.api.dto;

import br.com.mensure.domain.enums.StatusAmostra;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter // Importa todos os Getters
@Setter // Importa todos os Setters
public class MedicaoAmostraRequestDTO {

    @NotBlank(message = "O código da amostra não pode ser vazio ou nulo.")
    @Size(max = 50, message = "O código da amostra deve ter no máximo 50 caracteres.")
    private String codigoAmostra;

    @NotNull(message = "O volume não pode ser nulo.")
    @Positive(message = "O volume deve ser um valor positivo.")
    private BigDecimal volume;

    @NotNull(message = "O comprimento não pode ser nulo.")
    @Positive(message = "O comprimento deve ser um valor positivo.")
    private BigDecimal comprimento;

    @NotNull(message = "A largura não pode ser nula.")
    @Positive(message = "A largura deve ser um valor positivo.")
    private BigDecimal largura;

    @NotNull(message = "A altura não pode ser nula.")
    @Positive(message = "A altura deve ser um valor positivo.")
    private BigDecimal altura;

    private String observacoes;

    @NotNull(message = "O status não pode ser nulo.")
    private StatusAmostra status;

    @NotNull(message = "O paciente deve ser informado.")
    private Long pacienteId;

    private Long medicoId;
}