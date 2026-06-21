package ec.edu.gutierrez.landazuri.leiton.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class PermisoDAO extends BaseDAO {

    public Set<String> listarCodigosPorPerfil(String perfilCodigo) {
        Set<String> codigos = new HashSet<>();

        String sql = "SELECT XEOPC_CODIGO FROM xeoxp_opcper "
                + "WHERE XEPER_CODIGO = ? "
                + "AND XEOXP_FECRET IS NULL "
                + "AND COALESCE(XEOXP_VER, 'S') = 'S'";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, perfilCodigo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String codigo = rs.getString("XEOPC_CODIGO");

                    if (codigo != null && !codigo.trim().isEmpty()) {
                        codigos.add(codigo.trim());
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar permisos: " + e.getMessage());
        }

        return codigos;
    }

    public boolean guardarPermisos(String perfilCodigo, String[] opciones) {
        try (Connection con = obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                eliminarPermisos(con, perfilCodigo);

                if (opciones != null) {
                    for (String opcion : opciones) {
                        if (opcion != null && !opcion.trim().isEmpty()) {
                            insertarPermiso(con, perfilCodigo, opcion.trim());
                        }
                    }
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar permisos: " + e.getMessage());
        }

        return false;
    }

    private void eliminarPermisos(Connection con, String perfilCodigo) throws SQLException {
        String sql = "DELETE FROM xeoxp_opcper WHERE XEPER_CODIGO = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, perfilCodigo);
            ps.executeUpdate();
        }
    }

    private void insertarPermiso(Connection con, String perfilCodigo, String opcionCodigo) throws SQLException {
        String sql = "INSERT INTO xeoxp_opcper "
                + "(XEOPC_CODIGO, XEPER_CODIGO, XEOXP_FECASI, XEOXP_FECRET, XEOXP_VER, XEOXP_CREAR, XEOXP_EDITAR, XEOXP_ELIMINAR) "
                + "VALUES (?, ?, ?, NULL, 'S', 'S', 'S', 'S')";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, opcionCodigo);
            ps.setString(2, perfilCodigo);
            ps.setDate(3, new Date(System.currentTimeMillis()));
            ps.executeUpdate();
        }
    }

    public boolean tienePermiso(String perfilCodigo, String opcionCodigo) {
        if (perfilCodigo == null || perfilCodigo.trim().isEmpty()
                || opcionCodigo == null || opcionCodigo.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 "
                + "FROM xeoxp_opcper "
                + "WHERE XEPER_CODIGO = ? "
                + "AND XEOPC_CODIGO = ? "
                + "AND XEOXP_FECRET IS NULL "
                + "AND COALESCE(XEOXP_VER, 'S') = 'S' "
                + "LIMIT 1";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, perfilCodigo.trim());
            ps.setString(2, opcionCodigo.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error al validar permiso: " + e.getMessage());
        }

        return false;
    }
}