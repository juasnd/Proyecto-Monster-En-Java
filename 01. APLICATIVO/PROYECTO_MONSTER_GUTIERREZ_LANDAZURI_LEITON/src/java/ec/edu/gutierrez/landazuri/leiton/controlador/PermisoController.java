package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.OpcionDAO;
import ec.edu.gutierrez.landazuri.leiton.dao.PerfilDAO;
import ec.edu.gutierrez.landazuri.leiton.dao.PermisoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PermisoController", urlPatterns = {"/PermisoController"})
public class PermisoController extends HttpServlet {

    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final OpcionDAO opcionDAO = new OpcionDAO();
    private final PermisoDAO permisoDAO = new PermisoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        listar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String perfilCodigo = valor(request, "perfilCodigo");
        String[] opciones = request.getParameterValues("opciones");
        boolean ok = permisoDAO.guardarPermisos(perfilCodigo, opciones);
        response.sendRedirect(request.getContextPath() + "/PermisoController?perfilCodigo="
                + perfilCodigo + "&mensaje=" + (ok ? "guardado" : "no_guardado"));
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String perfilCodigo = valor(request, "perfilCodigo");
        request.setAttribute("perfiles", perfilDAO.listarPerfiles());
        request.setAttribute("opciones", opcionDAO.listarOpciones());
        request.setAttribute("perfilSeleccionado", perfilCodigo);
        request.setAttribute("permisosSeleccionados", permisoDAO.listarCodigosPorPerfil(perfilCodigo));
        request.getRequestDispatcher("permisos.jsp").forward(request, response);
    }

    private String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}
