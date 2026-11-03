package es.activa.main;

import es.activa.config.JPAUtil;
import es.activa.service.ActividadService;
import es.activa.service.ClienteService;
import es.activa.service.MonitorService;
import es.activa.service.ReservaService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ActividadService actividadService = new ActividadService();
        ClienteService clienteService = new ClienteService();
        MonitorService monitorService = new MonitorService();
        ReservaService reservaService = new ReservaService();

        try (Scanner scanner = new Scanner(System.in)) {

            int opcion = 0;

            do {
                mostrarMenu();
                System.out.println();
                System.out.print("Selecciona una opción: ");
                String textoOpcion = scanner.nextLine().trim();

                try {
                    opcion = Integer.parseInt(textoOpcion);
                } catch (NumberFormatException e) {
                    opcion = 0;
                }

                switch (opcion) {

                    case 1:
                        monitorService.crearMonitor(scanner);
                        break;

                    case 2:
                        actividadService.crearActividad(scanner);
                        break;

                    case 3:
                        actividadService.buscarActividadesDisponibles(scanner);
                        break;

                    case 4:
                        clienteService.crearCliente(scanner);
                        break;

                    case 5:
                        reservaService.crearReserva(scanner);
                        break;

                    case 6:
                        reservaService.cancelarReserva(scanner);
                        break;

                    case 7:
                        actividadService.listarActividades();
                        break;

                    case 8:
                        System.out.println("Saliendo de ACTIVA. ¡Hasta pronto!");
                        break;

                    default:
                        System.out.println(
                            "Opción no válida. Introduce un número del 1 al 8."
                        );
                }

            } while (opcion != 8);

        } finally {
            JPAUtil.close();
        }
    }

    private static void mostrarMenu() {
    	
    	System.out.println();
        System.out.println("=== ACTIVA · Gestión de actividades ===");
        System.out.println();
        System.out.println("1. Crear monitor");
        System.out.println("2. Crear actividad");
        System.out.println("3. Buscar actividades disponibles");
        System.out.println("4. Crear cliente");
        System.out.println("5. Crear reserva");
        System.out.println("6. Cancelar reserva");
        System.out.println("7. Listar actividades");
        System.out.println("8. Salir");
    }
}






