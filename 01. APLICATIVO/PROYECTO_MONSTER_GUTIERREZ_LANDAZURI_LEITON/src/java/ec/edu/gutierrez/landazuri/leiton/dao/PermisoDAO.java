package ec.edu.gutierrez.landazuri.leiton.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PermisoDAO extends BaseDAO {

    public Set<String> listarCodigosPorPerfil(String perfilCodigo) {
        Set<String> codigos = new HashSet<>();
        String sql = "SELECT XEOPC_CODIGO FROM xeoxp_opcper "
                + "WHERE XEPER_CODIGO = ? AND XEOXP_FECRET IS NULL";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, perfilCodigo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String codigo = rs.getString("XEOPC_CODIGO");

                    if (codigoPermitido(codigo)) {
                        codigos.add(codigo);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar permisos: " + e.getMessage());
        }

        return codigos;
    }

    public boolean guardarPermisos(String perfilCodigo, String[] opciones) {
        List<String> codigos = new ArrayList<>();

        if (opciones != null) {
            for (String opcion : opciones) {
                if (codigoPermitido(opcion)) {
                    codigos.add(opcion.trim());
                }
            }
        }

        try (Connection con = obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                eliminarPermisos(con, perfilCodigo);
                insertarPermisos(con, perfilCodigo, codigos);
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

    private void insertarPermisos(Connection con, String perfilCodigo, List<String> opciones)
            throws SQLException {

        String sql = "INSERT INTO xeoxp_opcper (XEOPC_CODIGO, XEPER_CODIGO, XEOXP_FECASI, XEOXP_FECRET) "
                + "VALUES (?, ?, ?, NULL)";

        for (String opcion : opciones) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, opcion);
                ps.setString(2, perfilCodigo);
                ps.setDate(3, new Date(System.currentTimeMillis()));
                ps.executeUpdate();
            }
        }
    }

    private boolean codigoPermitido(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        String limpio = codigo.trim().toUpperCase(Locale.ROOT);
        return "INI".equals(limpio)
                || "DEP".equals(limpio)
                || "CAR".equals(limpio)
                || "EMP".equals(limpio)
                || "USU".equals(limpio)
                || "PER".equals(limpio)
                || "OCP".equals(limpio)
                || "OPC".equals(limpio);
    }
}
