package com.GrupoIoT.iot_api.controller;

import com.GrupoIoT.iot_api.dto.request.DispositivoRequestDTO;
import com.GrupoIoT.iot_api.dto.response.DispositivoResponseDTO;
import com.GrupoIoT.iot_api.dto.response.InativoResponseDTO;
import com.GrupoIoT.iot_api.model.entity.Dispositivo.StatusDispositivo;
import com.GrupoIoT.iot_api.service.DispositivoService;
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
@RequestMapping("/api/dispositivos")
public class DispositivoController {

  private final DispositivoService dispositivoService;

  @Autowired
  public DispositivoController(DispositivoService dispositivoService) {
    this.dispositivoService = dispositivoService;
  }


  @PostMapping
  public ResponseEntity<DispositivoResponseDTO> create(@Valid @RequestBody DispositivoRequestDTO dto) {
    DispositivoResponseDTO created = dispositivoService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }


  @GetMapping("/{id}")
  public ResponseEntity<DispositivoResponseDTO> getOne(@PathVariable Long id) {
    DispositivoResponseDTO dispositivo = dispositivoService.getOne(id);
    return ResponseEntity.ok(dispositivo);
  }


  @GetMapping
  public ResponseEntity<Page<DispositivoResponseDTO>> getAll(
    @RequestParam(required = false) String nome,
    @RequestParam(required = false) String tipo,
    @RequestParam(required = false) String localizacao,
    @RequestParam(required = false) StatusDispositivo status,
    @RequestParam(required = false) Long usuarioId,
    @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

    Page<DispositivoResponseDTO> dispositivos = dispositivoService.getAllWithFilters(
      nome, tipo, localizacao, status, usuarioId, pageable);
    return ResponseEntity.ok(dispositivos);
  }


  @GetMapping("/inativos")
  public ResponseEntity<Page<InativoResponseDTO>> getInativos(
    @PageableDefault(size = 20, sort = "ultimaAtualizacao", direction = Sort.Direction.ASC) Pageable pageable) {
    Page<InativoResponseDTO> inativos = dispositivoService.getInativos(pageable);
    return ResponseEntity.ok(inativos);
  }

  @GetMapping("/inativos/count")
  public ResponseEntity<Map<String, Object>> countInativos() {
    long count = dispositivoService.countInativos();
    Map<String, Object> response = new HashMap<>();
    response.put("total", count);
    response.put("descricao", "Dispositivos sem atualização há mais de 7 dias");
    return ResponseEntity.ok(response);
  }


  @PutMapping("/{id}")
  public ResponseEntity<DispositivoResponseDTO> update(
    @PathVariable Long id,
    @Valid @RequestBody DispositivoRequestDTO dto) {
    DispositivoResponseDTO updated = dispositivoService.update(id, dto);
    return ResponseEntity.ok(updated);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    dispositivoService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
