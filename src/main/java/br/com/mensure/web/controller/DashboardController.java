package br.com.mensure.web.controller;

import br.com.mensure.api.dto.MedicaoAmostraRequestDTO;
import br.com.mensure.api.dto.MedicaoAmostraResponseDTO;
import br.com.mensure.domain.enums.StatusAmostra;
import br.com.mensure.domain.service.MedicaoAmostraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/novo")
    public String showCreateForm(Model model) {
        // Cria um objeto DTO vazio para ser preenchido pelo formulário.
        model.addAttribute("medicao", new MedicaoAmostraRequestDTO());
        // Disponibiliza os valores do Enum para o <select> no HTML.
        model.addAttribute("statusOptions", StatusAmostra.values());
        // Retorna o nome do novo arquivo HTML que vamos criar.
        return "create-form";
    }

    @PostMapping("/salvar")
    public String saveMedicao(@ModelAttribute("medicao") MedicaoAmostraRequestDTO requestDTO) {
        medicaoAmostraService.create(requestDTO);

        // Redireciona o usuário de volta para a página principal do dashboard.
        return "redirect:/dashboard";
    }

    @PostMapping("/excluir/{id}")
    public String deleteMedicao(@PathVariable Long id) {
        medicaoAmostraService.delete(id);
        // Redireciona de volta para o dashboard com a lista atualizada.
        return "redirect:/dashboard";
    }

    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        // Busca os dados existentes.
        MedicaoAmostraResponseDTO medicaoDTO = medicaoAmostraService.findById(id);

        // Adiciona os dados ao model para que o formulário seja preenchido.
        model.addAttribute("medicao", medicaoDTO);
        model.addAttribute("id", id); // Passa o ID separadamente para a action do form.

        // Adiciona as opções de status, como no formulário de criação.
        model.addAttribute("statusOptions", StatusAmostra.values());

        // Retorna o nome do arquivo HTML do formulário de edição.
        return "edit-form";
    }

    @PostMapping("/atualizar/{id}")
    public String updateMedicao(@PathVariable Long id, @ModelAttribute("medicao") MedicaoAmostraRequestDTO requestDTO) {
        medicaoAmostraService.update(id, requestDTO);
        return "redirect:/dashboard";
    }
}