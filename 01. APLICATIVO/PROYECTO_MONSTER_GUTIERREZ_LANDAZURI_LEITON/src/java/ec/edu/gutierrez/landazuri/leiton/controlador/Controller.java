package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.UsuarioDAO;
import ec.edu.gutierrez.landazuri.leiton.modelo.MenuOpcion;
import ec.edu.gutierrez.landazuri.leiton.modelo.Perfil;
import ec.edu.gutierrez.landazuri.leiton.modelo.Usuario;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Controller", urlPatterns = {"/Controller"})
public class Controller extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreUsuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscarPorLogin(nombreUsuario);

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=usuario_no_existe");
            return;
        }

        if (usuario.estaBloqueado()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=bloqueado");
            return;
        }

        if (!usuario.estaActivo()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=inactivo");
            return;
        }

        if (usuarioDAO.validarPassword(usuario, password)) {

            usuarioDAO.reiniciarIntentos(nombreUsuario);
            usuarioDAO.actualizarUltimoAcceso(nombreUsuario);

            List<MenuOpcion> menu = usuarioDAO.obtenerMenuUsuario(usuario);
            Perfil perfil = usuario.getPerfil();
            if (perfil == null) {
                perfil = obtenerPerfilDesdeMenu(menu);
                usuario.setPerfil(perfil);
            }

            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuarioLogueado", nombreUsuario);
            sesion.setAttribute("usuario", usuario);
            sesion.setAttribute("perfilUsuario", perfil);
            sesion.setAttribute("menuUsuario", menu);
            sesion.setAttribute("permisosUsuario", construirPermisos(menu));

            if (perfil != null) {
                sesion.setAttribute("perfilCodigo", perfil.getCodigo());
                sesion.setAttribute("perfilNombre", perfil.getDescripcion());
            }

            if (usuario.requiereCambioClave()) {
                response.sendRedirect(request.getContextPath() + "/cambiarClave.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/pagPrincipal.jsp");
            }

        } else {

            int intentos = usuarioDAO.sumarIntentosFallidos(nombreUsuario);

            if (intentos >= 3) {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=bloqueado");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=intentos&num=" + intentos);
            }
        }
    }

    private Perfil obtenerPerfilDesdeMenu(List<MenuOpcion> menu) {
        if (menu == null) {
            return null;
        }

        for (MenuOpcion opcion : menu) {
            Perfil perfil = obtenerPerfilDesdeOpcion(opcion);

            if (perfil != null) {
                return perfil;
            }
        }

        return null;
    }

    private Perfil obtenerPerfilDesdeOpcion(MenuOpcion opcion) {
        if (opcion == null) {
            return null;
        }

        if (opcion.getPerfilCodigo() != null && !opcion.getPerfilCodigo().trim().isEmpty()) {
            Perfil perfil = new Perfil();
            perfil.setCodigo(opcion.getPerfilCodigo());
            perfil.setDescripcion(opcion.getPerfilDescripcion());
            return perfil;
        }

        if (opcion.getHijos() != null) {
            for (MenuOpcion hijo : opcion.getHijos()) {
                Perfil perfil = obtenerPerfilDesdeOpcion(hijo);

                if (perfil != null) {
                    return perfil;
                }
            }
        }

        return null;
    }

    private Set<String> construirPermisos(List<MenuOpcion> menu) {
        Set<String> permisos = new HashSet<>();

        if (menu == null) {
            return permisos;
        }

        for (MenuOpcion opcion : menu) {
            agregarPermisosRecursivo(opcion, permisos);
        }

        return permisos;
    }

    private void agregarPermisosRecursivo(MenuOpcion opcion, Set<String> permisos) {
        if (opcion == null) {
            return;
        }

        if (opcion.getUrl() != null && !opcion.getUrl().trim().isEmpty()) {
            permisos.add(limpiarUrl(opcion.getUrl()));
        }

        if (opcion.getHijos() != null) {
            for (MenuOpcion hijo : opcion.getHijos()) {
                agregarPermisosRecursivo(hijo, permisos);
            }
        }
    }

    private String limpiarUrl(String url) {
        String limpia = url == null ? "" : url.trim();
        int query = limpia.indexOf('?');

        if (query >= 0) {
            limpia = limpia.substring(0, query);
        }

        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }

        return limpia;
    }

    public static String getMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());

            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
