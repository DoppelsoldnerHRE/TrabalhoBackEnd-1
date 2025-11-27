package com.GrupoIoT.iot_api.controller;

import com.GrupoIoT.iot_api.dto.request.LeituraRequestDTO;
import com.GrupoIoT.iot_api.dto.response.EstatisticasResponseDTO;
import com.GrupoIoT.iot_api.dto.response.LeituraResponseDTO;
import com.GrupoIoT.iot_api.service.LeituraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/leituras")
public class LeituraController {

  private final LeituraService leituraService;

  @Autowired
  public LeituraController(LeituraService leituraService) {
    this.leituraService = leituraService;
  }


  @PostMapping
  public ResponseEntity<LeituraResponseDTO> create(@Valid @RequestBody LeituraRequestDTO dto) {
    LeituraResponseDTO created = leituraService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeituraResponseDTO> getOne(@PathVariable Long id) {
    LeituraResponseDTO leitura = leituraService.getOne(id);
    return ResponseEntity.ok(leitura);
  }


  @GetMapping
  public ResponseEntity<Page<LeituraResponseDTO>> getAll(
    @RequestParam(required = false) Long sensorId,
    @RequestParam(required = false) Boolean alerta,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
    @PageableDefault(size = 20, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {

    Page<LeituraResponseDTO> leituras = leituraService.getAllWithFilters(
      sensorId, alerta, dataInicio, dataFim, pageable);
    return ResponseEntity.ok(leituras);
  }


  @GetMapping("/sensor/{sensorId}/ultimas")
  public ResponseEntity<Page<LeituraResponseDTO>> getUltimasLeituras(
    @PathVariable Long sensorId,
    @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<LeituraResponseDTO> leituras = leituraService.getUltimasLeituras(sensorId, pageable);
    return ResponseEntity.ok(leituras);
  }


  @GetMapping("/sensor/{sensorId}/estatisticas")
  public ResponseEntity<EstatisticasResponseDTO> getEstatisticas(
    @PathVariable Long sensorId,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
    EstatisticasResponseDTO estatisticas = leituraService.getEstatisticas(sensorId, dataInicio, dataFim);
    return ResponseEntity.ok(estatisticas);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    leituraService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
