package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.PermisoDAO;
import ec.edu.gutierrez.landazuri.leiton.dao.ReporteHistorialDAO;
import ec.edu.gutierrez.landazuri.leiton.modelo.ReporteHistorial;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ReporteController", urlPatterns = {"/ReporteController"})
public class ReporteController extends HttpServlet {

    private final ReporteHistorialDAO reporteDAO = new ReporteHistorialDAO();
    private final PermisoDAO permisoDAO = new PermisoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!sesionValida(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = valor(request, "accion");

        if ("detalle".equalsIgnoreCase(accion)) {
            detalle(request, response);
            return;
        }

        listar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!sesionValida(request)) {
            responderJson(response, HttpServletResponse.SC_UNAUTHORIZED, false, "Sesion no valida.");
            return;
        }

        String accion = valor(request, "accion");

        if ("registrar".equalsIgnoreCase(accion)) {
            registrar(request, response);
            return;
        }

        if ("eliminar".equalsIgnoreCase(accion)) {
            eliminar(request, response);
            return;
        }

        listar(request, response);
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("historialReportes", reporteDAO.listarHistorial());
        request.getRequestDispatcher("reportes.jsp").forward(request, response);
    }

    private void detalle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String codigo = valor(request, "codigo");
        request.setAttribute("reporteDetalle", reporteDAO.buscarPorCodigo(codigo));
        listar(request, response);
    }

    private void registrar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String codigoReporte = valor(request, "codigoReporte").toUpperCase();
        String perfilCodigo = valorSesion(request, "perfilCodigo");

        if (codigoReporte.isEmpty() || !permisoDAO.tienePermiso(perfilCodigo, codigoReporte)) {
            responderJson(response, HttpServletResponse.SC_FORBIDDEN, false, "No tiene permiso para registrar este reporte.");
            return;
        }

        ReporteHistorial reporte = new ReporteHistorial();
        reporte.setCodigoReporte(codigoReporte);
        reporte.setModulo(valor(request, "modulo"));
        reporte.setTipoReporte(valor(request, "tipoReporte"));
        reporte.setFormato(valor(request, "formato"));
        reporte.setTotalRegistros(entero(request, "totalRegistros"));
        reporte.setFiltros(valor(request, "filtros"));
        reporte.setUsuario(valorSesion(request, "usuarioLogueado"));
        reporte.setPerfil(perfilCodigo);

        boolean ok = reporteDAO.registrar(reporte);

        if (ok) {
            responderJson(response, HttpServletResponse.SC_OK, true, "Historial registrado.");
        } else {
            responderJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "No se pudo registrar el historial.");
        }
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String codigo = valor(request, "codigo");
        boolean ok = reporteDAO.eliminar(codigo);
        response.sendRedirect(request.getContextPath() + "/ReporteController?mensaje="
                + (ok ? "eliminado" : "no_eliminado"));
    }

    private boolean sesionValida(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion != null && sesion.getAttribute("usuarioLogueado") != null;
    }

    private String valorSesion(HttpServletRequest request, String nombre) {
        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute(nombre) == null) {
            return "";
        }

        return String.valueOf(sesion.getAttribute(nombre)).trim();
    }

    private String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }

    private int entero(HttpServletRequest request, String nombre) {
        try {
            return Integer.parseInt(valor(request, nombre));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void responderJson(HttpServletResponse response, int estado, boolean ok, String mensaje)
            throws IOException {

        response.setStatus(estado);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"ok\":" + ok + ",\"mensaje\":\""
                + escaparJson(mensaje) + "\"}");
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}