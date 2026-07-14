package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.UsuarioPerfil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AsignacionUsuarioPerfilDAO extends BaseDAO {

    public List<UsuarioPerfil> listarDisponibles(String perfilCodigo) {

        List<UsuarioPerfil> usuarios = new ArrayList<>();

        if (vacio(perfilCodigo)) {
            return usuarios;
        }

        String sql
                = "SELECT u.PEPER_CODIGO, "
                + "u.XEUSU_LOGIN, "
                + "p.PEPER_CEDULA, "
                + "p.PEPER_NOMBRE, "
                + "p.PEPER_APELLIDO, "
                + "COALESCE(( "
                + "    SELECT pf2.XEPER_DESCRI "
                + "    FROM xeuxp_usuper up2 "
                + "    INNER JOIN xeper_perfil pf2 "
                + "        ON pf2.XEPER_CODIGO = up2.XEPER_CODIGO "
                + "    WHERE up2.PEPER_CODIGO = u.PEPER_CODIGO "
                + "      AND up2.XEUSU_LOGIN = u.XEUSU_LOGIN "
                + "      AND up2.XEUXP_FECRET IS NULL "
                + "    ORDER BY up2.XEUXP_FECASI DESC "
                + "    LIMIT 1 "
                + "), 'Sin perfil') AS XEPER_DESCRI "
                + "FROM xeusu_usuari u "
                + "LEFT JOIN peper_person p "
                + "    ON p.PEPER_CODIGO = u.PEPER_CODIGO "
                + "WHERE NOT EXISTS ( "
                + "    SELECT 1 "
                + "    FROM xeuxp_usuper up "
                + "    WHERE up.PEPER_CODIGO = u.PEPER_CODIGO "
                + "      AND up.XEUSU_LOGIN = u.XEUSU_LOGIN "
                + "      AND up.XEPER_CODIGO = ? "
                + "      AND up.XEUXP_FECRET IS NULL "
                + ") "
                + "ORDER BY p.PEPER_APELLIDO, "
                + "p.PEPER_NOMBRE, "
                + "u.XEUSU_LOGIN";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, perfilCodigo.trim());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar usuarios disponibles: "
                    + e.getMessage()
            );
        }

        return usuarios;
    }

    public List<UsuarioPerfil> listarAsignados(String perfilCodigo) {

        List<UsuarioPerfil> usuarios = new ArrayList<>();

        if (vacio(perfilCodigo)) {
            return usuarios;
        }

        String sql
                = "SELECT u.PEPER_CODIGO, "
                + "u.XEUSU_LOGIN, "
                + "p.PEPER_CEDULA, "
                + "p.PEPER_NOMBRE, "
                + "p.PEPER_APELLIDO, "
                + "pf.XEPER_CODIGO, "
                + "pf.XEPER_DESCRI "
                + "FROM xeusu_usuari u "
                + "LEFT JOIN peper_person p "
                + "    ON p.PEPER_CODIGO = u.PEPER_CODIGO "
                + "INNER JOIN xeuxp_usuper up "
                + "    ON up.PEPER_CODIGO = u.PEPER_CODIGO "
                + "    AND up.XEUSU_LOGIN = u.XEUSU_LOGIN "
                + "    AND up.XEUXP_FECRET IS NULL "
                + "INNER JOIN xeper_perfil pf "
                + "    ON pf.XEPER_CODIGO = up.XEPER_CODIGO "
                + "WHERE up.XEPER_CODIGO = ? "
                + "ORDER BY p.PEPER_APELLIDO, "
                + "p.PEPER_NOMBRE, "
                + "u.XEUSU_LOGIN";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, perfilCodigo.trim());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al listar usuarios asignados: "
                    + e.getMessage()
            );
        }

        return usuarios;
    }

    public boolean asignarUsuarios(
            String perfilCodigo,
            String[] logins
    ) {

        if (
                vacio(perfilCodigo)
                || logins == null
                || logins.length == 0
        ) {
            return false;
        }

        try (Connection con = obtenerConexion()) {

            con.setAutoCommit(false);

            try {

                int procesados = 0;

                for (String login : limpiarLogins(logins)) {

                    if (
                            asignarUsuario(
                                    con,
                                    perfilCodigo.trim(),
                                    login
                            )
                    ) {
                        procesados++;
                    }
                }

                con.commit();

                return procesados > 0;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al asignar usuarios al perfil: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public boolean asignarTodos(String perfilCodigo) {

        if (vacio(perfilCodigo)) {
            return false;
        }

        try (Connection con = obtenerConexion()) {

            con.setAutoCommit(false);

            try {

                List<String> logins
                        = listarLoginsDisponibles(
                                con,
                                perfilCodigo.trim()
                        );

                for (String login : logins) {

                    asignarUsuario(
                            con,
                            perfilCodigo.trim(),
                            login
                    );
                }

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
                    "Error al asignar todos los usuarios: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public boolean retirarUsuarios(
            String perfilCodigo,
            String[] logins
    ) {

        if (
                vacio(perfilCodigo)
                || logins == null
                || logins.length == 0
        ) {
            return false;
        }

        String sql
                = "UPDATE xeuxp_usuper "
                + "SET XEUXP_FECRET = ? "
                + "WHERE XEPER_CODIGO = ? "
                + "AND XEUSU_LOGIN = ? "
                + "AND XEUXP_FECRET IS NULL";

        try (Connection con = obtenerConexion()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                int procesados = 0;

                Date fechaActual
                        = new Date(System.currentTimeMillis());

                for (String login : limpiarLogins(logins)) {

                    ps.setDate(1, fechaActual);
                    ps.setString(2, perfilCodigo.trim());
                    ps.setString(3, login);

                    procesados += ps.executeUpdate();
                }

                con.commit();

                return procesados > 0;

            } catch (SQLException e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al retirar usuarios del perfil: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public boolean retirarTodos(String perfilCodigo) {

        if (vacio(perfilCodigo)) {
            return false;
        }

        String sql
                = "UPDATE xeuxp_usuper "
                + "SET XEUXP_FECRET = ? "
                + "WHERE XEPER_CODIGO = ? "
                + "AND XEUXP_FECRET IS NULL";

        try (
                Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setDate(
                    1,
                    new Date(System.currentTimeMillis())
            );

            ps.setString(
                    2,
                    perfilCodigo.trim()
            );

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {

            System.out.println(
                    "Error al retirar todos los usuarios: "
                    + e.getMessage()
            );

            return false;
        }
    }

    private boolean asignarUsuario(
            Connection con,
            String perfilCodigo,
            String login
    ) throws SQLException {

        String personaCodigo
                = buscarPersonaCodigo(con, login);

        if (vacio(personaCodigo)) {
            return false;
        }

        /*
         * Cerramos cualquier perfil activo anterior.
         * Esto mantiene un solo perfil activo por usuario.
         */
        cerrarAsignacionesActivas(
                con,
                personaCodigo,
                login
        );

        /*
         * Primero se intenta reactivar una relación histórica
         * que ya exista entre el usuario y el perfil.
         */
        String reactivar
                = "UPDATE xeuxp_usuper "
                + "SET XEUXP_FECASI = ?, "
                + "XEUXP_FECRET = NULL "
                + "WHERE PEPER_CODIGO = ? "
                + "AND XEUSU_LOGIN = ? "
                + "AND XEPER_CODIGO = ?";

        try (
                PreparedStatement ps
                = con.prepareStatement(reactivar)
        ) {

            ps.setDate(
                    1,
                    new Date(System.currentTimeMillis())
            );

            ps.setString(
                    2,
                    personaCodigo
            );

            ps.setString(
                    3,
                    login
            );

            ps.setString(
                    4,
                    perfilCodigo
            );

            if (ps.executeUpdate() > 0) {
                return true;
            }
        }

        /*
         * Si la relación nunca existió, se crea.
         */
        String insertar
                = "INSERT INTO xeuxp_usuper "
                + "(PEPER_CODIGO, "
                + "XEUSU_LOGIN, "
                + "XEPER_CODIGO, "
                + "XEUXP_FECASI, "
                + "XEUXP_FECRET) "
                + "VALUES (?, ?, ?, ?, NULL)";

        try (
                PreparedStatement ps
                = con.prepareStatement(insertar)
        ) {

            ps.setString(
                    1,
                    personaCodigo
            );

            ps.setString(
                    2,
                    login
            );

            ps.setString(
                    3,
                    perfilCodigo
            );

            ps.setDate(
                    4,
                    new Date(System.currentTimeMillis())
            );

            return ps.executeUpdate() > 0;
        }
    }

    private void cerrarAsignacionesActivas(
            Connection con,
            String personaCodigo,
            String login
    ) throws SQLException {

        String sql
                = "UPDATE xeuxp_usuper "
                + "SET XEUXP_FECRET = ? "
                + "WHERE PEPER_CODIGO = ? "
                + "AND XEUSU_LOGIN = ? "
                + "AND XEUXP_FECRET IS NULL";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            ps.setDate(
                    1,
                    new Date(System.currentTimeMillis())
            );

            ps.setString(
                    2,
                    personaCodigo
            );

            ps.setString(
                    3,
                    login
            );

            ps.executeUpdate();
        }
    }

    private String buscarPersonaCodigo(
            Connection con,
            String login
    ) throws SQLException {

        String sql
                = "SELECT PEPER_CODIGO "
                + "FROM xeusu_usuari "
                + "WHERE XEUSU_LOGIN = ? "
                + "LIMIT 1";

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
                    return rs.getString("PEPER_CODIGO");
                }

                return "";
            }
        }
    }

    private List<String> listarLoginsDisponibles(
            Connection con,
            String perfilCodigo
    ) throws SQLException {

        List<String> logins = new ArrayList<>();

        String sql
                = "SELECT u.XEUSU_LOGIN "
                + "FROM xeusu_usuari u "
                + "WHERE NOT EXISTS ( "
                + "    SELECT 1 "
                + "    FROM xeuxp_usuper up "
                + "    WHERE up.PEPER_CODIGO = u.PEPER_CODIGO "
                + "      AND up.XEUSU_LOGIN = u.XEUSU_LOGIN "
                + "      AND up.XEPER_CODIGO = ? "
                + "      AND up.XEUXP_FECRET IS NULL "
                + ") "
                + "ORDER BY u.XEUSU_LOGIN";

        try (
                PreparedStatement ps
                = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    perfilCodigo
            );

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    logins.add(
                            rs.getString("XEUSU_LOGIN")
                    );
                }
            }
        }

        return logins;
    }

    private Set<String> limpiarLogins(String[] logins) {

        Set<String> resultado
                = new LinkedHashSet<>();

        for (String login : logins) {

            if (!vacio(login)) {

                resultado.add(
                        login.trim()
                );
            }
        }

        return resultado;
    }

    private UsuarioPerfil mapearUsuario(
            ResultSet rs
    ) throws SQLException {

        UsuarioPerfil usuario
                = new UsuarioPerfil();

        usuario.setPersonaCodigo(
                obtenerTexto(
                        rs,
                        "PEPER_CODIGO"
                )
        );

        usuario.setLogin(
                obtenerTexto(
                        rs,
                        "XEUSU_LOGIN"
                )
        );

        usuario.setCedula(
                obtenerTexto(
                        rs,
                        "PEPER_CEDULA"
                )
        );

        usuario.setNombres(
                obtenerTexto(
                        rs,
                        "PEPER_NOMBRE"
                )
        );

        usuario.setApellidos(
                obtenerTexto(
                        rs,
                        "PEPER_APELLIDO"
                )
        );

        usuario.setPerfilCodigo(
                obtenerTexto(
                        rs,
                        "XEPER_CODIGO"
                )
        );

        usuario.setPerfilDescripcion(
                obtenerTexto(
                        rs,
                        "XEPER_DESCRI"
                )
        );

        return usuario;
    }

    private boolean vacio(String valor) {

        return valor == null
                || valor.trim().isEmpty();
    }
}