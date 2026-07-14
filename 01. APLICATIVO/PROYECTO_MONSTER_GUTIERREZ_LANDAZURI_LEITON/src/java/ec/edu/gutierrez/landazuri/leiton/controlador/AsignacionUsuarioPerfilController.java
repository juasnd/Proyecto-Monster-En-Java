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
        urlPatterns = {
            "/AsignacionUsuarioPerfilController"
        }
)
public class AsignacionUsuarioPerfilController
        extends HttpServlet {

    private final PerfilDAO perfilDAO
            = new PerfilDAO();

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

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        String perfilCodigo
                = valor(request, "perfilCodigo");

        String accion
                = valor(request, "accion");

        if (perfilCodigo.isEmpty()) {

            redirigir(
                    request,
                    response,
                    "",
                    "seleccione_perfil"
            );

            return;
        }

        boolean resultado;
        String mensajeExito;

        switch (accion) {

            case "asignarSeleccionados": {

                String[] usuariosDisponibles
                        = request.getParameterValues(
                                "usuariosDisponibles"
                        );

                if (
                        usuariosDisponibles == null
                        || usuariosDisponibles.length == 0
                ) {

                    redirigir(
                            request,
                            response,
                            perfilCodigo,
                            "seleccione_usuario"
                    );

                    return;
                }

                resultado
                        = asignacionDAO.asignarUsuarios(
                                perfilCodigo,
                                usuariosDisponibles
                        );

                mensajeExito = "asignados";

                break;
            }

            case "asignarTodos": {

                resultado
                        = asignacionDAO.asignarTodos(
                                perfilCodigo
                        );

                mensajeExito = "asignados_todos";

                break;
            }

            case "retirarSeleccionados": {

                String[] usuariosAsignados
                        = request.getParameterValues(
                                "usuariosAsignados"
                        );

                if (
                        usuariosAsignados == null
                        || usuariosAsignados.length == 0
                ) {

                    redirigir(
                            request,
                            response,
                            perfilCodigo,
                            "seleccione_usuario"
                    );

                    return;
                }

                resultado
                        = asignacionDAO.retirarUsuarios(
                                perfilCodigo,
                                usuariosAsignados
                        );

                mensajeExito = "retirados";

                break;
            }

            case "retirarTodos": {

                resultado
                        = asignacionDAO.retirarTodos(
                                perfilCodigo
                        );

                mensajeExito = "retirados_todos";

                break;
            }

            default: {

                redirigir(
                        request,
                        response,
                        perfilCodigo,
                        "accion_invalida"
                );

                return;
            }
        }

        redirigir(
                request,
                response,
                perfilCodigo,
                resultado
                        ? mensajeExito
                        : "error"
        );
    }

    private void listar(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String perfilCodigo
                = valor(request, "perfilCodigo");

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
                    asignacionDAO.listarDisponibles(
                            perfilCodigo
                    )
            );

            request.setAttribute(
                    "usuariosAsignados",
                    asignacionDAO.listarAsignados(
                            perfilCodigo
                    )
            );
        }

        request.getRequestDispatcher(
                "/asignarUsuariosPerfil.jsp"
        ).forward(request, response);
    }

    private void redirigir(
            HttpServletRequest request,
            HttpServletResponse response,
            String perfilCodigo,
            String mensaje
    ) throws IOException {

        String perfilCodificado
                = URLEncoder.encode(
                        perfilCodigo == null
                                ? ""
                                : perfilCodigo,
                        StandardCharsets.UTF_8.name()
                );

        String mensajeCodificado
                = URLEncoder.encode(
                        mensaje == null
                                ? ""
                                : mensaje,
                        StandardCharsets.UTF_8.name()
                );

        response.sendRedirect(
                request.getContextPath()
                + "/AsignacionUsuarioPerfilController"
                + "?perfilCodigo=" + perfilCodificado
                + "&mensaje=" + mensajeCodificado
        );
    }

    private String valor(
            HttpServletRequest request,
            String nombre
    ) {

        String dato
                = request.getParameter(nombre);

        return dato == null
                ? ""
                : dato.trim();
    }
}