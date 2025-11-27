package com.GrupoIoT.iot_api.repository;

import com.GrupoIoT.iot_api.model.entity.Leitura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraRepository extends JpaRepository<Leitura, Long> {

  // Buscar leituras de um sensor
  Page<Leitura> findBySensorId(Long sensorId, Pageable pageable);

  // Buscar leituras com alerta
  Page<Leitura> findByAlerta(Boolean alerta, Pageable pageable);

  // Buscar leituras com alerta de um sensor específico
  Page<Leitura> findBySensorIdAndAlerta(Long sensorId, Boolean alerta, Pageable pageable);

  // Buscar leituras por período
  @Query("SELECT l FROM Leitura l WHERE l.dataHora BETWEEN :dataInicio AND :dataFim")
  Page<Leitura> findByPeriodo(
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim,
    Pageable pageable
  );

  // Buscar leituras de um sensor por período
  @Query("SELECT l FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  Page<Leitura> findBySensorIdAndPeriodo(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim,
    Pageable pageable
  );

  // Buscar últimas leituras de um sensor
  @Query("SELECT l FROM Leitura l WHERE l.sensor.id = :sensorId ORDER BY l.dataHora DESC")
  Page<Leitura> findUltimasLeiturasBySensorId(@Param("sensorId") Long sensorId, Pageable pageable);

  // Buscar leituras de um dispositivo
  @Query("SELECT l FROM Leitura l WHERE l.sensor.dispositivo.id = :dispositivoId")
  Page<Leitura> findByDispositivoId(@Param("dispositivoId") Long dispositivoId, Pageable pageable);

  // Estatísticas: Média de valores de um sensor em um período
  @Query("SELECT AVG(l.valor) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  Double calcularMedia(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );

  // Estatísticas: Valor mínimo
  @Query("SELECT MIN(l.valor) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  Double calcularMinimo(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );

  // Estatísticas: Valor máximo
  @Query("SELECT MAX(l.valor) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  Double calcularMaximo(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );

  // Contar alertas de um sensor em um período
  @Query("SELECT COUNT(l) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.alerta = true AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  long contarAlertas(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );

  // Contar leituras de um sensor em um período
  @Query("SELECT COUNT(l) FROM Leitura l WHERE l.sensor.id = :sensorId AND l.dataHora BETWEEN :dataInicio AND :dataFim")
  long contarLeituras(
    @Param("sensorId") Long sensorId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim
  );

  // Buscar com filtros combinados
  @Query("SELECT l FROM Leitura l WHERE " +
    "(:sensorId IS NULL OR l.sensor.id = :sensorId) AND " +
    "(:alerta IS NULL OR l.alerta = :alerta) AND " +
    "(:dataInicio IS NULL OR l.dataHora >= :dataInicio) AND " +
    "(:dataFim IS NULL OR l.dataHora <= :dataFim)")
  Page<Leitura> findWithFilters(
    @Param("sensorId") Long sensorId,
    @Param("alerta") Boolean alerta,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim,
    Pageable pageable
  );

  // Deletar leituras antigas (limpeza de dados)
  @Query("DELETE FROM Leitura l WHERE l.dataHora < :dataLimite")
  void deletarLeiturasAntigas(@Param("dataLimite") LocalDateTime dataLimite);

  // Buscar última leitura de um sensor
  @Query("SELECT l FROM Leitura l WHERE l.sensor.id = :sensorId ORDER BY l.dataHora DESC LIMIT 1")
  Leitura findUltimaLeituraBySensorId(@Param("sensorId") Long sensorId);
}
