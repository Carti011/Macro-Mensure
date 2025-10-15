package br.com.measure.api.dto;

import br.com.measure.domain.enums.StatusAmostra;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class MedicaoAmostraResponseDTO {

    private Long id;
    private String codigoAmostra;
    private BigDecimal volume;
    private BigDecimal comprimento;
    private BigDecimal largura;
    private BigDecimal altura;
    private String observacoes;
    private StatusAmostra status;
    private LocalDateTime dataRegistro;

    private Long pacienteId;
    private String pacienteNome;

    private Long medicoId;
    private String medicoNome;
}