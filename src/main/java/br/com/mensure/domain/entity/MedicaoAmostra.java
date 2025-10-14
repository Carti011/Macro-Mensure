package br.com.mensure.domain.entity;

import br.com.mensure.domain.enums.StatusAmostra;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEDICOES_AMOSTRAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicaoAmostra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Define o lado "muitos" do relacionamento.
    @JoinColumn(name = "paciente_id", nullable = false) // Especifica a coluna da chave estrangeira.
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id") // A coluna da FK.
    private Medico medico;

    @Column(nullable = false, unique = true, length = 50)
    private String codigoAmostra;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal volume;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal comprimento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal largura;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal altura;

    @Lob
    @Column
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAmostra status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataRegistro;
}