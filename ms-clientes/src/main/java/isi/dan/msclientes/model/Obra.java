package isi.dan.msclientes.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "MS_CLI_OBRA")
@Data
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "ES_REMODELACION")
    private Boolean esRemodelacion;

    private float lat;

    private float lng;


    //@JsonIgnoreProperties("obrasHabilitadas")
    @Column(name = "idCliente")
    private Integer idCliente;

    @NotNull(message = "El presupuesto es obligatorio")
    @Min(value = 100, message = "El presupuesto debe ser al menos de 100")
    private BigDecimal presupuesto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estadoObra")
    private EstadoObra estado;
}
