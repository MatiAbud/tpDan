package isi.dan.msclientes.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
    //@Column(length = 2)
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
    //@Min(value = 10000, message = "El descubierto maximo debe ser al menos 10000")
    private Integer maximoDescubierto;

    private Integer maxObrasEnEjecucion;

    public Integer getMaxObrasEnEjecucion() {
        return maxObrasEnEjecucion;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Obra> obrasClientes;

    public void setidCliente(Integer id) {
        throw new UnsupportedOperationException("Unimplemented method 'setidCliente'");
    }
    //@OneToMany
    //@JoinColumn(name="clienteId",referencedColumnName = "id")
    //private List<UsuarioHabilitado> listaUsuariosHabilitados;
    
    // getters y setters

}
