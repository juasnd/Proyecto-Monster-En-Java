package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.PermisoDAO;

import ec.edu.gutierrez.landazuri.leiton.modelo.Departamento;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "DepartamentoController", urlPatterns = {"/DepartamentoController"})
public class DepartamentoController extends HttpServlet {

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

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        if (accion == null) {
            accion = "listar";
        }

        BDController bdc = new BDController();

        switch (accion) {
            case "nuevo":
                request.setAttribute("modo", "nuevo");
                cargarLista(request, bdc);
                request.getRequestDispatcher("departamentos.jsp").forward(request, response);
                break;

            case "guardar":
                guardar(request, response, bdc);
                break;

            case "editar":
                editar(request, response, bdc);
                break;

            case "actualizar":
                actualizar(request, response, bdc);
                break;

            case "ver":
                ver(request, response, bdc);
                break;

            case "eliminar":
                eliminar(request, response, bdc);
                break;

            default:
                cargarLista(request, bdc);
                request.getRequestDispatcher("departamentos.jsp").forward(request, response);
                break;
        }
    }

    private void cargarLista(HttpServletRequest request, BDController bdc) {
        List<Departamento> lista = bdc.listarDepartamentos();
        request.setAttribute("departamentos", lista);
        cargarPermisoReporte(request);
    }


    private void cargarPermisoReporte(HttpServletRequest request) {
        String perfilCodigo = obtenerPerfilCodigo(request);
        boolean permitido = permisoDAO.tienePermiso(perfilCodigo, "RDE");
        request.setAttribute("puedeReporteDepartamentos", permitido);
    }

    private String obtenerPerfilCodigo(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("perfilCodigo") == null) {
            return "";
        }

        return String.valueOf(sesion.getAttribute("perfilCodigo"));
    }
    private void guardar(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");
        String descripcion = request.getParameter("descripcion");

        if (codigo == null || descripcion == null
                || codigo.trim().isEmpty()
                || descripcion.trim().isEmpty()) {

            request.setAttribute("error", "Debe ingresar código y descripción.");
            request.setAttribute("modo", "nuevo");
            cargarLista(request, bdc);
            request.getRequestDispatcher("departamentos.jsp").forward(request, response);
            return;
        }

        codigo = codigo.trim().toUpperCase();
        descripcion = descripcion.trim();

        if (codigo.length() > 3) {
            request.setAttribute("error", "El código del departamento debe tener máximo 3 caracteres.");
            request.setAttribute("modo", "nuevo");
            cargarLista(request, bdc);
            request.getRequestDispatcher("departamentos.jsp").forward(request, response);
            return;
        }

        Departamento existente = bdc.buscarDepartamento(codigo);

        if (existente != null) {
            request.setAttribute("error", "Ya existe un departamento con ese código.");
            request.setAttribute("modo", "nuevo");
            cargarLista(request, bdc);
            request.getRequestDispatcher("departamentos.jsp").forward(request, response);
            return;
        }

        Departamento dep = new Departamento(codigo, descripcion);

        if (bdc.insertarDepartamento(dep)) {
            response.sendRedirect(request.getContextPath() + "/DepartamentoController?mensaje=guardado");
        } else {
            request.setAttribute("error", "No se pudo guardar el departamento.");
            request.setAttribute("modo", "nuevo");
            cargarLista(request, bdc);
            request.getRequestDispatcher("departamentos.jsp").forward(request, response);
        }
    }

    private void editar(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");
        Departamento dep = bdc.buscarDepartamento(codigo);

        if (dep == null) {
            response.sendRedirect(request.getContextPath() + "/DepartamentoController?error=no_encontrado");
            return;
        }

        request.setAttribute("departamentoEditar", dep);
        request.setAttribute("modo", "editar");
        cargarLista(request, bdc);
        request.getRequestDispatcher("departamentos.jsp").forward(request, response);
    }

    private void actualizar(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");
        String descripcion = request.getParameter("descripcion");

        if (codigo == null || descripcion == null
                || codigo.trim().isEmpty()
                || descripcion.trim().isEmpty()) {

            request.setAttribute("error", "Debe ingresar la descripción.");
            request.setAttribute("modo", "editar");
            request.setAttribute("departamentoEditar", new Departamento(codigo, descripcion));
            cargarLista(request, bdc);
            request.getRequestDispatcher("departamentos.jsp").forward(request, response);
            return;
        }

        Departamento dep = new Departamento(codigo.trim().toUpperCase(), descripcion.trim());

        if (bdc.actualizarDepartamento(dep)) {
            response.sendRedirect(request.getContextPath() + "/DepartamentoController?mensaje=actualizado");
        } else {
            request.setAttribute("error", "No se pudo actualizar el departamento.");
            request.setAttribute("modo", "editar");
            request.setAttribute("departamentoEditar", dep);
            cargarLista(request, bdc);
            request.getRequestDispatcher("departamentos.jsp").forward(request, response);
        }
    }

    private void ver(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");
        Departamento dep = bdc.buscarDepartamento(codigo);

        if (dep == null) {
            response.sendRedirect(request.getContextPath() + "/DepartamentoController?error=no_encontrado");
            return;
        }

        request.setAttribute("departamentoVer", dep);
        request.setAttribute("modo", "ver");
        cargarLista(request, bdc);
        request.getRequestDispatcher("departamentos.jsp").forward(request, response);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");

        if (bdc.departamentoEnUso(codigo)) {
            response.sendRedirect(request.getContextPath() + "/DepartamentoController?error=en_uso");
            return;
        }

        if (bdc.eliminarDepartamento(codigo)) {
            response.sendRedirect(request.getContextPath() + "/DepartamentoController?mensaje=eliminado");
        } else {
            response.sendRedirect(request.getContextPath() + "/DepartamentoController?error=no_eliminado");
        }
    }
}