package com.sportreserve.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PersonaRequest {

    @NotBlank(message = "La identificacion es obligatoria")
    @Size(max = 20, message = "La identificacion no debe exceder 20 caracteres")
    private String identificacion;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no debe exceder 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no debe exceder 50 caracteres")
    private String apellido;

    @Size(max = 15, message = "El telefono no debe exceder 15 caracteres")
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser valido")
    private String correo;

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
}
