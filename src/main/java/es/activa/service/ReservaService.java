package es.activa.service;

import es.activa.config.JPAUtil;
import es.activa.domain.Actividad;
import es.activa.domain.Cliente;
import es.activa.domain.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ReservaService {

    public void crearReserva(Scanner scanner) {

        System.out.println("\n=== Crear reserva ===");

        System.out.print("Introduce el ID de la actividad: ");
        String actividadTexto = scanner.nextLine().trim();

        Long actividadId;

        try {
            actividadId = Long.parseLong(actividadTexto);

            if (actividadId <= 0) {
                System.out.println("Error: el ID debe ser mayor que cero.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: introduce un ID válido.");
            return;
        }

        System.out.print("Introduce el ID del cliente: ");
        String clienteTexto = scanner.nextLine().trim();

        Long clienteId;

        try {
            clienteId = Long.parseLong(clienteTexto);

            if (clienteId <= 0) {
                System.out.println("Error: el ID debe ser mayor que cero.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: introduce un ID válido.");
            return;
        }

        System.out.print("Introduce el número de plazas que quieres reservar: ");
        String plazasTexto = scanner.nextLine().trim();

        int numeroPlazas;

        try {
            numeroPlazas = Integer.parseInt(plazasTexto);

            if (numeroPlazas <= 0) {
                System.out.println("Error: debes reservar al menos una plaza.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: introduce un número entero válido.");
            return;
        }

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            Actividad actividad = em.find(
                Actividad.class, actividadId,
                jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
            );

            if (actividad == null) {
                System.out.println("Error: no existe esa actividad.");
                tx.rollback();
                return;
            }

            Cliente cliente = em.find(Cliente.class, clienteId);

            if (cliente == null) {
                System.out.println("Error: no existe ese cliente.");
                tx.rollback();
                return;
            }

            if (actividad.getPlazasDisponibles() < numeroPlazas) {
                System.out.println("Error: no hay suficientes plazas disponibles.");
                System.out.println("Plazas disponibles: " + actividad.getPlazasDisponibles());
                tx.rollback();
                return;
            }

            actividad.setPlazasDisponibles(
                actividad.getPlazasDisponibles() - numeroPlazas
            );

            Reserva reserva = new Reserva(
                LocalDateTime.now(),
                numeroPlazas,
                actividad,
                cliente
            );

            em.persist(reserva);

            tx.commit();

            System.out.println("Reserva creada correctamente.");
            System.out.println("ID de la reserva: " + reserva.getId());
            System.out.println("Cliente: " + cliente.getNombre());
            System.out.println("Actividad: " + actividad.getNombre());
            System.out.println("Plazas reservadas: " + numeroPlazas);
            System.out.println("Plazas restantes: " + actividad.getPlazasDisponibles());

        } catch (Exception e) {

            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            System.out.println("Error al crear la reserva: " + e.getMessage());
            e.printStackTrace();

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
	public void cancelarReserva(Scanner scanner) {
	
	    System.out.println("\n=== Cancelar reserva ===");
	
	    System.out.print("Introduce el ID de la reserva que quieres cancelar: ");
	    String reservaTexto = scanner.nextLine().trim();
	
	    Long reservaId;
	
	    try {
	        reservaId = Long.parseLong(reservaTexto);
	
	        if (reservaId <= 0) {
	            System.out.println("Error: el ID debe ser mayor que cero.");
	            return;
	        }
	
	    } catch (NumberFormatException e) {
	        System.out.println("Error: introduce un ID válido.");
	        return;
	    }
	
	    EntityManager em = null;
	    EntityTransaction tx = null;
	
	    try {
	        em = JPAUtil.getEntityManager();
	        tx = em.getTransaction();
	        tx.begin();
	
	        Reserva reserva = em.find(
	            Reserva.class,
	            reservaId,
	            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
	        );
	
	        if (reserva == null) {
	            System.out.println("Error: no existe una reserva con ese ID.");
	            tx.rollback();
	            return;
	        }
	
	        Actividad actividad = em.find(
	            Actividad.class,
	            reserva.getActividad().getId(),
	            jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
	        );
	
	        if (actividad == null) {
	            throw new IllegalStateException("No se ha encontrado la actividad asociada a la reserva.");
	        }
	
	        System.out.println("Reserva encontrada:");
	        System.out.println("Cliente: " + reserva.getCliente().getNombre());
	        System.out.println("Actividad: " + actividad.getNombre());
	        System.out.println("Plazas reservadas: " + reserva.getNumeroPlazas());
	
	        int plazasRestantes =
	            actividad.getPlazasTotales() - actividad.getPlazasDisponibles();
	
	        if (reserva.getNumeroPlazas() > plazasRestantes) {
	            throw new IllegalStateException("La cancelación superaría las plazas totales de la actividad.");
	        }
	
	        actividad.setPlazasDisponibles(
	            actividad.getPlazasDisponibles() + reserva.getNumeroPlazas()
	        );
	
	        em.remove(reserva);
	
	        tx.commit();
	
	        System.out.println("Reserva cancelada correctamente.");
	        System.out.println("Plazas disponibles ahora: " + actividad.getPlazasDisponibles());
	
	    } catch (Exception e) {
	
	        if (tx != null && tx.isActive()) {
	            tx.rollback();
	        }
	
	        System.out.println("Error al cancelar la reserva: " + e.getMessage());
	        e.printStackTrace();
	
	    } finally {
	        if (em != null && em.isOpen()) {
	            em.close();
	        }
	    }
	}
}



