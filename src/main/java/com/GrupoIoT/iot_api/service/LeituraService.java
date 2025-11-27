package com.GrupoIoT.iot_api.service;

import com.GrupoIoT.iot_api.dto.request.LeituraRequestDTO;
import com.GrupoIoT.iot_api.dto.response.EstatisticasResponseDTO;
import com.GrupoIoT.iot_api.dto.response.LeituraResponseDTO;
import com.GrupoIoT.iot_api.model.entity.Leitura;
import com.GrupoIoT.iot_api.model.entity.Sensor;
import com.GrupoIoT.iot_api.repository.LeituraRepository;
import com.GrupoIoT.iot_api.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LeituraService {

  private final LeituraRepository leituraRepository;
  private final SensorRepository sensorRepository;

  @Autowired
  public LeituraService(LeituraRepository leituraRepository,
                        SensorRepository sensorRepository) {
    this.leituraRepository = leituraRepository;
    this.sensorRepository = sensorRepository;
  }

  // CREATE
  @Transactional
  public LeituraResponseDTO create(LeituraRequestDTO dto) {
    // Buscar sensor
    Sensor sensor = sensorRepository.findById(dto.getSensorId())
      .orElseThrow(() -> new IllegalArgumentException("Sensor não encontrado"));

    // Verificar se sensor está ativo
    if (!sensor.getAtivo()) {
      throw new IllegalStateException("Não é possível registrar leitura em sensor inativo");
    }

    Leitura leitura = new Leitura();
    leitura.setValor(dto.getValor());
    leitura.setSensor(sensor);
    // dataHora e alerta são definidos automaticamente

    Leitura saved = leituraRepository.save(leitura);

    // Atualizar ultimaAtualizacao do sensor
    sensor.setUltimaAtualizacao(LocalDateTime.now());
    sensorRepository.save(sensor);

    return toResponseDTO(saved);
  }

  // READ ONE
  @Transactional(readOnly = true)
  public LeituraResponseDTO getOne(Long id) {
    Leitura leitura = leituraRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Leitura não encontrada"));
    return toResponseDTO(leitura);
  }

  // READ ALL
  @Transactional(readOnly = true)
  public Page<LeituraResponseDTO> getAll(Pageable pageable) {
    return leituraRepository.findAll(pageable)
      .map(this::toResponseDTO);
  }

  // READ ALL com filtros
  @Transactional(readOnly = true)
  public Page<LeituraResponseDTO> getAllWithFilters(
    Long sensorId,
    Boolean alerta,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    Pageable pageable) {

    return leituraRepository.findWithFilters(sensorId, alerta, dataInicio, dataFim, pageable)
      .map(this::toResponseDTO);
  }

  // READ por sensor
  @Transactional(readOnly = true)
  public Page<LeituraResponseDTO> getAllBySensor(Long sensorId, Pageable pageable) {
    return leituraRepository.findBySensorId(sensorId, pageable)
      .map(this::toResponseDTO);
  }

  // READ últimas leituras de um sensor
  @Transactional(readOnly = true)
  public Page<LeituraResponseDTO> getUltimasLeituras(Long sensorId, Pageable pageable) {
    return leituraRepository.findUltimasLeiturasBySensorId(sensorId, pageable)
      .map(this::toResponseDTO);
  }

  // Estatísticas de um sensor em um período
  @Transactional(readOnly = true)
  public EstatisticasResponseDTO getEstatisticas(Long sensorId, LocalDateTime dataInicio, LocalDateTime dataFim) {
    Sensor sensor = sensorRepository.findById(sensorId)
      .orElseThrow(() -> new IllegalArgumentException("Sensor não encontrado"));

    EstatisticasResponseDTO dto = new EstatisticasResponseDTO();
    dto.setSensorId(sensorId);
    dto.setSensorNome(sensor.getNome());
    dto.setDataInicio(dataInicio);
    dto.setDataFim(dataFim);
    dto.setUnidadeMedida(sensor.getUnidadeMedida());

    dto.setTotalLeituras(leituraRepository.contarLeituras(sensorId, dataInicio, dataFim));
    dto.setTotalAlertas(leituraRepository.contarAlertas(sensorId, dataInicio, dataFim));
    dto.setValorMedio(leituraRepository.calcularMedia(sensorId, dataInicio, dataFim));
    dto.setValorMinimo(leituraRepository.calcularMinimo(sensorId, dataInicio, dataFim));
    dto.setValorMaximo(leituraRepository.calcularMaximo(sensorId, dataInicio, dataFim));

    return dto;
  }

  // DELETE
  @Transactional
  public void delete(Long id) {
    if (!leituraRepository.existsById(id)) {
      throw new IllegalArgumentException("Leitura não encontrada");
    }
    leituraRepository.deleteById(id);
  }

  // Converter Entity para DTO
  private LeituraResponseDTO toResponseDTO(Leitura leitura) {
    LeituraResponseDTO dto = new LeituraResponseDTO();
    dto.setId(leitura.getId());
    dto.setValor(leitura.getValor());
    dto.setDataHora(leitura.getDataHora());
    dto.setAlerta(leitura.getAlerta());
    dto.setSensorId(leitura.getSensor().getId());
    dto.setSensorNome(leitura.getSensor().getNome());
    dto.setUnidadeMedida(leitura.getSensor().getUnidadeMedida());
    dto.setDispositivoId(leitura.getSensor().getDispositivo().getId());
    dto.setDispositivoNome(leitura.getSensor().getDispositivo().getNome());
    return dto;
  }
}
