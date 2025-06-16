package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Entities.Categoria; // Importar Categoria
import ElBuenSabor.ProyectoFinal.Entities.Promocion; // Importar Promocion
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.time.LocalTime; // Importar LocalTime
import java.util.List; // Importar List
import java.util.Optional; // Importar Optional

@Service

public class SucursalServiceImpl extends BaseServiceImpl<Sucursal, Long> implements SucursalService {
    public SucursalServiceImpl(SucursalRepository sucursalRepository) {
        super(sucursalRepository); // Llama al constructor de la clase base
    }

    @Override
    @Transactional
    public Sucursal update(Long id, Sucursal updatedSucursal) throws Exception { // <<-- Añadir throws Exception
        try {

            Sucursal actual = findById(id);

            actual.setNombre(updatedSucursal.getNombre());
            actual.setHorarioApertura(updatedSucursal.getHorarioApertura());
            actual.setHorarioCierre(updatedSucursal.getHorarioCierre());
            actual.setDomicilio(updatedSucursal.getDomicilio());
            actual.setEmpresa(updatedSucursal.getEmpresa());
            if (updatedSucursal.getCategorias() != null) {
                actual.getCategorias().clear(); // Limpia las categorías existentes
                actual.getCategorias().addAll(updatedSucursal.getCategorias()); // Agrega las nuevas
            } else {
                actual.getCategorias().clear(); // Si no se envían categorías, limpiar las existentes
            }

            if (updatedSucursal.getPromociones() != null) {
                actual.getPromociones().clear(); // Limpia las promociones existentes
                actual.getPromociones().addAll(updatedSucursal.getPromociones()); // Agrega las nuevas
            } else {
                actual.getPromociones().clear(); // Si no se envían promociones, limpiar las existentes
            }
            return baseRepository.save(actual);
        } catch (Exception e) {
            // Re-lanzamos cualquier excepción, manteniendo la consistencia con BaseService.
            throw new Exception("Error al actualizar la sucursal: " + e.getMessage());
        }
    }
}