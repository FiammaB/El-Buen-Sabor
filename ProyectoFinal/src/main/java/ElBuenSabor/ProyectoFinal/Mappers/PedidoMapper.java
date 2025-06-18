package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        ClienteMapper.class,
        DomicilioMapper.class,
        SucursalMapper.class,
        UsuarioMapper.class,       // ← para el empleado
        FacturaMapper.class,
        DetallePedidoMapper.class
})
public interface PedidoMapper {
    @Mapping(target = "domicilio", source = "domicilioEntrega") // Mapear domicilioEntrega de la entidad a domicilio del DTO
    @Mapping(target = "detalles", source = "detallesPedidos")
    PedidoDTO toDTO(Pedido pedido);

    @Mapping(target = "domicilioEntrega", ignore = true) // El servicio asignará el objeto Domicilio
    @Mapping(target = "detallesPedidos", ignore = true) // El servicio asignará la colección Set<DetallePedido>
    @Mapping(target = "cliente", ignore = true) // El servicio asignará el objeto Cliente
    @Mapping(target = "sucursal", ignore = true) // El servicio asignará el objeto Sucursal
    @Mapping(target = "empleado", ignore = true) // El servicio asignará el objeto Usuario
    @Mapping(target = "factura", ignore = true)
    Pedido toEntity(PedidoCreateDTO dto);
}
