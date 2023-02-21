package com.bolsadeideas.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FotoServiceImpl implements FotoService {

	@Override
	public Resource load(String filename) throws MalformedURLException {
		Path pathFoto = getPath(filename);
		Resource recurso = null;
		recurso = new UrlResource(pathFoto.toUri());
		if (!recurso.exists() && !recurso.isReadable()) {
			throw new MalformedURLException("Error: no se puede cargar la imagen: " + pathFoto.toString());

		}
		return recurso;
	}

	private Path getPath(String filename) {
		return Paths.get(FotoService.UPLOADS).resolve(filename).toAbsolutePath();
	}

	@Override
	public String copy(MultipartFile file) throws IOException {
		String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath = Paths.get(FotoService.UPLOADS).resolve(uniqueFileName);

		Path rootAbsolutePath = rootPath.toAbsolutePath();

		Files.copy(file.getInputStream(), rootAbsolutePath);
		return uniqueFileName;
	}

	@Override
	public boolean delete(String filename) {
		Path rootPath = getPath(filename);
		File archivo = rootPath.toFile();
		if (archivo.exists() && archivo.canRead()) {
			return archivo.delete();
		}
		return false;
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS).toFile());
		
	}

	@Override
	public void init() throws IOException {
		Files.createDirectories(Paths.get(UPLOADS));
	}

}
