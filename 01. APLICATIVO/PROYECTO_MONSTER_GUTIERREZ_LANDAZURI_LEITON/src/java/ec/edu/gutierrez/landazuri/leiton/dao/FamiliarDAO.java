package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Familiar;
import ec.edu.gutierrez.landazuri.leiton.modelo.Parentesco;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class FamiliarDAO extends BaseDAO {

    public List<Familiar> listarPorPersona(String peperCodigo) {
        List<Familiar> familiares = new ArrayList<>();
        String sql = "SELECT f.PEFAM_CODIGO, f.PEPER_CODIGO, f.PEPAR_CODIGO, "
                + "p.PEPAR_DESCRI, f.PEFAM_NOMBRE, f.PEFAM_APELLIDO, "
                + "f.PEFAM_FECHANACI, f.PEFAM_TELEFONO, f.PEFAM_CARGA, f.PEFAM_OBSER "
                + "FROM pefam_famil f "
                + "INNER JOIN pepar_parent p ON f.PEPAR_CODIGO = p.PEPAR_CODIGO "
                + "WHERE f.PEPER_CODIGO = ? "
                + "ORDER BY f.PEFAM_APELLIDO, f.PEFAM_NOMBRE, f.PEFAM_CODIGO";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, peperCodigo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    familiares.add(mapearFamiliar(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar familiares: " + e.getMessage());
        }

        return familiares;
    }

    public List<Parentesco> listarParentescos() {
        List<Parentesco> parentescos = new ArrayList<>();
        String sql = "SELECT PEPAR_CODIGO, PEPAR_DESCRI "
                + "FROM pepar_parent ORDER BY PEPAR_DESCRI";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                parentescos.add(new Parentesco(
                        rs.getString("PEPAR_CODIGO"),
                        rs.getString("PEPAR_DESCRI")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar parentescos: " + e.getMessage());
        }

        return parentescos;
    }

    public boolean insertar(Connection con, Familiar familiar) throws SQLException {
        String sql = "INSERT INTO pefam_famil "
                + "(PEFAM_CODIGO, PEPER_CODIGO, PEPAR_CODIGO, PEFAM_NOMBRE, "
                + "PEFAM_APELLIDO, PEFAM_FECHANACI, PEFAM_TELEFONO, PEFAM_CARGA, PEFAM_OBSER) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, familiar.getCodigo());
            ps.setString(2, familiar.getCodigoPersona());
            ps.setString(3, familiar.getCodigoParentesco());
            ps.setString(4, familiar.getNombre());
            ps.setString(5, familiar.getApellido());
            ps.setDate(6, Date.valueOf(familiar.getFechaNacimiento()));
            setNullableString(ps, 7, familiar.getTelefono());
            ps.setString(8, familiar.getCargaFamiliar());
            setNullableString(ps, 9, familiar.getObservacion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarPorPersona(Connection con, String peperCodigo) throws SQLException {
        String sql = "DELETE FROM pefam_famil WHERE PEPER_CODIGO = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, peperCodigo);
            ps.executeUpdate();
            return true;
        }
    }

    public void reemplazarPorPersona(Connection con, String peperCodigo, List<Familiar> familiares)
            throws SQLException {

        eliminarPorPersona(con, peperCodigo);

        if (familiares == null) {
            return;
        }

        for (Familiar familiar : familiares) {
            familiar.setCodigoPersona(peperCodigo);
            familiar.setCodigo(generarCodigoFamiliar(con));
            insertar(con, familiar);
        }
    }

    public String generarCodigoFamiliar() {
        try (Connection con = obtenerConexion()) {
            return generarCodigoFamiliar(con);
        } catch (SQLException e) {
            System.out.println("Error al generar codigo familiar: " + e.getMessage());
            return "FAM0000001";
        }
    }

    public String generarCodigoFamiliar(Connection con) throws SQLException {
        int numero = obtenerMayorSecuencia(con) + 1;
        String codigo;

        do {
            codigo = String.format("FAM%07d", numero);
            numero++;
        } while (existeCodigo(con, codigo));

        return codigo;
    }

    private int obtenerMayorSecuencia(Connection con) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(PEFAM_CODIGO, 4) AS UNSIGNED)) AS maximo "
                + "FROM pefam_famil WHERE PEFAM_CODIGO LIKE 'FAM%'";

        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("maximo");
            }
        }

        return 0;
    }

    private boolean existeCodigo(Connection con, String codigo) throws SQLException {
        String sql = "SELECT PEFAM_CODIGO FROM pefam_famil WHERE PEFAM_CODIGO = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Familiar mapearFamiliar(ResultSet rs) throws SQLException {
        Familiar familiar = new Familiar();
        familiar.setCodigo(rs.getString("PEFAM_CODIGO"));
        familiar.setCodigoPersona(rs.getString("PEPER_CODIGO"));
        familiar.setCodigoParentesco(rs.getString("PEPAR_CODIGO"));
        familiar.setDescripcionParentesco(rs.getString("PEPAR_DESCRI"));
        familiar.setNombre(rs.getString("PEFAM_NOMBRE"));
        familiar.setApellido(rs.getString("PEFAM_APELLIDO"));

        Date fecha = rs.getDate("PEFAM_FECHANACI");
        familiar.setFechaNacimiento(fecha != null ? fecha.toString() : "");

        familiar.setTelefono(rs.getString("PEFAM_TELEFONO"));
        familiar.setCargaFamiliar(rs.getString("PEFAM_CARGA"));
        familiar.setObservacion(rs.getString("PEFAM_OBSER"));
        return familiar;
    }

    private void setNullableString(PreparedStatement ps, int indice, String valor) throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            ps.setNull(indice, Types.VARCHAR);
        } else {
            ps.setString(indice, valor.trim());
        }
    }
}
