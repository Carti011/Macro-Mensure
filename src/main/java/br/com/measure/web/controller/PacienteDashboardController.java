package br.com.measure.web.controller;

import br.com.measure.api.dto.PacienteRequestDTO;
import br.com.measure.api.dto.PacienteResponseDTO;
import br.com.measure.domain.enums.Genero;
import br.com.measure.domain.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pacientes")
public class PacienteDashboardController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public String showPacienteList(Model model) {
        model.addAttribute("listaDePacientes", pacienteService.findAll());
        return "pacientes";
    }

    @GetMapping("/novo")
    public String showPacienteCreateForm(Model model) {
        model.addAttribute("paciente", new PacienteResponseDTO());
        model.addAttribute("generoOptions", Genero.values());
        return "paciente-form";
    }

    @PostMapping("/salvar")
    public String savePaciente(@ModelAttribute("paciente") PacienteRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
        pacienteService.create(requestDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Paciente cadastrado com sucesso!");
        return "redirect:/pacientes";
    }

    @GetMapping("/editar/{id}")
    public String showPacienteEditForm(@PathVariable Long id, Model model) {
        PacienteResponseDTO pacienteDTO = pacienteService.findById(id);
        model.addAttribute("paciente", pacienteDTO);
        model.addAttribute("id", id);
        model.addAttribute("generoOptions", Genero.values());
        return "paciente-form";
    }

    @PostMapping("/atualizar/{id}")
    public String updatePaciente(@PathVariable Long id, @ModelAttribute("paciente") PacienteRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
        pacienteService.update(id, requestDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Paciente atualizado com sucesso!");
        return "redirect:/pacientes";
    }

    @PostMapping("/excluir/{id}")
    public String deletePaciente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pacienteService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Paciente excluído com sucesso!");
        return "redirect:/pacientes";
    }

    @ModelAttribute("activePage")
    public String getActivePage() {
        return "pacientes";
    }
}