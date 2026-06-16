package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.modelo.Usuario;
import java.io.IOException;
import java.util.HashSet;
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

        if (!esRutaInternaSiemprePermitida(ruta) && !esRutaDelSistemaPermitida(ruta)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/accesoDenegado.jsp");
            return;
        }

        if (!esRutaInternaSiemprePermitida(ruta) && !tienePermiso(sesion, ruta)) {
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

    private boolean esRutaDelSistemaPermitida(String ruta) {
        return "/DepartamentoController".equals(ruta)
                || "/departamentos.jsp".equals(ruta)
                || "/CargoController".equals(ruta)
                || "/cargos.jsp".equals(ruta)
                || "/EmpleadoController".equals(ruta)
                || "/empleados.jsp".equals(ruta)
                || "/UsuarioController".equals(ruta)
                || "/usuarios.jsp".equals(ruta)
                || "/PerfilController".equals(ruta)
                || "/perfiles.jsp".equals(ruta)
                || "/PermisoController".equals(ruta)
                || "/permisos.jsp".equals(ruta);
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

        Set<String> rutasValidas = aliasRuta(ruta);

        for (String rutaValida : rutasValidas) {
            if (permisos.contains(normalizar(rutaValida))) {
                return true;
            }
        }

        return false;
    }

    private Set<String> aliasRuta(String ruta) {
        Set<String> alias = new HashSet<>();
        alias.add(ruta);

        if ("/DepartamentoController".equals(ruta)) {
            alias.add("/departamentos.jsp");
            alias.add("DepartamentoController");
        } else if ("/departamentos.jsp".equals(ruta)) {
            alias.add("/DepartamentoController");
            alias.add("DepartamentoController");
        } else if ("/CargoController".equals(ruta)) {
            alias.add("/cargos.jsp");
            alias.add("CargoController");
        } else if ("/cargos.jsp".equals(ruta)) {
            alias.add("/CargoController");
            alias.add("CargoController");
        } else if ("/EmpleadoController".equals(ruta)) {
            alias.add("/empleados.jsp");
            alias.add("EmpleadoController");
        } else if ("/empleados.jsp".equals(ruta)) {
            alias.add("/EmpleadoController");
            alias.add("EmpleadoController");
        } else if ("/UsuarioController".equals(ruta)) {
            alias.add("/usuarios.jsp");
            alias.add("UsuarioController");
        } else if ("/usuarios.jsp".equals(ruta)) {
            alias.add("/UsuarioController");
            alias.add("UsuarioController");
        } else if ("/PerfilController".equals(ruta)) {
            alias.add("/perfiles.jsp");
            alias.add("PerfilController");
        } else if ("/perfiles.jsp".equals(ruta)) {
            alias.add("/PerfilController");
            alias.add("PerfilController");
        } else if ("/PermisoController".equals(ruta)) {
            alias.add("/permisos.jsp");
            alias.add("PermisoController");
        } else if ("/permisos.jsp".equals(ruta)) {
            alias.add("/PermisoController");
            alias.add("PermisoController");
        }

        return alias;
    }

    private String normalizar(String ruta) {
        String normalizada = ruta == null ? "" : ruta.trim().toLowerCase();
        int query = normalizada.indexOf('?');

        if (query >= 0) {
            normalizada = normalizada.substring(0, query);
        }

        if (normalizada.startsWith("/")) {
            normalizada = normalizada.substring(1);
        }

        return normalizada;
    }
}
