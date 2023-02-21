package com.bolsadeideas.springboot.app.controller;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.controller.paginator.PageRender;
import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.ClienteService;
import com.bolsadeideas.springboot.app.models.service.FotoService;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	private static final String SUCCESS = "success";
	private static final String TITULO = "titulo";
	private static final String CLIENTE = "cliente";
	private static final String CREAR_CLIENTE = "crearCliente";
	private static final String REDIRECT_LISTAR = "redirect:/listar";
	@Autowired
	private ClienteService clienteService;
	@Autowired
	private FotoService fotoService;

	@GetMapping(value = "/listar")
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		Pageable pageRequest = PageRequest.of(page, 4);

		Page<Cliente> clientes = clienteService.findAll(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<Cliente>("/listar", clientes);
		model.addAttribute(TITULO, "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";
	}

	@GetMapping(value = "/form")
	public String crear(Model model) {
		Cliente cliente = new Cliente();
		model.addAttribute(TITULO, "Creación de clientes");
		model.addAttribute(CLIENTE, cliente);
		return CREAR_CLIENTE;
	}

	@PostMapping(value = "/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus sessionStatus) {
		if (result.hasErrors()) {
			model.addAttribute(TITULO, "Error en la creación | Revise los datos");
			model.addAttribute("errors", result);
			return CREAR_CLIENTE;
		}

		if (!foto.isEmpty()) {

			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0 && fotoService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", String.format("Imagen '%s' eliminada corectamente", cliente.getFoto()));

			}
			try {
				String uniqueFileName = fotoService.copy(foto);
				flash.addFlashAttribute("info", String.format("Imagen '%s' corectamente subida", uniqueFileName));
				cliente.setFoto(uniqueFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (cliente.getId() != null) {
			clienteService.save(cliente);
			sessionStatus.setComplete();
			flash.addFlashAttribute(SUCCESS, "Cliente modificado con éxito!");
		} else {
			clienteService.save(cliente);
			sessionStatus.setComplete();
			flash.addFlashAttribute(SUCCESS, "Cliente agregado con éxito!");
		}
		return "redirect:listar";
	}

	@GetMapping(value = "/editar/{id}")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		Cliente cliente = clienteService.findOne(id);
		if (cliente != null) {
			model.addAttribute(TITULO,
					"Modificación de cliente: " + cliente.getNombre() + " " + cliente.getApellido());
			model.addAttribute(CLIENTE, cliente);
			return CREAR_CLIENTE;
		}
		flash.addFlashAttribute("error", "No existe el cliente solicitado!");
		return REDIRECT_LISTAR;
	}

	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable Long id, Model model, RedirectAttributes flash) {
		Cliente cliente = clienteService.findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos!");
			return REDIRECT_LISTAR;
		}

		model.addAttribute(CLIENTE, cliente);
		model.addAttribute(TITULO, "Detalle cliente: " + cliente.getNombre());

		return "ver";
	}

	@GetMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable Long id, Model model, RedirectAttributes flash) {
		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			flash.addFlashAttribute(SUCCESS, "Cliente eliminado con éxito!");

			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0 && fotoService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", String.format("Imagen '%s' eliminada corectamente", cliente.getFoto()));

			}
		}
		return REDIRECT_LISTAR;
	}

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Resource recurso = null;
		try {
			recurso = fotoService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

}
