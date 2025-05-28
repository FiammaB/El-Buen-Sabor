package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoResponseDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteSimpleResponseDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.SucursalSimpleDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.DetallePedidoDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoSimpleDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoSimpleDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.PaisDTO; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO; // Para el helper DTO


import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.Usuario; // Para el actor
import ElBuenSabor.ProyectoFinal.Entities.Empleado; // Para castear el actor
import ElBuenSabor.ProyectoFinal.Entities.Pedido; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.Cliente; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.Sucursal; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.Domicilio; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.Factura; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.DetallePedido; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.Localidad; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.Provincia; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.Pais; // Para el helper DTO
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida; // Para el helper DTO


import ElBuenSabor.ProyectoFinal.Service.PedidoService;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService; // Para buscar el actor si solo tenemos ID
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Para el Optional de Usuario
import java.util.Set; // Para el Set de DetallePedidoDTO
import java.util.HashSet; // Para el Set de DetallePedidoDTO
import java.util.stream.Collectors; // Para el Set de DetallePedidoDTO



@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService; // Para cargar el 'actor' si es necesario

    @PostMapping("")
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoCreateDTO pedidoCreateDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario actorCliente = null;
            if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
                actorCliente = (Usuario) authentication.getPrincipal();
            } else {
                if (pedidoCreateDTO.getClienteId() != null) {
                    // Esta es una solución temporal para pruebas sin seguridad completa.
                    // En producción, el actor DEBERÍA venir del contexto de seguridad.
                    actorCliente = usuarioService.findByIdIncludingDeleted(pedidoCreateDTO.getClienteId())
                            .orElseThrow(() -> new Exception("Cliente del pedido (ID: " + pedidoCreateDTO.getClienteId() + ") no encontrado para la creación del pedido."));
                    if (!(actorCliente instanceof Cliente)) {
                        throw new Exception("El ID proporcionado no corresponde a un Cliente.");
                    }
                } else {
                    return new ResponseEntity<>("Falta clienteId en el DTO y no hay actor autenticado.", HttpStatus.BAD_REQUEST);
                }
                System.err.println("ADVERTENCIA: Actor cliente para crearPedido obtenido a través de clienteId del DTO. Asegurar con Spring Security.");
            }

            PedidoResponseDTO nuevoPedido = pedidoService.crearPedido(pedidoCreateDTO, actorCliente);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long id) {
        try {
            PedidoResponseDTO pedido = pedidoService.findPedidoByIdDTO(id); // CORREGIDO: Nombre del método
            if (pedido == null) { // El servicio devuelve DTO o null si no se encuentra (activo)
                return new ResponseEntity<>("Pedido activo no encontrado.", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> obtenerPedidosPorCliente(@PathVariable Long clienteId) {
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findPedidosByClienteId(clienteId); // CORREGIDO: Nombre del método
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPedidosPorEstado(@PathVariable Estado estado,
                                                     @RequestParam(defaultValue = "true") boolean soloActivos) {
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findPedidosByEstado(estado, soloActivos); // CORREGIDO: Nombre y parámetro
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sucursal/{sucursalId}/estado/{estado}")
    public ResponseEntity<?> obtenerPedidosPorSucursalYEstado(@PathVariable Long sucursalId,
                                                              @PathVariable Estado estado,
                                                              @RequestParam(defaultValue = "true") boolean soloActivos) {
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findPedidosBySucursalAndEstado(sucursalId, estado, soloActivos);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para listar todos los pedidos (generalmente para admin)
    @GetMapping("")
    public ResponseEntity<?> listarTodosLosPedidos(@RequestParam(defaultValue = "true") boolean soloActivos) {
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findAllPedidosDTO(soloActivos); // CORREGIDO: Nombre y parámetro
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/cambiar-estado")
    public ResponseEntity<?> cambiarEstadoPedido(@PathVariable Long id, @RequestParam Estado nuevoEstado) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario actorEmpleado = null;
            if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
                actorEmpleado = (Usuario) authentication.getPrincipal();
                if (!(actorEmpleado instanceof Empleado)){
                    return new ResponseEntity<>("Acción no permitida: el actor debe ser un Empleado.", HttpStatus.FORBIDDEN);
                }
            } else {
                System.err.println("ADVERTENCIA: Actor empleado no obtenido de SecurityContext para cambiarEstadoPedido. La operación podría fallar si el servicio lo requiere.");
                // Para pruebas sin seguridad, si el servicio no puede manejar actor nulo, esto fallará.
                // Podrías simular un actor aquí para pruebas, pero es un hack.
                // Ejemplo de simulación (NO PARA PRODUCCIÓN):
                // Empleado mockCajero = new Empleado(); mockCajero.setId(-1L); /* Setear un ID temporal */ mockCajero.setRol(Rol.CAJERO); actorEmpleado = mockCajero;
                // O lanzar un error si es estrictamente necesario:
                return new ResponseEntity<>("Se requiere un empleado autenticado para cambiar el estado del pedido.", HttpStatus.UNAUTHORIZED);
            }

            PedidoResponseDTO pedidoActualizado = pedidoService.cambiarEstadoPedido(id, nuevoEstado, actorEmpleado); // CORREGIDO: Pasar actorEmpleado
            return ResponseEntity.ok(pedidoActualizado);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("No tiene permisos") || e.getMessage().contains("Solo un") || e.getMessage().contains("Acción no permitida")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}/anular")
    public ResponseEntity<?> anularPedido(@PathVariable Long id) {
        try {
            pedidoService.softDelete(id);
            return ResponseEntity.ok("Pedido ID: " + id + " anulado correctamente.");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("ya está anulado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al anular el pedido: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/admin/todos")
    public ResponseEntity<?> getAllPedidosIncludingAnuladosForAdmin() {
        try {
            List<PedidoResponseDTO> dtos = pedidoService.findAllPedidosIncludingAnulados();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todos los pedidos (admin): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getPedidoByIdIncludingAnuladoForAdmin(@PathVariable Long id) {
        try {
            PedidoResponseDTO dto = pedidoService.findPedidoByIdIncludingAnulado(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Pedido (activo o anulado) no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el pedido (admin): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/admin/{id}/reactivar")
    public ResponseEntity<?> reactivarPedidoAdmin(@PathVariable Long id) {
        try {
            Pedido pedidoReactivadoEntity = pedidoService.reactivate(id);
            return ResponseEntity.ok(convertToDTOController(pedidoReactivadoEntity));
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("no está anulado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar el pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para convertir Pedido a PedidoResponseDTO (si el servicio devuelve entidad)
    // Idealmente, este helper estaría en el servicio o en una clase Mapper dedicada.
    private PedidoResponseDTO convertToDTOController(Pedido pedido) {
        if (pedido == null) return null;
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setHoraEstimadaFinalizacion(pedido.getHoraEstimadaFinalizacion());
        dto.setTotal(pedido.getTotal());
        dto.setTotalCosto(pedido.getTotalCosto());
        dto.setMontoDescuento(pedido.getMontoDescuento());
        dto.setEstado(pedido.getEstado());
        dto.setTipoEnvio(pedido.getTipoEnvio());
        dto.setFormaPago(pedido.getFormaPago());
        dto.setBaja(pedido.isBaja());

        if (pedido.getCliente() != null) {
            ClienteSimpleResponseDTO clienteDTO = new ClienteSimpleResponseDTO();
            clienteDTO.setId(pedido.getCliente().getId());
            clienteDTO.setNombre(pedido.getCliente().getNombre());
            clienteDTO.setApellido(pedido.getCliente().getApellido());
            clienteDTO.setEmail(pedido.getCliente().getEmail());
            clienteDTO.setTelefono(pedido.getCliente().getTelefono());
            clienteDTO.setBaja(pedido.getCliente().isBaja());
            dto.setCliente(clienteDTO);
        }

        if (pedido.getDomicilioEntrega() != null) {
            dto.setDomicilioEntrega(convertToDomicilioDTO(pedido.getDomicilioEntrega()));
        }

        if (pedido.getSucursal() != null) {
            SucursalSimpleDTO sucursalDTO = new SucursalSimpleDTO();
            sucursalDTO.setId(pedido.getSucursal().getId());
            sucursalDTO.setNombre(pedido.getSucursal().getNombre());
            sucursalDTO.setBaja(pedido.getSucursal().isBaja());
            dto.setSucursal(sucursalDTO);
        }

        if (pedido.getFactura() != null) {
            dto.setFactura(convertToFacturaDTO(pedido.getFactura()));
        }

        if (pedido.getDetallesPedidos() != null) {
            dto.setDetallesPedidos(pedido.getDetallesPedidos().stream()
                    .map(this::convertToDetallePedidoDTO).collect(Collectors.toSet()));
        }
        return dto;
    }

    private DetallePedidoDTO convertToDetallePedidoDTO(DetallePedido detalle) {
        if (detalle == null) return null;
        DetallePedidoDTO dto = new DetallePedidoDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setSubTotal(detalle.getSubTotal());
        dto.setBaja(detalle.isBaja());

        if (detalle.getArticuloManufacturado() != null) {
            dto.setArticuloManufacturado(convertToArticuloManufacturadoSimpleDTO(detalle.getArticuloManufacturado()));
        }
        if (detalle.getArticuloInsumo() != null) {
            dto.setArticuloInsumo(convertToArticuloInsumoSimpleDTO(detalle.getArticuloInsumo()));
        }
        return dto;
    }

    private DomicilioDTO convertToDomicilioDTO(Domicilio domicilio) {
        if (domicilio == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(domicilio.getId());
        dto.setCalle(domicilio.getCalle());
        dto.setNumero(domicilio.getNumero());
        dto.setCp(domicilio.getCp());
        dto.setBaja(domicilio.isBaja());
        if (domicilio.getLocalidad() != null) {
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(domicilio.getLocalidad().getId());
            locDto.setNombre(domicilio.getLocalidad().getNombre());
            locDto.setBaja(domicilio.getLocalidad().isBaja());
            if (domicilio.getLocalidad().getProvincia() != null) {
                ProvinciaDTO provDto = new ProvinciaDTO();
                provDto.setId(domicilio.getLocalidad().getProvincia().getId());
                provDto.setNombre(domicilio.getLocalidad().getProvincia().getNombre());
                provDto.setBaja(domicilio.getLocalidad().getProvincia().isBaja());
                if (domicilio.getLocalidad().getProvincia().getPais() != null) {
                    PaisDTO paisDto = new PaisDTO();
                    paisDto.setId(domicilio.getLocalidad().getProvincia().getPais().getId());
                    paisDto.setNombre(domicilio.getLocalidad().getProvincia().getPais().getNombre());
                    paisDto.setBaja(domicilio.getLocalidad().getProvincia().getPais().isBaja());
                    provDto.setPais(paisDto);
                }
                locDto.setProvincia(provDto);
            }
            dto.setLocalidad(locDto);
        }
        return dto;
    }

    private FacturaDTO convertToFacturaDTO(Factura factura) {
        if (factura == null) return null;
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setFechaFacturacion(factura.getFechaFacturacion());
        dto.setTotalVenta(factura.getTotalVenta());
        dto.setFormaPago(factura.getFormaPago());
        dto.setBaja(factura.isBaja());
        dto.setMpPaymentId(factura.getMpPaymentId());
        dto.setMpMerchantOrderId(factura.getMpMerchantOrderId());
        dto.setMpPreferenceId(factura.getMpPreferenceId());
        dto.setMpPaymentType(factura.getMpPaymentType());
        // No intentamos cargar el pedidoId desde aquí para evitar llamadas extras a la BD en el DTO de factura
        return dto;
    }

    private ArticuloManufacturadoSimpleDTO convertToArticuloManufacturadoSimpleDTO(ArticuloManufacturado am){
        if(am == null) return null;
        ArticuloManufacturadoSimpleDTO dto = new ArticuloManufacturadoSimpleDTO();
        dto.setId(am.getId());
        dto.setDenominacion(am.getDenominacion());
        dto.setPrecioVenta(am.getPrecioVenta());
        dto.setBaja(am.isBaja());
        return dto;
    }

    private ArticuloInsumoSimpleDTO convertToArticuloInsumoSimpleDTO(ArticuloInsumo ai){
        if(ai == null) return null;
        ArticuloInsumoSimpleDTO dto = new ArticuloInsumoSimpleDTO();
        dto.setId(ai.getId());
        dto.setDenominacion(ai.getDenominacion());
        dto.setPrecioVenta(ai.getPrecioVenta());
        dto.setBaja(ai.isBaja());
        if (ai.getUnidadMedida() != null) {
            UnidadMedidaDTO umDto = new UnidadMedidaDTO();
            umDto.setId(ai.getUnidadMedida().getId());
            umDto.setDenominacion(ai.getUnidadMedida().getDenominacion());
            umDto.setBaja(ai.getUnidadMedida().isBaja());
            dto.setUnidadMedida(umDto);
        }
        return dto;
    }
}
