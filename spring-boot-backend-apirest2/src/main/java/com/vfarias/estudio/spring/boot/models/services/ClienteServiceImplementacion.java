package com.vfarias.estudio.spring.boot.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vfarias.estudio.spring.boot.models.dao.IClienteDAO;
import com.vfarias.estudio.spring.boot.models.entity.Cliente;

@Service
public class ClienteServiceImplementacion implements IClienteService{
	
	@Autowired
	private IClienteDAO clienteDAO;

	@Transactional
	@Override
	public List<Cliente> findAll() {
		
		return clienteDAO.findAll();
	}

	@Transactional
	@Override
	public Cliente save(Cliente cliente) {
		
		return clienteDAO.save(cliente);
	}

	@Transactional
	@Override
	public void delete(Long id) {
		clienteDAO.deleteById(id);
	
	}

	@Transactional
	@Override
	public void findById(Long id) {
		clienteDAO.findById(id);
		
	}

	@Override
	public Cliente update(Long id) {
		  return clienteDAO.sa;
	}
	
	
	
	
	

}
