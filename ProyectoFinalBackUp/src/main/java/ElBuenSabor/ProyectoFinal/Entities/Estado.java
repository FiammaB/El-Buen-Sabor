package ElBuenSabor.ProyectoFinal.Entities;

public enum Estado {
    A_CONFIRMAR, // Estado inicial de un pedido [cite: 128, 153]
    EN_COCINA,   // Pedido pasado a cocina [cite: 155]
    LISTO,       // Pedido listo para retirar/enviar [cite: 156, 176]
    EN_DELIVERY, // Pedido en reparto [cite: 156, 163]
    ENTREGADO,   // Pedido finalizado [cite: 156, 165]
    CANCELADO,
    RECHAZADO
}