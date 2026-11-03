
package es.activa.service;

import es.activa.config.JPAUtil;
import es.activa.domain.Monitor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Scanner;

public class MonitorService {

    public void crearMonitor(Scanner scanner) {

        System.out.println("\n=== Crear monitor ===");

        System.out.print("Nombre del monitor: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("Error: el nombre no puede estar vacío.");
            return;
        }

        System.out.print("Especialidad: ");
        String especialidad = scanner.nextLine().trim();

        if (especialidad.isEmpty()) {
            System.out.println("Error: la especialidad no puede estar vacía.");
            return;
        }

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();

            tx.begin();

            Monitor monitor = new Monitor(nombre, especialidad);
            em.persist(monitor);

            tx.commit();

            System.out.println("Monitor creado correctamente.");
            System.out.println("ID asignado: " + monitor.getId());

        } catch (Exception e) {

            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            System.out.println("Error al crear el monitor. No se ha confirmado el alta.");
            e.printStackTrace();

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}



