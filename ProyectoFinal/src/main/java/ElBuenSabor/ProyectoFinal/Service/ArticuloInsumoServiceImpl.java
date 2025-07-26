package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.PromocionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArticuloInsumoServiceImpl extends BaseServiceImpl<ArticuloInsumo, Long> implements ArticuloInsumoService {

    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final PromocionRepository promocionRepository;

    public ArticuloInsumoServiceImpl(
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepository,
            PromocionRepository promocionRepository
    ) {
        super(articuloInsumoRepository);
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.promocionRepository = promocionRepository;
    }


    @Override
    @Transactional
    public ArticuloInsumo update(Long id, ArticuloInsumo updated) throws Exception {
        // Lógica de actualización específica para ArticuloManufacturado
        // Puedes llamar a super.update(id, updated) o implementar la lógica
        // que involucre los detalles aquí, como ya te había mostrado.
        ArticuloInsumo actual = findById(id); // Usa el findById del padre

        actual.setDenominacion(updated.getDenominacion());
        actual.setPrecioVenta(updated.getPrecioVenta());
        actual.setPrecioCompra(updated.getPrecioCompra());
        actual.setStockActual(updated.getStockActual());
        actual.setStockMinimo(updated.getStockMinimo());
        actual.setEsParaElaborar(updated.getEsParaElaborar());
        actual.setCategoria(updated.getCategoria());
        actual.setUnidadMedida(updated.getUnidadMedida());
        actual.setImagen(updated.getImagen());


        return baseRepository.save(actual); // <-- AQUÍ ES DONDE DEBE IR
    }

    @Transactional
    public ArticuloInsumo actualizarPrecioYPropagar(Long id, Double nuevoPrecioCompra) throws Exception {
        ArticuloInsumo insumo = findById(id);
        insumo.setPrecioCompra(nuevoPrecioCompra);
        ArticuloInsumo actualizado = articuloInsumoRepository.save(insumo);

        // Buscar manufacturados que usen este insumo
        List<ArticuloManufacturado> manufacturadosAfectados =
                articuloManufacturadoRepository.findAllByDetalles_ArticuloInsumo_Id(insumo.getId());

        double margenPorcentual = 0.7;

        for (ArticuloManufacturado manufacturado : manufacturadosAfectados) {
            double costoTotal = manufacturado.getDetalles().stream()
                    .mapToDouble(det -> {
                        ArticuloInsumo ing = det.getArticuloInsumo();
                        return (ing.getPrecioCompra() != null ? ing.getPrecioCompra() : 0.0) * det.getCantidad();
                    })
                    .sum();

            double precioVenta = costoTotal * (1 + margenPorcentual);
            manufacturado.setPrecioVenta(precioVenta);
            articuloManufacturadoRepository.save(manufacturado);
        }

        return actualizado;
    }

    @Transactional
    public void verificarYReactivarArticulosManufacturados(ArticuloInsumo insumoActualizado) {
        // 1️⃣ Reactivar ArticuloManufacturado si todos sus insumos están OK
        List<ArticuloManufacturado> manufacturados = articuloManufacturadoRepository.findByInsumoId(insumoActualizado.getId());

        Set<Long> idsManufacturadosReactivados = new HashSet<>();

        for (ArticuloManufacturado am : manufacturados) {
            if (!am.getBaja()) continue;

            boolean todosInsumosOK = true;

            for (ArticuloManufacturadoDetalle det : am.getDetalles()) {
                ArticuloInsumo insumo = articuloInsumoRepository.findById(det.getArticuloInsumo().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado: " + det.getArticuloInsumo().getId()));

                if (insumo.getStockActual() < insumo.getStockMinimo()) {
                    todosInsumosOK = false;
                    break;
                }
            }

            if (todosInsumosOK) {
                am.setBaja(false);
                articuloManufacturadoRepository.save(am);
                idsManufacturadosReactivados.add(am.getId());
                System.out.println("✅ AM '" + am.getDenominacion() + "' reactivado.");
            }
        }

        // 2️⃣ Reactivar Promociones si todos sus componentes están OK
        List<Promocion> promociones = promocionRepository.findAll();

        for (Promocion promo : promociones) {
            if (!promo.getBaja()) continue;

            boolean todosAMOK = true;
            boolean todosInsumosOK = true;

            // Verificar los AM de la promoción
            if (promo.getPromocionDetalles() != null) {
                for (PromocionDetalle pd : promo.getPromocionDetalles()) {
                    ArticuloManufacturado am = pd.getArticuloManufacturado();
                    ArticuloManufacturado amEstado = articuloManufacturadoRepository.findById(am.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("AM en promoción no encontrado: " + am.getId()));
                    if (amEstado.getBaja()) {
                        todosAMOK = false;
                        break;
                    }
                }
            }

            // Verificar los insumos directos de la promoción
            if (promo.getPromocionInsumoDetalles() != null) {
                for (PromocionInsumoDetalle pid : promo.getPromocionInsumoDetalles()) {
                    ArticuloInsumo insumo = articuloInsumoRepository.findById(pid.getArticuloInsumo().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Insumo en promoción no encontrado: " + pid.getArticuloInsumo().getId()));
                    if (insumo.getBaja() || insumo.getStockActual() < insumo.getStockMinimo()) {
                        todosInsumosOK = false;
                        break;
                    }
                }
            }

            if (todosAMOK && todosInsumosOK) {
                promo.setBaja(false);
                promocionRepository.save(promo);
                System.out.println("✅ Promo '" + promo.getDenominacion() + "' reactivada.");
            }
        }
    }


    @Transactional
    public void verificarYDarDeBajaRelacionadosPorStockBajo(ArticuloInsumo insumoActualizado) {
        // 🔴 Paso 1: Dar de baja los ArticuloManufacturado que usan este insumo
        List<ArticuloManufacturado> relacionados = articuloManufacturadoRepository.findByInsumoId(insumoActualizado.getId());

        Set<Long> idsAMDadosDeBaja = new HashSet<>();

        for (ArticuloManufacturado am : relacionados) {
            if (!am.getBaja()) {
                am.setBaja(true);
                articuloManufacturadoRepository.save(am);
                idsAMDadosDeBaja.add(am.getId());
                System.out.println("⛔ AM '" + am.getDenominacion() + "' dado de BAJA por insumo con stock bajo.");
            }
        }

        // 🔴 Paso 2: Dar de baja las Promociones relacionadas con esos AM o con el insumo directo
        List<Promocion> promociones = promocionRepository.findAll(); // o crear query optimizada si tenés muchas

        for (Promocion promo : promociones) {
            boolean debeDarseDeBaja = false;

            // Verifica si usa un AM dado de baja
            if (promo.getPromocionDetalles() != null) {
                for (PromocionDetalle pd : promo.getPromocionDetalles()) {
                    if (idsAMDadosDeBaja.contains(pd.getArticuloManufacturado().getId())) {
                        debeDarseDeBaja = true;
                        break;
                    }
                }
            }

            // Verifica si usa este insumo directamente
            if (!debeDarseDeBaja && promo.getPromocionInsumoDetalles() != null) {
                for (PromocionInsumoDetalle pid : promo.getPromocionInsumoDetalles()) {
                    if (pid.getArticuloInsumo().getId().equals(insumoActualizado.getId())) {
                        debeDarseDeBaja = true;
                        break;
                    }
                }
            }

            if (debeDarseDeBaja && !promo.getBaja()) {
                promo.setBaja(true);
                promocionRepository.save(promo);
                System.out.println("⛔ Promo '" + promo.getDenominacion() + "' dada de BAJA por relación con insumo o AM en baja.");
            }
        }
    }


}
