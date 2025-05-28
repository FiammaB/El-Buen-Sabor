package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.EmpresaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.EmpresaDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO; // Para el endpoint de sucursales por empresa
import ElBuenSabor.ProyectoFinal.DTO.SucursalSimpleDTO;
import ElBuenSabor.ProyectoFinal.Entities.Empresa; // Para el tipo de retorno de reactivate
import ElBuenSabor.ProyectoFinal.Service.EmpresaService;
import ElBuenSabor.ProyectoFinal.Service.SucursalService; // Para obtener sucursales
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private SucursalService sucursalService; // Para listar sucursales de una empresa

    @PostMapping("")
    public ResponseEntity<?> createEmpresa(@Valid @RequestBody EmpresaCreateUpdateDTO empresaDTO) {
        try {
            EmpresaDTO nuevaEmpresa = empresaService.createEmpresa(empresaDTO);
            return new ResponseEntity<>(nuevaEmpresa, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmpresaById(@PathVariable Long id) {
        try {
            EmpresaDTO dto = empresaService.findEmpresaById(id); // Devuelve activas
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Empresa activa no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la empresa: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllEmpresasActivas() {
        try {
            List<EmpresaDTO> dtos = empresaService.findAllEmpresas(); // Devuelve DTOs de activas
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener empresas activas: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpresa(@PathVariable Long id, @Valid @RequestBody EmpresaCreateUpdateDTO empresaDTO) {
        try {
            EmpresaDTO empresaActualizada = empresaService.updateEmpresa(id, empresaDTO);
            return ResponseEntity.ok(empresaActualizada);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaEmpresa(@PathVariable Long id) {
        try {
            empresaService.softDelete(id); // El servicio ahora verifica si tiene sucursales activas
            return ResponseEntity.ok("Empresa dada de baja correctamente (borrado lógico).");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("ya está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("tiene sucursales activas")) { // Mensaje del servicio
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
            }
            return new ResponseEntity<>("Error al dar de baja la empresa: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarEmpresa(@PathVariable Long id) {
        try {
            Empresa empresaReactivadaEntity = empresaService.reactivate(id);
            // Necesitamos un método para convertir Empresa a EmpresaDTO si el servicio devuelve la entidad
            return ResponseEntity.ok(convertToEmpresaDTO(empresaReactivadaEntity));
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar la empresa: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEmpresasIncludingDeletedForAdmin() {
        try {
            List<EmpresaDTO> dtos = empresaService.findAllEmpresasIncludingDeleted();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todas las empresas (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEmpresaByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            EmpresaDTO dto = empresaService.findEmpresaByIdIncludingDeleted(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Empresa (activa o inactiva) no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la empresa (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener las sucursales (activas) de una empresa específica
    @GetMapping("/{empresaId}/sucursales")
    public ResponseEntity<?> obtenerSucursalesPorEmpresa(@PathVariable Long empresaId) {
        try {
            EmpresaDTO empresa = empresaService.findEmpresaById(empresaId); // Verifica si la empresa activa existe
            if (empresa == null) {
                return new ResponseEntity<>("Empresa activa no encontrada con ID: " + empresaId, HttpStatus.NOT_FOUND);
            }
            // El SucursalService debería tener un método findByEmpresaId que devuelva DTOs de sucursales activas
            // List<SucursalDTO> sucursales = sucursalService.findByEmpresaIdAndBajaFalse(empresaId);
            // Por ahora, si EmpresaDTO ya las carga (filtradas o no):
            if (empresa.getSucursales() != null) {
                // Filtrar para mostrar solo sucursales activas si el convertToDTO de Empresa no lo hizo ya
                List<SucursalSimpleDTO> sucursalesActivas = empresa.getSucursales().stream()
                        .filter(s -> !s.isBaja())
                        .collect(Collectors.toList());
                return ResponseEntity.ok(sucursalesActivas);
            }
            return ResponseEntity.ok(List.of()); // Lista vacía si no hay sucursales
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener las sucursales de la empresa: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para convertir Empresa a EmpresaDTO (si el servicio devuelve la entidad)
    private EmpresaDTO convertToEmpresaDTO(Empresa empresa) {
        if (empresa == null) return null;
        // Esta lógica ya está en EmpresaServiceImpl. Si el servicio siempre devuelve DTOs,
        // este helper en el controlador no es estrictamente necesario para todos los casos.
        // Pero es útil si 'reactivate' devuelve la entidad.
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(empresa.getId());
        dto.setNombre(empresa.getNombre());
        dto.setRazonSocial(empresa.getRazonSocial());
        dto.setCuil(empresa.getCuil());
        dto.setBaja(empresa.isBaja());
        if (empresa.getSucursales() != null) {
            dto.setSucursales(empresa.getSucursales().stream().map(sucursal -> {
                SucursalSimpleDTO sucDto = new SucursalSimpleDTO();
                sucDto.setId(sucursal.getId());
                sucDto.setNombre(sucursal.getNombre());
                sucDto.setBaja(sucursal.isBaja());
                // ... otros campos para SucursalSimpleDTO
                return sucDto;
            }).collect(Collectors.toList()));
        } else {
            dto.setSucursales(new ArrayList<>());
        }
        return dto;
    }
}
