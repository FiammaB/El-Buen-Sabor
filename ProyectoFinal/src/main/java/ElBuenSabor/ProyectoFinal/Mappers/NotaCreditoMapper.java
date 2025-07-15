package ElBuenSabor.ProyectoFinal.Mappers;


import ElBuenSabor.ProyectoFinal.DTO.NotaCreditoDTO;
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Entities.NotaCredito;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {DetallePedidoMapper.class, PersonaMapper.class}) // Usar otros mappers
public interface NotaCreditoMapper {

    @Mapping(target = "facturaAnuladaId", source = "facturaAnulada.id")
    @Mapping(target = "pedidoOriginalId", source = "pedidoOriginal.id")
    NotaCreditoDTO toDTO(NotaCredito notaCredito);

    @Mapping(target = "facturaAnulada", source = "facturaAnuladaId", qualifiedByName = "mapFacturaIdToFactura")
    @Mapping(target = "pedidoOriginal", source = "pedidoOriginalId", qualifiedByName = "mapPedidoIdToPedido")
    @Mapping(target = "persona", ignore = true) // Persona se asignará en el servicio/controlador
    NotaCredito toEntity(NotaCreditoDTO notaCreditoDTO);

    // Métodos auxiliares para MapStruct si los necesitas para toEntity desde ID
    @Named("mapFacturaIdToFactura")
    default Factura mapFacturaIdToFactura(Long id) {
        if (id == null) return null;
        Factura factura = new Factura(); // Solo crea un placeholder
        factura.setId(id);
        return factura;
    }

    @Named("mapPedidoIdToPedido")
    default Pedido mapPedidoIdToPedido(Long id) {
        if (id == null) return null;
        Pedido pedido = new Pedido(); // Solo crea un placeholder
        pedido.setId(id);
        return pedido;
    }
}
