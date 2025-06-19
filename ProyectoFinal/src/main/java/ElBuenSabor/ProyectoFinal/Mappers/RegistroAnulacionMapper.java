package ElBuenSabor.ProyectoFinal.Mappers;


import ElBuenSabor.ProyectoFinal.DTO.RegistroAnulacionDTO;
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Entities.NotaCredito;
import ElBuenSabor.ProyectoFinal.Entities.RegistroAnulacion;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring") // No necesita 'uses' para ID a entidad, a menos que sea un mapper complejo
public interface RegistroAnulacionMapper {

    @Mapping(target = "usuarioAnuladorId", source = "usuarioAnulador.id")
    @Mapping(target = "facturaAnuladaId", source = "facturaAnulada.id")
    @Mapping(target = "notaCreditoGeneradaId", source = "notaCreditoGenerada.id")
    RegistroAnulacionDTO toDTO(RegistroAnulacion registroAnulacion);

    @Mapping(target = "usuarioAnulador", source = "usuarioAnuladorId", qualifiedByName = "mapUsuarioIdToUsuario")
    @Mapping(target = "facturaAnulada", source = "facturaAnuladaId", qualifiedByName = "mapFacturaIdToFactura")
    @Mapping(target = "notaCreditoGenerada", source = "notaCreditoGeneradaId", qualifiedByName = "mapNotaCreditoIdToNotaCredito")
    RegistroAnulacion toEntity(RegistroAnulacionDTO registroAnulacionDTO);

    // Métodos auxiliares para MapStruct si los necesitas para toEntity desde ID
    @Named("mapUsuarioIdToUsuario")
    default Usuario mapUsuarioIdToUsuario(Long id) {
        if (id == null) return null;
        Usuario usuario = new Usuario(); // Solo crea un placeholder
        usuario.setId(id);
        return usuario;
    }

    @Named("mapFacturaIdToFactura")
    default Factura mapFacturaIdToFactura(Long id) {
        if (id == null) return null;
        Factura factura = new Factura(); // Solo crea un placeholder
        factura.setId(id);
        return factura;
    }

    @Named("mapNotaCreditoIdToNotaCredito")
    default NotaCredito mapNotaCreditoIdToNotaCredito(Long id) {
        if (id == null) return null;
        NotaCredito notaCredito = new NotaCredito(); // Solo crea un placeholder
        notaCredito.setId(id);
        return notaCredito;
    }
}