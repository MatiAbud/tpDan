package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;

//import isi.dan.ms_productos.modelo.Producto;
import lombok.Data;

@Data
public class OrdenCompraDetalle {
    @Field("producto")
    private Producto producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal precioFinal;

    public BigDecimal calcularTotalLinea() {
        BigDecimal precioSinDescuento = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        System.out.println("---------------------PRECIO SIN DESC ------------------------------------");
        System.out.println(precioSinDescuento);
        System.out.println("---------------------PRECIO SIN DESC ------------------------------------");
  
        BigDecimal descuentoCalculado = precioSinDescuento.multiply(descuento.divide(BigDecimal.valueOf(100))); 
        System.out.println("---------------------DESCUENTO ------------------------------------");
        System.out.println(descuentoCalculado);
        System.out.println("---------------------DESCUENTO ------------------------------------");
        
        BigDecimal precioFinal = precioSinDescuento.subtract(descuentoCalculado);
        System.out.println("---------------------PRECIO FINAL EN CALCULAR LINEA ------------------------------------");
        System.out.println(precioFinal);
        System.out.println("---------------------PRECIO FINAL EN CALCULAR LINEA ------------------------------------");
        
        return precioFinal; 
    }

}
