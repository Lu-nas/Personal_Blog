package com.generation.personalblog.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.personalblog.model.Usuario;
import com.generation.personalblog.model.UsuarioLogin;
import com.generation.personalblog.repository.UsuarioRepository;
import com.generation.personalblog.security.JwtService;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	// criptografar senha no ato do cadastro
	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.encode(senha);

	}

	// token no momento do loguin que regenera o usuaria da pessoa
	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}

	// função de ultilização do usuario final
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		// verifica se os dados email ja exixte no banco d' dados
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();

		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		return Optional.of(usuarioRepository.save(usuario));
	}

	// verifica id e para a atualização
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {

		if (usuarioRepository.findById(usuario.getId()).isPresent()) {

			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

			if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != usuario.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario já exixte!", null);

			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			return Optional.ofNullable(usuarioRepository.save(usuario));

		}

		return Optional.empty();

	}

	// recebe e verifica os dados vindos da model login passando pelas validacoes
	// criadas na camada de segurança
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		// Gera o Objeto de autentivação
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(),
				usuarioLogin.get().getSenha());

		// autentica o usuario
		Authentication authentication = authenticationManager.authenticate(credenciais);

		// se a autenticação foi efetuada com sucesso
		if (authentication.isAuthenticated()) {

			// Busca os dados do usuario
			Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

			// se encontrado
			if (usuario.isPresent()) {
				// preencher o Objeto usuarioLoguin com os dados encontrados
				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setFoto(usuario.get().getFoto());
				usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
				usuarioLogin.get().setSenha("");

				// O Objeto retorna preenchido
				return usuarioLogin;
			}

		}

		return Optional.empty();

	}

}
