package br.com.mensure.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicoResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String crm;
}