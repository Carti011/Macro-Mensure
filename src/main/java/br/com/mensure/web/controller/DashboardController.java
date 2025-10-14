package br.com.mensure.web.controller;

import br.com.mensure.api.dto.MedicaoAmostraRequestDTO;
import br.com.mensure.api.dto.MedicaoAmostraResponseDTO;
import br.com.mensure.domain.enums.StatusAmostra;
import br.com.mensure.domain.service.MedicaoAmostraService;
import br.com.mensure.domain.service.MedicoService;
import br.com.mensure.domain.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private MedicaoAmostraService medicaoAmostraService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public String showDashboard(Model model,
                                @RequestParam(defaultValue = "dataRegistro") String sort,
                                @RequestParam(defaultValue = "desc") String dir) {
        List<MedicaoAmostraResponseDTO> medicoes = medicaoAmostraService.findAll(sort, dir);
        model.addAttribute("listaDeMedicoes", medicoes);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDir", dir);
        model.addAttribute("reverseSortDir", dir.equals("asc") ? "desc" : "asc");
        return "dashboard";
    }

    @GetMapping("/novo")
    public String showCreateForm(Model model) {
        model.addAttribute("medicao", new MedicaoAmostraRequestDTO());
        model.addAttribute("statusOptions", StatusAmostra.values());
        model.addAttribute("listaDePacientes", pacienteService.findAll());
        model.addAttribute("listaDeMedicos", medicoService.findAll());
        return "create-form";
    }

    @PostMapping("/salvar")
    public String saveMedicao(@ModelAttribute("medicao") MedicaoAmostraRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
        medicaoAmostraService.create(requestDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Medição registrada com sucesso!");
        return "redirect:/dashboard";
    }

    @PostMapping("/excluir/{id}")
    public String deleteMedicao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        medicaoAmostraService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Registro excluído com sucesso!");
        return "redirect:/dashboard";
    }

    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MedicaoAmostraResponseDTO medicaoDTO = medicaoAmostraService.findById(id);

        model.addAttribute("medicao", medicaoDTO);
        model.addAttribute("id", id);
        model.addAttribute("statusOptions", StatusAmostra.values());
        model.addAttribute("listaDePacientes", pacienteService.findAll());
        model.addAttribute("listaDeMedicos", medicoService.findAll());

        return "edit-form";
    }

    @PostMapping("/atualizar/{id}")
    public String updateMedicao(@PathVariable Long id, @ModelAttribute("medicao") MedicaoAmostraRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
        medicaoAmostraService.update(id, requestDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Medição atualizada com sucesso!");
        return "redirect:/dashboard";
    }

    @GetMapping("/detalhes/{id}")
    public String showDetalhesMedicao(@PathVariable Long id, Model model) {
        MedicaoAmostraResponseDTO medicaoDTO = medicaoAmostraService.findById(id);
        model.addAttribute("medicao", medicaoDTO);

        return "detalhes-medicao";
    }
}