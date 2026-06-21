package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.PermisoDAO;

import ec.edu.gutierrez.landazuri.leiton.dao.PerfilDAO;
import ec.edu.gutierrez.landazuri.leiton.modelo.Perfil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "PerfilController", urlPatterns = {"/PerfilController"})
public class PerfilController extends HttpServlet {

    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final PermisoDAO permisoDAO = new PermisoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = valor(request, "accion");

        if (accion.isEmpty()) {
            accion = "listar";
        }

        switch (accion) {
            case "nuevo":
                request.setAttribute("modo", "nuevo");
                listar(request, response);
                break;
            case "guardar":
                guardar(request, response);
                break;
            case "editar":
                request.setAttribute("perfilEditar", perfilDAO.buscarPorCodigo(valor(request, "codigo")));
                request.setAttribute("modo", "editar");
                listar(request, response);
                break;
            case "actualizar":
                actualizar(request, response);
                break;
            case "activar":
                cambiarEstado(request, response, "A");
                break;
            case "inactivar":
                cambiarEstado(request, response, "I");
                break;
            default:
                listar(request, response);
                break;
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("perfiles", perfilDAO.listarPerfiles());
        cargarPermisoReporte(request);
        request.getRequestDispatcher("perfiles.jsp").forward(request, response);
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Perfil perfil = leerPerfil(request);

        if (perfil.getCodigo().isEmpty() || perfil.getDescripcion().isEmpty()) {
            request.setAttribute("error", "Ingrese codigo y descripcion del perfil.");
            request.setAttribute("modo", "nuevo");
            listar(request, response);
            return;
        }

        if (perfilDAO.guardar(perfil)) {
            response.sendRedirect(request.getContextPath() + "/PerfilController?mensaje=guardado");
        } else {
            request.setAttribute("error", "No se pudo guardar el perfil.");
            request.setAttribute("modo", "nuevo");
            listar(request, response);
        }
    }

    private void actualizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Perfil perfil = leerPerfil(request);

        if (perfilDAO.actualizar(perfil)) {
            response.sendRedirect(request.getContextPath() + "/PerfilController?mensaje=actualizado");
        } else {
            request.setAttribute("error", "No se pudo actualizar el perfil.");
            request.setAttribute("perfilEditar", perfil);
            request.setAttribute("modo", "editar");
            listar(request, response);
        }
    }

    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response, String estado)
            throws IOException {

        boolean ok = perfilDAO.cambiarEstado(valor(request, "codigo"), estado);
        response.sendRedirect(request.getContextPath() + "/PerfilController?mensaje="
                + (ok ? "actualizado" : "no_actualizado"));
    }


    private void cargarPermisoReporte(HttpServletRequest request) {
        String perfilCodigo = obtenerPerfilCodigo(request);
        boolean permitido = permisoDAO.tienePermiso(perfilCodigo, "RPF");
        request.setAttribute("puedeReportePerfiles", permitido);
    }

    private String obtenerPerfilCodigo(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("perfilCodigo") == null) {
            return "";
        }

        return String.valueOf(sesion.getAttribute("perfilCodigo"));
    }
    private Perfil leerPerfil(HttpServletRequest request) {
        Perfil perfil = new Perfil();
        perfil.setCodigo(valor(request, "codigo").toUpperCase());
        perfil.setDescripcion(valor(request, "descripcion"));
        perfil.setEstadoCodigo(valor(request, "estado").isEmpty() ? "A" : valor(request, "estado"));
        return perfil;
    }

    private String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}
