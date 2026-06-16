package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Formacion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class FormacionDAO extends BaseDAO {

    private final CodigoDAO codigoDAO = new CodigoDAO();

    public List<Formacion> listarPorEmpleado(String codigoEmpleado) {
        List<Formacion> formaciones = new ArrayList<>();
        String sql = "SELECT PEFOR_CODIGO, PEEMP_CODIGO, PEFOR_NIVEL, PEFOR_TITULO, "
                + "PEFOR_INSTITUCION, PEFOR_FECINI, PEFOR_FECFIN, PEFOR_OBSERVA "
                + "FROM pefor_formac "
                + "WHERE PEEMP_CODIGO = ? "
                + "ORDER BY PEFOR_FECINI DESC, PEFOR_CODIGO";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    formaciones.add(mapearFormacion(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar formacion academica: " + e.getMessage());
        }

        return formaciones;
    }

    public void reemplazarPorEmpleado(Connection con, String codigoEmpleado, List<Formacion> formaciones)
            throws SQLException {

        eliminarPorEmpleado(con, codigoEmpleado);

        if (formaciones == null) {
            return;
        }

        for (Formacion formacion : formaciones) {
            if (formacionVacia(formacion)) {
                continue;
            }

            formacion.setCodigoEmpleado(codigoEmpleado);
            formacion.setCodigo(generarCodigoFormacion(con));
            insertar(con, formacion);
        }
    }

    public boolean eliminarPorEmpleado(Connection con, String codigoEmpleado) throws SQLException {
        String sql = "DELETE FROM pefor_formac WHERE PEEMP_CODIGO = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoEmpleado);
            ps.executeUpdate();
            return true;
        }
    }

    private boolean insertar(Connection con, Formacion formacion) throws SQLException {
        String sql = "INSERT INTO pefor_formac "
                + "(PEFOR_CODIGO, PEEMP_CODIGO, PEFOR_NIVEL, PEFOR_TITULO, "
                + "PEFOR_INSTITUCION, PEFOR_FECINI, PEFOR_FECFIN, PEFOR_OBSERVA) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, formacion.getCodigo());
            ps.setString(2, formacion.getCodigoEmpleado());
            ps.setString(3, formacion.getNivel());
            ps.setString(4, formacion.getTitulo());
            ps.setString(5, formacion.getInstitucion());
            ps.setDate(6, Date.valueOf(formacion.getFechaInicio()));
            setNullableDate(ps, 7, formacion.getFechaFin());
            setNullableString(ps, 8, formacion.getObservacion());
            return ps.executeUpdate() > 0;
        }
    }

    private String generarCodigoFormacion(Connection con) throws SQLException {
        try {
            return codigoDAO.generarCodigo(con, "PEFOR_CODIGO");
        } catch (SQLException e) {
            System.out.println("No se pudo generar PEFOR_CODIGO con sp_generar_codigo: " + e.getMessage());
        }

        int numero = obtenerMayorSecuencia(con) + 1;
        String codigo;

        do {
            codigo = String.format("FOR%07d", numero);
            numero++;
        } while (existeCodigo(con, codigo));

        return codigo;
    }

    private int obtenerMayorSecuencia(Connection con) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(PEFOR_CODIGO, 4) AS UNSIGNED)) AS maximo "
                + "FROM pefor_formac WHERE PEFOR_CODIGO LIKE 'FOR%'";

        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("maximo");
            }
        }

        return 0;
    }

    private boolean existeCodigo(Connection con, String codigo) throws SQLException {
        String sql = "SELECT PEFOR_CODIGO FROM pefor_formac WHERE PEFOR_CODIGO = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Formacion mapearFormacion(ResultSet rs) throws SQLException {
        Formacion formacion = new Formacion();
        formacion.setCodigo(rs.getString("PEFOR_CODIGO"));
        formacion.setCodigoEmpleado(rs.getString("PEEMP_CODIGO"));
        formacion.setNivel(rs.getString("PEFOR_NIVEL"));
        formacion.setTitulo(rs.getString("PEFOR_TITULO"));
        formacion.setInstitucion(rs.getString("PEFOR_INSTITUCION"));

        Date fechaInicio = rs.getDate("PEFOR_FECINI");
        formacion.setFechaInicio(fechaInicio != null ? fechaInicio.toString() : "");

        Date fechaFin = rs.getDate("PEFOR_FECFIN");
        formacion.setFechaFin(fechaFin != null ? fechaFin.toString() : "");

        formacion.setObservacion(rs.getString("PEFOR_OBSERVA"));
        return formacion;
    }

    private boolean formacionVacia(Formacion formacion) {
        return formacion == null
                || (vacio(formacion.getNivel())
                && vacio(formacion.getTitulo())
                && vacio(formacion.getInstitucion())
                && vacio(formacion.getFechaInicio())
                && vacio(formacion.getFechaFin())
                && vacio(formacion.getObservacion()));
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private void setNullableString(PreparedStatement ps, int indice, String valor) throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            ps.setNull(indice, Types.VARCHAR);
        } else {
            ps.setString(indice, valor.trim());
        }
    }

    private void setNullableDate(PreparedStatement ps, int indice, String valor) throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            ps.setNull(indice, Types.DATE);
        } else {
            ps.setDate(indice, Date.valueOf(valor));
        }
    }
}
