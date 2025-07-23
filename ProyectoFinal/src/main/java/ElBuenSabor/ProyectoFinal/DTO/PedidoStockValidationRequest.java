package ElBuenSabor.ProyectoFinal.DTO;



import java.util.Set; // O List, si prefieres

public class PedidoStockValidationRequest {
    private Set<DetallePedidoCreateDTO> detalles;

    // Constructor vacío (necesario para Spring/JSON deserialization)
    public PedidoStockValidationRequest() {
    }

    // Constructor con todos los campos
    public PedidoStockValidationRequest(Set<DetallePedidoCreateDTO> detalles) {
        this.detalles = detalles;
    }

    // Getters y Setters
    public Set<DetallePedidoCreateDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(Set<DetallePedidoCreateDTO> detalles) {
        this.detalles = detalles;
    }
}
