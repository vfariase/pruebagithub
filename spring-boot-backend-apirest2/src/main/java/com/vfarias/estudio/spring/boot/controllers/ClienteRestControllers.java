package com.vfarias.estudio.spring.boot.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vfarias.estudio.spring.boot.models.entity.Cliente;
import com.vfarias.estudio.spring.boot.models.services.IClienteService;


@CrossOrigin(origins = { "http://localhost:4200" })
@RequestMapping("/api")
@RestController
public class ClienteRestControllers {

	@Autowired
	private IClienteService clienteService;
	
	private final Logger log=LoggerFactory.getLogger(ClienteRestControllers.class);
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
	   return this.clienteService.findAll();	
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page){
		Pageable pageable=PageRequest.of(page, 4);
	   return this.clienteService.findAll(pageable);	
	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		
		Cliente clienteNew=null;
		Map<String,Object> response = new HashMap<String, Object>();
		
       
		List<String> errors = result.getFieldErrors()
				.stream()
				.map(err -> {
					return "El campo '"+err.getField()+"' "+ err.getDefaultMessage();
				}).collect(Collectors.toList());
		
		
		
		/*
	if(result.hasErrors()) {
			List<String> errors = new ArrayList<>();
			for(FieldError err: result.getFieldErrors()) {
				errors.add("El campo '"+err.getField()+"' "+ err.getDefaultMessage());
			}
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}*/
		
		
		
		if(cliente==null) {
			response.put("message", "Cliente ha crear esta vacio");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
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
			
			String nombreFotoAnterior = clienteDelete.getFoto();
			
			if(nombreFotoAnterior != null && nombreFotoAnterior.length()>0)
			{
				Path rutaFotoAnterior= Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
			    File archivoFotoAnterior=rutaFotoAnterior.toFile();
			    if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead())
			    {
			    	archivoFotoAnterior.delete();
			    }
			}
			clienteService.delete(id);	
		} catch (DataAccessException e) {
			response.put("message", "Error al realizar la consulta en la base de datos");
			response.put("message", e.getMessage().concat(":").concat(e.getMostSpecificCause().toString()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		response.put("message", "El cliente fue eliminado con exito");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
	}
	
	
	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo,@RequestParam("id") Long id){
		Map<String,Object> response = new HashMap<String, Object>();
		
		log.info("archivo : "+archivo.getOriginalFilename());
		log.info("id : "+id.toString());
		log.info("resultado boolean : "+archivo.isEmpty());
		
		Cliente cliente=clienteService.findById(id);
	
		if(!archivo.isEmpty()) {
			
			String nombreArchivo = UUID.randomUUID().toString() + "_" +  archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo=Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			
			try {
				Files.copy(archivo.getInputStream(),rutaArchivo);
			} catch (IOException e) {
				response.put("message", "Error al subir la imagen del cliente"+nombreArchivo);
				response.put("error", e.getMessage().concat(":").concat(e.getCause().getMessage()));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
        
			String nombreFotoAnterior = cliente.getFoto();
			log.info("nombreFotoAnterior"+nombreFotoAnterior);
			
			if(nombreFotoAnterior != null && nombreFotoAnterior.length()>0)
			{
				
				Path rutaFotoAnterior= Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
			    File archivoFotoAnterior=rutaFotoAnterior.toFile();
			    if(archivoFotoAnterior.exists() && archivoFotoAnterior.canRead())
			    {
			    	archivoFotoAnterior.delete();
			    	log.info("delete"+nombreFotoAnterior);
			    }
			}
			
			cliente.setFoto(nombreArchivo);
			clienteService.save(cliente);
			
			response.put("cliente", cliente);
			response.put("mensaje", "Has subido correctamente la imagen "+nombreArchivo);
			log.info("response : "+response);
		}
		
		
		
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}
	
	
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
		
		
		log.info("nombre de foto : "+nombreFoto);	
		
		
		
		Path rutaArchivo=Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
		log.info("rutaArchivo "+ rutaArchivo);;
		Resource recurso =null;
		try {
		recurso = new UrlResource(rutaArchivo.toUri()); 
		}catch (MalformedURLException e) {
			
		}
		
		if(!recurso.exists() && recurso.isReadable()){
			throw new RuntimeException("Error no se pudo cargar la imagen:"+nombreFoto);
		}
		
		HttpHeaders cabecera = new  HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachement;filename=\""+recurso.getFilename()+"\"");
		return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
	}
	
	
	
	
	
}
