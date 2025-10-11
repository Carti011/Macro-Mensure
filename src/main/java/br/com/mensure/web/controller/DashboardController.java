package br.com.mensure.web.controller;

import br.com.mensure.api.dto.MedicaoAmostraResponseDTO;
import br.com.mensure.domain.service.MedicaoAmostraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller // Anotação para controllers que retornam views (páginas HTML).
@RequestMapping("/dashboard") // URL base
public class DashboardController {

    @Autowired
    private MedicaoAmostraService medicaoAmostraService;

    @GetMapping // Responde a requisições GET em http://localhost:8080/dashboard
    public String showDashboard(Model model) {
        // Busca a lista de medições usando o mesmo serviço da API.
        List<MedicaoAmostraResponseDTO> medicoes = medicaoAmostraService.findAll();

        // Adiciona a lista de medições ao "Model".
        model.addAttribute("listaDeMedicoes", medicoes);

        // Retorna o nome do arquivo HTML que deve ser renderizado.
        return "dashboard";
    }
}