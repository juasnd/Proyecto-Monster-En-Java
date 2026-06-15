package ec.edu.gutierrez.landazuri.leiton.controlador;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

        BDController bdc = new BDController();

        if (!bdc.usuarioExiste(nombreUsuario)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=usuario_no_existe");
            return;
        }

        if (bdc.usuarioBloqueado(nombreUsuario)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=bloqueado");
            return;
        }

        if (bdc.isRegistered(nombreUsuario, password)) {

            bdc.reiniciarIntentos(nombreUsuario);

            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuarioLogueado", nombreUsuario);

            response.sendRedirect(request.getContextPath() + "/pagPrincipal.jsp");

        } else {

            bdc.aumentarIntentosFallidos(nombreUsuario);

            int intentos = bdc.obtenerIntentosFallidos(nombreUsuario);

            if (intentos >= 3) {
                bdc.bloquearUsuario(nombreUsuario);
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=bloqueado");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=intentos&num=" + intentos);
            }
        }
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