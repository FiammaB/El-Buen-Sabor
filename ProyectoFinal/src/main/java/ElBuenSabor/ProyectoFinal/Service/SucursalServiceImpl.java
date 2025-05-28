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
import java.util.Optional;
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
    private final PromocionRepository promocionRepository;
    private final PedidoRepository pedidoRepository; // Para isSucursalInActiveUse

    @Autowired
    public SucursalServiceImpl(SucursalRepository sucursalRepository,
                               EmpresaRepository empresaRepository,
                               DomicilioRepository domicilioRepository,
                               LocalidadRepository localidadRepository,
                               ProvinciaRepository provinciaRepository,
                               PaisRepository paisRepository,
                               CategoriaRepository categoriaRepository,
                               PromocionRepository promocionRepository,
                               PedidoRepository pedidoRepository) {
        super(sucursalRepository);
        this.sucursalRepository = sucursalRepository;
        this.empresaRepository = empresaRepository;
        this.domicilioRepository = domicilioRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
        this.categoriaRepository = categoriaRepository;
        this.promocionRepository = promocionRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional
    public SucursalDTO createSucursal(SucursalCreateUpdateDTO dto) throws Exception {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId()) // Solo empresas activas
                .orElseThrow(() -> new Exception("Empresa activa no encontrada con ID: " + dto.getEmpresaId()));
        if (empresa.isBaja()) throw new Exception("La empresa seleccionada está dada de baja.");

        Optional<Sucursal> existenteNombre = sucursalRepository.findByNombreAndEmpresaIdRaw(dto.getNombre().trim(), dto.getEmpresaId());
        if (existenteNombre.isPresent()) {
            throw new Exception("Ya existe una sucursal con el nombre '" + dto.getNombre().trim() + "' para esta empresa.");
        }

        // Crear Domicilio
        DomicilioCreateUpdateDTO domicilioDTO = dto.getDomicilio();
        Localidad localidad;
        if (domicilioDTO.getLocalidadId() != null) {
            localidad = localidadRepository.findById(domicilioDTO.getLocalidadId()) // Solo activas
                    .orElseThrow(() -> new Exception("Localidad activa no encontrada con ID: " + domicilioDTO.getLocalidadId()));
        } else if (domicilioDTO.getNombreLocalidad() != null && domicilioDTO.getNombreProvincia() != null && domicilioDTO.getNombrePais() != null) {
            Pais pais = paisRepository.findByNombre(domicilioDTO.getNombrePais());
            if (pais == null || pais.isBaja()) { pais = paisRepository.save(Pais.builder().nombre(domicilioDTO.getNombrePais()).build()); }

            Provincia provincia = provinciaRepository.findByNombre(domicilioDTO.getNombreProvincia());
            if (provincia == null || provincia.isBaja()) { provincia = provinciaRepository.save(Provincia.builder().nombre(domicilioDTO.getNombreProvincia()).pais(pais).build());}
            else if (!provincia.getPais().getId().equals(pais.getId())) { throw new Exception ("La provincia encontrada no pertenece al país especificado.");}

            Optional<Localidad> locOpt = localidadRepository.findByNombreAndProvinciaRaw(domicilioDTO.getNombreLocalidad(), provincia);
            if(locOpt.isPresent()){ localidad = locOpt.get(); if(localidad.isBaja()) throw new Exception("La localidad encontrada por nombre está dada de baja.");}
            else {localidad = localidadRepository.save(Localidad.builder().nombre(domicilioDTO.getNombreLocalidad()).provincia(provincia).build());}
        } else {
            throw new Exception("Se debe proveer localidadId o los nombres de país, provincia y localidad para el domicilio.");
        }
        if(localidad.isBaja()) throw new Exception("La localidad del domicilio está dada de baja.");


        Domicilio domicilio = Domicilio.builder()
                .calle(domicilioDTO.getCalle())
                .numero(domicilioDTO.getNumero())
                .cp(domicilioDTO.getCp())
                .localidad(localidad)
                .build();
        domicilio = domicilioRepository.save(domicilio);

        Sucursal sucursal = Sucursal.builder()
                .nombre(dto.getNombre().trim())
                .horarioApertura(dto.getHorarioApertura())
                .horarioCierre(dto.getHorarioCierre())
                .empresa(empresa)
                .domicilio(domicilio)
                .build();

        // Guardar sucursal para tener ID antes de manejar relaciones ManyToMany
        Sucursal savedSucursal = sucursalRepository.save(sucursal);

        // Manejar Categorías (Sucursal es dueña de la tabla de unión sucursal_categoria)
        if (dto.getCategoriaIds() != null) {
            List<Categoria> categoriasAsociadas = new ArrayList<>();
            for (Long catId : dto.getCategoriaIds()) {
                Categoria cat = categoriaRepository.findById(catId) // Solo activas
                        .orElseThrow(() -> new Exception("Categoría activa no encontrada con ID: " + catId));
                if(cat.isBaja()) throw new Exception("La categoría '" + cat.getDenominacion() + "' está dada de baja.");
                categoriasAsociadas.add(cat);
            }
            savedSucursal.setCategorias(categoriasAsociadas); // Asignar y guardar de nuevo
        }

        // Manejar Promociones (Promocion es dueña de la tabla de unión promocion_sucursal)
        if (dto.getPromocionIds() != null) {
            Set<Promocion> promocionesAsociadas = new HashSet<>();
            for (Long promoId : dto.getPromocionIds()) {
                Promocion promo = promocionRepository.findById(promoId) // Solo activas
                        .orElseThrow(() -> new Exception("Promoción activa no encontrada con ID: " + promoId));
                if(promo.isBaja()) throw new Exception("La promoción '" + promo.getDenominacion() + "' está dada de baja.");
                promocionesAsociadas.add(promo);
                // Actualizar el lado inverso (Promocion)
                promo.getSucursales().add(savedSucursal);
                // promocionRepository.save(promo); // Guardar la promoción para persistir el cambio en la relación
            }
            // savedSucursal.setPromociones(promocionesAsociadas); // No es necesario si Promocion es dueña
        }

        // Guardar sucursal final con todas las relaciones
        return convertToDTO(sucursalRepository.save(savedSucursal));
    }

    @Override
    @Transactional
    public SucursalDTO updateSucursal(Long id, SucursalCreateUpdateDTO dto) throws Exception {
        Sucursal sucursal = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + id + " para actualizar."));

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new Exception("Empresa activa no encontrada con ID: " + dto.getEmpresaId()));
        if (empresa.isBaja()) throw new Exception("La empresa seleccionada para la sucursal está dada de baja.");

        Optional<Sucursal> existenteNombre = sucursalRepository.findByNombreAndEmpresaIdRaw(dto.getNombre().trim(), dto.getEmpresaId());
        if (existenteNombre.isPresent() && !existenteNombre.get().getId().equals(id)) {
            throw new Exception("Ya existe otra sucursal con el nombre '" + dto.getNombre().trim() + "' para esta empresa.");
        }

        // Actualizar Domicilio
        Domicilio domicilio = sucursal.getDomicilio(); // El domicilio ya existe y está asociado
        DomicilioCreateUpdateDTO domicilioDTO = dto.getDomicilio();
        Localidad localidad;
        if (domicilioDTO.getLocalidadId() != null) {
            localidad = localidadRepository.findById(domicilioDTO.getLocalidadId())
                    .orElseThrow(() -> new Exception("Localidad activa no encontrada con ID: " + domicilioDTO.getLocalidadId()));
        } else if (domicilioDTO.getNombreLocalidad() != null && domicilioDTO.getNombreProvincia() != null && domicilioDTO.getNombrePais() != null) {
            // Lógica para buscar/crear País, Provincia, Localidad (similar a createSucursal)
            Pais pais = paisRepository.findByNombre(domicilioDTO.getNombrePais());
            if (pais == null || pais.isBaja()) { pais = paisRepository.save(Pais.builder().nombre(domicilioDTO.getNombrePais()).build()); }

            Provincia provincia = provinciaRepository.findByNombre(domicilioDTO.getNombreProvincia());
            if (provincia == null || provincia.isBaja()) { provincia = provinciaRepository.save(Provincia.builder().nombre(domicilioDTO.getNombreProvincia()).pais(pais).build());}
            else if (!provincia.getPais().getId().equals(pais.getId())) { throw new Exception ("La provincia encontrada no pertenece al país especificado.");}

            Optional<Localidad> locOpt = localidadRepository.findByNombreAndProvinciaRaw(domicilioDTO.getNombreLocalidad(), provincia);
            if(locOpt.isPresent()){ localidad = locOpt.get(); if(localidad.isBaja()) throw new Exception("La localidad encontrada por nombre está dada de baja.");}
            else {localidad = localidadRepository.save(Localidad.builder().nombre(domicilioDTO.getNombreLocalidad()).provincia(provincia).build());}
        } else {
            throw new Exception("Se debe proveer localidadId o los nombres de país, provincia y localidad para el domicilio.");
        }
        if(localidad.isBaja()) throw new Exception("La localidad del domicilio está dada de baja.");

        domicilio.setCalle(domicilioDTO.getCalle());
        domicilio.setNumero(domicilioDTO.getNumero());
        domicilio.setCp(domicilioDTO.getCp());
        domicilio.setLocalidad(localidad);
        domicilioRepository.save(domicilio); // Guardar cambios en domicilio

        sucursal.setNombre(dto.getNombre().trim());
        sucursal.setHorarioApertura(dto.getHorarioApertura());
        sucursal.setHorarioCierre(dto.getHorarioCierre());
        sucursal.setEmpresa(empresa);
        // sucursal.setDomicilio(domicilio); // Ya está asociado

        // Actualizar Categorías (Sucursal es dueña)
        List<Categoria> nuevasCategoriasAsociadas = new ArrayList<>();
        if (dto.getCategoriaIds() != null) {
            for (Long catId : dto.getCategoriaIds()) {
                Categoria cat = categoriaRepository.findById(catId)
                        .orElseThrow(() -> new Exception("Categoría activa no encontrada con ID: " + catId));
                if(cat.isBaja()) throw new Exception("La categoría '" + cat.getDenominacion() + "' está dada de baja.");
                nuevasCategoriasAsociadas.add(cat);
            }
        }
        sucursal.setCategorias(nuevasCategoriasAsociadas);

        // Actualizar Promociones (Promocion es dueña)
        // 1. Quitar la sucursal de las promociones antiguas
        for (Promocion promoAntigua : new HashSet<>(sucursal.getPromociones())) {
            if (dto.getPromocionIds() == null || !dto.getPromocionIds().contains(promoAntigua.getId())) {
                promoAntigua.getSucursales().remove(sucursal);
                // promocionRepository.save(promoAntigua); // Guardar promoción para actualizar la relación
            }
        }
        // 2. Añadir la sucursal a las nuevas promociones
        Set<Promocion> nuevasPromocionesAsociadas = new HashSet<>();
        if (dto.getPromocionIds() != null) {
            for (Long promoIdNuevo : dto.getPromocionIds()) {
                Promocion promoNueva = promocionRepository.findById(promoIdNuevo)
                        .orElseThrow(() -> new Exception("Promoción activa no encontrada con ID: " + promoIdNuevo));
                if(promoNueva.isBaja()) throw new Exception("La promoción '" + promoNueva.getDenominacion() + "' está dada de baja.");
                promoNueva.getSucursales().add(sucursal); // Añadir sucursal a la promoción
                // promocionRepository.save(promoNueva); // Guardar promoción
                nuevasPromocionesAsociadas.add(promoNueva);
            }
        }
        // sucursal.setPromociones(nuevasPromocionesAsociadas); // No setear directamente si Promocion es dueña

        // Si el DTO permite cambiar 'baja'
        // if (dto.getBaja() != null) sucursal.setBaja(dto.isBaja());

        return convertToDTO(sucursalRepository.save(sucursal));
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalDTO findSucursalByIdDTO(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalDTO> findAllSucursalesDTO() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalDTO> findByEmpresaId(Long empresaId, boolean soloActivas) throws Exception {
        List<Sucursal> sucursales;
        if (soloActivas) {
            // Valida que la empresa exista y esté activa
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new Exception("Empresa activa no encontrada con ID: " + empresaId));
            if(empresa.isBaja()) throw new Exception("La empresa con ID: " + empresaId + " está dada de baja.");
            sucursales = sucursalRepository.findByEmpresaId(empresaId); // @Where en Sucursal filtra
        } else {
            sucursales = sucursalRepository.findByEmpresaIdRaw(empresaId);
        }
        return sucursales.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sucursal> findByNombreAndEmpresaIdRaw(String nombre, Long empresaId) throws Exception {
        return sucursalRepository.findByNombreAndEmpresaIdRaw(nombre, empresaId);
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Sucursal> findAllIncludingDeleted() throws Exception {
        return sucursalRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sucursal> findByIdIncludingDeleted(Long id) throws Exception {
        return sucursalRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Sucursal softDelete(Long id) throws Exception {
        Sucursal sucursal = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + id + " para dar de baja."));
        if (sucursal.isBaja()) {
            throw new Exception("La sucursal ya está dada de baja.");
        }
        if (isSucursalInActiveUse(id)) {
            throw new Exception("No se puede dar de baja la sucursal porque tiene pedidos activos no finalizados.");
        }
        // Al dar de baja una sucursal, ¿qué pasa con sus categorías y promociones asociadas?
        // Las relaciones ManyToMany se mantienen, pero la sucursal no aparecerá en listados activos.
        // Si se quisiera desvincular:
        // sucursal.getCategorias().clear(); // Si Sucursal es dueña
        // for (Promocion promo : new HashSet<>(sucursal.getPromociones())) { // Si Promocion es dueña
        //     promo.getSucursales().remove(sucursal);
        //     promocionRepository.save(promo);
        // }
        // sucursal.getPromociones().clear();

        sucursal.setBaja(true);
        return sucursalRepository.save(sucursal);
    }

    @Override
    @Transactional
    public Sucursal reactivate(Long id) throws Exception {
        Sucursal sucursal = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + id + " para reactivar."));
        if (!sucursal.isBaja()) {
            throw new Exception("La sucursal no está dada de baja, no se puede reactivar.");
        }
        // Validar que la empresa, domicilio y localidad estén activos
        if (sucursal.getEmpresa() == null || sucursal.getEmpresa().isBaja()) {
            throw new Exception("No se puede reactivar la sucursal, su empresa asociada no está activa.");
        }
        if (sucursal.getDomicilio() == null || sucursal.getDomicilio().isBaja()) {
            throw new Exception("No se puede reactivar la sucursal, su domicilio asociado no está activo.");
        }
        if (sucursal.getDomicilio().getLocalidad() == null || sucursal.getDomicilio().getLocalidad().isBaja()) {
            throw new Exception("No se puede reactivar la sucursal, la localidad de su domicilio no está activa.");
        }
        sucursal.setBaja(false);
        return sucursalRepository.save(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSucursalInActiveUse(Long sucursalId) throws Exception {
        // Verificar si hay pedidos activos (no finales) para esta sucursal
        long countPedidosActivos = pedidoRepository.findAllRaw().stream()
                .filter(p -> p.getSucursal().getId().equals(sucursalId) && !p.isBaja() &&
                        p.getEstado() != Estado.ENTREGADO &&
                        p.getEstado() != Estado.FACTURADO &&
                        p.getEstado() != Estado.CANCELADO &&
                        p.getEstado() != Estado.RECHAZADO)
                .count();
        return countPedidosActivos > 0;
    }

    // --- Implementación de métodos de SucursalService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<SucursalDTO> findAllSucursalesIncludingDeleted() throws Exception {
        return this.findAllIncludingDeleted().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalDTO findSucursalByIdIncludingDeletedDTO(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private SucursalDTO convertToDTO(Sucursal sucursal) {
        if (sucursal == null) return null;
        SucursalDTO dto = new SucursalDTO();
        dto.setId(sucursal.getId());
        dto.setNombre(sucursal.getNombre());
        dto.setHorarioApertura(sucursal.getHorarioApertura());
        dto.setHorarioCierre(sucursal.getHorarioCierre());
        dto.setBaja(sucursal.isBaja());

        if (sucursal.getEmpresa() != null) {
            EmpresaSimpleDTO empDto = new EmpresaSimpleDTO();
            empDto.setId(sucursal.getEmpresa().getId());
            empDto.setNombre(sucursal.getEmpresa().getNombre());
            empDto.setBaja(sucursal.getEmpresa().isBaja());
            dto.setEmpresa(empDto);
        }
        if (sucursal.getDomicilio() != null) {
            dto.setDomicilio(convertToDomicilioDTO(sucursal.getDomicilio()));
        }
        if (sucursal.getCategorias() != null) {
            dto.setCategorias(sucursal.getCategorias().stream()
                    .filter(cat -> !cat.isBaja()) // Mostrar solo categorías activas
                    .map(this::convertToCategoriaSimpleDTO).collect(Collectors.toList()));
        }
        if (sucursal.getPromociones() != null) {
            dto.setPromociones(sucursal.getPromociones().stream()
                    .filter(promo -> !promo.isBaja()) // Mostrar solo promociones activas
                    .map(this::convertToPromocionSimpleDTO).collect(Collectors.toSet()));
        }
        return dto;
    }

    // Helpers para DTOs simples (ya definidos en otros servicios, se pueden centralizar o replicar)
    private DomicilioDTO convertToDomicilioDTO(Domicilio dom) { /* ... implementación ... */
        if (dom == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(dom.getId());
        dto.setCalle(dom.getCalle());
        dto.setNumero(dom.getNumero());
        dto.setCp(dom.getCp());
        dto.setBaja(dom.isBaja());
        if (dom.getLocalidad() != null) {
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(dom.getLocalidad().getId());
            locDto.setNombre(dom.getLocalidad().getNombre());
            locDto.setBaja(dom.getLocalidad().isBaja());
            // ... anidar Provincia y Pais si es necesario
            dto.setLocalidad(locDto);
        }
        return dto;
    }
    private CategoriaSimpleDTO convertToCategoriaSimpleDTO(Categoria cat) { /* ... */
        if (cat == null) return null;
        CategoriaSimpleDTO dto = new CategoriaSimpleDTO();
        dto.setId(cat.getId());
        dto.setDenominacion(cat.getDenominacion());
        dto.setTipoRubro(cat.getTipoRubro());
        dto.setBaja(cat.isBaja());
        return dto;
    }
    private PromocionSimpleDTO convertToPromocionSimpleDTO(Promocion promo) { /* ... */
        if (promo == null) return null;
        PromocionSimpleDTO dto = new PromocionSimpleDTO();
        dto.setId(promo.getId());
        dto.setDenominacion(promo.getDenominacion());
        dto.setFechaHasta(promo.getFechaHasta());
        dto.setTipoPromocion(promo.getTipoPromocion());
        dto.setPrecioPromocional(promo.getPrecioPromocional());
        dto.setBaja(promo.isBaja());
        return dto;
    }
}
