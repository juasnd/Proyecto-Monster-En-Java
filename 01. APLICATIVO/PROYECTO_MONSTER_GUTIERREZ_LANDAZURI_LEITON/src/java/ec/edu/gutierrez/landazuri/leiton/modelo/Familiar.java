package ec.edu.gutierrez.landazuri.leiton.modelo;

public class Familiar {

    private String codigo;
    private String codigoPersona;
    private String codigoParentesco;
    private String descripcionParentesco;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String telefono;
    private String cargaFamiliar;
    private String observacion;

    public Familiar() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoPersona() {
        return codigoPersona;
    }

    public void setCodigoPersona(String codigoPersona) {
        this.codigoPersona = codigoPersona;
    }

    public String getCodigoParentesco() {
        return codigoParentesco;
    }

    public void setCodigoParentesco(String codigoParentesco) {
        this.codigoParentesco = codigoParentesco;
    }

    public String getDescripcionParentesco() {
        return descripcionParentesco;
    }

    public void setDescripcionParentesco(String descripcionParentesco) {
        this.descripcionParentesco = descripcionParentesco;
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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCargaFamiliar() {
        return cargaFamiliar;
    }

    public void setCargaFamiliar(String cargaFamiliar) {
        this.cargaFamiliar = cargaFamiliar;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
