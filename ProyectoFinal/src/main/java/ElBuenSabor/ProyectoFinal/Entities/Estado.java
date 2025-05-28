package ElBuenSabor.ProyectoFinal.Entities;

public enum Estado {
    A_CONFIRMAR, // Estado inicial de un pedido
    EN_COCINA,   // Pedido pasado a cocina
    LISTO,       // Pedido listo para retirar/enviar
    EN_DELIVERY, // Pedido en reparto
    ENTREGADO,   // Pedido entregado (podría ser un estado intermedio antes de facturado si el pago no es inmediato)
    CANCELADO,   // Pedido cancelado por el cliente o sistema
    RECHAZADO,   // Pedido rechazado por el local
    FACTURADO    // Pedido completado y facturado (estado final según consignas)
}