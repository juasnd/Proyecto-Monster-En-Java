package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.PerfilDAO;
import ec.edu.gutierrez.landazuri.leiton.dao.UsuarioDAO;
import ec.edu.gutierrez.landazuri.leiton.modelo.Persona;
import java.io.IOException;
import java.sql.SQLException;
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
            case "guardarExistente":
                guardarExistente(request, response);
                break;
            case "guardarExterno":
                guardarExterno(request, response);
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

    private void guardarExistente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String personaCodigo = valor(request, "personaCodigo");
        String login = valor(request, "loginExistente");
        String perfilCodigo = valor(request, "perfilCodigoExistente");

        if (personaCodigo.isEmpty() || perfilCodigo.isEmpty()) {
            volverConError(request, response, "Seleccione una persona y un perfil.");
            return;
        }

        try {
            usuarioDAO.registrarUsuarioParaPersona(personaCodigo, login, perfilCodigo);
            response.sendRedirect(request.getContextPath() + "/UsuarioController?mensaje=guardado");
        } catch (SQLException e) {
            e.printStackTrace();
            volverConError(request, response, e.getMessage());
        }
    }

    private void guardarExterno(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Persona persona = new Persona();
        persona.setTipo(valor(request, "tipoPersona"));
        persona.setNombres(valor(request, "nombres"));
        persona.setApellidos(valor(request, "apellidos"));
        persona.setCedula(valor(request, "cedula"));
        persona.setEmail(valor(request, "email"));
        persona.setCelular(valor(request, "celular"));
        persona.setDireccion(valor(request, "direccion"));

        String login = valor(request, "loginExterno");
        String perfilCodigo = valor(request, "perfilCodigoExterno");

        if (persona.getNombres().isEmpty() || persona.getApellidos().isEmpty() || perfilCodigo.isEmpty()) {
            volverConError(request, response, "Complete nombres, apellidos y perfil del usuario.");
            return;
        }

        try {
            usuarioDAO.crearUsuarioExterno(persona, login, perfilCodigo);
            response.sendRedirect(request.getContextPath() + "/UsuarioController?mensaje=guardado");
        } catch (SQLException e) {
            e.printStackTrace();
            volverConError(request, response, e.getMessage());
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

    private void volverConError(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        request.setAttribute("error", error == null || error.trim().isEmpty()
                ? "No se pudo registrar el usuario."
                : error);
        cargarDatos(request);
        request.getRequestDispatcher("usuarios.jsp").forward(request, response);
    }

    private void cargarDatos(HttpServletRequest request) {
        request.setAttribute("usuarios", usuarioDAO.listarUsuarios());
        request.setAttribute("perfiles", perfilDAO.listarPerfiles());
        request.setAttribute("personasSinUsuario", usuarioDAO.listarPersonasSinUsuario());
        request.setAttribute("empleadosSinUsuario", usuarioDAO.listarPersonasSinUsuario());
        request.setAttribute("claveTemporal", UsuarioDAO.CONTRASENA_TEMPORAL);
    }

    private String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}
