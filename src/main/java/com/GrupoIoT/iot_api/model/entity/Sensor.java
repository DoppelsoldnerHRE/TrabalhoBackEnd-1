package com.GrupoIoT.iot_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sensores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nome;

  @Column(nullable = false, length = 50)
  private String tipoSensor;

  @Column(nullable = false, length = 20)
  private String unidadeMedida;

  @Column
  private Double limiteMinimo;

  @Column
  private Double limiteMaximo;

  @Column(nullable = false)
  private Boolean ativo = true;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime ultimaAtualizacao;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dispositivo_id", nullable = false)
  private Dispositivo dispositivo;

  @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Leitura> leituras = new ArrayList<>();


  public boolean isForaDosLimites(Double valor) {
    if (limiteMinimo != null && valor < limiteMinimo) {
      return true;
    }
    if (limiteMaximo != null && valor > limiteMaximo) {
      return true;
    }
    return false;
  }
}
