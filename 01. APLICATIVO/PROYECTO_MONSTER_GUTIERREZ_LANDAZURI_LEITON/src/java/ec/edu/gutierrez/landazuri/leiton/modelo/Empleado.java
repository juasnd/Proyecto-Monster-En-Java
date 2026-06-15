package ec.edu.gutierrez.landazuri.leiton.modelo;

import java.util.ArrayList;
import java.util.List;

public class Empleado {

    private String peempCodigo;
    private String pedepCodigo;
    private String pecarCodigo;
    private String pedPedepCodigo;
    private String nombreDepartamento;
    private String nombreCargo;
    private String nombreDepartamentoPadre;
    private Persona persona;
    private List<Familiar> familiares;
    private List<Formacion> formaciones;

    public Empleado() {
        this.persona = new Persona();
        this.familiares = new ArrayList<>();
        this.formaciones = new ArrayList<>();
    }

    public String getPeempCodigo() {
        return peempCodigo;
    }

    public void setPeempCodigo(String peempCodigo) {
        this.peempCodigo = peempCodigo;
    }

    public String getPedepCodigo() {
        return pedepCodigo;
    }

    public void setPedepCodigo(String pedepCodigo) {
        this.pedepCodigo = pedepCodigo;
    }

    public String getPecarCodigo() {
        return pecarCodigo;
    }

    public void setPecarCodigo(String pecarCodigo) {
        this.pecarCodigo = pecarCodigo;
    }

    public String getPedPedepCodigo() {
        return pedPedepCodigo;
    }

    public void setPedPedepCodigo(String pedPedepCodigo) {
        this.pedPedepCodigo = pedPedepCodigo;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }

    public String getNombreDepartamentoPadre() {
        return nombreDepartamentoPadre;
    }

    public void setNombreDepartamentoPadre(String nombreDepartamentoPadre) {
        this.nombreDepartamentoPadre = nombreDepartamentoPadre;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public List<Familiar> getFamiliares() {
        return familiares;
    }

    public void setFamiliares(List<Familiar> familiares) {
        this.familiares = familiares == null ? new ArrayList<Familiar>() : familiares;
    }

    public List<Formacion> getFormaciones() {
        return formaciones;
    }

    public void setFormaciones(List<Formacion> formaciones) {
        this.formaciones = formaciones == null ? new ArrayList<Formacion>() : formaciones;
    }
}
