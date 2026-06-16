package ec.edu.gutierrez.landazuri.leiton.modelo;

public class Usuario {

    private String codigo;
    private String codigoPersona;
    private String usuario;
    private String password;
    private String estadoCodigo;
    private String estadoDescripcion;
    private int intentosFallidos;
    private String cambioClave;
    private String ultimoAcceso;
    private Perfil perfil;

    public Usuario() {
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public String getCambioClave() {
        return cambioClave;
    }

    public void setCambioClave(String cambioClave) {
        this.cambioClave = cambioClave;
    }

    public String getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(String ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public boolean requiereCambioClave() {
        return "S".equalsIgnoreCase(cambioClave);
    }

    public boolean estaActivo() {
        return estadoCodigo == null || "A".equalsIgnoreCase(estadoCodigo);
    }

    public boolean estaBloqueado() {
        return "B".equalsIgnoreCase(estadoCodigo);
    }
}
