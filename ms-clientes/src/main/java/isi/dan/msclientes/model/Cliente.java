package isi.dan.msclientes.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    private BigDecimal maximoDescubierto;

    private Integer maxObrasEnEjecucion;

    @OneToMany()
    private List<Obra> obrasClientes;

    @Column
    private BigDecimal saldo;

    // Considera implementar correctamente setidCliente si es necesario
    public void setidCliente(Integer id) {
        this.id = id;
    }

    public List<Obra> getObrasHabilitadas() {
        if (obrasClientes == null) {
            return Collections.emptyList();
        } else {
            return obrasClientes.stream()
                    .filter(obra -> obra.getEstado() == EstadoObra.HABILITADA)
                    .collect(Collectors.toList());

        }
    }

    @OneToMany
    @JoinColumn(name = "clienteId") 
    private List<UsuarioHabilitado> usuariosHabilitados;

    public boolean puedeAgregarObra() {
        if (maxObrasEnEjecucion == null) {
            return true;
        }
        long obrasEnEjecucion = obrasClientes.stream()
                .filter(obra -> obra.getEstado() == EstadoObra.HABILITADA)
                .count();
        return obrasEnEjecucion < maxObrasEnEjecucion;
    }

}
