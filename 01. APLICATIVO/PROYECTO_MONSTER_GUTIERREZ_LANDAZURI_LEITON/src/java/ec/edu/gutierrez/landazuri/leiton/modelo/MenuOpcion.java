package ec.edu.gutierrez.landazuri.leiton.modelo;

import java.util.ArrayList;
import java.util.List;

public class MenuOpcion extends Opcion {

    private String perfilCodigo;
    private String perfilDescripcion;

    private boolean puedeVer;
    private boolean puedeCrear;
    private boolean puedeEditar;
    private boolean puedeEliminar;

    private List<MenuOpcion> hijos = new ArrayList<>();

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

    public boolean isPuedeVer() {
        return puedeVer;
    }

    public void setPuedeVer(boolean puedeVer) {
        this.puedeVer = puedeVer;
    }

    public boolean isPuedeCrear() {
        return puedeCrear;
    }

    public void setPuedeCrear(boolean puedeCrear) {
        this.puedeCrear = puedeCrear;
    }

    public boolean isPuedeEditar() {
        return puedeEditar;
    }

    public void setPuedeEditar(boolean puedeEditar) {
        this.puedeEditar = puedeEditar;
    }

    public boolean isPuedeEliminar() {
        return puedeEliminar;
    }

    public void setPuedeEliminar(boolean puedeEliminar) {
        this.puedeEliminar = puedeEliminar;
    }

    public List<MenuOpcion> getHijos() {
        return hijos;
    }

    public void setHijos(List<MenuOpcion> hijos) {
        this.hijos = hijos;
    }

    public boolean tieneHijos() {
        return hijos != null && !hijos.isEmpty();
    }
}