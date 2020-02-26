package com.vfarias.estudio.spring.boot.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vfarias.estudio.spring.boot.models.entity.Cliente;

public interface IClienteDAO extends JpaRepository<Cliente, Long>{

}
