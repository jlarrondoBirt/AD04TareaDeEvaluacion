
package es.activa.service;

import es.activa.config.JPAUtil;
import es.activa.domain.Actividad;
import es.activa.domain.Monitor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.List;

public class ActividadService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void crearActividad(Scanner scanner) {

        System.out.println("\n=== Crear actividad ===");

        System.out.print("Nombre de la actividad: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("Error: el nombre no puede estar vacío.");
            return;
        }

        System.out.print("Localidad: ");
        String localidad = scanner.nextLine().trim();

        if (localidad.isEmpty()) {
            System.out.println("Error: la localidad no puede estar vacía.");
            return;
        }

        System.out.println("Formato de fecha y hora: dd/MM/yyyy HH:mm");
        System.out.print("Fecha y hora: ");
        String fechaTexto = scanner.nextLine().trim();

        LocalDateTime fechaHora;

        try {
            fechaHora = LocalDateTime.parse(fechaTexto, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            System.out.println("Error: fecha inválida. Utiliza dd/MM/yyyy HH:mm.");
            return;
        }

        System.out.print("Número de plazas totales: ");
        String plazasTexto = scanner.nextLine().trim();

        int plazasTotales;

        try {
            plazasTotales = Integer.parseInt(plazasTexto);

            if (plazasTotales <= 0) {
                System.out.println("Error: las plazas totales deben ser positivas.");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: debes introducir un número entero de plazas.");
            return;
        }

        System.out.print("ID del monitor: ");
        String monitorTexto = scanner.nextLine().trim();

        long monitorId;

        try {
            monitorId = Long.parseLong(monitorTexto);

            if (monitorId <= 0) {
                System.out.println("Error: el ID debe ser positivo.");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: el ID debe ser un número entero.");
            return;
        }

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAUtil.getEntityManager();

            Monitor monitor = em.find(Monitor.class, monitorId);

            if (monitor == null) {
                System.out.println( "Error: no existe un monitor con ese ID.");
                return;
            }

            tx = em.getTransaction();
            tx.begin();

            Actividad actividad = new Actividad(
                    nombre,
                    localidad,
                    fechaHora,
                    plazasTotales,
                    monitor
            );

            em.persist(actividad);

            tx.commit();

            System.out.println("Actividad creada correctamente.");
            System.out.println("ID asignado: " + actividad.getId());
            System.out.println("Plazas disponibles: " + actividad.getPlazasDisponibles()
            );

        } catch (Exception e) {

            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            System.out.println("Error al crear la actividad. No se ha confirmado el alta.");
            e.printStackTrace();

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
	public void buscarActividadesDisponibles(Scanner scanner) {
	
	    System.out.println("\n=== Buscar actividades disponibles ===");
	
	    System.out.print("Introduce la localidad: ");
	    String localidad = scanner.nextLine().trim();
	
	    if (localidad.isEmpty()) {
	        System.out.println("Error: la localidad no puede estar vacía.");
	        return;
	    }
	
	    System.out.print("Introduce la fecha (dd/MM/yyyy): ");
	    String fechaTexto = scanner.nextLine().trim();
	
	    LocalDate fecha;
	
	    try {
	        fecha = LocalDate.parse(
	            fechaTexto,
	            DateTimeFormatter.ofPattern("dd/MM/yyyy")
	        );
	    } catch (DateTimeParseException e) {
	        System.out.println("Error: la fecha no tiene un formato válido.");
	        return;
	    }
	
	    System.out.print("Introduce el número de plazas necesarias: ");
	    String plazasTexto = scanner.nextLine().trim();
	
	    int plazas;
	
	    try {
	        plazas = Integer.parseInt(plazasTexto);
	
	        if (plazas <= 0) {
	            System.out.println("Error: las plazas deben ser mayores que cero.");
	            return;
	        }
	    } catch (NumberFormatException e) {
	        System.out.println("Error: introduce un número entero válido.");
	        return;
	    }
	
	    EntityManager em = null;
	
	    try {
	        em = JPAUtil.getEntityManager();
	
	        LocalDateTime inicio = fecha.atStartOfDay();
	        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
	
	        List<Actividad> actividades = em.createQuery(
	            "SELECT a FROM Actividad a " +
	            "JOIN FETCH a.monitor " +
	            "WHERE LOWER(a.localidad) = LOWER(:localidad) " +
	            "AND a.fechaHora >= :inicio " +
	            "AND a.fechaHora < :fin " +
	            "AND a.plazasDisponibles >= :plazas " +
	            "ORDER BY a.fechaHora",
	            Actividad.class
	        )
	        .setParameter("localidad", localidad)
	        .setParameter("inicio", inicio)
	        .setParameter("fin", fin)
	        .setParameter("plazas", plazas)
	        .getResultList();
	
	        if (actividades.isEmpty()) {
	            System.out.println("No se encontraron actividades disponibles.");
	            return;
	        }
	
	        System.out.println("\nActividades encontradas:");
	
	        for (Actividad actividad : actividades) {
	            System.out.println("------------------------------");
	            System.out.println("ID: " + actividad.getId());
	            System.out.println("Nombre: " + actividad.getNombre());
	            System.out.println("Localidad: " + actividad.getLocalidad());
	            System.out.println("Fecha y hora: " + actividad.getFechaHora());
	            System.out.println("Monitor: " + actividad.getMonitor().getNombre());
	            System.out.println("Plazas disponibles: "
	                + actividad.getPlazasDisponibles());
	        }
	
	    } catch (Exception e) {
	        System.out.println("Error al buscar actividades: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        if (em != null && em.isOpen()) {
	            em.close();
	        }
	    }
	}
	
	public void listarActividades() {
	
	    System.out.println("\n=== Listado de actividades ===");
	
	    EntityManager em = null;
	
	    try {
	        em = JPAUtil.getEntityManager();
	
	        List<Actividad> actividades = em.createQuery(
	            "SELECT a FROM Actividad a " +
	            "JOIN FETCH a.monitor " +
	            "ORDER BY a.fechaHora",
	            Actividad.class
	        ).getResultList();
	
	        if (actividades.isEmpty()) {
	            System.out.println("No hay actividades registradas.");
	            return;
	        }
	
	        for (Actividad actividad : actividades) {
	            System.out.println("------------------------------");
	            System.out.println("ID: " + actividad.getId());
	            System.out.println("Nombre: " + actividad.getNombre());
	            System.out.println("Localidad: " + actividad.getLocalidad());
	            System.out.println("Fecha y hora: " + actividad.getFechaHora());
	            System.out.println("Monitor: " + actividad.getMonitor().getNombre());
	            System.out.println("Plazas totales: " + actividad.getPlazasTotales());
	            System.out.println("Plazas disponibles: "
	                + actividad.getPlazasDisponibles());
	        }
	
	    } catch (Exception e) {
	        System.out.println("Error al listar actividades: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        if (em != null && em.isOpen()) {
	            em.close();
	        }
	    }
	}
}



