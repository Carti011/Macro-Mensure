package br.com.mensure.api.controller;

import br.com.mensure.api.dto.MedicaoAmostraRequestDTO;
import br.com.mensure.api.dto.MedicaoAmostraResponseDTO;
import br.com.mensure.domain.service.MedicaoAmostraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Combina @Controller e @ResponseBody.
@RequestMapping("/api/medicoes") // Define o prefixo da URL para todos os métodos deste controller.
public class MedicaoAmostraController {

    @Autowired // Injeta a nossa camada de serviço.
    private MedicaoAmostraService service;

    @PostMapping // Mapeia este metodo para requisições (HTTP POST)
    public ResponseEntity<MedicaoAmostraResponseDTO> create(@RequestBody @Valid MedicaoAmostraRequestDTO request) {
        MedicaoAmostraResponseDTO response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 (Created).
    }
}

