package com.GrupoIoT.iot_api.service;

import com.GrupoIoT.iot_api.dto.request.UsuarioRequestDTO;
import com.GrupoIoT.iot_api.dto.response.UsuarioResponseDTO;
import com.GrupoIoT.iot_api.model.entity.Usuario;
import com.GrupoIoT.iot_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;

  @Autowired
  public UsuarioService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  // CREATE
  @Transactional
  public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
    // Validar email único
    if (usuarioRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email já cadastrado");
    }

    Usuario usuario = new Usuario();
    usuario.setNome(dto.getNome());
    usuario.setEmail(dto.getEmail());
    usuario.setSenha(dto.getSenha()); // TODO: Criptografar senha

    Usuario saved = usuarioRepository.save(usuario);
    return toResponseDTO(saved);
  }

  // READ ONE
  @Transactional(readOnly = true)
  public UsuarioResponseDTO getOne(Long id) {
    Usuario usuario = usuarioRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    return toResponseDTO(usuario);
  }

  // READ ALL
  @Transactional(readOnly = true)
  public Page<UsuarioResponseDTO> getAll(Pageable pageable) {
    return usuarioRepository.findAll(pageable)
      .map(this::toResponseDTO);
  }

  // READ ALL com filtro por nome
  @Transactional(readOnly = true)
  public Page<UsuarioResponseDTO> getAllByNome(String nome, Pageable pageable) {
    return usuarioRepository.findByNomeContainingIgnoreCase(nome, pageable)
      .map(this::toResponseDTO);
  }

  // UPDATE
  @Transactional
  public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
    Usuario usuario = usuarioRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    // Validar email único (se mudou)
    if (!usuario.getEmail().equals(dto.getEmail())) {
      if (usuarioRepository.existsByEmail(dto.getEmail())) {
        throw new IllegalArgumentException("Email já cadastrado");
      }
    }

    usuario.setNome(dto.getNome());
    usuario.setEmail(dto.getEmail());

    if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
      usuario.setSenha(dto.getSenha()); // TODO: Criptografar senha
    }

    Usuario updated = usuarioRepository.save(usuario);
    return toResponseDTO(updated);
  }

  // DELETE
  @Transactional
  public void delete(Long id) {
    if (!usuarioRepository.existsById(id)) {
      throw new IllegalArgumentException("Usuário não encontrado");
    }

    // Verificar se tem dispositivos cadastrados
    Usuario usuario = usuarioRepository.findById(id).get();
    if (!usuario.getDispositivos().isEmpty()) {
      throw new IllegalStateException("Não é possível excluir usuário com dispositivos cadastrados");
    }

    usuarioRepository.deleteById(id);
  }

  // Converter Entity para DTO
  private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
    UsuarioResponseDTO dto = new UsuarioResponseDTO();
    dto.setId(usuario.getId());
    dto.setNome(usuario.getNome());
    dto.setEmail(usuario.getEmail());
    dto.setDataCriacao(usuario.getDataCriacao());
    return dto;
  }
}
