package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.AsignacionUsuarioPerfilDAO;
import ec.edu.gutierrez.landazuri.leiton.dao.PerfilDAO;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "AsignacionUsuarioPerfilController",
        urlPatterns = {"/AsignacionUsuarioPerfilController"}
)
public class AsignacionUsuarioPerfilController extends HttpServlet {

    private final PerfilDAO perfilDAO = new PerfilDAO();

    private final AsignacionUsuarioPerfilDAO asignacionDAO
            = new AsignacionUsuarioPerfilDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        listar(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String perfilCodigo = obtenerValor(request, "perfilCodigo");
        String accion = obtenerValor(request, "accion");

        if (perfilCodigo.isEmpty()) {
            redirigir(
                    request,
                    response,
                    "",
                    "seleccione_perfil"
            );
            return;
        }

        switch (accion) {

            case "asignarSeleccionados":
                asignarSeleccionados(
                        request,
                        response,
                        perfilCodigo
                );
                break;

            case "asignarTodos":
                asignarTodos(
                        request,
                        response,
                        perfilCodigo
                );
                break;

            case "retirarSeleccionados":
                retirarSeleccionados(
                        request,
                        response,
                        perfilCodigo
                );
                break;

            case "retirarTodos":
                retirarTodos(
                        request,
                        response,
                        perfilCodigo
                );
                break;

            default:
                redirigir(
                        request,
                        response,
                        perfilCodigo,
                        "accion_invalida"
                );
                break;
        }
    }

    private void listar(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String perfilCodigo
                = obtenerValor(request, "perfilCodigo");

        request.setAttribute(
                "perfiles",
                perfilDAO.listarPerfiles()
        );

        request.setAttribute(
                "perfilSeleccionado",
                perfilCodigo
        );

        if (!perfilCodigo.isEmpty()) {

            request.setAttribute(
                    "usuariosDisponibles",
                    asignacionDAO.listarDisponibles(perfilCodigo)
            );

            request.setAttribute(
                    "usuariosAsignados",
                    asignacionDAO.listarAsignados(perfilCodigo)
            );
        }

        request.getRequestDispatcher(
                "/asignarUsuariosPerfil.jsp"
        ).forward(request, response);
    }

    private void asignarSeleccionados(
            HttpServletRequest request,
            HttpServletResponse response,
            String perfilCodigo
    ) throws IOException {

        String[] usuarios
                = request.getParameterValues(
                        "usuariosDisponibles"
                );

        if (usuarios == null || usuarios.length == 0) {
            redirigir(
                    request,
                    response,
                    perfilCodigo,
                    "seleccione_usuario"
            );
            return;
        }

        boolean resultado
                = asignacionDAO.asignarUsuarios(
                        perfilCodigo,
                        usuarios
                );

        redirigir(
                request,
                response,
                perfilCodigo,
                resultado ? "asignados" : "error"
        );
    }

    private void asignarTodos(
            HttpServletRequest request,
            HttpServletResponse response,
            String perfilCodigo
    ) throws IOException {

        boolean resultado
                = asignacionDAO.asignarTodos(
                        perfilCodigo
                );

        redirigir(
                request,
                response,
                perfilCodigo,
                resultado ? "asignados_todos" : "error"
        );
    }

    private void retirarSeleccionados(
            HttpServletRequest request,
            HttpServletResponse response,
            String perfilCodigo
    ) throws IOException {

        String[] usuarios
                = request.getParameterValues(
                        "usuariosAsignados"
                );

        if (usuarios == null || usuarios.length == 0) {
            redirigir(
                    request,
                    response,
                    perfilCodigo,
                    "seleccione_usuario"
            );
            return;
        }

        boolean resultado
                = asignacionDAO.retirarUsuarios(
                        perfilCodigo,
                        usuarios
                );

        redirigir(
                request,
                response,
                perfilCodigo,
                resultado ? "retirados" : "error"
        );
    }

    private void retirarTodos(
            HttpServletRequest request,
            HttpServletResponse response,
            String perfilCodigo
    ) throws IOException {

        boolean resultado
                = asignacionDAO.retirarTodos(
                        perfilCodigo
                );

        redirigir(
                request,
                response,
                perfilCodigo,
                resultado ? "retirados_todos" : "error"
        );
    }

    private void redirigir(
            HttpServletRequest request,
            HttpServletResponse response,
            String perfilCodigo,
            String mensaje
    ) throws IOException {

        String perfilCodificado = URLEncoder.encode(
                perfilCodigo == null ? "" : perfilCodigo,
                StandardCharsets.UTF_8.name()
        );

        String mensajeCodificado = URLEncoder.encode(
                mensaje,
                StandardCharsets.UTF_8.name()
        );

        response.sendRedirect(
                request.getContextPath()
                + "/AsignacionUsuarioPerfilController"
                + "?perfilCodigo=" + perfilCodificado
                + "&mensaje=" + mensajeCodificado
        );
    }

    private String obtenerValor(
            HttpServletRequest request,
            String parametro
    ) {

        String valor = request.getParameter(parametro);

        return valor == null
                ? ""
                : valor.trim();
    }
}