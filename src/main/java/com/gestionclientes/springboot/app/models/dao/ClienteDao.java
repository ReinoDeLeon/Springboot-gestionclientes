package com.gestionclientes.springboot.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gestionclientes.springboot.app.models.entity.Cliente;

public interface ClienteDao extends PagingAndSortingRepository<Cliente, Long>, CrudRepository<Cliente, Long>{

	
	
}
