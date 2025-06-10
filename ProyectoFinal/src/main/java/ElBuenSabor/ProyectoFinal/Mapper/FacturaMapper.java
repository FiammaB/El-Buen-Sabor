package ElBuenSabor.ProyectoFinal.Mapper;


import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacturaMapper extends BaseMapper<Factura, FacturaDTO> {

    FacturaDTO facturaToFacturaDto(Factura factura);
    Factura facturaDtoToFactura(FacturaDTO facturaDTO);

    List<FacturaDTO> facturaListToFacturaDtoList(List<Factura> facturas);
    List<Factura> facturaDtoListToFactura(List<FacturaDTO> facturasDTO);

}
