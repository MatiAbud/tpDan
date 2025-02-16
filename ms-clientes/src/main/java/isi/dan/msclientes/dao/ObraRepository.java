package isi.dan.msclientes.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Integer> {

    List<Obra> findByPresupuestoGreaterThanEqual(BigDecimal price);

    List<Obra> findByIdCliente(Integer clienteId);

    Obra findFirstByEstado(EstadoObra pendiente);

    //int countByClienteAndEstado(Cliente cliente, EstadoObra habilitada);
}
