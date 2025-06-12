package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromocionServiceImpl extends BaseServiceImpl<Promocion, Long> implements PromocionService {

    private final PromocionRepository promocionRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final SucursalRepository sucursalRepository;
    private final ImagenRepository imagenRepository;

    @Autowired
    public PromocionServiceImpl(PromocionRepository promocionRepository,
                                ArticuloManufacturadoRepository articuloManufacturadoRepository,
                                SucursalRepository sucursalRepository,
                                ImagenRepository imagenRepository) {
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.sucursalRepository = sucursalRepository;
        this.imagenRepository = imagenRepository;
    }

    @Override
    @Transactional
    public PromocionDTO createPromocion(PromocionDTO dto) throws Exception {
        try {
            Promocion promocion = new Promocion();
            mapDtoToEntity(dto, promocion); // Método helper para mapear

            // Manejar imagen
            if (dto.getImagenId() != null) {
                Imagen imagen = imagenRepository.findById(dto.getImagenId())
                        .orElseThrow(() -> new Exception("Imagen no encontrada con ID: " + dto.getImagenId()));
                promocion.setImagen(imagen);
            } else if (dto.getImagen() != null && dto.getImagen().getDenominacion() != null && !dto.getImagen().getDenominacion().isEmpty()) {
                Imagen nuevaImagen = Imagen.builder().denominacion(dto.getImagen().getDenominacion()).build();
                promocion.setImagen(imagenRepository.save(nuevaImagen));
            }


            Promocion savedPromocion = promocionRepository.save(promocion);
            return convertToDTO(savedPromocion);
        } catch (Exception e) {
            throw new Exception("Error al crear la promoción: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PromocionDTO updatePromocion(Long id, PromocionDTO dto) throws Exception {
        try {
            Promocion promocion = promocionRepository.findById(id)
                    .orElseThrow(() -> new Exception("Promoción no encontrada con ID: " + id));
            mapDtoToEntity(dto, promocion);

            // Manejar imagen
            if (dto.getImagenId() != null) {
                if(dto.getImagenId() == 0L){ // Convención para quitar imagen
                    if(promocion.getImagen() != null) {
                        Imagen imgActual = promocion.getImagen();
                        promocion.setImagen(null);
                        // Opcional: borrar la imagen de ImagenRepository si no está referenciada en otro lugar
                        // imagenRepository.delete(imgActual); (cuidado con esto)
                    }
                } else {
                    Imagen imagen = imagenRepository.findById(dto.getImagenId())
                            .orElseThrow(() -> new Exception("Imagen no encontrada con ID: " + dto.getImagenId()));
                    promocion.setImagen(imagen);
                }
            } else if (dto.getImagen() != null && dto.getImagen().getDenominacion() != null && !dto.getImagen().getDenominacion().isEmpty()) {
                Imagen imagenExistente = promocion.getImagen();
                if (imagenExistente != null) {
                    imagenExistente.setDenominacion(dto.getImagen().getDenominacion());
                    promocion.setImagen(imagenRepository.save(imagenExistente));
                } else {
                    Imagen nuevaImagen = Imagen.builder().denominacion(dto.getImagen().getDenominacion()).build();
                    promocion.setImagen(imagenRepository.save(nuevaImagen));
                }
            }


            Promocion updatedPromocion = promocionRepository.save(promocion);
            return convertToDTO(updatedPromocion);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la promoción: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> findActivePromociones(LocalDate fechaActual, LocalTime horaActual) throws Exception { //
        List<Promocion> promociones = promocionRepository.findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqualAndHoraDesdeLessThanEqualAndHoraHastaGreaterThanEqual(
                fechaActual, fechaActual, horaActual, horaActual
        );
        return promociones.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> findPromocionesBySucursalId(Long sucursalId) throws Exception { //
        // Necesitas una forma de obtener promociones por sucursal.
        // Si la relación es Promocion ManyToMany Sucursal (Promocion dueña), usa:
        // List<Promocion> promociones = promocionRepository.findBySucursalesId(sucursalId);
        // Si Sucursal es dueña (Sucursal ManyToMany Promocion), entonces:
        Sucursal sucursal = sucursalRepository.findById(sucursalId).orElse(null);
        if (sucursal == null || sucursal.getPromociones() == null) return new ArrayList<>(); // Asumiendo que Sucursal tiene un Set<Promocion> promociones
        // return sucursal.getPromociones().stream().map(this::convertToDTO).collect(Collectors.toList());
        // La forma más directa desde PromocionRepository es si tienes un método como findBySucursalesId
        // Basado en PromocionRepository.findBySucursalesId(Long sucursalId);
        List<Promocion> promociones = promocionRepository.findBySucursalesId(sucursalId);
        return promociones.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    private void mapDtoToEntity(PromocionDTO dto, Promocion promocion) throws Exception {
        promocion.setDenominacion(dto.getDenominacion());
        promocion.setFechaDesde(dto.getFechaDesde());
        promocion.setFechaHasta(dto.getFechaHasta());
        promocion.setHoraDesde(dto.getHoraDesde());
        promocion.setHoraHasta(dto.getHoraHasta());
        promocion.setDescripcionDescuento(dto.getDescripcionDescuento());
        promocion.setPrecioPromocional(dto.getPrecioPromocional());
        promocion.setTipoPromocion(dto.getTipoPromocion());

        if (dto.getArticuloManufacturadoIds() != null) {
            List<ArticuloManufacturado> articulos = articuloManufacturadoRepository.findAllById(dto.getArticuloManufacturadoIds());
            if (articulos.size() != dto.getArticuloManufacturadoIds().size()) {
                throw new Exception("Algunos artículos manufacturados especificados para la promoción no fueron encontrados.");
            }
            promocion.setArticulosManufacturados(articulos);
        } else {
            if (promocion.getArticulosManufacturados() != null) promocion.getArticulosManufacturados().clear();
            else promocion.setArticulosManufacturados(new ArrayList<>());
        }

        // Manejar la relación ManyToMany con Sucursal
        // Promocion es la dueña de la relación mappedBy en Sucursal.promociones
        // por lo que Promocion.sucursales es la que se persiste en la tabla de unión.
        Set<Sucursal> sucursalesNuevas = new HashSet<>();
        if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
            List<Sucursal> sucursalesEncontradas = sucursalRepository.findAllById(dto.getSucursalIds());
            if (sucursalesEncontradas.size() != dto.getSucursalIds().size()) {
                throw new Exception("Algunas sucursales especificadas para la promoción no fueron encontradas.");
            }
            sucursalesNuevas.addAll(sucursalesEncontradas);
        }

        // Actualizar la colección en Promocion
        if (promocion.getSucursales() == null) {
            promocion.setSucursales(new HashSet<>());
        }
        promocion.getSucursales().clear();
        promocion.getSucursales().addAll(sucursalesNuevas);

        // Sincronizar el lado inverso (Sucursal)
        // 1. Quitar esta promoción de las sucursales que ya no la tienen
        // Este paso es complejo si no se carga la colección completa de sucursales antiguas.
        // Una forma más simple es confiar en que la actualización de promocion.getSucursales()
        // maneje la tabla de unión. Para consistencia bidireccional en memoria:
        // (Este código asume que queremos mantener la colección Sucursal.promociones actualizada)
        // Set<Sucursal> sucursalesAntiguas = promocion.getSucursales() != null ? new HashSet<>(promocion.getSucursales()) : new HashSet<>();
        // sucursalesAntiguas.stream()
        //     .filter(sAntigua -> !sucursalesNuevas.contains(sAntigua))
        //     .forEach(sAntigua -> { if (sAntigua.getPromociones() != null) sAntigua.getPromociones().remove(promocion); });
        //
        // // 2. Añadir esta promoción a las nuevas sucursales
        // sucursalesNuevas.forEach(sNueva -> {
        //     if (sNueva.getPromociones() == null) sNueva.setPromociones(new HashSet<>());
        //     sNueva.getPromociones().add(promocion);
        // });
        // Dado que Promocion es la dueña de la tabla de unión (mappedBy está en Sucursal),
        // simplemente settear promocion.setSucursales(sucursalesNuevas) y guardar promoción
        // debería ser suficiente para que JPA actualice la tabla de unión.
    }

    private PromocionDTO convertToDTO(Promocion promocion) {
        if (promocion == null) return null;
        PromocionDTO dto = new PromocionDTO();
        dto.setId(promocion.getId());
        dto.setDenominacion(promocion.getDenominacion());
        dto.setFechaDesde(promocion.getFechaDesde());
        dto.setFechaHasta(promocion.getFechaHasta());
        dto.setHoraDesde(promocion.getHoraDesde());
        dto.setHoraHasta(promocion.getHoraHasta());
        dto.setDescripcionDescuento(promocion.getDescripcionDescuento());
        dto.setPrecioPromocional(promocion.getPrecioPromocional());
        dto.setTipoPromocion(promocion.getTipoPromocion());

        if (promocion.getImagen() != null) {
            ImagenDTO imgDto = new ImagenDTO();
            imgDto.setId(promocion.getImagen().getId());
            imgDto.setDenominacion(promocion.getImagen().getDenominacion());
            dto.setImagen(imgDto);
        }

        if (promocion.getArticulosManufacturados() != null) {
            dto.setArticuloManufacturadoIds(promocion.getArticulosManufacturados().stream().map(ArticuloManufacturado::getId).collect(Collectors.toList()));
            // Si se necesita el DTO completo de artículos:
            // dto.setArticulosManufacturados(promocion.getArticulosManufacturados().stream().map(am -> {
            //    ArticuloManufacturadoDTO amDto = new ArticuloManufacturadoDTO();
            //    // Mapear campos de am a amDto
            //    amDto.setId(am.getId());
            //    amDto.setDenominacion(am.getDenominacion());
            //    return amDto;
            // }).collect(Collectors.toList()));
        }

        if (promocion.getSucursales() != null) {
            dto.setSucursalIds(promocion.getSucursales().stream().map(Sucursal::getId).collect(Collectors.toSet()));
            // Si se necesita el DTO completo de sucursales:
            // dto.setSucursales(promocion.getSucursales().stream().map(s -> {
            //    SucursalDTO sDto = new SucursalDTO();
            //    // Mapear campos de s a sDto
            //    sDto.setId(s.getId());
            //    sDto.setNombre(s.getNombre());
            //    return sDto;
            // }).collect(Collectors.toSet()));
        }
        return dto;
    }
}