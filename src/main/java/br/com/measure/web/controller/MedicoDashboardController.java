package br.com.measure.web.controller;

import br.com.measure.api.dto.MedicoRequestDTO;
import br.com.measure.api.dto.MedicoResponseDTO;
import br.com.measure.domain.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medicos")
public class MedicoDashboardController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public String showMedicoList(Model model) {
        model.addAttribute("listaDeMedicos", medicoService.findAll());
        return "medicos";
    }

    @GetMapping("/novo")
    public String showMedicoCreateForm(Model model) {
        model.addAttribute("medico", new MedicoResponseDTO());
        return "medico-form";
    }

    @PostMapping("/salvar")
    public String saveMedico(@ModelAttribute("medico") MedicoRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
        medicoService.create(requestDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Médico cadastrado com sucesso!");
        return "redirect:/medicos";
    }

    @GetMapping("/editar/{id}")
    public String showMedicoEditForm(@PathVariable Long id, Model model) {
        MedicoResponseDTO medicoDTO = medicoService.findById(id);
        model.addAttribute("medico", medicoDTO);
        model.addAttribute("id", id);
        return "medico-form";
    }

    @PostMapping("/atualizar/{id}")
    public String updateMedico(@PathVariable Long id, @ModelAttribute("medico") MedicoRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
        medicoService.update(id, requestDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Médico atualizado com sucesso!");
        return "redirect:/medicos";
    }

    @PostMapping("/excluir/{id}")
    public String deleteMedico(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        medicoService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Médico excluído com sucesso!");
        return "redirect:/medicos";
    }

    @ModelAttribute("activePage")
    public String getActivePage() {
        return "medicos";
    }
}