package com.GrupoIoT.iot_api.controller;

import com.GrupoIoT.iot_api.dto.request.SensorRequestDTO;
import com.GrupoIoT.iot_api.dto.response.InativoResponseDTO;
import com.GrupoIoT.iot_api.dto.response.SensorResponseDTO;
import com.GrupoIoT.iot_api.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sensores")
public class SensorController {

  private final SensorService sensorService;

  @Autowired
  public SensorController(SensorService sensorService) {
    this.sensorService = sensorService;
  }

  // CREATE
  @PostMapping
  public ResponseEntity<SensorResponseDTO> create(@Valid @RequestBody SensorRequestDTO dto) {
    SensorResponseDTO created = sensorService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  // READ ONE
  @GetMapping("/{id}")
  public ResponseEntity<SensorResponseDTO> getOne(@PathVariable Long id) {
    SensorResponseDTO sensor = sensorService.getOne(id);
    return ResponseEntity.ok(sensor);
  }

  // READ ALL com paginação, ordenação e filtros
  @GetMapping
  public ResponseEntity<Page<SensorResponseDTO>> getAll(
    @RequestParam(required = false) String nome,
    @RequestParam(required = false) String tipoSensor,
    @RequestParam(required = false) Boolean ativo,
    @RequestParam(required = false) Long dispositivoId,
    @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

    Page<SensorResponseDTO> sensores = sensorService.getAllWithFilters(
      nome, tipoSensor, ativo, dispositivoId, pageable);
    return ResponseEntity.ok(sensores);
  }

  // 🎯 CARTA-DESAFIO: Listar sensores inativos (7+ dias sem atualização)
  @GetMapping("/inativos")
  public ResponseEntity<Page<InativoResponseDTO>> getInativos(
    @PageableDefault(size = 20, sort = "ultimaAtualizacao", direction = Sort.Direction.ASC) Pageable pageable) {
    Page<InativoResponseDTO> inativos = sensorService.getInativos(pageable);
    return ResponseEntity.ok(inativos);
  }

  // 🎯 CARTA-DESAFIO: Contar sensores inativos
  @GetMapping("/inativos/count")
  public ResponseEntity<Map<String, Object>> countInativos() {
    long count = sensorService.countInativos();
    Map<String, Object> response = new HashMap<>();
    response.put("total", count);
    response.put("descricao", "Sensores sem atualização há mais de 7 dias");
    return ResponseEntity.ok(response);
  }

  // UPDATE
  @PutMapping("/{id}")
  public ResponseEntity<SensorResponseDTO> update(
    @PathVariable Long id,
    @Valid @RequestBody SensorRequestDTO dto) {
    SensorResponseDTO updated = sensorService.update(id, dto);
    return ResponseEntity.ok(updated);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    sensorService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
