package com.sportreserve.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La identificacion es obligatoria")
    @Size(max = 20, message = "La identificacion no debe exceder 20 caracteres")
    @Column(name = "identificacion", nullable = false, unique = true, length = 20)
    private String identificacion;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no debe exceder 50 caracteres")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no debe exceder 50 caracteres")
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;

    @Size(max = 15, message = "El telefono no debe exceder 15 caracteres")
    @Column(name = "telefono", length = 15)
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser valido")
    @Column(name = "correo", nullable = false)
    private String correo;

    public Persona() {
    }

    public Persona(String identificacion, String nombre, String apellido, String telefono, String correo) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona persona)) return false;
        return identificacion != null && Objects.equals(identificacion, persona.identificacion);
    }

    @Override
    public int hashCode() {
        return identificacion != null
                ? Objects.hash(identificacion)
                : Objects.hash(System.identityHashCode(this));
    }

    @Override
    public String toString() {
        return "Persona{id=" + id + ", identificacion='" + identificacion + "', nombre='" + nombre + "', apellido='" + apellido + "'}";
    }
}
