package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoShortDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Mappers.ArticuloInsumoMapper;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import ElBuenSabor.ProyectoFinal.Repositories.UnidadMedidaRepository;
import ElBuenSabor.ProyectoFinal.Service.ArticuloInsumoService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articuloInsumo")
@RequiredArgsConstructor
public class ArticuloInsumoController {

    private final ArticuloInsumoService articuloInsumoService;
    private final ArticuloInsumoMapper articuloInsumoMapper;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ImagenRepository imagenRepository;

    @GetMapping("/insumos")
    public ResponseEntity<List<ArticuloInsumoDTO>> getAll() {
        return ResponseEntity.ok(
                articuloInsumoService.findAll().stream()
                        .map(articuloInsumoMapper::toDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticuloInsumoDTO> getById(@PathVariable Long id) {
        ArticuloInsumo insumo = articuloInsumoService.findById(id);
        return ResponseEntity.ok(articuloInsumoMapper.toDTO(insumo));
    }

    @PostMapping
    public ResponseEntity<ArticuloInsumoDTO> create(@RequestBody ArticuloInsumoDTO dto) {
        ArticuloInsumo entity = articuloInsumoMapper.toEntity(dto);

        entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
        entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
        entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));

        ArticuloInsumo saved = articuloInsumoService.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(articuloInsumoMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticuloInsumoDTO> update(@PathVariable Long id, @RequestBody ArticuloInsumoDTO dto) {
        ArticuloInsumo entity = articuloInsumoMapper.toEntity(dto);

        entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
        entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
        entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));

        ArticuloInsumo updated = articuloInsumoService.update(id, entity);
        return ResponseEntity.ok(articuloInsumoMapper.toDTO(updated));
    }

    @PutMapping("/{id}/baja")
    public ResponseEntity<Void> darDeBaja(@PathVariable Long id) {
        ArticuloInsumo insumo = articuloInsumoService.findById(id);
        insumo.setBaja(true);
        articuloInsumoService.save(insumo);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reinsertar")
    public ResponseEntity<Void> reinsertar(@PathVariable Long id) {
        ArticuloInsumo insumo = articuloInsumoService.findById(id);
        insumo.setBaja(false);
        articuloInsumoService.save(insumo);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articuloInsumoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
