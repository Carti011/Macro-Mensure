package br.com.mensure.api.controller;

import br.com.mensure.api.dto.MedicaoAmostraRequestDTO;
import br.com.mensure.api.dto.MedicaoAmostraResponseDTO;
import br.com.mensure.domain.service.MedicaoAmostraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<MedicaoAmostraResponseDTO>> findAll(
            @RequestParam(name = "sort", defaultValue = "id") String sortField,
            @RequestParam(name = "dir", defaultValue = "asc") String sortDir) {

        List<MedicaoAmostraResponseDTO> response = service.findAll(sortField, sortDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}") // Mapeia para GET. ID sendo meu Path
    public ResponseEntity<MedicaoAmostraResponseDTO> findById(@PathVariable Long id) {
        MedicaoAmostraResponseDTO response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}") // Mapeia para requisições (HTTP PUT)
    public ResponseEntity<MedicaoAmostraResponseDTO> update(@PathVariable Long id, @RequestBody @Valid MedicaoAmostraRequestDTO request) {
        MedicaoAmostraResponseDTO response = service.update(id, request);
        return ResponseEntity.ok(response); // Retorna 200 OK com o objeto atualizado no corpo.
    }

    @DeleteMapping("/{id}") // Mapeia para requisições (HTTP DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT) // Define o status de resposta padrão para 204.
    public void delete(@PathVariable Long id) {
        service.delete(id); // Apenas chama o serviço e não retorna nada no corpo.
    }
}

