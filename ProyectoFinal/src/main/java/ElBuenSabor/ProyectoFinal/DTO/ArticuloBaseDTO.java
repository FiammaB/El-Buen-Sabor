package ElBuenSabor.ProyectoFinal.DTO;

public interface ArticuloBaseDTO {
    String getDenominacion();
    Double getPrecioVenta();
    Long getCategoriaId();
    Long getUnidadMedidaId();
    Long getImagenId();
    ImagenDTO getImagen(); // Para obtener la denominación si se crea nueva imagen
    boolean isBaja(); // Para permitir que el DTO de actualización modifique el estado de baja
    // void setBaja(boolean baja); // Lombok @Data o @Setter en implementaciones lo harán
}