package com.GrupoIoT.iot_api.service;

import com.GrupoIoT.iot_api.dto.request.SensorRequestDTO;
import com.GrupoIoT.iot_api.dto.response.SensorResponseDTO;
import com.GrupoIoT.iot_api.dto.response.InativoResponseDTO;
import com.GrupoIoT.iot_api.model.entity.Dispositivo;
import com.GrupoIoT.iot_api.model.entity.Sensor;
import com.GrupoIoT.iot_api.repository.DispositivoRepository;
import com.GrupoIoT.iot_api.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class SensorService {

  private final SensorRepository sensorRepository;
  private final DispositivoRepository dispositivoRepository;

  @Autowired
  public SensorService(SensorRepository sensorRepository,
                       DispositivoRepository dispositivoRepository) {
    this.sensorRepository = sensorRepository;
    this.dispositivoRepository = dispositivoRepository;
  }

  // CREATE
  @Transactional
  public SensorResponseDTO create(SensorRequestDTO dto) {
    // Buscar dispositivo
    Dispositivo dispositivo = dispositivoRepository.findById(dto.getDispositivoId())
      .orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado"));

    // Validar limites
    if (dto.getLimiteMinimo() != null && dto.getLimiteMaximo() != null) {
      if (dto.getLimiteMinimo() >= dto.getLimiteMaximo()) {
        throw new IllegalArgumentException("Limite mínimo deve ser menor que o máximo");
      }
    }

    Sensor sensor = new Sensor();
    sensor.setNome(dto.getNome());
    sensor.setTipoSensor(dto.getTipoSensor());
    sensor.setUnidadeMedida(dto.getUnidadeMedida());
    sensor.setLimiteMinimo(dto.getLimiteMinimo());
    sensor.setLimiteMaximo(dto.getLimiteMaximo());
    sensor.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
    sensor.setDispositivo(dispositivo);

    Sensor saved = sensorRepository.save(sensor);
    return toResponseDTO(saved);
  }

  // READ ONE
  @Transactional(readOnly = true)
  public SensorResponseDTO getOne(Long id) {
    Sensor sensor = sensorRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Sensor não encontrado"));
    return toResponseDTO(sensor);
  }

  // READ ALL
  @Transactional(readOnly = true)
  public Page<SensorResponseDTO> getAll(Pageable pageable) {
    return sensorRepository.findAll(pageable)
      .map(this::toResponseDTO);
  }

  // READ ALL com filtros
  @Transactional(readOnly = true)
  public Page<SensorResponseDTO> getAllWithFilters(
    String nome,
    String tipoSensor,
    Boolean ativo,
    Long dispositivoId,
    Pageable pageable) {

    return sensorRepository.findWithFilters(nome, tipoSensor, ativo, dispositivoId, pageable)
      .map(this::toResponseDTO);
  }

  // READ por dispositivo
  @Transactional(readOnly = true)
  public Page<SensorResponseDTO> getAllByDispositivo(Long dispositivoId, Pageable pageable) {
    return sensorRepository.findByDispositivoId(dispositivoId, pageable)
      .map(this::toResponseDTO);
  }

  // 🎯 CARTA-DESAFIO: Listar Inativos (7+ dias sem atualização)
  @Transactional(readOnly = true)
  public Page<InativoResponseDTO> getInativos(Pageable pageable) {
    LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);
    return sensorRepository.findInativos(dataLimite, pageable)
      .map(this::toInativoDTO);
  }

  // 🎯 CARTA-DESAFIO: Contar inativos
  @Transactional(readOnly = true)
  public long countInativos() {
    LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);
    return sensorRepository.countInativos(dataLimite);
  }

  // UPDATE
  @Transactional
  public SensorResponseDTO update(Long id, SensorRequestDTO dto) {
    Sensor sensor = sensorRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Sensor não encontrado"));

    // Validar limites
    if (dto.getLimiteMinimo() != null && dto.getLimiteMaximo() != null) {
      if (dto.getLimiteMinimo() >= dto.getLimiteMaximo()) {
        throw new IllegalArgumentException("Limite mínimo deve ser menor que o máximo");
      }
    }

    // Validar dispositivo (se mudou)
    if (!sensor.getDispositivo().getId().equals(dto.getDispositivoId())) {
      Dispositivo novoDispositivo = dispositivoRepository.findById(dto.getDispositivoId())
        .orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado"));
      sensor.setDispositivo(novoDispositivo);
    }

    sensor.setNome(dto.getNome());
    sensor.setTipoSensor(dto.getTipoSensor());
    sensor.setUnidadeMedida(dto.getUnidadeMedida());
    sensor.setLimiteMinimo(dto.getLimiteMinimo());
    sensor.setLimiteMaximo(dto.getLimiteMaximo());
    sensor.setAtivo(dto.getAtivo());
    // ultimaAtualizacao é atualizada automaticamente pelo @UpdateTimestamp

    Sensor updated = sensorRepository.save(sensor);
    return toResponseDTO(updated);
  }

  // DELETE
  @Transactional
  public void delete(Long id) {
    if (!sensorRepository.existsById(id)) {
      throw new IllegalArgumentException("Sensor não encontrado");
    }
    sensorRepository.deleteById(id);
  }

  // Converter Entity para DTO
  private SensorResponseDTO toResponseDTO(Sensor sensor) {
    SensorResponseDTO dto = new SensorResponseDTO();
    dto.setId(sensor.getId());
    dto.setNome(sensor.getNome());
    dto.setTipoSensor(sensor.getTipoSensor());
    dto.setUnidadeMedida(sensor.getUnidadeMedida());
    dto.setLimiteMinimo(sensor.getLimiteMinimo());
    dto.setLimiteMaximo(sensor.getLimiteMaximo());
    dto.setAtivo(sensor.getAtivo());
    dto.setUltimaAtualizacao(sensor.getUltimaAtualizacao());
    dto.setDispositivoId(sensor.getDispositivo().getId());
    dto.setDispositivoNome(sensor.getDispositivo().getNome());
    dto.setQuantidadeLeituras(sensor.getLeituras().size());
    return dto;
  }

  // Converter para DTO de Inativos (Carta-Desafio)
  private InativoResponseDTO toInativoDTO(Sensor sensor) {
    InativoResponseDTO dto = new InativoResponseDTO();
    dto.setId(sensor.getId());
    dto.setNome(sensor.getNome());
    dto.setTipo("SENSOR");
    dto.setUltimaAtualizacao(sensor.getUltimaAtualizacao());
    dto.setDiasInativo(ChronoUnit.DAYS.between(sensor.getUltimaAtualizacao(), LocalDateTime.now()));
    dto.setTipoSensor(sensor.getTipoSensor());
    dto.setDispositivoId(sensor.getDispositivo().getId());
    dto.setDispositivoNome(sensor.getDispositivo().getNome());
    return dto;
  }
}
