package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.app.models.dao.ClienteDao;
import com.bolsadeideas.springboot.app.models.entity.Cliente;

import jakarta.transaction.Transactional;

@Service
public class ClienteServiceImpl implements ClienteService{

	@Autowired
	private ClienteDao clienteDao;
	

	@Override
	@Transactional
	public List<Cliente> findAll() {
	    return (List<Cliente>) clienteDao.findAll();
	}
	 
	@Override
	@Transactional
	public void save(Cliente cliente) {
	    clienteDao.save(cliente);
	}
	 
	@Override
	@Transactional
	public Cliente findOne(Long id) {
	    return clienteDao.findById(id).get();
	}
	 
	@Override
	@Transactional
	public void delete(Long id) {
	    clienteDao.deleteById(id);
	}

	@Transactional
	@Override
	public Page<Cliente> findAll(Pageable pageable) {
		return clienteDao.findAll(pageable);
	}
	
	
}
