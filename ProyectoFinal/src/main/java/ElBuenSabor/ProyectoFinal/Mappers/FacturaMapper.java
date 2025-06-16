package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.FacturaCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FacturaMapper {

    FacturaDTO toDTO(Factura factura);

    Factura toEntity(FacturaDTO dto);

    @Mapping(target = "formaPago", source = "formaPago", qualifiedByName = "stringToFormaPago")
    Factura toEntity(FacturaCreateDTO dto);

    @Named("stringToFormaPago")
    default FormaPago stringToFormaPago(String formaPago) {
        return FormaPago.valueOf(formaPago.toUpperCase()); // Maneja bien EFECTIVO, efectivo, etc.
    }

}