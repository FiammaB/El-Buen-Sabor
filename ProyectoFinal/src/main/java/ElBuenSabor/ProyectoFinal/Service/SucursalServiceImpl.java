package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SucursalServiceImpl extends BaseServiceImpl<Sucursal, Long> implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final EmpresaRepository empresaRepository;
    private final DomicilioRepository domicilioRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public SucursalServiceImpl(SucursalRepository sucursalRepository,
                               EmpresaRepository empresaRepository,
                               DomicilioRepository domicilioRepository,
                               LocalidadRepository localidadRepository,
                               ProvinciaRepository provinciaRepository,
                               PaisRepository paisRepository,
                               CategoriaRepository categoriaRepository) {
        super(sucursalRepository);
        this.sucursalRepository = sucursalRepository;
        this.empresaRepository = empresaRepository;
        this.domicilioRepository = domicilioRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public SucursalDTO createSucursal(SucursalCreateUpdateDTO dto) throws Exception {
        try {
            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new Exception("Empresa no encontrada con ID: " + dto.getEmpresaId()));

            // Crear o encontrar Pais, Provincia, Localidad para el Domicilio
            DomicilioCreateUpdateDTO domicilioDTO = dto.getDomicilio();
            Pais pais = paisRepository.findByNombre(domicilioDTO.getNombrePais()); // Asumiendo que DomicilioCreateUpdateDTO tiene estos campos
            if (pais == null) {
                pais = Pais.builder().nombre(domicilioDTO.getNombrePais()).build();
                pais = paisRepository.save(pais);
            }
            Provincia provincia = provinciaRepository.findByNombre(domicilioDTO.getNombreProvincia());
            if (provincia == null) {
                provincia = Provincia.builder().nombre(domicilioDTO.getNombreProvincia()).pais(pais).build();
                provincia = provinciaRepository.save(provincia);
            }
            Localidad localidad = localidadRepository.findByNombre(domicilioDTO.getNombreLocalidad());
            if (localidad == null) {
                localidad = Localidad.builder().nombre(domicilioDTO.getNombreLocalidad()).provincia(provincia).build();
                localidad = localidadRepository.save(localidad);
            }

            Domicilio domicilio = Domicilio.builder()
                    .calle(domicilioDTO.getCalle())
                    .numero(domicilioDTO.getNumero())
                    .cp(domicilioDTO.getCp())
                    .localidad(localidad)
                    .build();
            domicilio = domicilioRepository.save(domicilio);

            Sucursal sucursal = Sucursal.builder()
                    .nombre(dto.getNombre())
                    .horarioApertura(dto.getHorarioApertura())
                    .horarioCierre(dto.getHorarioCierre())
                    .empresa(empresa)
                    .domicilio(domicilio)
                    .build();

            if (dto.getCategoriaIds() != null && !dto.getCategoriaIds().isEmpty()) {
                List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategoriaIds());
                if (categorias.size() != dto.getCategoriaIds().size()) {
                    throw new Exception("Algunas categorías especificadas para la sucursal no fueron encontradas.");
                }
                sucursal.setCategorias(categorias); // Asumiendo que Sucursal.categorias es List
                // Actualizar el lado inverso si es necesario para la tabla de unión
                for (Categoria cat : categorias) {
                    if (cat.getSucursales() == null) cat.setSucursales(new HashSet<>());
                    cat.getSucursales().add(sucursal);
                }
            }

            Sucursal savedSucursal = sucursalRepository.save(sucursal);
            return convertToDTO(savedSucursal);
        } catch (Exception e) {
            throw new Exception("Error al crear la sucursal: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public SucursalDTO updateSucursal(Long id, SucursalCreateUpdateDTO dto) throws Exception {
        try {
            Sucursal sucursal = sucursalRepository.findById(id)
                    .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + id));

            Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new Exception("Empresa no encontrada con ID: " + dto.getEmpresaId()));

            // Actualizar Domicilio
            Domicilio domicilio = sucursal.getDomicilio();
            if (domicilio == null) { // Debería existir si la sucursal existe
                throw new Exception("El domicilio de la sucursal no puede ser nulo para la actualización.");
            }
            DomicilioCreateUpdateDTO domicilioDTO = dto.getDomicilio();
            Pais pais = paisRepository.findByNombre(domicilioDTO.getNombrePais());
            if (pais == null) {
                pais = Pais.builder().nombre(domicilioDTO.getNombrePais()).build();
                pais = paisRepository.save(pais);
            }
            Provincia provincia = provinciaRepository.findByNombre(domicilioDTO.getNombreProvincia());
            if (provincia == null) {
                provincia = Provincia.builder().nombre(domicilioDTO.getNombreProvincia()).pais(pais).build();
                provincia = provinciaRepository.save(provincia);
            }
            Localidad localidad = localidadRepository.findByNombre(domicilioDTO.getNombreLocalidad());
            if (localidad == null) {
                localidad = Localidad.builder().nombre(domicilioDTO.getNombreLocalidad()).provincia(provincia).build();
                localidad = localidadRepository.save(localidad);
            }
            domicilio.setCalle(domicilioDTO.getCalle());
            domicilio.setNumero(domicilioDTO.getNumero());
            domicilio.setCp(domicilioDTO.getCp());
            domicilio.setLocalidad(localidad);
            domicilioRepository.save(domicilio);

            sucursal.setNombre(dto.getNombre());
            sucursal.setHorarioApertura(dto.getHorarioApertura());
            sucursal.setHorarioCierre(dto.getHorarioCierre());
            sucursal.setEmpresa(empresa);
            // sucursal.setDomicilio(domicilio); // Ya está asociado

            // Actualizar categorías
            // Desvincular categorías antiguas
            if (sucursal.getCategorias() != null) {
                for (Categoria catActual : new HashSet<>(sucursal.getCategorias())) { // Iterar sobre una copia
                    if (dto.getCategoriaIds() == null || !dto.getCategoriaIds().contains(catActual.getId())) {
                        sucursal.getCategorias().remove(catActual);
                        catActual.getSucursales().remove(sucursal); // Mantener consistencia bidireccional
                    }
                }
            } else {
                sucursal.setCategorias(new ArrayList<>());
            }

            // Vincular nuevas categorías
            if (dto.getCategoriaIds() != null && !dto.getCategoriaIds().isEmpty()) {
                List<Long> idsActuales = sucursal.getCategorias().stream().map(BaseEntity::getId).collect(Collectors.toList());
                for (Long catIdNuevo : dto.getCategoriaIds()) {
                    if (!idsActuales.contains(catIdNuevo)) {
                        Categoria catNueva = categoriaRepository.findById(catIdNuevo)
                                .orElseThrow(() -> new Exception("Categoría con ID " + catIdNuevo + " no encontrada."));
                        sucursal.getCategorias().add(catNueva);
                        if (catNueva.getSucursales() == null) catNueva.setSucursales(new HashSet<>());
                        catNueva.getSucursales().add(sucursal); // Mantener consistencia bidireccional
                    }
                }
            }


            Sucursal updatedSucursal = sucursalRepository.save(sucursal);
            return convertToDTO(updatedSucursal);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la sucursal: " + e.getMessage(), e);
        }
    }

    private SucursalDTO convertToDTO(Sucursal sucursal) {
        if (sucursal == null) return null;
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
            // Convertir Domicilio a DomicilioDTO
            Domicilio domicilioEnt = sucursal.getDomicilio();
            DomicilioDTO domDto = new DomicilioDTO();
            domDto.setId(domicilioEnt.getId());
            domDto.setCalle(domicilioEnt.getCalle());
            domDto.setNumero(domicilioEnt.getNumero());
            domDto.setCp(domicilioEnt.getCp());
            if (domicilioEnt.getLocalidad() != null) {
                Localidad locEnt = domicilioEnt.getLocalidad();
                LocalidadDTO locDto = new LocalidadDTO();
                locDto.setId(locEnt.getId());
                locDto.setNombre(locEnt.getNombre());
                // Mapear Provincia y Pais si el DTO lo requiere
                domDto.setLocalidad(locDto);
            }
            dto.setDomicilio(domDto);
        }
        if (sucursal.getCategorias() != null) {
            dto.setCategorias(sucursal.getCategorias().stream().map(cat -> {
                CategoriaDTO catDto = new CategoriaDTO();
                catDto.setId(cat.getId());
                catDto.setDenominacion(cat.getDenominacion());
                // No incluir subcategorías o padre aquí para evitar ciclos/respuestas enormes
                return catDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}