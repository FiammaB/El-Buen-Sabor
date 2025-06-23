// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/ArticuloManufacturadoServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal; // Importar Sucursal
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository; // Importar SucursalRepository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticuloManufacturadoServiceImpl extends BaseServiceImpl<ArticuloManufacturado, Long> implements ArticuloManufacturadoService {

    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final SucursalRepository sucursalRepository; // Inyectar SucursalRepository

    public ArticuloManufacturadoServiceImpl(ArticuloManufacturadoRepository articuloManufacturadoRepository,
                                            SucursalRepository sucursalRepository) { // Modificar constructor
        super(articuloManufacturadoRepository);
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.sucursalRepository = sucursalRepository; // Asignar
    }

    @Override
    @Transactional
    public ArticuloManufacturado save(ArticuloManufacturado newEntity) throws Exception {
        // Al guardar una nueva entidad, asegurarse de que la sucursal está establecida
        if (newEntity.getSucursal() == null && newEntity.getId() == null) {
            // Esto es un lugar para manejar el caso donde el DTO no trae el objeto Sucursal completo,
            // pero sí el ID. Sin embargo, para la creación a través de un DTO específico (como ArticuloManufacturadoCreateDTO),
            // la lógica se manejará en el controlador o en un método de servicio más específico.
            // Aquí, asumimos que si llega aquí, la sucursal ya viene establecida o se establecerá después.
            // Para ser robustos, podrías forzar que el DTO incluya sucursalId y cargarla aquí.
            throw new Exception("El ArticuloManufacturado debe estar asociado a una sucursal.");
        }
        return super.save(newEntity);
    }


    @Override
    @Transactional
    public ArticuloManufacturado update(Long id, ArticuloManufacturado updated) throws Exception {
        try {
            ArticuloManufacturado actual = findById(id);

            // Asegurarse de que el artículo pertenece a la misma sucursal (o permitir cambio si la lógica lo permite)
            // Aquí, asumimos que un artículo manufacturado no cambia de sucursal una vez creado.
            // Si updated.getSucursal() es nulo, significa que no se envió en el DTO,
            // pero si se envió, verificamos que sea el mismo ID o que se actualice.
            if (updated.getSucursal() != null && !updated.getSucursal().getId().equals(actual.getSucursal().getId())) {
                // Si la lógica permite cambiar de sucursal, cargamos la nueva sucursal
                Sucursal nuevaSucursal = sucursalRepository.findById(updated.getSucursal().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + updated.getSucursal().getId()));
                actual.setSucursal(nuevaSucursal);
            }


            actual.setDenominacion(updated.getDenominacion());
            actual.setPrecioVenta(updated.getPrecioVenta());
            actual.setDescripcion(updated.getDescripcion());
            actual.setTiempoEstimadoMinutos(updated.getTiempoEstimadoMinutos());
            actual.setPreparacion(updated.getPreparacion());
            actual.setCategoria(updated.getCategoria());
            actual.setUnidadMedida(updated.getUnidadMedida());
            actual.setImagen(updated.getImagen());

            actual.getDetalles().clear();

            if (updated.getDetalles() != null) {
                for (ArticuloManufacturadoDetalle detalle : updated.getDetalles()) {
                    detalle.setArticuloManufacturado(actual);
                    actual.getDetalles().add(detalle);
                }
            }

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el artículo manufacturado: " + e.getMessage());
        }
    }

    @Override
    public List<ArticuloManufacturado> filtrar(Long sucursalId, Long categoriaId, String denominacion, Boolean baja) {
        // Llama al método filtrar del repositorio, que ahora incluye el sucursalId
        return articuloManufacturadoRepository.filtrar(sucursalId, categoriaId, denominacion, baja);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ArticuloManufacturado> findAllBySucursalId(Long sucursalId) throws Exception {
        try {
            // Verificar si la sucursal existe
            sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));

            // Llamar al método del repositorio para obtener artículos por sucursal
            return articuloManufacturadoRepository.findBySucursalId(sucursalId);
        } catch (Exception e) {
            throw new Exception("Error al obtener artículos manufacturados por sucursal: " + e.getMessage());
        }
    }
}
