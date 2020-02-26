package com.vfarias.estudio.spring.boot.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vfarias.estudio.spring.boot.models.entity.Cliente;
import com.vfarias.estudio.spring.boot.models.services.IClienteService;

@RequestMapping("/api")
@RestController
public class ClienteRestControllers {

	@Autowired
	private IClienteService clienteService;
	
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
	   return clienteService.findAll();	
	}
	
	@PostMapping("/clientes")
	public Cliente create(@PathVariable Cliente cliente) {
		return clienteService.save(cliente);
		
	}
	
	@GetMapping("/clientes/{id}")
	public void show(Long id) {
		clienteService.findById(id);;
		
	}
	
	@DeleteMapping("/clientes/{id}")
	public void delete(Long id) {
		clienteService.findById(id);;
	}
	
	
}
