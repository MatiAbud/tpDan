package isi.dan.msclientes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "MS_CLI_CLIENTE")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "CORREO_ELECTRONICO")
    @Email(message = "Email debe ser valido")
    @NotBlank(message = "Email es obligatorio")
    private String correoElectronico;

    private String cuit;

    @Column(name = "MAXIMO_DESCUBIERTO")
    @Min(value = 10000, message = "El descubierto maximo debe ser al menos 10000")
    private Integer maximoDescubierto;

    private Integer maxObrasEnEjecucion;

    public Integer getMaxObrasEnEjecucion() {
        return maxObrasEnEjecucion;
    }

    /*
     * public Cliente(Integer id, @NotBlank(message = "El nombre es obligatorio")
     * String nombre,
     * 
     * @Email(message = "Email debe ser valido") @NotBlank(message =
     * "Email es obligatorio") String correoElectronico,
     * String cuit,
     * 
     * @Min(value = 10000, message =
     * "El descubierto maximo debe ser al menos 10000") Integer maximoDescubierto,
     * Integer maxObrasEnEjecucion) {
     * this.id = id;
     * this.nombre = nombre;
     * this.correoElectronico = correoElectronico;
     * this.cuit = cuit;
     * this.maximoDescubierto = maximoDescubierto;
     * this.maxObrasEnEjecucion = maxObrasEnEjecucion;
     * }
     */

    // getters y setters

}
