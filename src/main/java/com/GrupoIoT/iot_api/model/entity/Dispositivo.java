package com.GrupoIoT.iot_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispositivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dispositivo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nome;

  @Column(nullable = false, length = 100)
  private String tipo;

  @Column(nullable = false, length = 200)
  private String localizacao;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private StatusDispositivo status = StatusDispositivo.ATIVO;

  @Column(nullable = false, unique = true, length = 17)
  private String enderecoMac;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime dataCadastro;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime ultimaAtualizacao;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @OneToMany(mappedBy = "dispositivo", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Sensor> sensores = new ArrayList<>();

  // Enum para Status
  public enum StatusDispositivo {
    ATIVO,
    INATIVO,
    MANUTENCAO
  }
}
