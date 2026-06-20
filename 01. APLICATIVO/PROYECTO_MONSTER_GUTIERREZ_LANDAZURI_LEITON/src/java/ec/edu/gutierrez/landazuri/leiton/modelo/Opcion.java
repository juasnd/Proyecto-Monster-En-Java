package ec.edu.gutierrez.landazuri.leiton.modelo;

public class Opcion {

    private String codigo;
    private String sistemaCodigo;
    private String descripcion;
    private String url;
    private String icono;
    private String codigoPadre;
    private String tipo;
    private int nivel;
    private int orden;
    private String estadoCodigo;

    public Opcion() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSistemaCodigo() {
        return sistemaCodigo;
    }

    public void setSistemaCodigo(String sistemaCodigo) {
        this.sistemaCodigo = sistemaCodigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getCodigoPadre() {
        return codigoPadre;
    }

    public void setCodigoPadre(String codigoPadre) {
        this.codigoPadre = codigoPadre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getEstadoCodigo() {
        return estadoCodigo;
    }

    public void setEstadoCodigo(String estadoCodigo) {
        this.estadoCodigo = estadoCodigo;
    }

    public boolean esSubsistema() {
        return "S".equalsIgnoreCase(tipo);
    }

    public boolean esGrupo() {
        return "G".equalsIgnoreCase(tipo);
    }

    public boolean esOpcionFinal() {
        return "O".equalsIgnoreCase(tipo);
    }
}