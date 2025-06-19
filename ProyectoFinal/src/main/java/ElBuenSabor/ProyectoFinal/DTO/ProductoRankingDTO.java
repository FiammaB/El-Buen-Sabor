package ElBuenSabor.ProyectoFinal.DTO;

public class ProductoRankingDTO {
    private String nombreProducto;
    private Long cantidadVendida;

    public ProductoRankingDTO(String nombreProducto, Long cantidadVendida) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }
}
