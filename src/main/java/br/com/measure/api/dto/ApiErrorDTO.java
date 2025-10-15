package br.com.measure.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiErrorDTO {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path; // A URL onde o erro ocorreu

}