package com.vfarias.estudio.spring.boot.models.services;

import java.util.List;

import com.vfarias.estudio.spring.boot.models.entity.Cliente;

public interface IClienteService {
	
	public List<Cliente> findAll();
	public Cliente save(Cliente cliente);
	public Cliente update(Long id);
	public void findById(Long id);
	public void delete(Long id);

}
