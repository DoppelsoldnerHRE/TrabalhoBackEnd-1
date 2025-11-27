package com.GrupoIoT.iot_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leituras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leitura {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Double valor;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime dataHora;

  @Column(nullable = false)
  private Boolean alerta = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sensor_id", nullable = false)
  private Sensor sensor;


  @PrePersist
  public void verificarAlerta() {
    if (sensor != null) {
      this.alerta = sensor.isForaDosLimites(this.valor);
    }
  }
}
