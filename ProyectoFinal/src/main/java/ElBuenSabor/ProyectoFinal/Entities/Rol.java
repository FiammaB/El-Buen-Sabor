package ElBuenSabor.ProyectoFinal.Entities;

public enum Rol {
    CLIENTE,
    ADMINISTRADOR, // Administrador general del sistema
    EMPLEADO,      // Rol genérico de empleado, podría no usarse si se usan los específicos
    CAJERO,        // Rol para el empleado que gestiona caja y pedidos
    COCINERO,      // Rol para el empleado de cocina
    DELIVERY       // Rol para el empleado que hace las entregas
}
