package es.activa.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "monitor")
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "especialidad", nullable = false)
    private String especialidad;

    @OneToMany(mappedBy = "monitor")
    private List<Actividad> actividades = new ArrayList<>();

    // Constructor vacío obligatorio para JPA
    public Monitor() {
    }

    // Constructor para crear monitores nuevos
    public Monitor(String nombre, String especialidad) {
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
    }

    // Mantiene sincronizados los dos extremos de la relación
    public void addActividad(Actividad actividad) {
        if (actividad != null && !actividades.contains(actividad)) {
            actividades.add(actividad);
            actividad.setMonitor(this);
        }
    }

    public void removeActividad(Actividad actividad) {
        if (actividad != null && actividades.remove(actividad)) {
            actividad.setMonitor(null);
        }
    }

    @Override
    public String toString() {
        return "Monitor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", especialidad='" + especialidad + '\'' +
                '}';
    }
}




