package com.vfarias.estudio.spring.boot.models.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vfarias.estudio.spring.boot.models.entity.Cliente;

public interface IClienteService {
	
	public List<Cliente> findAll();
	public Page<Cliente> findAll(Pageable pageable);
	public Cliente save(Cliente cliente);
	//public Cliente update(Cliente cliente);
	public Cliente findById(Long id);
	public void delete(Long id);

}
