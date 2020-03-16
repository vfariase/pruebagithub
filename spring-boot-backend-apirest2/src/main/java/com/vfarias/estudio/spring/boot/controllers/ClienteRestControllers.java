package com.vfarias.estudio.spring.boot.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vfarias.estudio.spring.boot.models.entity.Cliente;
import com.vfarias.estudio.spring.boot.models.services.IClienteService;


@CrossOrigin(origins = { "http://localhost:4200" })
@RequestMapping("/api")
@RestController
public class ClienteRestControllers {

	@Autowired
	private IClienteService clienteService;
	
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
	   return this.clienteService.findAll();	
	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@RequestBody Cliente cliente) {
		
		
		
		Cliente clienteNew=null;
		Map<String,Object> response = new HashMap<String, Object>();
		
		if(cliente==null) {
			response.put("message", "Cliente ha crear esta vacio");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		try {
			cliente.setFecha(new Date());
			clienteNew=this.clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar el insert en la base de datos");
			response.put("message", e.getMessage().concat(":").concat(e.getMostSpecificCause().toString()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		response.put("message", "El cliente fue creado con exito");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Cliente cliente=null;
		Map<String,Object> response =new HashMap<String, Object>();
		try {
		
			cliente = this.clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar la consulta en la base de datos");
			response.put("message", e.getMessage().concat(":").concat(e.getMostSpecificCause().toString()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		if(cliente == null) {
			response.put("message","El cliente, con el id : ".concat(id.toString()).concat(" No existe en la base de datos") );
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
		
		
	}
	
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@RequestBody Cliente cliente, @PathVariable Long id) {
		
		Cliente clienteActual=clienteService.findById(id);
		Cliente clienteUpdate = new Cliente();
		Map<String,Object> response =new HashMap<String, Object>();
		
		if(clienteActual==null) {
			response.put("message","El cliente, con el id : ".concat(id.toString()).concat(" No existe en la base de datos, No se puede editar") );
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		try {
			
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setFecha(cliente.getFecha());
				
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar la consulta en la base de datos");
			response.put("message", e.getMessage().concat(":").concat(e.getMostSpecificCause().toString()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		response.put("message", "El cliente fue actualizado con exito");
		response.put("cliente", clienteActual);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		
	}
	
	
	//Se podría haber realizado de manera mas sencilla
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Cliente clienteDelete=clienteService.findById(id);
		Map<String,Object> response =new HashMap<String, Object>();
		
		if(clienteDelete==null) {
			response.put("message","No se puede eliminar cliente : ".concat(id.toString()).concat(" Debido que, No existe en base de datos") );
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		
		try {
			clienteService.delete(id);	
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar la consulta en la base de datos");
			response.put("message", e.getMessage().concat(":").concat(e.getMostSpecificCause().toString()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		response.put("message", "El cliente fue eliminado con exito");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	
}
