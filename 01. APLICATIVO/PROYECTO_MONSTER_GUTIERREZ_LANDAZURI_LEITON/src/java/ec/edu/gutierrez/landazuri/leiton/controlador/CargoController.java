package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.PermisoDAO;

import ec.edu.gutierrez.landazuri.leiton.modelo.Cargo;
import ec.edu.gutierrez.landazuri.leiton.modelo.Departamento;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CargoController", urlPatterns = {"/CargoController"})
public class CargoController extends HttpServlet {

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
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        BDController bdc = new BDController();

        switch (accion) {
            case "nuevo":
                request.setAttribute("modo", "nuevo");
                cargarLista(request, bdc);
                request.getRequestDispatcher("cargos.jsp").forward(request, response);
                break;
            case "guardar":
                guardar(request, response, bdc);
                break;
            case "editar":
                editar(request, response, bdc);
                break;
            case "ver":
                ver(request, response, bdc);
                break;
            case "actualizar":
                actualizar(request, response, bdc);
                break;
            case "eliminar":
                eliminar(request, response, bdc);
                break;
            default:
                cargarLista(request, bdc);
                request.getRequestDispatcher("cargos.jsp").forward(request, response);
                break;
        }
    }

    private void cargarLista(HttpServletRequest request, BDController bdc) {
        request.setAttribute("cargos", bdc.listarCargos());
        // ¡ESTO ES CLAVE PARA EL COMBOBOX! Enviamos la lista de departamentos siempre.
        request.setAttribute("departamentos", bdc.listarDepartamentos());
        cargarPermisoReporte(request);
    }


    private void cargarPermisoReporte(HttpServletRequest request) {
        String perfilCodigo = obtenerPerfilCodigo(request);
        boolean permitido = permisoDAO.tienePermiso(perfilCodigo, "RCA");
        request.setAttribute("puedeReporteCargos", permitido);
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
        String depCodigo = request.getParameter("pedepCodigo");
        String carCodigo = request.getParameter("pecarCodigo");
        String descripcion = request.getParameter("pecarDescri");

        Cargo cargo = new Cargo(depCodigo.trim().toUpperCase(), carCodigo.trim().toUpperCase(), descripcion.trim());
        bdc.insertarCargo(cargo);
        response.sendRedirect(request.getContextPath() + "/CargoController?mensaje=guardado");
    }

    private void ver(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {
        String depCodigo = request.getParameter("depCode");
        String carCodigo = request.getParameter("carCode");
        
        Cargo cargo = bdc.buscarCargo(depCodigo, carCodigo);
        
        if (cargo == null) {
            response.sendRedirect(request.getContextPath() + "/CargoController?error=no_encontrado");
            return;
        }

        Departamento dep = bdc.buscarDepartamento(depCodigo);
        if (dep != null) {
            cargo.setNombreDepartamento(dep.getDescripcion());
        }

        request.setAttribute("cargoVer", cargo);
        request.setAttribute("modo", "ver");
        
        cargarLista(request, bdc); 
        request.getRequestDispatcher("cargos.jsp").forward(request, response);
    }
    
    private void editar(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {
        String depCodigo = request.getParameter("depCode");
        String carCodigo = request.getParameter("carCode");
        
        Cargo cargo = bdc.buscarCargo(depCodigo, carCodigo);
        request.setAttribute("cargoEditar", cargo);
        request.setAttribute("modo", "editar");
        cargarLista(request, bdc);
        request.getRequestDispatcher("cargos.jsp").forward(request, response);
    }

    private void actualizar(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {
        String depCodigo = request.getParameter("pedepCodigo");
        String carCodigo = request.getParameter("pecarCodigo");
        String descripcion = request.getParameter("pecarDescri");

        Cargo cargo = new Cargo(depCodigo, carCodigo, descripcion.trim());
        bdc.actualizarCargo(cargo);
        response.sendRedirect(request.getContextPath() + "/CargoController?mensaje=actualizado");
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response, BDController bdc)
            throws ServletException, IOException {
        String depCodigo = request.getParameter("depCode");
        String carCodigo = request.getParameter("carCode");
        bdc.eliminarCargo(depCodigo, carCodigo);
        response.sendRedirect(request.getContextPath() + "/CargoController?mensaje=eliminado");
    }
}
