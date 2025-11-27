package com.GrupoIoT.iot_api.service;

import com.GrupoIoT.iot_api.dto.request.DispositivoRequestDTO;
import com.GrupoIoT.iot_api.dto.response.DispositivoResponseDTO;
import com.GrupoIoT.iot_api.dto.response.InativoResponseDTO;
import com.GrupoIoT.iot_api.model.entity.Dispositivo;
import com.GrupoIoT.iot_api.model.entity.Dispositivo.StatusDispositivo;
import com.GrupoIoT.iot_api.model.entity.Usuario;
import com.GrupoIoT.iot_api.repository.DispositivoRepository;
import com.GrupoIoT.iot_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class DispositivoService {

  private final DispositivoRepository dispositivoRepository;
  private final UsuarioRepository usuarioRepository;

  @Autowired
  public DispositivoService(DispositivoRepository dispositivoRepository,
                            UsuarioRepository usuarioRepository) {
    this.dispositivoRepository = dispositivoRepository;
    this.usuarioRepository = usuarioRepository;
  }

  // CREATE
  @Transactional
  public DispositivoResponseDTO create(DispositivoRequestDTO dto) {
    // Validar endereço MAC único
    if (dispositivoRepository.existsByEnderecoMac(dto.getEnderecoMac())) {
      throw new IllegalArgumentException("Endereço MAC já cadastrado");
    }

    // Buscar usuário
    Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
      .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    Dispositivo dispositivo = new Dispositivo();
    dispositivo.setNome(dto.getNome());
    dispositivo.setTipo(dto.getTipo());
    dispositivo.setLocalizacao(dto.getLocalizacao());
    dispositivo.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusDispositivo.ATIVO);
    dispositivo.setEnderecoMac(dto.getEnderecoMac());
    dispositivo.setUsuario(usuario);

    Dispositivo saved = dispositivoRepository.save(dispositivo);
    return toResponseDTO(saved);
  }

  // READ ONE
  @Transactional(readOnly = true)
  public DispositivoResponseDTO getOne(Long id) {
    Dispositivo dispositivo = dispositivoRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado"));
    return toResponseDTO(dispositivo);
  }

  // READ ALL
  @Transactional(readOnly = true)
  public Page<DispositivoResponseDTO> getAll(Pageable pageable) {
    return dispositivoRepository.findAll(pageable)
      .map(this::toResponseDTO);
  }

  // READ ALL com filtros
  @Transactional(readOnly = true)
  public Page<DispositivoResponseDTO> getAllWithFilters(
    String nome,
    String tipo,
    String localizacao,
    StatusDispositivo status,
    Long usuarioId,
    Pageable pageable) {

    return dispositivoRepository.findWithFilters(nome, tipo, localizacao, status, usuarioId, pageable)
      .map(this::toResponseDTO);
  }

  // READ por usuário
  @Transactional(readOnly = true)
  public Page<DispositivoResponseDTO> getAllByUsuario(Long usuarioId, Pageable pageable) {
    return dispositivoRepository.findByUsuarioId(usuarioId, pageable)
      .map(this::toResponseDTO);
  }

  // 🎯 CARTA-DESAFIO: Listar Inativos (7+ dias sem atualização)
  @Transactional(readOnly = true)
  public Page<InativoResponseDTO> getInativos(Pageable pageable) {
    LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);
    return dispositivoRepository.findInativos(dataLimite, pageable)
      .map(this::toInativoDTO);
  }

  // 🎯 CARTA-DESAFIO: Contar inativos
  @Transactional(readOnly = true)
  public long countInativos() {
    LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);
    return dispositivoRepository.countInativos(dataLimite);
  }

  // UPDATE
  @Transactional
  public DispositivoResponseDTO update(Long id, DispositivoRequestDTO dto) {
    Dispositivo dispositivo = dispositivoRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Dispositivo não encontrado"));

    // Validar endereço MAC único (se mudou)
    if (!dispositivo.getEnderecoMac().equals(dto.getEnderecoMac())) {
      if (dispositivoRepository.existsByEnderecoMac(dto.getEnderecoMac())) {
        throw new IllegalArgumentException("Endereço MAC já cadastrado");
      }
    }

    // Validar usuário (se mudou)
    if (!dispositivo.getUsuario().getId().equals(dto.getUsuarioId())) {
      Usuario novoUsuario = usuarioRepository.findById(dto.getUsuarioId())
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
      dispositivo.setUsuario(novoUsuario);
    }

    dispositivo.setNome(dto.getNome());
    dispositivo.setTipo(dto.getTipo());
    dispositivo.setLocalizacao(dto.getLocalizacao());
    dispositivo.setStatus(dto.getStatus());
    dispositivo.setEnderecoMac(dto.getEnderecoMac());
    // ultimaAtualizacao é atualizada automaticamente pelo @UpdateTimestamp

    Dispositivo updated = dispositivoRepository.save(dispositivo);
    return toResponseDTO(updated);
  }

  // DELETE
  @Transactional
  public void delete(Long id) {
    if (!dispositivoRepository.existsById(id)) {
      throw new IllegalArgumentException("Dispositivo não encontrado");
    }
    dispositivoRepository.deleteById(id);
  }

  // Converter Entity para DTO
  private DispositivoResponseDTO toResponseDTO(Dispositivo dispositivo) {
    DispositivoResponseDTO dto = new DispositivoResponseDTO();
    dto.setId(dispositivo.getId());
    dto.setNome(dispositivo.getNome());
    dto.setTipo(dispositivo.getTipo());
    dto.setLocalizacao(dispositivo.getLocalizacao());
    dto.setStatus(dispositivo.getStatus());
    dto.setEnderecoMac(dispositivo.getEnderecoMac());
    dto.setDataCadastro(dispositivo.getDataCadastro());
    dto.setUltimaAtualizacao(dispositivo.getUltimaAtualizacao());
    dto.setUsuarioId(dispositivo.getUsuario().getId());
    dto.setUsuarioNome(dispositivo.getUsuario().getNome());
    dto.setQuantidadeSensores(dispositivo.getSensores().size());
    return dto;
  }

  // Converter para DTO de Inativos (Carta-Desafio)
  private InativoResponseDTO toInativoDTO(Dispositivo dispositivo) {
    InativoResponseDTO dto = new InativoResponseDTO();
    dto.setId(dispositivo.getId());
    dto.setNome(dispositivo.getNome());
    dto.setTipo("DISPOSITIVO");
    dto.setUltimaAtualizacao(dispositivo.getUltimaAtualizacao());
    dto.setDiasInativo(ChronoUnit.DAYS.between(dispositivo.getUltimaAtualizacao(), LocalDateTime.now()));
    dto.setLocalizacao(dispositivo.getLocalizacao());
    return dto;
  }
}
