package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Service.SucursalService;
import ElBuenSabor.ProyectoFinal.Service.CategoriaService; // Para obtener categorías
import ElBuenSabor.ProyectoFinal.Service.PromocionService; // Para obtener promociones
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate; // Para promociones activas
import java.time.LocalTime; // Para promociones activas


@RestController
@RequestMapping("/api/v1/sucursales")
@CrossOrigin(origins = "*") // Ajustar según necesidades de seguridad
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private CategoriaService categoriaService; // Para obtener categorías por sucursal

    @Autowired
    private PromocionService promocionService; // Para obtener promociones por sucursal

    // Endpoint para crear una nueva sucursal
    // Usualmente restringido a Administradores
    @PostMapping("")
    public ResponseEntity<?> crearSucursal(@RequestBody SucursalCreateUpdateDTO sucursalCreateUpdateDTO) {
        try {
            SucursalDTO nuevaSucursal = sucursalService.createSucursal(sucursalCreateUpdateDTO);
            return new ResponseEntity<>(nuevaSucursal, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para obtener una sucursal por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSucursalPorId(@PathVariable Long id) {
        try {
            // El SucursalService.findById debería devolver la entidad, luego la convertimos a DTO.
            // O podríamos tener un método en SucursalService que ya devuelva el DTO.
            Optional<Sucursal> sucursalOptional = sucursalService.findById(id);
            if (sucursalOptional.isPresent()) {
                // Aquí usamos el convertToDTO del propio servicio de sucursal si lo tiene
                // o un helper local si el servicio devuelve la entidad.
                // Asumiendo que SucursalServiceImpl tiene un convertToDTO o lo implementamos aquí.
                return ResponseEntity.ok(convertToSucursalDTO(sucursalOptional.get()));
            } else {
                return new ResponseEntity<>("Sucursal no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para actualizar una sucursal
    // Usualmente restringido a Administradores
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSucursal(@PathVariable Long id, @RequestBody SucursalCreateUpdateDTO sucursalCreateUpdateDTO) {
        try {
            SucursalDTO sucursalActualizada = sucursalService.updateSucursal(id, sucursalCreateUpdateDTO);
            return ResponseEntity.ok(sucursalActualizada);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para listar todas las sucursales
    @GetMapping("")
    public ResponseEntity<?> listarSucursales() {
        try {
            List<Sucursal> sucursales = sucursalService.findAll();
            List<SucursalDTO> dtos = sucursales.stream()
                    .map(this::convertToSucursalDTO) // Usar helper
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener las categorías de una sucursal específica
    @GetMapping("/{id}/categorias")
    public ResponseEntity<?> obtenerCategoriasPorSucursal(@PathVariable Long id) {
        try {
            // Verificar si la sucursal existe
            if (!sucursalService.existsById(id)) {
                return new ResponseEntity<>("Sucursal no encontrada.", HttpStatus.NOT_FOUND);
            }
            List<Categoria> categorias = categoriaService.findBySucursalesId(id);
            List<CategoriaDTO> dtos = categorias.stream()
                    // Usar el convertToDTO (o simple) de CategoriaController o uno local
                    .map(this::convertToCategoriaDTOSimpleForSucursal)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener las promociones activas de una sucursal específica
    @GetMapping("/{id}/promociones/activas")
    public ResponseEntity<?> obtenerPromocionesActivasPorSucursal(@PathVariable Long id) {
        try {
            if (!sucursalService.existsById(id)) {
                return new ResponseEntity<>("Sucursal no encontrada.", HttpStatus.NOT_FOUND);
            }
            // Primero obtenemos todas las promociones de la sucursal
            List<PromocionDTO> promocionesDeLaSucursal = promocionService.findPromocionesBySucursalId(id);

            // Luego filtramos las que están activas por fecha y hora
            LocalDate hoy = LocalDate.now();
            LocalTime ahora = LocalTime.now();

            List<PromocionDTO> activas = promocionesDeLaSucursal.stream()
                    .filter(p -> !hoy.isBefore(p.getFechaDesde()) && !hoy.isAfter(p.getFechaHasta())) // Dentro del rango de fechas
                    .filter(p -> {
                        if (p.getHoraDesde() == null && p.getHoraHasta() == null) return true; // Si no hay hora, es activa todo el día
                        if (p.getHoraDesde() != null && p.getHoraHasta() == null) return !ahora.isBefore(p.getHoraDesde());
                        if (p.getHoraDesde() == null && p.getHoraHasta() != null) return !ahora.isAfter(p.getHoraHasta());
                        return !ahora.isBefore(p.getHoraDesde()) && !ahora.isAfter(p.getHoraHasta()); // Dentro del rango de horas
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(activas);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Endpoint para eliminar una sucursal
    // Usualmente restringido a Administradores
    // Considerar restricciones: no eliminar si tiene pedidos asociados, empleados, etc.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSucursal(@PathVariable Long id) {
        try {
            // Añadir lógica en el servicio para verificar dependencias antes de eliminar
            // (ej. pedidos no finalizados, empleados asignados, etc.)
            boolean eliminado = sucursalService.delete(id);
            if (eliminado) {
                return ResponseEntity.ok("Sucursal eliminada correctamente.");
            } else {
                return new ResponseEntity<>("Sucursal no encontrada para eliminar.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Podría ser DataIntegrityViolationException si hay FKs
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- Helpers para convertir a DTOs de Respuesta ---
    // (Estos métodos podrían estar en los respectivos servicios o en una clase Mapper dedicada)

    private SucursalDTO convertToSucursalDTO(Sucursal sucursal) {
        if (sucursal == null) return null;
        // Este método fue implementado en SucursalServiceImpl,
        // Idealmente, el servicio devolvería el DTO directamente o tendríamos un mapper.
        // Por ahora, replicamos una conversión simple aquí o llamamos a la del servicio si es accesible.
        // Para este ejemplo, asumiré que el SucursalService no tiene el convertToDTO público
        // o que preferimos hacerlo aquí.
        SucursalDTO dto = new SucursalDTO();
        dto.setId(sucursal.getId());
        dto.setNombre(sucursal.getNombre());
        dto.setHorarioApertura(sucursal.getHorarioApertura());
        dto.setHorarioCierre(sucursal.getHorarioCierre());

        if (sucursal.getEmpresa() != null) {
            dto.setEmpresaId(sucursal.getEmpresa().getId());
            // Podrías añadir un EmpresaSimpleDTO aquí si es necesario en la respuesta
        }
        if (sucursal.getDomicilio() != null) {
            // Convertir Domicilio a DomicilioDTO (similar a como se hizo en ClienteController)
            DomicilioDTO domDto = new DomicilioDTO(); // Necesitarías el DTO y su lógica de conversión
            domDto.setId(sucursal.getDomicilio().getId());
            domDto.setCalle(sucursal.getDomicilio().getCalle());
            domDto.setNumero(sucursal.getDomicilio().getNumero());
            domDto.setCp(sucursal.getDomicilio().getCp());
            if (sucursal.getDomicilio().getLocalidad() != null) {
                // Llenar localidad y su jerarquía
                LocalidadDTO locDto = new LocalidadDTO();
                locDto.setId(sucursal.getDomicilio().getLocalidad().getId());
                locDto.setNombre(sucursal.getDomicilio().getLocalidad().getNombre());
                if (sucursal.getDomicilio().getLocalidad().getProvincia() != null) {
                    ProvinciaDTO provDto = new ProvinciaDTO();
                    provDto.setId(sucursal.getDomicilio().getLocalidad().getProvincia().getId());
                    provDto.setNombre(sucursal.getDomicilio().getLocalidad().getProvincia().getNombre());
                    if (sucursal.getDomicilio().getLocalidad().getProvincia().getPais() != null) {
                        PaisDTO paisDto = new PaisDTO();
                        paisDto.setId(sucursal.getDomicilio().getLocalidad().getProvincia().getPais().getId());
                        paisDto.setNombre(sucursal.getDomicilio().getLocalidad().getProvincia().getPais().getNombre());
                        provDto.setPais(paisDto);
                    }
                    locDto.setProvincia(provDto);
                }
                domDto.setLocalidad(locDto);
            }
            dto.setDomicilio(domDto);
        }
        if (sucursal.getCategorias() != null) { //
            dto.setCategoriaIds(sucursal.getCategorias().stream().map(Categoria::getId).collect(Collectors.toList())); //
            // Para incluir los DTOs completos de categoría:
            dto.setCategorias(sucursal.getCategorias().stream() //
                    .map(this::convertToCategoriaDTOSimpleForSucursal)
                    .collect(Collectors.toList()));
        }
        // Podrías añadir una lista de PromocionSimpleDTO si fuera necesario.
        return dto;
    }

    private CategoriaDTO convertToCategoriaDTOSimpleForSucursal(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        // No incluir padre, subcategorías o sucursales aquí para evitar ciclos y mantenerlo simple
        return dto;
    }
}