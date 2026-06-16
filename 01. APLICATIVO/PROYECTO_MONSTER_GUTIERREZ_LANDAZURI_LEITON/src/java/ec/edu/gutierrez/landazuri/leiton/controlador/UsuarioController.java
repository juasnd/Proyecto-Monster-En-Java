package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.PerfilDAO;
import ec.edu.gutierrez.landazuri.leiton.dao.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UsuarioController", urlPatterns = {"/UsuarioController"})
public class UsuarioController extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PerfilDAO perfilDAO = new PerfilDAO();

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
            case "guardar":
                guardar(request, response);
                break;
            case "activar":
                cambiarEstado(request, response, true);
                break;
            case "bloquear":
                cambiarEstado(request, response, false);
                break;
            case "resetear":
                resetear(request, response);
                break;
            case "asignarPerfil":
                asignarPerfil(request, response);
                break;
            default:
                listar(request, response);
                break;
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        cargarDatos(request);
        request.getRequestDispatcher("usuarios.jsp").forward(request, response);
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String personaCodigo = valor(request, "personaCodigo");
        String login = valor(request, "login");
        String perfilCodigo = valor(request, "perfilCodigo");

        if (personaCodigo.isEmpty() || login.isEmpty() || perfilCodigo.isEmpty()) {
            request.setAttribute("error", "Seleccione persona, login y perfil.");
            cargarDatos(request);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
            return;
        }

        if (usuarioDAO.registrarUsuarioParaPersona(personaCodigo, login, perfilCodigo)) {
            response.sendRedirect(request.getContextPath() + "/UsuarioController?mensaje=guardado");
        } else {
            request.setAttribute("error", "No se pudo registrar el usuario.");
            cargarDatos(request);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
        }
    }

    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response, boolean activar)
            throws IOException {

        String login = valor(request, "login");
        boolean ok = activar ? usuarioDAO.activarUsuario(login) : usuarioDAO.bloquearUsuario(login);
        response.sendRedirect(request.getContextPath() + "/UsuarioController?mensaje="
                + (ok ? (activar ? "activado" : "bloqueado") : "no_actualizado"));
    }

    private void resetear(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = valor(request, "login");
        boolean ok = usuarioDAO.resetearPassword(login);
        response.sendRedirect(request.getContextPath() + "/UsuarioController?mensaje="
                + (ok ? "reseteado" : "no_actualizado"));
    }

    private void asignarPerfil(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = valor(request, "login");
        String perfilCodigo = valor(request, "perfilCodigo");
        boolean ok = usuarioDAO.asignarPerfil(login, perfilCodigo);
        response.sendRedirect(request.getContextPath() + "/UsuarioController?mensaje="
                + (ok ? "perfil_asignado" : "no_actualizado"));
    }

    private void cargarDatos(HttpServletRequest request) {
        request.setAttribute("usuarios", usuarioDAO.listarUsuarios());
        request.setAttribute("perfiles", perfilDAO.listarPerfiles());
        request.setAttribute("empleadosSinUsuario", usuarioDAO.listarEmpleadosSinUsuario());
        request.setAttribute("claveTemporal", UsuarioDAO.CONTRASENA_TEMPORAL);
    }

    private String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}
