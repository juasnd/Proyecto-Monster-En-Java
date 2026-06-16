package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.UsuarioDAO;
import ec.edu.gutierrez.landazuri.leiton.modelo.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CambiarClaveController", urlPatterns = {"/CambiarClaveController"})
public class CambiarClaveController extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String login = String.valueOf(sesion.getAttribute("usuarioLogueado"));
        String actual = valor(request, "claveActual");
        String nueva = valor(request, "claveNueva");
        String confirmar = valor(request, "claveConfirmar");
        Usuario usuario = usuarioDAO.buscarPorLogin(login);

        if (usuario == null || !usuarioDAO.validarPassword(usuario, actual)) {
            volver(request, response, "La contrasena actual no es correcta.");
            return;
        }

        if (nueva.isEmpty()) {
            volver(request, response, "La nueva contrasena es obligatoria.");
            return;
        }

        if (!nueva.equals(confirmar)) {
            volver(request, response, "La confirmacion no coincide con la nueva contrasena.");
            return;
        }

        if (usuarioDAO.actualizarPassword(login, nueva, false)) {
            usuario.setCambioClave("N");
            sesion.setAttribute("usuario", usuario);
            response.sendRedirect(request.getContextPath() + "/pagPrincipal.jsp?mensaje=clave_actualizada");
        } else {
            volver(request, response, "No se pudo actualizar la contrasena.");
        }
    }

    private void volver(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        request.setAttribute("error", error);
        request.getRequestDispatcher("cambiarClave.jsp").forward(request, response);
    }

    private String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}
