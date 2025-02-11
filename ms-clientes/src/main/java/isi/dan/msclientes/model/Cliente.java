package isi.dan.msclientes.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
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
    @Email(message = "Email debe ser válido")
    @NotBlank(message = "Email es obligatorio")
    private String correoElectronico;

    private String cuit;

    @Column(name = "MAXIMO_DESCUBIERTO")
    private Integer maximoDescubierto;

    private Integer maxObrasEnEjecucion;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Obra> obrasClientes;

    @Column
    private BigDecimal saldo;

    // Considera implementar correctamente setidCliente si es necesario
    public void setidCliente(Integer id) {
        this.id = id;
    }

}
