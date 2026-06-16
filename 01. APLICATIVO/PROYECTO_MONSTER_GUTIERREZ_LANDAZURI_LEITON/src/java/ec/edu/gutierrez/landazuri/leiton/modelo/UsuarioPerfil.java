package ec.edu.gutierrez.landazuri.leiton.modelo;

public class UsuarioPerfil {

    private String usuarioCodigo;
    private String login;
    private String personaCodigo;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String perfilCodigo;
    private String perfilDescripcion;
    private String estadoCodigo;
    private String estadoDescripcion;
    private String cambioClave;
    private int intentosFallidos;
    private String ultimoAcceso;

    public String getUsuarioCodigo() {
        return usuarioCodigo;
    }

    public void setUsuarioCodigo(String usuarioCodigo) {
        this.usuarioCodigo = usuarioCodigo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPersonaCodigo() {
        return personaCodigo;
    }

    public void setPersonaCodigo(String personaCodigo) {
        this.personaCodigo = personaCodigo;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPerfilCodigo() {
        return perfilCodigo;
    }

    public void setPerfilCodigo(String perfilCodigo) {
        this.perfilCodigo = perfilCodigo;
    }

    public String getPerfilDescripcion() {
        return perfilDescripcion;
    }

    public void setPerfilDescripcion(String perfilDescripcion) {
        this.perfilDescripcion = perfilDescripcion;
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

    public String getCambioClave() {
        return cambioClave;
    }

    public void setCambioClave(String cambioClave) {
        this.cambioClave = cambioClave;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public String getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(String ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}
