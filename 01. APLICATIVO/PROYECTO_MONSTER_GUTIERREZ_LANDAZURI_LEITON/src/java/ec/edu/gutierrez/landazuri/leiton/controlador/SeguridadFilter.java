package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.modelo.Usuario;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class SeguridadFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String ruta = httpRequest.getServletPath();

        if (esRecursoLibre(ruta)) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        HttpSession sesion = httpRequest.getSession(false);

        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
            return;
        }

        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        if (usuario != null && usuario.requiereCambioClave() && !esRutaCambioClave(ruta)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/cambiarClave.jsp");
            return;
        }

        if (!esRegistroHistorialReporte(httpRequest)
                && !esRutaInternaSiemprePermitida(ruta)
                && !tienePermiso(sesion, ruta)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/accesoDenegado.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean esRecursoLibre(String ruta) {
        return ruta == null
                || ruta.isEmpty()
                || "/".equals(ruta)
                || "/index.jsp".equals(ruta)
                || "/Controller".equals(ruta)
                || ruta.startsWith("/CSS/")
                || ruta.startsWith("/js/")
                || ruta.startsWith("/img/")
                || ruta.startsWith("/uploads/");
    }

    private boolean esRutaCambioClave(String ruta) {
        return "/cambiarClave.jsp".equals(ruta)
                || "/CambiarClaveController".equals(ruta)
                || "/Logout".equals(ruta);
    }

    private boolean esRutaInternaSiemprePermitida(String ruta) {
        return "/pagPrincipal.jsp".equals(ruta)
                || "/cambiarClave.jsp".equals(ruta)
                || "/CambiarClaveController".equals(ruta)
                || "/Logout".equals(ruta)
                || "/accesoDenegado.jsp".equals(ruta);
    }
    private boolean esRegistroHistorialReporte(HttpServletRequest request) {
        return "/ReporteController".equals(request.getServletPath())
                && "registrar".equalsIgnoreCase(request.getParameter("accion"));
    }

    private boolean tienePermiso(HttpSession sesion, String ruta) {
        Object atributo = sesion.getAttribute("permisosUsuario");

        if (!(atributo instanceof Set)) {
            return false;
        }

        Set<?> permisosSesion = (Set<?>) atributo;
        Set<String> permisos = new HashSet<>();

        for (Object permiso : permisosSesion) {
            if (permiso != null) {
                permisos.add(normalizar(String.valueOf(permiso)));
            }
        }

        for (String alias : aliasRuta(ruta)) {
            if (permisos.contains(normalizar(alias))) {
                return true;
            }
        }

        return false;
    }

    private Set<String> aliasRuta(String ruta) {
        Set<String> alias = new HashSet<>();
        String limpia = ruta == null ? "" : ruta.trim();

        alias.add(limpia);

        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }

        alias.add(limpia);

        if (limpia.endsWith(".jsp")) {
            alias.add(convertirJspAController(limpia));
        } else if (limpia.endsWith("Controller")) {
            alias.add(convertirControllerAJsp(limpia));
        }

        return alias;
    }

    private String convertirJspAController(String ruta) {
        String limpia = ruta.toLowerCase(Locale.ROOT);

        if ("departamentos.jsp".equals(limpia)) {
            return "DepartamentoController";
        }

        if ("cargos.jsp".equals(limpia)) {
            return "CargoController";
        }

        if ("empleados.jsp".equals(limpia)) {
            return "EmpleadoController";
        }

        if ("usuarios.jsp".equals(limpia)) {
            return "UsuarioController";
        }

        if ("perfiles.jsp".equals(limpia)) {
            return "PerfilController";
        }

        if ("permisos.jsp".equals(limpia)) {
            return "PermisoController";
        }
        if ("reportes.jsp".equals(limpia)) {
            return "ReporteController";
        }

        if ("opciones.jsp".equals(limpia)) {
            return "OpcionController";
        }

        if ("sistemas.jsp".equals(limpia)) {
            return "SistemaController";
        }

        return ruta;
    }

    private String convertirControllerAJsp(String ruta) {
        if ("DepartamentoController".equals(ruta)) {
            return "departamentos.jsp";
        }

        if ("CargoController".equals(ruta)) {
            return "cargos.jsp";
        }

        if ("EmpleadoController".equals(ruta)) {
            return "empleados.jsp";
        }

        if ("UsuarioController".equals(ruta)) {
            return "usuarios.jsp";
        }

        if ("PerfilController".equals(ruta)) {
            return "perfiles.jsp";
        }

        if ("PermisoController".equals(ruta)) {
            return "permisos.jsp";
        }
        if ("ReporteController".equals(ruta)) {
            return "reportes.jsp";
        }

        if ("OpcionController".equals(ruta)) {
            return "opciones.jsp";
        }

        if ("SistemaController".equals(ruta)) {
            return "sistemas.jsp";
        }

        return ruta;
    }

    private String normalizar(String ruta) {
        String limpia = ruta == null ? "" : ruta.trim();
        int query = limpia.indexOf('?');

        if (query >= 0) {
            limpia = limpia.substring(0, query);
        }

        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }

        return limpia.toLowerCase(Locale.ROOT);
    }
}
