package isi.dan.ms_productos.dto;

import lombok.Data;

@Data
public class DescuentoUpdateDTO {
    private Long idProducto;
    private double descuentoPromocional;

    public DescuentoUpdateDTO(Long idP, double descuentoProm) {
        this.idProducto = idP;
        this.descuentoPromocional = descuentoProm;
    }

}
