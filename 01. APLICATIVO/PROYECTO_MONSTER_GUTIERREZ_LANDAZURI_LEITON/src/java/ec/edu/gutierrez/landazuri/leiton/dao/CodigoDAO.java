package ec.edu.gutierrez.landazuri.leiton.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CodigoDAO extends BaseDAO {

    public String generarCodigo(String campoCodigo) throws SQLException {
        try (Connection con = obtenerConexion()) {
            return generarCodigo(con, campoCodigo);
        }
    }

    public String generarCodigo(Connection con, String campoCodigo) throws SQLException {
        SQLException error = null;

        try {
            return llamarProcedimientoTablaCampoConSalida(con, campoCodigo);
        } catch (SQLException e) {
            error = e;
        }

        try {
            return llamarProcedimientoConSalida(con, campoCodigo);
        } catch (SQLException e) {
            error = e;
        }

        try {
            return llamarProcedimientoConResultado(con, campoCodigo);
        } catch (SQLException e) {
            error = e;
        }

        try {
            return llamarFuncion(con, campoCodigo);
        } catch (SQLException e) {
            error = e;
        }

        String fallback = generarCodigoSecuencial(con, campoCodigo);

        if (fallback != null && !fallback.trim().isEmpty()) {
            return fallback;
        }

        throw error == null ? new SQLException("No se pudo generar codigo.") : error;
    }

    private String llamarProcedimientoTablaCampoConSalida(Connection con, String campoCodigo) throws SQLException {
        String tabla = tablaPorCampo(campoCodigo);

        if (tabla == null) {
            throw new SQLException("No hay tabla configurada para " + campoCodigo + ".");
        }

        try (CallableStatement cs = con.prepareCall("{CALL sp_generar_codigo(?, ?, ?)}")) {
            cs.setString(1, tabla);
            cs.setString(2, campoCodigo);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.execute();
            return limpiar(cs.getString(3));
        }
    }

    private String llamarProcedimientoConSalida(Connection con, String campoCodigo) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{CALL sp_generar_codigo(?, ?)}")) {
            cs.setString(1, campoCodigo);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            return limpiar(cs.getString(2));
        }
    }

    private String llamarProcedimientoConResultado(Connection con, String campoCodigo) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{CALL sp_generar_codigo(?)}")) {
            cs.setString(1, campoCodigo);

            boolean tieneResultado = cs.execute();

            if (tieneResultado) {
                try (ResultSet rs = cs.getResultSet()) {
                    if (rs.next()) {
                        return limpiar(rs.getString(1));
                    }
                }
            }
        }

        throw new SQLException("El procedimiento no retorno codigo.");
    }

    private String llamarFuncion(Connection con, String campoCodigo) throws SQLException {
        String sql = "SELECT sp_generar_codigo(?) AS codigo";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, campoCodigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return limpiar(rs.getString("codigo"));
                }
            }
        }

        throw new SQLException("La funcion no retorno codigo.");
    }

    private String generarCodigoSecuencial(Connection con, String campoCodigo) {
        String tabla = tablaPorCampo(campoCodigo);
        String prefijo = prefijoPorCampo(campoCodigo);

        if (tabla == null || prefijo == null) {
            return "";
        }

        String sql = "SELECT MAX(CAST(SUBSTRING(" + campoCodigo + ", ?) AS UNSIGNED)) AS maximo "
                + "FROM " + tabla + " WHERE " + campoCodigo + " LIKE ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, prefijo.length() + 1);
            ps.setString(2, prefijo + "%");

            try (ResultSet rs = ps.executeQuery()) {
                int numero = 1;

                if (rs.next()) {
                    numero = rs.getInt("maximo") + 1;
                }

                return prefijo + String.format("%07d", numero);
            }
        } catch (SQLException e) {
            System.out.println("No se pudo usar codigo secuencial para " + campoCodigo + ": " + e.getMessage());
            return "";
        }
    }

    private String tablaPorCampo(String campoCodigo) {
        if ("PEPER_CODIGO".equalsIgnoreCase(campoCodigo)) {
            return "peper_person";
        }

        if ("PEEMP_CODIGO".equalsIgnoreCase(campoCodigo)) {
            return "peemp_emplea";
        }

        if ("PEFAM_CODIGO".equalsIgnoreCase(campoCodigo)) {
            return "pefam_famil";
        }

        if ("PEFOR_CODIGO".equalsIgnoreCase(campoCodigo)) {
            return "pefor_formac";
        }

        return null;
    }

    private String prefijoPorCampo(String campoCodigo) {
        if ("PEPER_CODIGO".equalsIgnoreCase(campoCodigo)) {
            return "PER";
        }

        if ("PEEMP_CODIGO".equalsIgnoreCase(campoCodigo)) {
            return "EMP";
        }

        if ("PEFAM_CODIGO".equalsIgnoreCase(campoCodigo)) {
            return "FAM";
        }

        if ("PEFOR_CODIGO".equalsIgnoreCase(campoCodigo)) {
            return "FOR";
        }

        return null;
    }

    private String limpiar(String codigo) throws SQLException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new SQLException("El codigo generado esta vacio.");
        }

        return codigo.trim().toUpperCase();
    }
}
