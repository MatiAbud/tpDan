package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;

import isi.dan.ms_productos.modelo.Producto;
import lombok.Data;

@Data
public class OrdenCompraDetalle {
    @Field("producto")
    private Producto producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal precioFinal;

    public void calcularTotalLinea() {
        BigDecimal precioSinDescuento = producto.getPrecio().multiply(new BigDecimal(cantidad));
        BigDecimal descuentoCalculado = precioSinDescuento.multiply(descuento); // Asumiendo que descuento es un
                                                                                // porcentaje
        this.precioFinal = precioSinDescuento.subtract(descuentoCalculado);
    }

}
