package ec.edu.gutierrez.landazuri.leiton.modelo;

public class Perfil {

    private String codigo;
    private String descripcion;
    private String estadoCodigo;
    private String estadoDescripcion;

    public Perfil() {
    }

    public Perfil(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstadoCodigo() {
        return estadoCodigo;
    }

    public void setEstadoCodigo(String estadoCodigo) {
        this.estadoCodigo = estadoCodigo;
    }

    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }

    public boolean estaActivo() {
        return estadoCodigo == null || "A".equalsIgnoreCase(estadoCodigo);
    }
}
