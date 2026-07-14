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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
    private final PersonaDAO personaDAO = new PersonaDAO();
    private final CodigoDAO codigoDAO = new CodigoDAO();

    public Usuario buscarPorLogin(String login) {

        if (vacio(login)) {
            return null;
        }

        String sql
                = "SELECT * "
                + "FROM xeusu_usuari "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, login.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Usuario usuario = mapearUsuario(rs);

                    usuario.setPerfil(
                            obtenerPerfilPrincipal(
                                    con,
                                    usuario
                            )
                    );

                    return usuario;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar usuario: "
                    + e.getMessage()
            );
        }

        return null;
    }

    public boolean validarPassword(
            Usuario usuario,
            String passwordPlano
    ) {

        if (
                usuario == null
                || passwordPlano == null
        ) {
            return false;
        }

        String almacenado = usuario.getPassword();

        if (
                almacenado == null
                || almacenado.trim().isEmpty()
        ) {
            return false;
        }

        String hashGuardado = almacenado.trim();
        String md5 = hash(passwordPlano, "MD5");
        String sha256 = hash(passwordPlano, "SHA-256");

        return hashGuardado.equalsIgnoreCase(md5)
                || hashGuardado.equalsIgnoreCase(sha256)
                || hashGuardado.equalsIgnoreCase(
                        "{SHA256}" + sha256
                );
    }

    public List<UsuarioPerfil> listarUsuarios() {

        List<UsuarioPerfil> usuarios
                = listarUsuariosDesdeVista();

        if (!usuarios.isEmpty()) {
            return usuarios;
        }

        return listarUsuariosFallback();
    }

    private List<UsuarioPerfil> listarUsuariosDesdeVista() {

        List<UsuarioPerfil> usuarios
                = new ArrayList<>();

        String sql
                = "SELECT * "
                + "FROM vw_usuarios_perfiles "
                + "ORDER BY XEUSU_LOGIN";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                usuarios.add(
                        mapearUsuarioPerfil(rs)
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "No se pudo usar vw_usuarios_perfiles: "
                    + e.getMessage()
            );
        }

        return usuarios;
    }

    private List<UsuarioPerfil> listarUsuariosFallback() {

        List<UsuarioPerfil> usuarios
                = new ArrayList<>();

        String sql
                = "SELECT "
                + "u.XEUSU_LOGIN, "
                + "u.PEPER_CODIGO, "
                + "u.XEUSU_INTENTOS, "
                + "u.XEUSU_CAMBIO_CLAVE, "
                + "u.XEUSU_ULTIMO_ACCESO, "
                + "u.XEEST_CODIGO, "
                + "e.XEEST_DESCRI, "
                + "p.PEPER_TIPO, "
                + "p.PEPER_CEDULA, "
                + "p.PEPER_NOMBRE, "
                + "p.PEPER_APELLIDO, "
                + "CASE "
                + "WHEN emp.PEEMP_CODIGO IS NULL "
                + "THEN 'N' "
                + "ELSE 'S' "
                + "END AS ES_EMPLEADO, "
                + "emp.PEEMP_CODIGO, "
                + "pe.XEPER_CODIGO, "
                + "pe.XEPER_DESCRI "
                + "FROM xeusu_usuari u "
                + "LEFT JOIN peper_person p "
                + "ON u.PEPER_CODIGO = p.PEPER_CODIGO "
                + "LEFT JOIN peemp_emplea emp "
                + "ON p.PEPER_CODIGO = emp.PEPER_CODIGO "
                + "LEFT JOIN xeest_estado e "
                + "ON u.XEEST_CODIGO = e.XEEST_CODIGO "
                + "LEFT JOIN xeuxp_usuper up "
                + "ON u.PEPER_CODIGO = up.PEPER_CODIGO "
                + "AND u.XEUSU_LOGIN = up.XEUSU_LOGIN "
                + "AND up.XEUXP_FECRET IS NULL "
                + "LEFT JOIN xeper_perfil pe "
                + "ON up.XEPER_CODIGO = pe.XEPER_CODIGO "
                + "ORDER BY u.XEUSU_LOGIN";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                usuarios.add(
                        mapearUsuarioPerfil(rs)
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar usuarios: "
                    + e.getMessage()
            );
        }

        return usuarios;
    }

    public List<Persona> listarPersonasSinUsuario() {

        List<Persona> personas
                = listarPersonasSinUsuarioDesdeVista();

        if (!personas.isEmpty()) {
            return personas;
        }

        return listarPersonasSinUsuarioFallback();
    }

    public List<Persona> listarEmpleadosSinUsuario() {

        return listarPersonasSinUsuario();
    }

    private List<Persona> listarPersonasSinUsuarioDesdeVista() {

        List<Persona> personas
                = new ArrayList<>();

        String sql
                = "SELECT * "
                + "FROM vw_personas_sin_usuario "
                + "ORDER BY PEPER_APELLIDO, PEPER_NOMBRE";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                personas.add(
                        mapearPersonaBasica(rs)
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "No se pudo usar vw_personas_sin_usuario: "
                    + e.getMessage()
            );
        }

        return personas;
    }

    private List<Persona> listarPersonasSinUsuarioFallback() {

        List<Persona> personas
                = new ArrayList<>();

        String sql
                = "SELECT "
                + "p.PEPER_CODIGO, "
                + "p.PEPER_TIPO, "
                + "p.PEPER_CEDULA, "
                + "p.PEPER_NOMBRE, "
                + "p.PEPER_APELLIDO, "
                + "CASE "
                + "WHEN emp.PEEMP_CODIGO IS NULL "
                + "THEN 'N' "
                + "ELSE 'S' "
                + "END AS ES_EMPLEADO, "
                + "emp.PEEMP_CODIGO "
                + "FROM peper_person p "
                + "LEFT JOIN peemp_emplea emp "
                + "ON p.PEPER_CODIGO = emp.PEPER_CODIGO "
                + "WHERE NOT EXISTS ("
                + "SELECT 1 "
                + "FROM xeusu_usuari u "
                + "WHERE u.PEPER_CODIGO = p.PEPER_CODIGO"
                + ") "
                + "ORDER BY p.PEPER_APELLIDO, "
                + "p.PEPER_NOMBRE";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                personas.add(
                        mapearPersonaBasica(rs)
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar personas sin usuario: "
                    + e.getMessage()
            );
        }

        return personas;
    }

    public boolean registrarUsuarioParaPersona(
            String personaCodigo,
            String login,
            String perfilCodigo
    ) throws SQLException {

        try (Connection con = obtenerConexion()) {

            con.setAutoCommit(false);

            try {

                Persona persona
                        = buscarPersonaBasica(
                                con,
                                personaCodigo
                        );

                if (persona == null) {

                    throw new SQLException(
                            "No se encontró la persona seleccionada."
                    );
                }

                String loginFinal
                        = normalizarLogin(
                                login,
                                persona
                        );

                validarUsuarioNuevo(
                        con,
                        persona,
                        loginFinal,
                        perfilCodigo,
                        false
                );

                Usuario usuario
                        = crearUsuarioBase(
                                persona.getPeperCodigo(),
                                loginFinal
                        );

                registrarUsuario(
                        con,
                        usuario,
                        perfilCodigo
                );

                con.commit();
                return true;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }
        }
    }

    public boolean crearUsuarioExterno(
            Persona persona,
            String login,
            String perfilCodigo
    ) throws SQLException {

        try (Connection con = obtenerConexion()) {

            con.setAutoCommit(false);

            try {

                prepararPersonaExterna(
                        con,
                        persona
                );

                String loginFinal
                        = normalizarLogin(
                                login,
                                persona
                        );

                validarUsuarioNuevo(
                        con,
                        persona,
                        loginFinal,
                        perfilCodigo,
                        true
                );

                personaDAO.insertar(
                        con,
                        persona
                );

                Usuario usuario
                        = crearUsuarioBase(
                                persona.getPeperCodigo(),
                                loginFinal
                        );

                registrarUsuario(
                        con,
                        usuario,
                        perfilCodigo
                );

                con.commit();
                return true;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }
        }
    }

    public void crearUsuarioAutomaticoEmpleado(
            Connection con,
            Persona persona
    ) throws SQLException {

        if (
                persona == null
                || vacio(persona.getCedula())
                || vacio(persona.getPeperCodigo())
        ) {

            throw new SQLException(
                    "La persona no tiene cédula o código "
                    + "para crear el usuario."
            );
        }

        if (
                existeLogin(
                        con,
                        persona.getCedula()
                )
        ) {

            throw new SQLException(
                    "Ya existe un usuario registrado "
                    + "con esta cédula."
            );
        }

        String perfilEmpleado
                = perfilDAO.buscarCodigoPorNombre(
                        con,
                        "EMPLEADO"
                );

        if (vacio(perfilEmpleado)) {

            throw new SQLException(
                    "No existe el perfil EMPLEADO "
                    + "en la base de datos."
            );
        }

        persona.setTipo("EMP");

        Usuario usuario
                = crearUsuarioBase(
                        persona.getPeperCodigo(),
                        persona.getCedula()
                );

        registrarUsuario(
                con,
                usuario,
                perfilEmpleado
        );
    }

    public boolean asignarPerfil(
            String login,
            String perfilCodigo
    ) {

        if (
                vacio(login)
                || vacio(perfilCodigo)
        ) {
            return false;
        }

        try (Connection con = obtenerConexion()) {

            con.setAutoCommit(false);

            try {

                Usuario usuario
                        = buscarUsuarioBasico(
                                con,
                                login.trim()
                        );

                if (usuario == null) {

                    throw new SQLException(
                            "No existe el usuario seleccionado."
                    );
                }

                asignarPerfil(
                        con,
                        usuario,
                        perfilCodigo.trim()
                );

                con.commit();
                return true;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al asignar perfil: "
                    + e.getMessage()
            );
        }

        return false;
    }

    public boolean activarUsuario(String login) {

        return ejecutarActualizacionEstado(
                login,
                "A",
                true
        );
    }

    public boolean bloquearUsuario(String login) {

        return ejecutarActualizacionEstado(
                login,
                "B",
                false
        );
    }

    public boolean resetearPassword(String login) {

        String sql
                = "UPDATE xeusu_usuari "
                + "SET XEUSU_PASWD = ?, "
                + "XEUSU_CAMBIO_CLAVE = 'S', "
                + "XEUSU_INTENTOS = 0, "
                + "XEEST_CODIGO = 'A' "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    hash(
                            CONTRASENA_TEMPORAL,
                            "MD5"
                    )
            );

            ps.setString(
                    2,
                    login
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al resetear contraseña: "
                    + e.getMessage()
            );
        }

        return false;
    }

    public boolean actualizarPassword(
            String login,
            String nuevoPassword,
            boolean cambioObligatorio
    ) {

        String sql
                = "UPDATE xeusu_usuari "
                + "SET XEUSU_PASWD = ?, "
                + "XEUSU_CAMBIO_CLAVE = ?, "
                + "XEUSU_INTENTOS = 0 "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    hash(
                            nuevoPassword,
                            "MD5"
                    )
            );

            ps.setString(
                    2,
                    cambioObligatorio
                            ? "S"
                            : "N"
            );

            ps.setString(
                    3,
                    login
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar contraseña: "
                    + e.getMessage()
            );
        }

        return false;
    }

    public boolean marcarCambioClaveNo(String login) {

        String sql
                = "UPDATE xeusu_usuari "
                + "SET XEUSU_CAMBIO_CLAVE = 'N' "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    login
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al marcar cambio de clave: "
                    + e.getMessage()
            );
        }

        return false;
    }

    public int sumarIntentosFallidos(String login) {

        String sql
                = "UPDATE xeusu_usuari "
                + "SET XEUSU_INTENTOS = "
                + "COALESCE(XEUSU_INTENTOS, 0) + 1 "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    login
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "Error al sumar intentos: "
                    + e.getMessage()
            );
        }

        int intentos
                = obtenerIntentosFallidos(
                        login
                );

        if (intentos >= MAX_INTENTOS) {

            bloquearUsuario(login);
        }

        return intentos;
    }

    public boolean reiniciarIntentos(String login) {

        String sql
                = "UPDATE xeusu_usuari "
                + "SET XEUSU_INTENTOS = 0 "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    login
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al reiniciar intentos: "
                    + e.getMessage()
            );
        }

        return false;
    }

    public int obtenerIntentosFallidos(String login) {

        String sql
                = "SELECT XEUSU_INTENTOS "
                + "FROM xeusu_usuari "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    login
            );

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(
                            "XEUSU_INTENTOS"
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener intentos: "
                    + e.getMessage()
            );
        }

        return 0;
    }

    public boolean actualizarUltimoAcceso(String login) {

        String sql
                = "UPDATE xeusu_usuari "
                + "SET XEUSU_ULTIMO_ACCESO = ? "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setTimestamp(
                    1,
                    new Timestamp(
                            System.currentTimeMillis()
                    )
            );

            ps.setString(
                    2,
                    login
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar último acceso: "
                    + e.getMessage()
            );
        }

        return false;
    }

    public List<MenuOpcion> obtenerMenuUsuario(
            Usuario usuario
    ) {

        if (usuario == null) {
            return new ArrayList<>();
        }

        List<MenuOpcion> opciones
                = consultarMenu(
                        "XEUSU_LOGIN",
                        usuario.getUsuario()
                );

        if (
                opciones.isEmpty()
                && !vacio(
                        usuario.getCodigoPersona()
                )
        ) {

            opciones = consultarMenu(
                    "PEPER_CODIGO",
                    usuario.getCodigoPersona()
            );
        }

        if (
                opciones.isEmpty()
                && !vacio(
                        usuario.getCodigo()
                )
        ) {

            opciones = consultarMenu(
                    "XEUSU_CODIGO",
                    usuario.getCodigo()
            );
        }

        return construirArbolMenu(opciones);
    }

    private List<MenuOpcion> construirArbolMenu(
            List<MenuOpcion> opciones
    ) {

        Map<String, MenuOpcion> mapa
                = new LinkedHashMap<>();

        List<MenuOpcion> raices
                = new ArrayList<>();

        if (opciones == null) {
            return raices;
        }

        opciones.sort(
                Comparator
                        .comparingInt(
                                MenuOpcion::getNivel
                        )
                        .thenComparing(
                                MenuOpcion::getCodigoPadre,
                                Comparator.nullsFirst(
                                        String.CASE_INSENSITIVE_ORDER
                                )
                        )
                        .thenComparingInt(
                                MenuOpcion::getOrden
                        )
                        .thenComparing(
                                MenuOpcion::getDescripcion,
                                Comparator.nullsLast(
                                        String.CASE_INSENSITIVE_ORDER
                                )
                        )
        );

        for (MenuOpcion opcion : opciones) {

            if (
                    opcion == null
                    || vacio(opcion.getCodigo())
                    || !opcion.isPuedeVer()
            ) {
                continue;
            }

            if (vacio(opcion.getTipo())) {

                opcion.setTipo(
                        vacio(opcion.getUrl())
                                ? "G"
                                : "O"
                );
            }

            mapa.put(
                    opcion.getCodigo().trim(),
                    opcion
            );
        }

        for (MenuOpcion opcion : mapa.values()) {

            String padre
                    = opcion.getCodigoPadre();

            if (
                    !vacio(padre)
                    && mapa.containsKey(
                            padre.trim()
                    )
            ) {

                mapa.get(
                        padre.trim()
                ).getHijos().add(opcion);

            } else {

                raices.add(opcion);
            }
        }

        ordenarMenu(raices);

        return podarMenuVacio(raices);
    }

    private void ordenarMenu(
            List<MenuOpcion> opciones
    ) {

        if (opciones == null) {
            return;
        }

        opciones.sort(
                Comparator
                        .comparingInt(
                                MenuOpcion::getOrden
                        )
                        .thenComparing(
                                MenuOpcion::getDescripcion,
                                Comparator.nullsLast(
                                        String.CASE_INSENSITIVE_ORDER
                                )
                        )
        );

        for (MenuOpcion opcion : opciones) {

            ordenarMenu(
                    opcion.getHijos()
            );
        }
    }

    private List<MenuOpcion> podarMenuVacio(
            List<MenuOpcion> opciones
    ) {

        List<MenuOpcion> resultado
                = new ArrayList<>();

        if (opciones == null) {
            return resultado;
        }

        for (MenuOpcion opcion : opciones) {

            List<MenuOpcion> hijosVisibles
                    = podarMenuVacio(
                            opcion.getHijos()
                    );

            opcion.getHijos().clear();

            opcion.getHijos().addAll(
                    hijosVisibles
            );

            boolean tieneUrl
                    = !vacio(
                            opcion.getUrl()
                    );

            boolean esContenedor
                    = opcion.esSubsistema()
                    || opcion.esGrupo()
                    || !opcion.getHijos().isEmpty();

            if (
                    tieneUrl
                    || (
                            esContenedor
                            && !opcion.getHijos().isEmpty()
                    )
            ) {

                resultado.add(opcion);
            }
        }

        return resultado;
    }

    private List<MenuOpcion> consultarMenu(
            String columnaUsuario,
            String valor
    ) {

        List<MenuOpcion> menu
                = new ArrayList<>();

        if (vacio(valor)) {
            return menu;
        }

        String sql
                = "SELECT * "
                + "FROM vw_menu_usuario "
                + "WHERE " + columnaUsuario + " = ? "
                + "ORDER BY XEOPC_NIVEL, "
                + "XEOPC_PADRE, "
                + "XEOPC_ORDEN, "
                + "XEOPC_DESCRI";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    valor
            );

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    menu.add(
                            mapearMenu(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "No se pudo usar vw_menu_usuario por "
                    + columnaUsuario
                    + ": "
                    + e.getMessage()
            );

            menu = consultarMenuFallback(
                    columnaUsuario,
                    valor
            );
        }

        if (menu.isEmpty()) {

            menu = consultarMenuFallback(
                    columnaUsuario,
                    valor
            );
        }

        return menu;
    }

    private List<MenuOpcion> consultarMenuFallback(
            String columnaUsuario,
            String valor
    ) {

        List<MenuOpcion> menu
                = new ArrayList<>();

        if (vacio(valor)) {
            return menu;
        }

        String sql
                = "SELECT DISTINCT "
                + "u.PEPER_CODIGO, "
                + "u.XEUSU_LOGIN, "
                + "pf.XEPER_CODIGO, "
                + "pf.XEPER_DESCRI, "
                + "op.XEOPC_CODIGO, "
                + "op.XESIS_CODIGO, "
                + "op.XEOPC_PADRE, "
                + "op.XEOPC_DESCRI, "
                + "op.XEOPC_TIPO, "
                + "op.XEOPC_NIVEL, "
                + "op.XEOPC_URL, "
                + "op.XEOPC_ICONO, "
                + "op.XEOPC_ORDEN, "
                + "op.XEOPC_ESTADO, "
                + "oxp.XEOXP_VER, "
                + "oxp.XEOXP_CREAR, "
                + "oxp.XEOXP_EDITAR, "
                + "oxp.XEOXP_ELIMINAR "
                + "FROM xeusu_usuari u "
                + "JOIN xeuxp_usuper up "
                + "ON u.PEPER_CODIGO = up.PEPER_CODIGO "
                + "AND u.XEUSU_LOGIN = up.XEUSU_LOGIN "
                + "AND up.XEUXP_FECRET IS NULL "
                + "JOIN xeper_perfil pf "
                + "ON up.XEPER_CODIGO = pf.XEPER_CODIGO "
                + "AND COALESCE(pf.XEPER_ESTADO, 'A') = 'A' "
                + "JOIN xeoxp_opcper oxp "
                + "ON pf.XEPER_CODIGO = oxp.XEPER_CODIGO "
                + "AND oxp.XEOXP_FECRET IS NULL "
                + "AND COALESCE(oxp.XEOXP_VER, 'S') = 'S' "
                + "JOIN xeopc_opcion op "
                + "ON oxp.XEOPC_CODIGO = op.XEOPC_CODIGO "
                + "AND COALESCE(op.XEOPC_ESTADO, 'A') = 'A' "
                + "WHERE u." + columnaUsuario + " = ? "
                + "AND COALESCE(u.XEEST_CODIGO, 'A') = 'A' "
                + "ORDER BY op.XEOPC_NIVEL, "
                + "op.XEOPC_PADRE, "
                + "op.XEOPC_ORDEN, "
                + "op.XEOPC_DESCRI";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    valor
            );

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    menu.add(
                            mapearMenu(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "No se pudo cargar menú fallback por "
                    + columnaUsuario
                    + ": "
                    + e.getMessage()
            );
        }

        return menu;
    }

    private Usuario crearUsuarioBase(
            String codigoPersona,
            String login
    ) {

        Usuario usuario = new Usuario();

        usuario.setCodigoPersona(
                codigoPersona
        );

        usuario.setUsuario(
                login
        );

        usuario.setPassword(
                hash(
                        CONTRASENA_TEMPORAL,
                        "MD5"
                )
        );

        usuario.setIntentosFallidos(0);
        usuario.setCambioClave("S");
        usuario.setEstadoCodigo("A");

        return usuario;
    }

    private void prepararPersonaExterna(
            Connection con,
            Persona persona
    ) throws SQLException {

        if (persona == null) {

            throw new SQLException(
                    "No se recibieron los datos de la persona."
            );
        }

        if (vacio(persona.getPeperCodigo())) {

            persona.setPeperCodigo(
                    codigoDAO.generarCodigo(
                            con,
                            "PEPER_CODIGO"
                    )
            );
        }

        if (vacio(persona.getTipo())) {

            persona.setTipo("INV");
        }

        if (vacio(persona.getNombres())) {

            throw new SQLException(
                    "Los nombres de la persona son obligatorios."
            );
        }

        if (vacio(persona.getApellidos())) {

            throw new SQLException(
                    "Los apellidos de la persona son obligatorios."
            );
        }

        if (vacio(persona.getDireccion())) {

            persona.setDireccion(
                    "Sin dirección"
            );
        }

        persona.setCargasFamiliares(0);
    }

    private String normalizarLogin(
            String login,
            Persona persona
    ) throws SQLException {

        String loginFinal
                = login == null
                ? ""
                : login.trim();

        if (
                vacio(loginFinal)
                && persona != null
                && !vacio(persona.getCedula())
        ) {

            loginFinal
                    = persona.getCedula().trim();
        }

        if (
                vacio(loginFinal)
                && persona != null
                && !vacio(persona.getEmail())
        ) {

            loginFinal
                    = persona.getEmail().trim();
        }

        if (vacio(loginFinal)) {

            throw new SQLException(
                    "El login del usuario es obligatorio."
            );
        }

        return loginFinal;
    }

    private void validarUsuarioNuevo(
            Connection con,
            Persona persona,
            String login,
            String perfilCodigo,
            boolean validarCedula
    ) throws SQLException {

        if (existeLogin(con, login)) {

            throw new SQLException(
                    "Ya existe un usuario registrado "
                    + "con este login."
            );
        }

        if (
                validarCedula
                && persona != null
                && !vacio(persona.getCedula())
                && personaDAO.existeCedula(
                        con,
                        persona.getCedula()
                )
        ) {

            throw new SQLException(
                    "Ya existe una persona registrada "
                    + "con esta cédula."
            );
        }

        if (
                vacio(perfilCodigo)
                || !perfilExiste(
                        con,
                        perfilCodigo
                )
        ) {

            throw new SQLException(
                    "El perfil seleccionado no existe "
                    + "o está inactivo."
            );
        }
    }

    private Persona buscarPersonaBasica(
            Connection con,
            String personaCodigo
    ) throws SQLException {

        String sql
                = "SELECT "
                + "p.PEPER_CODIGO, "
                + "p.PEPER_TIPO, "
                + "p.PEPER_CEDULA, "
                + "p.PEPER_NOMBRE, "
                + "p.PEPER_APELLIDO, "
                + "p.PEPER_EMAIL, "
                + "CASE "
                + "WHEN emp.PEEMP_CODIGO IS NULL "
                + "THEN 'N' "
                + "ELSE 'S' "
                + "END AS ES_EMPLEADO, "
                + "emp.PEEMP_CODIGO "
                + "FROM peper_person p "
                + "LEFT JOIN peemp_emplea emp "
                + "ON p.PEPER_CODIGO = emp.PEPER_CODIGO "
                + "WHERE p.PEPER_CODIGO = ?";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    personaCodigo
            );

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return mapearPersonaBasica(rs);
                }
            }
        }

        return null;
    }

    private boolean perfilExiste(
            Connection con,
            String perfilCodigo
    ) throws SQLException {

        String sql
                = "SELECT XEPER_CODIGO "
                + "FROM xeper_perfil "
                + "WHERE XEPER_CODIGO = ? "
                + "AND COALESCE(XEPER_ESTADO, 'A') = 'A'";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    perfilCodigo
            );

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    private void registrarUsuario(
            Connection con,
            Usuario usuario,
            String perfilCodigo
    ) throws SQLException {

        if (
                existeLogin(
                        con,
                        usuario.getUsuario()
                )
        ) {

            throw new SQLException(
                    "Ya existe un usuario con ese login."
            );
        }

        if (vacio(usuario.getCodigo())) {

            usuario.setCodigo(
                    generarCodigoUsuario(con)
            );
        }

        Set<String> columnasDisponibles
                = obtenerColumnas(
                        con,
                        "xeusu_usuari"
                );

        List<String> columnas
                = new ArrayList<>();

        List<Object> valores
                = new ArrayList<>();

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_CODIGO",
                usuario.getCodigo()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "PEPER_CODIGO",
                usuario.getCodigoPersona()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_LOGIN",
                usuario.getUsuario()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_PASWD",
                usuario.getPassword()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_FECCRE",
                new Timestamp(
                        System.currentTimeMillis()
                )
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_FECMOD",
                new Timestamp(
                        System.currentTimeMillis()
                )
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_PIEFIR",
                "GUTIERREZ - LANDAZURI - LEITON"
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_INTENTOS",
                Integer.valueOf(
                        usuario.getIntentosFallidos()
                )
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_CAMBIO_CLAVE",
                usuario.getCambioClave()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEEST_CODIGO",
                usuario.getEstadoCodigo()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_ALGORITMO",
                "MD5"
        );

        String sql
                = construirInsert(
                        "xeusu_usuari",
                        columnas
                );

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            for (
                    int i = 0;
                    i < valores.size();
                    i++
            ) {

                ps.setObject(
                        i + 1,
                        valores.get(i)
                );
            }

            ps.executeUpdate();
        }

        asignarPerfil(
                con,
                usuario,
                perfilCodigo
        );
    }

    private void asignarPerfil(
            Connection con,
            Usuario usuario,
            String perfilCodigo
    ) throws SQLException {

        if (usuario == null) {

            throw new SQLException(
                    "No se recibió el usuario "
                    + "que se desea asignar."
            );
        }

        if (vacio(perfilCodigo)) {

            throw new SQLException(
                    "El perfil es obligatorio."
            );
        }

        if (
                !perfilExiste(
                        con,
                        perfilCodigo
                )
        ) {

            throw new SQLException(
                    "El perfil seleccionado no existe "
                    + "o está inactivo."
            );
        }

        Set<String> columnasDisponibles
                = obtenerColumnas(
                        con,
                        "xeuxp_usuper"
                );

        List<String> condicionesUsuario
                = new ArrayList<>();

        List<Object> valoresUsuario
                = new ArrayList<>();

        if (
                tieneColumna(
                        columnasDisponibles,
                        "PEPER_CODIGO"
                )
                && !vacio(
                        usuario.getCodigoPersona()
                )
        ) {

            condicionesUsuario.add(
                    "PEPER_CODIGO = ?"
            );

            valoresUsuario.add(
                    usuario.getCodigoPersona()
            );
        }

        if (
                tieneColumna(
                        columnasDisponibles,
                        "XEUSU_LOGIN"
                )
                && !vacio(
                        usuario.getUsuario()
                )
        ) {

            condicionesUsuario.add(
                    "XEUSU_LOGIN = ?"
            );

            valoresUsuario.add(
                    usuario.getUsuario()
            );

        } else if (
                tieneColumna(
                        columnasDisponibles,
                        "XEUSU_CODIGO"
                )
                && !vacio(
                        usuario.getCodigo()
                )
        ) {

            condicionesUsuario.add(
                    "XEUSU_CODIGO = ?"
            );

            valoresUsuario.add(
                    usuario.getCodigo()
            );
        }

        if (condicionesUsuario.isEmpty()) {

            throw new SQLException(
                    "No existen identificadores suficientes "
                    + "para asignar el perfil."
            );
        }

        boolean conservaHistorial
                = tieneColumna(
                        columnasDisponibles,
                        "XEUXP_FECRET"
                );

        if (!conservaHistorial) {

            eliminarAsignacionesSinHistorial(
                    con,
                    condicionesUsuario,
                    valoresUsuario
            );

            insertarAsignacionPerfil(
                    con,
                    columnasDisponibles,
                    usuario,
                    perfilCodigo
            );

            return;
        }

        cerrarAsignacionesActivas(
                con,
                condicionesUsuario,
                valoresUsuario
        );

        int reactivadas
                = reactivarAsignacionExistente(
                        con,
                        columnasDisponibles,
                        condicionesUsuario,
                        valoresUsuario,
                        perfilCodigo
                );

        if (reactivadas == 0) {

            insertarAsignacionPerfil(
                    con,
                    columnasDisponibles,
                    usuario,
                    perfilCodigo
            );
        }
    }

    private void cerrarAsignacionesActivas(
            Connection con,
            List<String> condicionesUsuario,
            List<Object> valoresUsuario
    ) throws SQLException {

        String sql
                = "UPDATE xeuxp_usuper "
                + "SET XEUXP_FECRET = ? "
                + "WHERE "
                + unirCondicionesConAnd(
                        condicionesUsuario
                )
                + " AND XEUXP_FECRET IS NULL";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            int indice = 1;

            ps.setDate(
                    indice++,
                    fechaActual()
            );

            for (Object valor : valoresUsuario) {

                ps.setObject(
                        indice++,
                        valor
                );
            }

            ps.executeUpdate();
        }
    }

    private int reactivarAsignacionExistente(
            Connection con,
            Set<String> columnasDisponibles,
            List<String> condicionesUsuario,
            List<Object> valoresUsuario,
            String perfilCodigo
    ) throws SQLException {

        List<String> cambios
                = new ArrayList<>();

        List<Object> valoresCambios
                = new ArrayList<>();

        if (
                tieneColumna(
                        columnasDisponibles,
                        "XEUXP_FECASI"
                )
        ) {

            cambios.add(
                    "XEUXP_FECASI = ?"
            );

            valoresCambios.add(
                    fechaActual()
            );
        }

        cambios.add(
                "XEUXP_FECRET = NULL"
        );

        if (
                tieneColumna(
                        columnasDisponibles,
                        "XEEST_CODIGO"
                )
        ) {

            cambios.add(
                    "XEEST_CODIGO = ?"
            );

            valoresCambios.add("A");
        }

        String sql
                = "UPDATE xeuxp_usuper SET "
                + String.join(", ", cambios)
                + " WHERE "
                + unirCondicionesConAnd(
                        condicionesUsuario
                )
                + " AND XEPER_CODIGO = ?";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            int indice = 1;

            for (Object valor : valoresCambios) {

                ps.setObject(
                        indice++,
                        valor
                );
            }

            for (Object valor : valoresUsuario) {

                ps.setObject(
                        indice++,
                        valor
                );
            }

            ps.setString(
                    indice,
                    perfilCodigo
            );

            return ps.executeUpdate();
        }
    }

    private void insertarAsignacionPerfil(
            Connection con,
            Set<String> columnasDisponibles,
            Usuario usuario,
            String perfilCodigo
    ) throws SQLException {

        List<String> columnas
                = new ArrayList<>();

        List<Object> valores
                = new ArrayList<>();

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "PEPER_CODIGO",
                usuario.getCodigoPersona()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_CODIGO",
                usuario.getCodigo()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUSU_LOGIN",
                usuario.getUsuario()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEPER_CODIGO",
                perfilCodigo
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEUXP_FECASI",
                fechaActual()
        );

        agregar(
                columnasDisponibles,
                columnas,
                valores,
                "XEEST_CODIGO",
                "A"
        );

        String sql
                = construirInsert(
                        "xeuxp_usuper",
                        columnas
                );

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            for (
                    int i = 0;
                    i < valores.size();
                    i++
            ) {

                ps.setObject(
                        i + 1,
                        valores.get(i)
                );
            }

            ps.executeUpdate();
        }
    }

    private void eliminarAsignacionesSinHistorial(
            Connection con,
            List<String> condicionesUsuario,
            List<Object> valoresUsuario
    ) throws SQLException {

        String sql
                = "DELETE FROM xeuxp_usuper "
                + "WHERE "
                + unirCondicionesConAnd(
                        condicionesUsuario
                );

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            for (
                    int i = 0;
                    i < valoresUsuario.size();
                    i++
            ) {

                ps.setObject(
                        i + 1,
                        valoresUsuario.get(i)
                );
            }

            ps.executeUpdate();
        }
    }

    private Usuario buscarUsuarioBasico(
            Connection con,
            String login
    ) throws SQLException {

        String sql
                = "SELECT * "
                + "FROM xeusu_usuari "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    login
            );

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return mapearUsuario(rs);
                }
            }
        }

        return null;
    }

    private Perfil obtenerPerfilPrincipal(
            Connection con,
            Usuario usuario
    ) throws SQLException {

        Set<String> columnas
                = obtenerColumnas(
                        con,
                        "xeuxp_usuper"
                );

        List<String> condiciones
                = new ArrayList<>();

        List<String> valores
                = new ArrayList<>();

        if (
                tieneColumna(
                        columnas,
                        "XEUSU_CODIGO"
                )
                && !vacio(
                        usuario.getCodigo()
                )
        ) {

            condiciones.add(
                    "up.XEUSU_CODIGO = ?"
            );

            valores.add(
                    usuario.getCodigo()
            );
        }

        if (
                tieneColumna(
                        columnas,
                        "PEPER_CODIGO"
                )
                && !vacio(
                        usuario.getCodigoPersona()
                )
                && tieneColumna(
                        columnas,
                        "XEUSU_LOGIN"
                )
                && !vacio(
                        usuario.getUsuario()
                )
        ) {

            condiciones.add(
                    "(up.PEPER_CODIGO = ? "
                    + "AND up.XEUSU_LOGIN = ?)"
            );

            valores.add(
                    usuario.getCodigoPersona()
            );

            valores.add(
                    usuario.getUsuario()
            );

        } else if (
                tieneColumna(
                        columnas,
                        "XEUSU_LOGIN"
                )
                && !vacio(
                        usuario.getUsuario()
                )
        ) {

            condiciones.add(
                    "up.XEUSU_LOGIN = ?"
            );

            valores.add(
                    usuario.getUsuario()
            );
        }

        if (condiciones.isEmpty()) {
            return null;
        }

        StringBuilder sql
                = new StringBuilder();

        sql.append(
                "SELECT "
        );

        sql.append(
                "p.XEPER_CODIGO, "
        );

        sql.append(
                "p.XEPER_DESCRI, "
        );

        sql.append(
                "p.XEPER_ESTADO AS XEEST_CODIGO "
        );

        sql.append(
                "FROM xeuxp_usuper up "
        );

        sql.append(
                "INNER JOIN xeper_perfil p "
        );

        sql.append(
                "ON up.XEPER_CODIGO = p.XEPER_CODIGO "
        );

        sql.append("WHERE (");

        sql.append(
                unirCondiciones(
                        condiciones
                )
        );

        sql.append(") ");

        if (
                tieneColumna(
                        columnas,
                        "XEUXP_FECRET"
                )
        ) {

            sql.append(
                    "AND up.XEUXP_FECRET IS NULL "
            );
        }

        sql.append(
                "AND COALESCE("
                + "p.XEPER_ESTADO, 'A'"
                + ") = 'A' "
        );

        if (
                tieneColumna(
                        columnas,
                        "XEUXP_FECASI"
                )
        ) {

            sql.append(
                    "ORDER BY up.XEUXP_FECASI DESC "
            );

        } else {

            sql.append(
                    "ORDER BY p.XEPER_CODIGO "
            );
        }

        sql.append("LIMIT 1");

        try (
                PreparedStatement ps
                = con.prepareStatement(
                        sql.toString()
                )
        ) {

            for (
                    int i = 0;
                    i < valores.size();
                    i++
            ) {

                ps.setString(
                        i + 1,
                        valores.get(i)
                );
            }

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Perfil perfil = new Perfil();

                    perfil.setCodigo(
                            obtenerTexto(
                                    rs,
                                    "XEPER_CODIGO"
                            )
                    );

                    perfil.setDescripcion(
                            obtenerTexto(
                                    rs,
                                    "XEPER_DESCRI"
                            )
                    );

                    perfil.setEstadoCodigo(
                            obtenerTexto(
                                    rs,
                                    "XEEST_CODIGO"
                            )
                    );

                    return perfil;
                }
            }
        }

        return null;
    }

    public boolean existeLogin(
            Connection con,
            String login
    ) throws SQLException {

        String sql
                = "SELECT XEUSU_LOGIN "
                + "FROM xeusu_usuari "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    login
            );

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    private boolean ejecutarActualizacionEstado(
            String login,
            String estado,
            boolean reiniciar
    ) {

        String sql
                = reiniciar
                ? "UPDATE xeusu_usuari "
                + "SET XEEST_CODIGO = ?, "
                + "XEUSU_INTENTOS = 0 "
                + "WHERE XEUSU_LOGIN = ?"
                : "UPDATE xeusu_usuari "
                + "SET XEEST_CODIGO = ? "
                + "WHERE XEUSU_LOGIN = ?";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    estado
            );

            ps.setString(
                    2,
                    login
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar estado de usuario: "
                    + e.getMessage()
            );
        }

        return false;
    }

    private String generarCodigoUsuario(
            Connection con
    ) {

        Set<String> columnas;

        try {

            columnas = obtenerColumnas(
                    con,
                    "xeusu_usuari"
            );

            if (
                    !tieneColumna(
                            columnas,
                            "XEUSU_CODIGO"
                    )
            ) {
                return "";
            }

        } catch (SQLException e) {

            return "";
        }

        String sql
                = "SELECT MAX("
                + "CAST(SUBSTRING(XEUSU_CODIGO, 4) "
                + "AS UNSIGNED)"
                + ") AS maximo "
                + "FROM xeusu_usuari "
                + "WHERE XEUSU_CODIGO LIKE 'USU%'";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            int numero = 1;

            if (rs.next()) {

                numero
                        = rs.getInt("maximo") + 1;
            }

            return "USU"
                    + String.format(
                            "%07d",
                            numero
                    );

        } catch (SQLException e) {

            return "";
        }
    }

    private Usuario mapearUsuario(
            ResultSet rs
    ) throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setCodigo(
                obtenerTexto(
                        rs,
                        "XEUSU_CODIGO",
                        "USUARIO_CODIGO"
                )
        );

        usuario.setCodigoPersona(
                obtenerTexto(
                        rs,
                        "PEPER_CODIGO"
                )
        );

        usuario.setUsuario(
                obtenerTexto(
                        rs,
                        "XEUSU_LOGIN",
                        "LOGIN",
                        "USUARIO"
                )
        );

        usuario.setPassword(
                obtenerTexto(
                        rs,
                        "XEUSU_PASWD",
                        "XEUSU_PASSWORD",
                        "PASSWORD"
                )
        );

        usuario.setIntentosFallidos(
                obtenerEntero(
                        rs,
                        "XEUSU_INTENTOS",
                        "INTENTOS"
                )
        );

        usuario.setCambioClave(
                obtenerTexto(
                        rs,
                        "XEUSU_CAMBIO_CLAVE",
                        "CAMBIO_CLAVE"
                )
        );

        usuario.setEstadoCodigo(
                obtenerTexto(
                        rs,
                        "XEEST_CODIGO",
                        "ESTADO_CODIGO"
                )
        );

        usuario.setEstadoDescripcion(
                obtenerTexto(
                        rs,
                        "XEEST_DESCRI",
                        "ESTADO"
                )
        );

        usuario.setUltimoAcceso(
                obtenerTexto(
                        rs,
                        "XEUSU_ULTIMO_ACCESO",
                        "ULTIMO_ACCESO"
                )
        );

        return usuario;
    }

    private UsuarioPerfil mapearUsuarioPerfil(
            ResultSet rs
    ) throws SQLException {

        UsuarioPerfil usuario
                = new UsuarioPerfil();

        usuario.setUsuarioCodigo(
                obtenerTexto(
                        rs,
                        "XEUSU_CODIGO",
                        "USUARIO_CODIGO"
                )
        );

        usuario.setLogin(
                obtenerTexto(
                        rs,
                        "XEUSU_LOGIN",
                        "LOGIN",
                        "USUARIO"
                )
        );

        usuario.setPersonaCodigo(
                obtenerTexto(
                        rs,
                        "PEPER_CODIGO",
                        "PERSONA_CODIGO"
                )
        );

        usuario.setTipoPersona(
                obtenerTexto(
                        rs,
                        "PEPER_TIPO"
                )
        );

        usuario.setTipoPersonaDescripcion(
                obtenerTexto(
                        rs,
                        "PEPER_TIPO_DESCRI",
                        "PETIP_DESCRI",
                        "TIPO_DESCRI"
                )
        );

        usuario.setCedula(
                obtenerTexto(
                        rs,
                        "PEPER_CEDULA",
                        "CEDULA"
                )
        );

        usuario.setNombres(
                obtenerTexto(
                        rs,
                        "PEPER_NOMBRE",
                        "NOMBRES",
                        "NOMBRE"
                )
        );

        usuario.setApellidos(
                obtenerTexto(
                        rs,
                        "PEPER_APELLIDO",
                        "APELLIDOS",
                        "APELLIDO"
                )
        );

        usuario.setPerfilCodigo(
                obtenerTexto(
                        rs,
                        "XEPER_CODIGO",
                        "PERFIL_CODIGO"
                )
        );

        usuario.setPerfilDescripcion(
                obtenerTexto(
                        rs,
                        "XEPER_DESCRI",
                        "PERFIL",
                        "PERFIL_DESCRIPCION"
                )
        );

        usuario.setEstadoCodigo(
                obtenerTexto(
                        rs,
                        "XEEST_CODIGO",
                        "ESTADO_CODIGO"
                )
        );

        usuario.setEstadoDescripcion(
                obtenerTexto(
                        rs,
                        "XEEST_DESCRI",
                        "ESTADO"
                )
        );

        usuario.setCambioClave(
                obtenerTexto(
                        rs,
                        "XEUSU_CAMBIO_CLAVE",
                        "CAMBIO_CLAVE"
                )
        );

        usuario.setIntentosFallidos(
                obtenerEntero(
                        rs,
                        "XEUSU_INTENTOS",
                        "INTENTOS"
                )
        );

        usuario.setUltimoAcceso(
                obtenerTexto(
                        rs,
                        "XEUSU_ULTIMO_ACCESO",
                        "ULTIMO_ACCESO"
                )
        );

        usuario.setEsEmpleado(
                obtenerTexto(
                        rs,
                        "ES_EMPLEADO"
                )
        );

        usuario.setCodigoEmpleado(
                obtenerTexto(
                        rs,
                        "PEEMP_CODIGO"
                )
        );

        return usuario;
    }

    private Persona mapearPersonaBasica(
            ResultSet rs
    ) throws SQLException {

        Persona persona = new Persona();

        persona.setPeperCodigo(
                obtenerTexto(
                        rs,
                        "PEPER_CODIGO",
                        "PERSONA_CODIGO"
                )
        );

        persona.setTipo(
                obtenerTexto(
                        rs,
                        "PEPER_TIPO"
                )
        );

        persona.setTipoDescripcion(
                obtenerTexto(
                        rs,
                        "PEPER_TIPO_DESCRI",
                        "PETIP_DESCRI",
                        "TIPO_DESCRI"
                )
        );

        persona.setCedula(
                obtenerTexto(
                        rs,
                        "PEPER_CEDULA",
                        "CEDULA"
                )
        );

        persona.setNombres(
                obtenerTexto(
                        rs,
                        "PEPER_NOMBRE",
                        "NOMBRES",
                        "NOMBRE"
                )
        );

        persona.setApellidos(
                obtenerTexto(
                        rs,
                        "PEPER_APELLIDO",
                        "APELLIDOS",
                        "APELLIDO"
                )
        );

        persona.setEmail(
                obtenerTexto(
                        rs,
                        "PEPER_EMAIL",
                        "EMAIL"
                )
        );

        persona.setEsEmpleado(
                obtenerTexto(
                        rs,
                        "ES_EMPLEADO"
                )
        );

        persona.setCodigoEmpleado(
                obtenerTexto(
                        rs,
                        "PEEMP_CODIGO"
                )
        );

        return persona;
    }

    private MenuOpcion mapearMenu(
            ResultSet rs
    ) throws SQLException {

        MenuOpcion opcion = new MenuOpcion();

        opcion.setCodigo(
                obtenerTexto(
                        rs,
                        "XEOPC_CODIGO",
                        "OPCION_CODIGO",
                        "CODIGO"
                )
        );

        opcion.setDescripcion(
                obtenerTexto(
                        rs,
                        "XEOPC_DESCRI",
                        "OPCION",
                        "DESCRIPCION"
                )
        );

        opcion.setUrl(
                obtenerTexto(
                        rs,
                        "XEOPC_URL",
                        "URL",
                        "RUTA"
                )
        );

        opcion.setIcono(
                obtenerTexto(
                        rs,
                        "XEOPC_ICONO",
                        "ICONO"
                )
        );

        opcion.setCodigoPadre(
                obtenerTexto(
                        rs,
                        "XEOPC_PADRE",
                        "PADRE"
                )
        );

        opcion.setTipo(
                obtenerTexto(
                        rs,
                        "XEOPC_TIPO",
                        "TIPO"
                )
        );

        opcion.setNivel(
                obtenerEntero(
                        rs,
                        "XEOPC_NIVEL",
                        "NIVEL"
                )
        );

        opcion.setOrden(
                obtenerEntero(
                        rs,
                        "XEOPC_ORDEN",
                        "ORDEN"
                )
        );

        opcion.setPerfilCodigo(
                obtenerTexto(
                        rs,
                        "XEPER_CODIGO",
                        "PERFIL_CODIGO"
                )
        );

        opcion.setPerfilDescripcion(
                obtenerTexto(
                        rs,
                        "XEPER_DESCRI",
                        "PERFIL"
                )
        );

        opcion.setPuedeVer(
                esPermisoActivo(
                        obtenerTexto(
                                rs,
                                "XEOXP_VER",
                                "PUEDE_VER",
                                "VER"
                        ),
                        true
                )
        );

        opcion.setPuedeCrear(
                esPermisoActivo(
                        obtenerTexto(
                                rs,
                                "XEOXP_CREAR",
                                "PUEDE_CREAR",
                                "CREAR"
                        ),
                        false
                )
        );

        opcion.setPuedeEditar(
                esPermisoActivo(
                        obtenerTexto(
                                rs,
                                "XEOXP_EDITAR",
                                "PUEDE_EDITAR",
                                "EDITAR"
                        ),
                        false
                )
        );

        opcion.setPuedeEliminar(
                esPermisoActivo(
                        obtenerTexto(
                                rs,
                                "XEOXP_ELIMINAR",
                                "PUEDE_ELIMINAR",
                                "ELIMINAR"
                        ),
                        false
                )
        );

        return opcion;
    }

    private boolean esPermisoActivo(
            String valor,
            boolean porDefecto
    ) {

        if (
                valor == null
                || valor.trim().isEmpty()
        ) {
            return porDefecto;
        }

        String limpio
                = valor
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return "S".equals(limpio)
                || "1".equals(limpio)
                || "TRUE".equals(limpio)
                || "A".equals(limpio);
    }

    private void agregar(
            Set<String> disponibles,
            List<String> columnas,
            List<Object> valores,
            String columna,
            Object valor
    ) {

        if (
                tieneColumna(
                        disponibles,
                        columna
                )
                && valor != null
        ) {

            columnas.add(columna);
            valores.add(valor);
        }
    }

    private String construirInsert(
            String tabla,
            List<String> columnas
    ) throws SQLException {

        if (
                columnas == null
                || columnas.isEmpty()
        ) {

            throw new SQLException(
                    "No hay columnas disponibles "
                    + "para insertar en "
                    + tabla
                    + "."
            );
        }

        StringBuilder sql
                = new StringBuilder(
                        "INSERT INTO "
                ).append(tabla).append(" (");

        StringBuilder marcas
                = new StringBuilder();

        for (
                int i = 0;
                i < columnas.size();
                i++
        ) {

            if (i > 0) {

                sql.append(", ");
                marcas.append(", ");
            }

            sql.append(
                    columnas.get(i)
            );

            marcas.append("?");
        }

        sql.append(
                ") VALUES ("
        ).append(
                marcas
        ).append(
                ")"
        );

        return sql.toString();
    }

    private String unirCondiciones(
            List<String> condiciones
    ) {

        StringBuilder sql
                = new StringBuilder();

        for (
                int i = 0;
                i < condiciones.size();
                i++
        ) {

            if (i > 0) {

                sql.append(" OR ");
            }

            sql.append(
                    condiciones.get(i)
            );
        }

        return sql.toString();
    }

    private String unirCondicionesConAnd(
            List<String> condiciones
    ) throws SQLException {

        if (
                condiciones == null
                || condiciones.isEmpty()
        ) {

            throw new SQLException(
                    "No hay condiciones suficientes "
                    + "para actualizar perfiles de usuario."
            );
        }

        StringBuilder sql
                = new StringBuilder();

        for (
                int i = 0;
                i < condiciones.size();
                i++
        ) {

            if (i > 0) {

                sql.append(" AND ");
            }

            sql.append(
                    condiciones.get(i)
            );
        }

        return sql.toString();
    }

    private Date fechaActual() {

        return new Date(
                System.currentTimeMillis()
        );
    }

    private String hash(
            String texto,
            String algoritmo
    ) {

        try {

            MessageDigest md
                    = MessageDigest.getInstance(
                            algoritmo
                    );

            byte[] digest
                    = md.digest(
                            texto.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            String resultado
                    = new BigInteger(
                            1,
                            digest
                    ).toString(16);

            int longitud
                    = "MD5".equalsIgnoreCase(
                            algoritmo
                    )
                    ? 32
                    : 64;

            while (
                    resultado.length()
                    < longitud
            ) {

                resultado
                        = "0" + resultado;
            }

            return resultado;

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(e);
        }
    }

    private boolean vacio(String valor) {

        return valor == null
                || valor.trim().isEmpty();
    }
}
