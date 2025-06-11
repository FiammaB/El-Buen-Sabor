package ElBuenSabor.ProyectoFinal.Servicios.Impl;


import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.Servicios.ArticuloManufacturadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Necesario para algunas operaciones de repositorio

@Service
public class ArticuloManufacturadoServiceImpl implements ArticuloManufacturadoService {

    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;

    @Autowired
    public ArticuloManufacturadoServiceImpl(ArticuloManufacturadoRepository articuloManufacturadoRepository) {
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        // ¡El mapper ya no se inyecta aquí! Se usará en el controlador.
    }

    @Override
    @Transactional
    public ArticuloManufacturado save(ArticuloManufacturado articuloManufacturado) {
        // La lógica de negocio va aquí antes de guardar
        // Por ejemplo, validaciones, asignación de valores por defecto, etc.
        return articuloManufacturadoRepository.save(articuloManufacturado);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticuloManufacturado findById(Long id) {
        // Tu BaseRepository.getById ya maneja la lógica de "estaDadoDeBaja"
        return articuloManufacturadoRepository.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloManufacturado> findAll() {
        // Tu BaseRepository.getAll ya filtra por "estaDadoDeBaja = false"
        return articuloManufacturadoRepository.getAll();
    }

    @Override
    @Transactional
    public ArticuloManufacturado update(Long id, ArticuloManufacturado articuloManufacturado) {
        // Opcional: Validar que el ID de la entidad coincida con el ID del path
        if (articuloManufacturado.getId() != null && !id.equals(articuloManufacturado.getId())) {
            throw new RuntimeException("El ID de la entidad no coincide con el ID de la URL.");
        }
        articuloManufacturado.setId(id); // Asegurar que la entidad tiene el ID correcto para la actualización

        // Aquí podrías cargar la entidad existente y copiar propiedades
        // ArticuloManufacturado existingArticulo = articuloManufacturadoRepository.getById(id);
        // // Lógica para copiar propiedades de 'articuloManufacturado' a 'existingArticulo'
        // // Esto evita problemas si la entidad que llega no tiene todos los campos cargados
        // return articuloManufacturadoRepository.save(existingArticulo);

        // Sin la lógica de copiar propiedades, simplemente guardamos la entidad que nos llega (con el ID correcto)
        return articuloManufacturadoRepository.save(articuloManufacturado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Se busca la entidad para asegurar que existe y no está borrada lógicamente
        // y luego se llama al delete sobreescrito del BaseRepository que marca estaDadoDeBaja en true
        ArticuloManufacturado articuloManufacturadoToDelete = articuloManufacturadoRepository.getById(id);
        articuloManufacturadoRepository.delete(articuloManufacturadoToDelete);
    }
}