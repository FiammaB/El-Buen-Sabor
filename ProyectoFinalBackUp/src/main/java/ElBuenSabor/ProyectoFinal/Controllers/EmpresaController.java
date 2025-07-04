package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.DTO.EmpresaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Empresa;
import ElBuenSabor.ProyectoFinal.Service.EmpresaService;
import ElBuenSabor.ProyectoFinal.Service.SucursalService; // Para listar sucursales de una empresa
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO; // Para la respuesta de sucursales
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;


@RestController
@RequestMapping("/api/v1/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private SucursalService sucursalService; // Para el helper convertToEmpresaDTO

    @PostMapping("")
    public ResponseEntity<?> createEmpresa(@RequestBody EmpresaDTO empresaDTO) {
        try {
            Empresa nuevaEmpresa = empresaService.createEmpresa(empresaDTO);
            return new ResponseEntity<>(convertToEmpresaDTO(nuevaEmpresa), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmpresaById(@PathVariable Long id) {
        try {
            Optional<Empresa> empresaOptional = empresaService.findById(id);
            if (empresaOptional.isPresent()) {
                return ResponseEntity.ok(convertToEmpresaDTO(empresaOptional.get()));
            } else {
                return new ResponseEntity<>("Empresa no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllEmpresas() {
        try {
            List<Empresa> empresas = empresaService.findAll();
            List<EmpresaDTO> dtos = empresas.stream()
                    .map(this::convertToEmpresaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpresa(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        try {
            Empresa empresaActualizada = empresaService.updateEmpresa(id, empresaDTO);
            return ResponseEntity.ok(convertToEmpresaDTO(empresaActualizada));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpresa(@PathVariable Long id) {
        try {
            // Considerar lógica de borrado: no permitir si tiene sucursales activas.
            // Esta lógica debería estar en el EmpresaService.delete o un método específico.
            boolean eliminado = empresaService.delete(id);
            if (eliminado) {
                return ResponseEntity.ok("Empresa eliminada correctamente.");
            } else {
                return new ResponseEntity<>("Empresa no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Helper para convertir Empresa a EmpresaDTO, incluyendo sucursales simples si es necesario
    private EmpresaDTO convertToEmpresaDTO(Empresa empresa) {
        if (empresa == null) return null;
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(empresa.getId());
        dto.setNombre(empresa.getNombre());
        dto.setRazonSocial(empresa.getRazonSocial());
        dto.setCuil(empresa.getCuil());

        // Convertir sucursales si existen y el DTO las espera
        if (empresa.getSucursales() != null && !empresa.getSucursales().isEmpty()) { //
            dto.setSucursales(empresa.getSucursales().stream().map(sucursal -> { //
                // Usar un SucursalSimpleDTO o el SucursalDTO completo si no es muy pesado
                // Aquí uso SucursalDTO pero limitado para evitar ciclos
                SucursalDTO sucDto = new SucursalDTO();
                sucDto.setId(sucursal.getId());
                sucDto.setNombre(sucursal.getNombre());
                sucDto.setHorarioApertura(sucursal.getHorarioApertura());
                sucDto.setHorarioCierre(sucursal.getHorarioCierre());
                // No incluir la empresa de nuevo aquí para evitar ciclo.
                // Domicilio podría ser un DomicilioSimpleDTO
                if (sucursal.getDomicilio() != null) {
                    DomicilioDTO domDto = new DomicilioDTO();
                    domDto.setId(sucursal.getDomicilio().getId());
                    domDto.setCalle(sucursal.getDomicilio().getCalle());
                    // ... más campos del domicilio si se necesitan
                    sucDto.setDomicilio(domDto);
                }
                return sucDto;
            }).collect(Collectors.toList()));
        } else {
            dto.setSucursales(new ArrayList<>());
        }
        return dto;
    }
}