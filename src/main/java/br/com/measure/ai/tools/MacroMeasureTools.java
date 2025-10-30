package br.com.measure.ai.tools;

import br.com.measure.api.dto.MedicaoAmostraResponseDTO;
import br.com.measure.api.dto.MedicoResponseDTO;
import br.com.measure.api.dto.PacienteResponseDTO;
import br.com.measure.domain.enums.StatusAmostra;
import br.com.measure.domain.service.MedicaoAmostraService;
import br.com.measure.domain.service.MedicoService;
import br.com.measure.domain.service.PacienteService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MacroMeasureTools {

    private final MedicaoAmostraService medicaoAmostraService;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;


    // --- FERRAMENTAS DE MEDIÇÃO (LISTAGEM) ---

    @Tool("""
        Busca e RETORNA A LISTA de medições de amostra com base em múltiplos filtros opcionais.
        Use esta ferramenta quando o usuário pedir para 'ver', 'listar' ou 'mostrar' as amostras.
        O agente DEVE fornecer pelo menos UM filtro.
        """)
    public List<MedicaoAmostraResponseDTO> buscarMedicoes(
            Long pacienteId,
            Long medicoId,
            String nomePaciente,
            String nomeMedico,
            String status,
            String codigoAmostra,
            Double volumeMin,
            Double volumeMax,
            String dataInicial,
            String dataFinal) {

        System.out.println(">>> Tool 'buscarMedicoes' chamada com parâmetros:");
        logParameters(pacienteId, medicoId, nomePaciente, nomeMedico, status, codigoAmostra, volumeMin, volumeMax, dataInicial, dataFinal);

        // Chama o metodo de lógica de stream refatorado e coleta a lista
        List<MedicaoAmostraResponseDTO> results = getFilteredMedicoesStream(
                pacienteId, medicoId, nomePaciente, nomeMedico, status,
                codigoAmostra, volumeMin, volumeMax, dataInicial, dataFinal
        ).toList();

        System.out.println(">>> Resultados encontrados: " + results.size());
        return results;
    }

    // --- FERRAMENTAS DE MEDIÇÃO (CONTAGEM) ---

    @Tool("""
        CONTA o número de medições de amostra com base em múltiplos filtros opcionais.
        Use esta ferramenta quando o usuário pedir a 'quantidade', 'número' ou 'total' de amostras.
        Retorna um número, não uma lista.
        """)
    public long contarMedicoes(
            Long pacienteId,
            Long medicoId,
            String nomePaciente,
            String nomeMedico,
            String status,
            String codigoAmostra,
            Double volumeMin,
            Double volumeMax,
            String dataInicial,
            String dataFinal) {

        System.out.println(">>> Tool 'contarMedicoes' chamada com parâmetros:");
        logParameters(pacienteId, medicoId, nomePaciente, nomeMedico, status, codigoAmostra, volumeMin, volumeMax, dataInicial, dataFinal);

        // Chama o metodo de lógica de stream refatorado e conta os resultados
        long count = getFilteredMedicoesStream(
                pacienteId, medicoId, nomePaciente, nomeMedico, status,
                codigoAmostra, volumeMin, volumeMax, dataInicial, dataFinal
        ).count();

        System.out.println(">>> Contagem encontrada: " + count);
        return count;
    }


    // --- LÓGICA DE FILTRO REATORADA (Privada) ---

    /**
     * Metodo central privado que aplica toda a lógica de filtro e retorna um Stream.
     * Usado tanto por buscarMedicoes() quanto por contarMedicoes() para evitar repetição de código (DRY).
     */
    private Stream<MedicaoAmostraResponseDTO> getFilteredMedicoesStream(
            Long pacienteId,
            Long medicoId,
            String nomePaciente,
            String nomeMedico,
            String status,
            String codigoAmostra,
            Double volumeMin,
            Double volumeMax,
            String dataInicial,
            String dataFinal) {

        // Obter a lista base (já ordenada)
        Stream<MedicaoAmostraResponseDTO> stream = medicaoAmostraService.findAll("dataRegistro", "desc").stream();

        // Aplicar filtros dinamicamente
        if (pacienteId != null) {
            stream = stream.filter(m -> m.getPacienteId() != null && m.getPacienteId().equals(pacienteId));
        }

        if (medicoId != null) {
            stream = stream.filter(m -> m.getMedicoId() != null && m.getMedicoId().equals(medicoId));
        }

        if (nomePaciente != null && !nomePaciente.isBlank()) {
            String normPaciente = normalize(nomePaciente);
            stream = stream.filter(m -> m.getPacienteNome() != null &&
                    normalize(m.getPacienteNome()).contains(normPaciente));
        }

        if (nomeMedico != null && !nomeMedico.isBlank()) {
            String normMedico = normalize(nomeMedico.replaceAll("^(Dr\\.|Dra\\.|Dr|Dra)\\s+", ""));
            stream = stream.filter(m -> m.getMedicoNome() != null &&
                    normalize(m.getMedicoNome()).contains(normMedico));
        }

        if (status != null && !status.isBlank()) {
            try {
                StatusAmostra statusEnum = StatusAmostra.valueOf(normalize(status).toUpperCase());
                stream = stream.filter(m -> m.getStatus() == statusEnum);
            } catch (IllegalArgumentException e) {
                System.err.println(">>> Erro de Tool: Status inválido: " + status);
            }
        }

        if (codigoAmostra != null && !codigoAmostra.isBlank()) {
            String normCodigo = normalize(codigoAmostra);
            stream = stream.filter(m -> m.getCodigoAmostra() != null &&
                    normalize(m.getCodigoAmostra()).equalsIgnoreCase(normCodigo));
        }

        if (volumeMin != null) {
            BigDecimal min = BigDecimal.valueOf(volumeMin);
            stream = stream.filter(m -> m.getVolume() != null && m.getVolume().compareTo(min) >= 0);
        }

        if (volumeMax != null) {
            BigDecimal max = BigDecimal.valueOf(volumeMax);
            stream = stream.filter(m -> m.getVolume() != null && m.getVolume().compareTo(max) <= 0);
        }

        LocalDate dtInicial = parseData(dataInicial);
        if (dtInicial != null) {
            stream = stream.filter(m -> m.getDataRegistro() != null &&
                    !m.getDataRegistro().toLocalDate().isBefore(dtInicial));
        }

        LocalDate dtFinal = parseData(dataFinal);
        if (dtFinal != null) {
            stream = stream.filter(m -> m.getDataRegistro() != null &&
                    !m.getDataRegistro().toLocalDate().isAfter(dtFinal));
        }

        return stream;
    }


    // --- FERRAMENTAS DE PACIENTE ---

    @Tool("Busca e retorna os dados de um paciente pelo seu NOME. A busca ignora acentos.")
    public PacienteResponseDTO buscarPacientePorNome(String nomePaciente) {
        System.out.println(">>> Tool chamada: buscarPacientePorNome com parâmetro: " + nomePaciente);
        String normalizedInput = normalize(nomePaciente);
        return pacienteService.findAll().stream()
                .filter(paciente -> paciente.getNomeCompleto() != null &&
                        normalize(paciente.getNomeCompleto()).contains(normalizedInput))
                .findFirst()
                .orElse(null);
    }

    @Tool("Busca e retorna os dados de um paciente pelo seu número de CPF. O CPF deve ser exato.")
    public PacienteResponseDTO buscarPacientePorCpf(String cpf) {
        System.out.println(">>> Tool chamada: buscarPacientePorCpf com parâmetro: " + cpf);

        // Adicionamos uma limpeza para remover pontuações comuns que o usuário possa digitar
        String cpfLimpo = cpf.replaceAll("[.-]", "");

        try {
            return pacienteService.findByCpf(cpfLimpo);
        } catch (Exception e) {
            System.err.println("Erro ao buscar por CPF: " + e.getMessage());
            return null;
        }
    }

    @Tool("Busca e retorna os dados de um paciente pelo seu ID numérico.")
    public PacienteResponseDTO buscarPacientePorId(Long pacienteId) {
        System.out.println(">>> Tool chamada: buscarPacientePorId com parâmetro: " + pacienteId);
        // O findById do service já retorna DTO e trata o 'not found'
        try {
            return pacienteService.findById(pacienteId);
        } catch (Exception e) {
            return null; // Retorna nulo se não encontrado
        }
    }

    @Tool("Retorna a LISTA COMPLETA de todos os pacientes cadastrados no sistema.")
    public List<PacienteResponseDTO> buscarTodosPacientes() {
        System.out.println(">>> Tool chamada: buscarTodosPacientes");
        return pacienteService.findAll();
    }


    // --- FERRAMENTAS DE MÉDICO ---

    @Tool("Busca e retorna os dados de um médico pelo seu NOME. Use para encontrar CRM ou ID. A busca ignora acentos e prefixos (Dr./Dra.).")
    public MedicoResponseDTO buscarMedicoPorNome(String nomeMedico) {
        System.out.println(">>> Tool chamada: buscarMedicoPorNome com parâmetro: " + nomeMedico);
        String cleanedName = nomeMedico.replaceAll("^(Dr\\.|Dra\\.|Dr|Dra)\\s+", "");
        String normalizedInput = normalize(cleanedName);
        return medicoService.findAll().stream()
                .filter(medico -> medico.getNomeCompleto() != null &&
                        normalize(medico.getNomeCompleto()).contains(normalizedInput))
                .findFirst()
                .orElse(null);
    }

    @Tool("Busca e retorna os dados de um médico pelo seu número de CRM. O CRM deve ser exato.")
    public MedicoResponseDTO buscarMedicoPorCrm(String crm) {
        System.out.println(">>> Tool chamada: buscarMedicoPorCrm com parâmetro: " + crm);

        // Limpeza simples para remover hífens ou espaços caso a IA os adicione
        String crmLimpo = crm.replaceAll("[ -]", "");

        try {
            return medicoService.findByCrm(crmLimpo);
        } catch (Exception e) {
            System.err.println("Erro ao buscar por CRM: " + e.getMessage());
            return null;
        }
    }

    @Tool("Busca e retorna os dados de um médico pelo seu ID numérico.")
    public MedicoResponseDTO buscarMedicoPorId(Long medicoId) {
        System.out.println(">>> Tool chamada: buscarMedicoPorId com parâmetro: " + medicoId);
        try {
            return medicoService.findById(medicoId);
        } catch (Exception e) {
            return null; // Retorna nulo se não encontrado
        }
    }

    @Tool("Retorna a LISTA COMPLETA de todos os médicos cadastrados no sistema.")
    public List<MedicoResponseDTO> buscarTodosMedicos() {
        System.out.println(">>> Tool chamada: buscarTodosMedicos");
        return medicoService.findAll();
    }


    // --- MÉTODOS AUXILIARES ---

    private String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .trim();
    }

    private LocalDate parseData(String data) {
        if (data == null || data.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            System.err.println(">>> Erro de Tool: Formato de data inválido recebido: " + data);
            return null;
        }
    }

    private void logParameters(Long pId, Long mId, String pNome, String mNome, String status, String codigo, Double vMin, Double vMax, String dIni, String dFim) {
        System.out.println("   pacienteId: " + pId);
        System.out.println("   medicoId: " + mId);
        System.out.println("   nomePaciente: " + pNome);
        System.out.println("   nomeMedico: " + mNome);
        System.out.println("   status: " + status);
        System.out.println("   codigoAmostra: " + codigo);
        System.out.println("   volumeMin: " + vMin);
        System.out.println("   volumeMax: " + vMax);
        System.out.println("   dataInicial: " + dIni);
        System.out.println("   dataFinal: " + dFim);
    }
}