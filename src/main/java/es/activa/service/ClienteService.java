
package es.activa.service;

import es.activa.config.JPAUtil;
import es.activa.domain.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Scanner;

public class ClienteService {

    public void crearCliente(Scanner scanner) {

        System.out.println("\n=== Crear cliente ===");

        System.out.print("Introduce el nombre del cliente: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("Error: el nombre no puede estar vacío.");
            return;
        }

        System.out.print("Introduce el email del cliente: ");
        String email = scanner.nextLine().trim();

        if (email.isEmpty() || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            System.out.println("Error: introduce un email válido.");
            return;
        }

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAUtil.getEntityManager();

            // Comprobar si ya existe un cliente con ese email.
            Long cantidad = em.createQuery(
                "SELECT COUNT(c) FROM Cliente c WHERE LOWER(c.email) = LOWER(:email)",
                Long.class
            )
            .setParameter("email", email)
            .getSingleResult();

            if (cantidad > 0) {
                System.out.println("Error: ya existe un cliente con ese email.");
                return;
            }

            tx = em.getTransaction();
            tx.begin();

            Cliente cliente = new Cliente(nombre, email);
            em.persist(cliente);

            tx.commit();

            System.out.println("Cliente creado correctamente.");
            System.out.println("ID asignado: " + cliente.getId());
            System.out.println("Nombre: " + cliente.getNombre());
            System.out.println("Email: " + cliente.getEmail());

        } catch (Exception e) {

            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

            System.out.println("Error al crear el cliente: " + e.getMessage());
            e.printStackTrace();

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
