package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.MenuOpcion;
import ec.edu.gutierrez.landazuri.leiton.modelo.Perfil;
import ec.edu.gutierrez.landazuri.leiton.modelo.Persona;
import ec.edu.gutierrez.landazuri.leiton.modelo.Usuario;
import ec.edu.gutierrez.landazuri.leiton.modelo.UsuarioPerfil;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class UsuarioDAO extends BaseDAO {

    public static final String CONTRASENA_TEMPORAL = "Monster2026";
    private static final int MAX_INTENTOS = 3;

    private final PerfilDAO perfilDAO = new PerfilDAO();

    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT * FROM xeusu_usuari WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapearUsuario(rs);
                    usuario.setPerfil(obtenerPerfilPrincipal(con, usuario));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }

        return null;
    }

    public boolean validarPassword(Usuario usuario, String passwordPlano) {
        if (usuario == null || passwordPlano == null) {
            return false;
        }

        String almacenado = usuario.getPassword();

        if (almacenado == null || almacenado.trim().isEmpty()) {
            return false;
        }

        String hashGuardado = almacenado.trim();
        String md5 = hash(passwordPlano, "MD5");
        String sha256 = hash(passwordPlano, "SHA-256");

        return hashGuardado.equalsIgnoreCase(md5)
                || hashGuardado.equalsIgnoreCase(sha256)
                || hashGuardado.equalsIgnoreCase("{SHA256}" + sha256);
    }

    public List<UsuarioPerfil> listarUsuarios() {
        List<UsuarioPerfil> usuarios = listarUsuariosDesdeVista();

        if (!usuarios.isEmpty()) {
            return usuarios;
        }

        return listarUsuariosFallback();
    }

    private List<UsuarioPerfil> listarUsuariosDesdeVista() {
        List<UsuarioPerfil> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM vw_usuarios_perfiles";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuarioPerfil(rs));
            }
        } catch (SQLException e) {
            System.out.println("No se pudo usar vw_usuarios_perfiles: " + e.getMessage());
        }

        return usuarios;
    }

    private List<UsuarioPerfil> listarUsuariosFallback() {
        List<UsuarioPerfil> usuarios = new ArrayList<>();
        String sql = "SELECT u.XEUSU_LOGIN, u.PEPER_CODIGO, u.XEUSU_INTENTOS, "
                + "u.XEUSU_CAMBIO_CLAVE, u.XEUSU_ULTIMO_ACCESO, u.XEEST_CODIGO, e.XEEST_DESCRI, "
                + "p.PEPER_CEDULA, p.PEPER_NOMBRE, p.PEPER_APELLIDO, "
                + "pe.XEPER_CODIGO, pe.XEPER_DESCRI "
                + "FROM xeusu_usuari u "
                + "LEFT JOIN peper_person p ON u.PEPER_CODIGO = p.PEPER_CODIGO "
                + "LEFT JOIN xeest_estado e ON u.XEEST_CODIGO = e.XEEST_CODIGO "
                + "LEFT JOIN xeuxp_usuper up ON u.PEPER_CODIGO = up.PEPER_CODIGO "
                + "AND u.XEUSU_LOGIN = up.XEUSU_LOGIN AND up.XEUXP_FECRET IS NULL "
                + "LEFT JOIN xeper_perfil pe ON up.XEPER_CODIGO = pe.XEPER_CODIGO "
                + "ORDER BY u.XEUSU_LOGIN";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuarioPerfil(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    public List<Persona> listarEmpleadosSinUsuario() {
        List<Persona> personas = listarEmpleadosSinUsuarioDesdeVista();

        if (!personas.isEmpty()) {
            return personas;
        }

        return listarEmpleadosSinUsuarioFallback();
    }

    private List<Persona> listarEmpleadosSinUsuarioDesdeVista() {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM vw_empleados_sin_usuario";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                personas.add(mapearPersonaBasica(rs));
            }
        } catch (SQLException e) {
            System.out.println("No se pudo usar vw_empleados_sin_usuario: " + e.getMessage());
        }

        return personas;
    }

    private List<Persona> listarEmpleadosSinUsuarioFallback() {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT p.PEPER_CODIGO, p.PEPER_CEDULA, p.PEPER_NOMBRE, p.PEPER_APELLIDO "
                + "FROM peemp_emplea em "
                + "INNER JOIN peper_person p ON em.PEPER_CODIGO = p.PEPER_CODIGO "
                + "WHERE NOT EXISTS ("
                + "SELECT 1 FROM xeusu_usuari u WHERE u.PEPER_CODIGO = p.PEPER_CODIGO"
                + ") "
                + "ORDER BY p.PEPER_APELLIDO, p.PEPER_NOMBRE";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                personas.add(mapearPersonaBasica(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar empleados sin usuario: " + e.getMessage());
        }

        return personas;
    }

    public boolean registrarUsuarioParaPersona(String personaCodigo, String login, String perfilCodigo) {
        try (Connection con = obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                Usuario usuario = new Usuario();
                usuario.setCodigoPersona(personaCodigo);
                usuario.setUsuario(login);
                usuario.setPassword(hash(CONTRASENA_TEMPORAL, "MD5"));
                usuario.setIntentosFallidos(0);
                usuario.setCambioClave("S");
                usuario.setEstadoCodigo("A");

                registrarUsuario(con, usuario, perfilCodigo);
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
        }

        return false;
    }

    public void crearUsuarioAutomaticoEmpleado(Connection con, Persona persona) throws SQLException {
        if (persona == null || vacio(persona.getCedula()) || vacio(persona.getPeperCodigo())) {
            throw new SQLException("La persona no tiene cedula o codigo para crear usuario.");
        }

        if (existeLogin(con, persona.getCedula())) {
            throw new SQLException("Ya existe un usuario con la cedula " + persona.getCedula() + ".");
        }

        String perfilEmpleado = perfilDAO.buscarCodigoPorNombre(con, "EMPLEADO");

        if (vacio(perfilEmpleado)) {
            throw new SQLException("No existe el perfil EMPLEADO.");
        }

        Usuario usuario = new Usuario();
        usuario.setCodigoPersona(persona.getPeperCodigo());
        usuario.setUsuario(persona.getCedula());
        usuario.setPassword(hash(CONTRASENA_TEMPORAL, "MD5"));
        usuario.setIntentosFallidos(0);
        usuario.setCambioClave("S");
        usuario.setEstadoCodigo("A");
        registrarUsuario(con, usuario, perfilEmpleado);
    }

    public boolean asignarPerfil(String login, String perfilCodigo) {
        try (Connection con = obtenerConexion()) {
            Usuario usuario = buscarPorLogin(login);

            if (usuario == null) {
                return false;
            }

            con.setAutoCommit(false);

            try {
                asignarPerfil(con, usuario, perfilCodigo);
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Error al asignar perfil: " + e.getMessage());
        }

        return false;
    }

    public boolean activarUsuario(String login) {
        return ejecutarActualizacionEstado(login, "A", true);
    }

    public boolean bloquearUsuario(String login) {
        return ejecutarActualizacionEstado(login, "B", false);
    }

    public boolean resetearPassword(String login) {
        String sql = "UPDATE xeusu_usuari SET XEUSU_PASWD = ?, XEUSU_CAMBIO_CLAVE = 'S', "
                + "XEUSU_INTENTOS = 0, XEEST_CODIGO = 'A' WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hash(CONTRASENA_TEMPORAL, "MD5"));
            ps.setString(2, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al resetear contrasena: " + e.getMessage());
        }

        return false;
    }

    public boolean actualizarPassword(String login, String nuevoPassword, boolean cambioObligatorio) {
        String sql = "UPDATE xeusu_usuari SET XEUSU_PASWD = ?, XEUSU_CAMBIO_CLAVE = ?, "
                + "XEUSU_INTENTOS = 0 WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hash(nuevoPassword, "MD5"));
            ps.setString(2, cambioObligatorio ? "S" : "N");
            ps.setString(3, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar contrasena: " + e.getMessage());
        }

        return false;
    }

    public boolean marcarCambioClaveNo(String login) {
        String sql = "UPDATE xeusu_usuari SET XEUSU_CAMBIO_CLAVE = 'N' WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al marcar cambio de clave: " + e.getMessage());
        }

        return false;
    }

    public int sumarIntentosFallidos(String login) {
        String sql = "UPDATE xeusu_usuari SET XEUSU_INTENTOS = COALESCE(XEUSU_INTENTOS, 0) + 1 "
                + "WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al sumar intentos: " + e.getMessage());
        }

        int intentos = obtenerIntentosFallidos(login);

        if (intentos >= MAX_INTENTOS) {
            bloquearUsuario(login);
        }

        return intentos;
    }

    public boolean reiniciarIntentos(String login) {
        String sql = "UPDATE xeusu_usuari SET XEUSU_INTENTOS = 0 WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al reiniciar intentos: " + e.getMessage());
        }

        return false;
    }

    public int obtenerIntentosFallidos(String login) {
        String sql = "SELECT XEUSU_INTENTOS FROM xeusu_usuari WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("XEUSU_INTENTOS");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener intentos: " + e.getMessage());
        }

        return 0;
    }

    public boolean actualizarUltimoAcceso(String login) {
        String sql = "UPDATE xeusu_usuari SET XEUSU_ULTIMO_ACCESO = ? WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar ultimo acceso: " + e.getMessage());
        }

        return false;
    }

    public List<MenuOpcion> obtenerMenuUsuario(Usuario usuario) {
        if (usuario == null) {
            return new ArrayList<>();
        }

        List<MenuOpcion> menu = consultarMenu("XEUSU_LOGIN", usuario.getUsuario());

        if (menu.isEmpty() && !vacio(usuario.getCodigo())) {
            menu = consultarMenu("XEUSU_CODIGO", usuario.getCodigo());
        }

        Map<String, MenuOpcion> unicos = new LinkedHashMap<>();

        for (MenuOpcion opcion : menu) {
            if (!opcionPermitida(opcion)) {
                continue;
            }

            String llave = !vacio(opcion.getCodigo()) ? opcion.getCodigo() : opcion.getUrl();

            if (!vacio(llave) && !unicos.containsKey(llave)) {
                unicos.put(llave, opcion);
            }
        }

        List<MenuOpcion> ordenado = new ArrayList<>(unicos.values());
        ordenado.sort(Comparator.comparingInt(MenuOpcion::getOrden)
                .thenComparing(MenuOpcion::getDescripcion, String.CASE_INSENSITIVE_ORDER));
        return ordenado;
    }

    private boolean opcionPermitida(MenuOpcion opcion) {
        if (opcion == null) {
            return false;
        }

        String codigo = opcion.getCodigo() == null
                ? ""
                : opcion.getCodigo().trim().toUpperCase(Locale.ROOT);

        if ("INI".equals(codigo)
                || "DEP".equals(codigo)
                || "CAR".equals(codigo)
                || "EMP".equals(codigo)
                || "USU".equals(codigo)
                || "PER".equals(codigo)
                || "OCP".equals(codigo)
                || "OPC".equals(codigo)) {
            return true;
        }

        return rutaPermitida(normalizarRuta(opcion.getUrl()));
    }

    private boolean rutaPermitida(String ruta) {
        return "pagprincipal.jsp".equals(ruta)
                || "departamentos.jsp".equals(ruta)
                || "departamentocontroller".equals(ruta)
                || "cargos.jsp".equals(ruta)
                || "cargocontroller".equals(ruta)
                || "empleados.jsp".equals(ruta)
                || "empleadocontroller".equals(ruta)
                || "usuarios.jsp".equals(ruta)
                || "usuariocontroller".equals(ruta)
                || "perfiles.jsp".equals(ruta)
                || "perfilcontroller".equals(ruta)
                || "permisos.jsp".equals(ruta)
                || "permisocontroller".equals(ruta);
    }

    private String normalizarRuta(String ruta) {
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

    private List<MenuOpcion> consultarMenu(String columnaUsuario, String valor) {
        List<MenuOpcion> menu = new ArrayList<>();

        if (vacio(valor)) {
            return menu;
        }

        String sql = "SELECT * FROM vw_menu_usuario WHERE " + columnaUsuario + " = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, valor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    menu.add(mapearMenu(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("No se pudo cargar menu por " + columnaUsuario + ": " + e.getMessage());
        }

        return menu;
    }

    private void registrarUsuario(Connection con, Usuario usuario, String perfilCodigo) throws SQLException {
        if (existeLogin(con, usuario.getUsuario())) {
            throw new SQLException("Ya existe un usuario con ese login.");
        }

        if (vacio(usuario.getCodigo())) {
            usuario.setCodigo(generarCodigoUsuario(con));
        }

        Set<String> columnasDisponibles = obtenerColumnas(con, "xeusu_usuari");
        List<String> columnas = new ArrayList<>();
        List<Object> valores = new ArrayList<>();

        agregar(columnasDisponibles, columnas, valores, "XEUSU_CODIGO", usuario.getCodigo());
        agregar(columnasDisponibles, columnas, valores, "PEPER_CODIGO", usuario.getCodigoPersona());
        agregar(columnasDisponibles, columnas, valores, "XEUSU_LOGIN", usuario.getUsuario());
        agregar(columnasDisponibles, columnas, valores, "XEUSU_PASWD", usuario.getPassword());
        agregar(columnasDisponibles, columnas, valores, "XEUSU_FECCRE", new Timestamp(System.currentTimeMillis()));
        agregar(columnasDisponibles, columnas, valores, "XEUSU_FECMOD", new Timestamp(System.currentTimeMillis()));
        agregar(columnasDisponibles, columnas, valores, "XEUSU_PIEFIR", "GUTIERREZ - LANDAZURI - LEITON");
        agregar(columnasDisponibles, columnas, valores, "XEUSU_INTENTOS", Integer.valueOf(usuario.getIntentosFallidos()));
        agregar(columnasDisponibles, columnas, valores, "XEUSU_CAMBIO_CLAVE", usuario.getCambioClave());
        agregar(columnasDisponibles, columnas, valores, "XEEST_CODIGO", usuario.getEstadoCodigo());
        agregar(columnasDisponibles, columnas, valores, "XEUSU_ALGORITMO", "MD5");

        String sql = construirInsert("xeusu_usuari", columnas);

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }

            ps.executeUpdate();
        }

        asignarPerfil(con, usuario, perfilCodigo);
    }

    private void asignarPerfil(Connection con, Usuario usuario, String perfilCodigo) throws SQLException {
        if (vacio(perfilCodigo)) {
            return;
        }

        Set<String> columnasDisponibles = obtenerColumnas(con, "xeuxp_usuper");
        boolean usaCodigo = tieneColumna(columnasDisponibles, "XEUSU_CODIGO") && !vacio(usuario.getCodigo());
        String columnaUsuario = usaCodigo ? "XEUSU_CODIGO" : "XEUSU_LOGIN";
        String valorUsuario = usaCodigo ? usuario.getCodigo() : usuario.getUsuario();

        List<String> condiciones = new ArrayList<>();
        List<String> valoresCondicion = new ArrayList<>();

        if (tieneColumna(columnasDisponibles, "PEPER_CODIGO") && !vacio(usuario.getCodigoPersona())) {
            condiciones.add("PEPER_CODIGO = ?");
            valoresCondicion.add(usuario.getCodigoPersona());
        }

        if (tieneColumna(columnasDisponibles, "XEUSU_LOGIN") && !vacio(usuario.getUsuario())) {
            condiciones.add("XEUSU_LOGIN = ?");
            valoresCondicion.add(usuario.getUsuario());
        } else if (usaCodigo) {
            condiciones.add("XEUSU_CODIGO = ?");
            valoresCondicion.add(usuario.getCodigo());
        }

        String deleteSql = "DELETE FROM xeuxp_usuper WHERE " + unirCondicionesConAnd(condiciones);

        try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
            for (int i = 0; i < valoresCondicion.size(); i++) {
                ps.setString(i + 1, valoresCondicion.get(i));
            }
            ps.executeUpdate();
        }

        List<String> columnas = new ArrayList<>();
        List<Object> valores = new ArrayList<>();
        agregar(columnasDisponibles, columnas, valores, "PEPER_CODIGO", usuario.getCodigoPersona());
        agregar(columnasDisponibles, columnas, valores, columnaUsuario, valorUsuario);
        agregar(columnasDisponibles, columnas, valores, "XEPER_CODIGO", perfilCodigo);
        agregar(columnasDisponibles, columnas, valores, "XEUXP_FECASI", new Date(System.currentTimeMillis()));
        agregar(columnasDisponibles, columnas, valores, "XEEST_CODIGO", "A");

        String insertSql = construirInsert("xeuxp_usuper", columnas);

        try (PreparedStatement ps = con.prepareStatement(insertSql)) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }

            ps.executeUpdate();
        }
    }

    private Perfil obtenerPerfilPrincipal(Connection con, Usuario usuario) throws SQLException {
        Set<String> columnas = obtenerColumnas(con, "xeuxp_usuper");
        List<String> condiciones = new ArrayList<>();
        List<String> valores = new ArrayList<>();

        if (tieneColumna(columnas, "XEUSU_CODIGO") && !vacio(usuario.getCodigo())) {
            condiciones.add("up.XEUSU_CODIGO = ?");
            valores.add(usuario.getCodigo());
        }

        if (tieneColumna(columnas, "PEPER_CODIGO") && !vacio(usuario.getCodigoPersona())
                && tieneColumna(columnas, "XEUSU_LOGIN") && !vacio(usuario.getUsuario())) {
            condiciones.add("(up.PEPER_CODIGO = ? AND up.XEUSU_LOGIN = ?)");
            valores.add(usuario.getCodigoPersona());
            valores.add(usuario.getUsuario());
        } else if (tieneColumna(columnas, "XEUSU_LOGIN") && !vacio(usuario.getUsuario())) {
            condiciones.add("up.XEUSU_LOGIN = ?");
            valores.add(usuario.getUsuario());
        }

        if (condiciones.isEmpty()) {
            return null;
        }

        String sql = "SELECT p.XEPER_CODIGO, p.XEPER_DESCRI, p.XEPER_ESTADO AS XEEST_CODIGO "
                + "FROM xeuxp_usuper up "
                + "INNER JOIN xeper_perfil p ON up.XEPER_CODIGO = p.XEPER_CODIGO "
                + "WHERE " + unirCondiciones(condiciones) + " LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setString(i + 1, valores.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Perfil perfil = new Perfil();
                    perfil.setCodigo(obtenerTexto(rs, "XEPER_CODIGO"));
                    perfil.setDescripcion(obtenerTexto(rs, "XEPER_DESCRI"));
                    perfil.setEstadoCodigo(obtenerTexto(rs, "XEEST_CODIGO"));
                    return perfil;
                }
            }
        }

        return null;
    }

    private boolean existeLogin(Connection con, String login) throws SQLException {
        String sql = "SELECT XEUSU_LOGIN FROM xeusu_usuari WHERE XEUSU_LOGIN = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean ejecutarActualizacionEstado(String login, String estado, boolean reiniciar) {
        String sql = reiniciar
                ? "UPDATE xeusu_usuari SET XEEST_CODIGO = ?, XEUSU_INTENTOS = 0 WHERE XEUSU_LOGIN = ?"
                : "UPDATE xeusu_usuari SET XEEST_CODIGO = ? WHERE XEUSU_LOGIN = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setString(2, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de usuario: " + e.getMessage());
        }

        return false;
    }

    private String generarCodigoUsuario(Connection con) {
        String sql = "SELECT MAX(CAST(SUBSTRING(XEUSU_CODIGO, 4) AS UNSIGNED)) AS maximo "
                + "FROM xeusu_usuari WHERE XEUSU_CODIGO LIKE 'USU%'";

        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int numero = 1;

            if (rs.next()) {
                numero = rs.getInt("maximo") + 1;
            }

            return "USU" + String.format("%07d", numero);
        } catch (SQLException e) {
            return "";
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setCodigo(obtenerTexto(rs, "XEUSU_CODIGO", "USUARIO_CODIGO"));
        usuario.setCodigoPersona(obtenerTexto(rs, "PEPER_CODIGO"));
        usuario.setUsuario(obtenerTexto(rs, "XEUSU_LOGIN", "LOGIN", "USUARIO"));
        usuario.setPassword(obtenerTexto(rs, "XEUSU_PASWD", "XEUSU_PASSWORD", "PASSWORD"));
        usuario.setIntentosFallidos(obtenerEntero(rs, "XEUSU_INTENTOS", "INTENTOS"));
        usuario.setCambioClave(obtenerTexto(rs, "XEUSU_CAMBIO_CLAVE", "CAMBIO_CLAVE"));
        usuario.setEstadoCodigo(obtenerTexto(rs, "XEEST_CODIGO", "ESTADO_CODIGO"));
        usuario.setEstadoDescripcion(obtenerTexto(rs, "XEEST_DESCRI", "ESTADO"));
        usuario.setUltimoAcceso(obtenerTexto(rs, "XEUSU_ULTIMO_ACCESO", "ULTIMO_ACCESO"));
        return usuario;
    }

    private UsuarioPerfil mapearUsuarioPerfil(ResultSet rs) throws SQLException {
        UsuarioPerfil usuario = new UsuarioPerfil();
        usuario.setUsuarioCodigo(obtenerTexto(rs, "XEUSU_CODIGO", "USUARIO_CODIGO"));
        usuario.setLogin(obtenerTexto(rs, "XEUSU_LOGIN", "LOGIN", "USUARIO"));
        usuario.setPersonaCodigo(obtenerTexto(rs, "PEPER_CODIGO", "PERSONA_CODIGO"));
        usuario.setCedula(obtenerTexto(rs, "PEPER_CEDULA", "CEDULA"));
        usuario.setNombres(obtenerTexto(rs, "PEPER_NOMBRE", "NOMBRES", "NOMBRE"));
        usuario.setApellidos(obtenerTexto(rs, "PEPER_APELLIDO", "APELLIDOS", "APELLIDO"));
        usuario.setPerfilCodigo(obtenerTexto(rs, "XEPER_CODIGO", "PERFIL_CODIGO"));
        usuario.setPerfilDescripcion(obtenerTexto(rs, "XEPER_DESCRI", "PERFIL", "PERFIL_DESCRIPCION"));
        usuario.setEstadoCodigo(obtenerTexto(rs, "XEEST_CODIGO", "ESTADO_CODIGO"));
        usuario.setEstadoDescripcion(obtenerTexto(rs, "XEEST_DESCRI", "ESTADO"));
        usuario.setCambioClave(obtenerTexto(rs, "XEUSU_CAMBIO_CLAVE", "CAMBIO_CLAVE"));
        usuario.setIntentosFallidos(obtenerEntero(rs, "XEUSU_INTENTOS", "INTENTOS"));
        usuario.setUltimoAcceso(obtenerTexto(rs, "XEUSU_ULTIMO_ACCESO", "ULTIMO_ACCESO"));
        return usuario;
    }

    private Persona mapearPersonaBasica(ResultSet rs) throws SQLException {
        Persona persona = new Persona();
        persona.setPeperCodigo(obtenerTexto(rs, "PEPER_CODIGO", "PERSONA_CODIGO"));
        persona.setCedula(obtenerTexto(rs, "PEPER_CEDULA", "CEDULA"));
        persona.setNombres(obtenerTexto(rs, "PEPER_NOMBRE", "NOMBRES", "NOMBRE"));
        persona.setApellidos(obtenerTexto(rs, "PEPER_APELLIDO", "APELLIDOS", "APELLIDO"));
        return persona;
    }

    private MenuOpcion mapearMenu(ResultSet rs) throws SQLException {
        MenuOpcion opcion = new MenuOpcion();
        opcion.setCodigo(obtenerTexto(rs, "XEOPC_CODIGO", "OPCION_CODIGO", "CODIGO"));
        opcion.setDescripcion(obtenerTexto(rs, "XEOPC_DESCRI", "OPCION", "DESCRIPCION"));
        opcion.setUrl(obtenerTexto(rs, "XEOPC_URL", "URL", "RUTA"));
        opcion.setIcono(obtenerTexto(rs, "XEOPC_ICONO", "ICONO"));
        opcion.setCodigoPadre(obtenerTexto(rs, "XEOPC_PADRE", "PADRE"));
        opcion.setOrden(obtenerEntero(rs, "XEOPC_ORDEN", "ORDEN"));
        opcion.setPerfilCodigo(obtenerTexto(rs, "XEPER_CODIGO", "PERFIL_CODIGO"));
        opcion.setPerfilDescripcion(obtenerTexto(rs, "XEPER_DESCRI", "PERFIL"));
        return opcion;
    }

    private void agregar(Set<String> disponibles, List<String> columnas, List<Object> valores,
            String columna, Object valor) {

        if (tieneColumna(disponibles, columna) && valor != null) {
            columnas.add(columna);
            valores.add(valor);
        }
    }

    private String construirInsert(String tabla, List<String> columnas) throws SQLException {
        if (columnas == null || columnas.isEmpty()) {
            throw new SQLException("No hay columnas disponibles para insertar en " + tabla + ".");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tabla).append(" (");
        StringBuilder marcas = new StringBuilder();

        for (int i = 0; i < columnas.size(); i++) {
            if (i > 0) {
                sql.append(", ");
                marcas.append(", ");
            }

            sql.append(columnas.get(i));
            marcas.append("?");
        }

        sql.append(") VALUES (").append(marcas).append(")");
        return sql.toString();
    }

    private String unirCondiciones(List<String> condiciones) {
        StringBuilder sql = new StringBuilder();

        for (int i = 0; i < condiciones.size(); i++) {
            if (i > 0) {
                sql.append(" OR ");
            }

            sql.append(condiciones.get(i));
        }

        return sql.toString();
    }

    private String unirCondicionesConAnd(List<String> condiciones) throws SQLException {
        if (condiciones == null || condiciones.isEmpty()) {
            throw new SQLException("No hay condiciones suficientes para actualizar perfiles de usuario.");
        }

        StringBuilder sql = new StringBuilder();

        for (int i = 0; i < condiciones.size(); i++) {
            if (i > 0) {
                sql.append(" AND ");
            }

            sql.append(condiciones.get(i));
        }

        return sql.toString();
    }

    private String hash(String texto, String algoritmo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            byte[] digest = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            String hash = new BigInteger(1, digest).toString(16);
            int longitud = "MD5".equalsIgnoreCase(algoritmo) ? 32 : 64;

            while (hash.length() < longitud) {
                hash = "0" + hash;
            }

            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
