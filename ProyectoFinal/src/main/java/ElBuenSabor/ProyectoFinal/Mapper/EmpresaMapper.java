package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.EmpresaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Empresa;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpresaMapper extends BaseMapper<Empresa, EmpresaDTO> {

    EmpresaDTO empresaToEmpresaDto(Empresa empresa);
    Empresa empresaDtoToEmpresa(EmpresaDTO empresaDTO);

    List<EmpresaDTO> empresaListToEmpresaDTOList(List<Empresa> empresas);
    List<Empresa> empresaDtoListToEmpresaList(List<EmpresaDTO> empresasDTO);

}
