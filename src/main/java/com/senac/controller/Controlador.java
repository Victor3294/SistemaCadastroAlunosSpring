package com.senac.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.senac.dao.AlunoJDBCdao;
import com.senac.model.Aluno;

import jakarta.servlet.http.HttpSession;

@Controller
public class Controlador {

	@Autowired
	AlunoJDBCdao dao;
	Aluno aluno = new Aluno();	
	
	@GetMapping("deslogar")
	public String deslogar() {
		HttpSession session = dao.getSession();
		session.invalidate();
		return "index";
	}
	
	@GetMapping("/listarAlunos")
	public String listarAlunos (Model model) {				
		ArrayList<Aluno> listaAlunos= dao.listarAlunos();			
		model.addAttribute("listaAlunos",listaAlunos);
		return "listarAlunos";
	}
	
	@PostMapping("autenticar")
	public String autenticar (
			@RequestParam("usuario") String usuario, 
			@RequestParam("senha") String senha,
			Model model,
			HttpSession session) {
		if (usuario.equals("admin") && senha.equals("admin")) {
			
			session = dao.getSession();
			session.setMaxInactiveInterval(500);
			session.setAttribute("usuario", usuario); 
			return "redirect:/listarAlunos";
		}else {
			model.addAttribute("error", "1");
			return "index";
		}
	}
	
	@GetMapping("/erroLogin")
	public String erroLogin (@RequestParam("error") String erro, Model model) {
		if(erro.equals("1")) {
			model.addAttribute("error", "1");
			return "index";
		}else if (erro.equals("2")) {
			model.addAttribute("error", "2");
			return "index";
		}
		
		return "index";
	}
	
	@GetMapping("/excluirAluno")
	public String excluirAluno (@RequestParam("id") Integer id) {
		aluno.setId(id);
		dao.excluirAluno(aluno);
		return "redirect:/listarAlunos";
	}
	
	@GetMapping("/detalharAluno")
	public String detalharAluno (@RequestParam("id") Integer id, Model model) {
		aluno.setId(id);
		aluno = dao.pesquisarPorId(aluno);	
		model.addAttribute("aluno", aluno);
		return "detalharAluno";
	}
	
	@PostMapping("/alterarAluno")
	public String alterarAluno (@RequestParam("id") Integer id, Model model) {		
		aluno.setId(id);
		aluno = dao.pesquisarPorId(aluno);	
		model.addAttribute("aluno", aluno);
		return "alterarAluno";
	}
	
	@PostMapping("/confirmarAlteracao")
	public String confirmarAlteracaoAluno (Aluno aluno, Model model) {
		dao.alterarAluno(aluno);
		model.addAttribute("aluno", aluno);
		return "detalharAluno";
	}
	
	@GetMapping("/cadastrarAluno")
	public String cadastrarAluno () {
		return "cadastrarAluno";
	}
	
	@PostMapping("/confirmarCadastro")
	public String confirmarCadastro (Aluno aluno, Model model) {
		String matricula = criarMatricula(aluno.getIdade(),aluno.getSemestre());
		aluno.setMatricula(matricula);
		int id = dao.cadastrarAluno(aluno);	
		aluno.setId(id);
		model.addAttribute("aluno", aluno);
		return "/detalharAluno";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
private String criarMatricula(String idade, String semestre) {
		
		
		
		LocalDate dataAtual = LocalDate.now();
		int mes = dataAtual.getMonthValue();
		int ano = dataAtual.getYear();
		// Assume que o semestre 1 é de Janeiro a Junho e o semestre 2 é de Julho a Dezembro
		int semestreEscolha = (mes < 7) ? 1 : 2;
		
		Random random = new Random();		
		String matricula = String.valueOf(ano) + String.valueOf(mes) + String.valueOf(semestreEscolha) + String.valueOf(idade);
		
        // Gera quatro números aleatórios entre 0 e 9
        for (int i = 0; i < 4; i++) {
        	matricula += String.valueOf(random.nextInt(10)); 
        }
       
    
		return matricula;    
	}
}
