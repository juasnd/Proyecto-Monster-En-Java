package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Persona;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PersonaDAO extends BaseDAO {

    public Persona buscarPorCodigo(String codigo) {
        String sql = "SELECT p.PEPER_CODIGO, p.PESEX_CODIGO, s.PESEX_DESCRI, "
                + "p.PEESC_CODIGO, e.PEESC_DESCRI, p.PEPER_NOMBRE, p.PEPER_APELLIDO, "
                + "p.PEPER_CEDULA, p.PEPER_FECHANACI, p.PEPER_CARGAS, "
                + "p.PEPER_DIRECCION, p.PEPER_CELULAR, p.PEPER_TELDOM, p.PEPER_EMAIL, "
                + "p.PEPER_FOTO "
                + "FROM peper_person p "
                + "INNER JOIN pesex_sexo s ON p.PESEX_CODIGO = s.PESEX_CODIGO "
                + "LEFT JOIN peesc_estciv e ON p.PEESC_CODIGO = e.PEESC_CODIGO "
                + "WHERE p.PEPER_CODIGO = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPersona(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar persona: " + e.getMessage());
        }

        return null;
    }

    public boolean existe(String codigo) {
        String sql = "SELECT PEPER_CODIGO FROM peper_person WHERE PEPER_CODIGO = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar persona: " + e.getMessage());
        }

        return false;
    }

    public boolean insertar(Connection con, Persona persona) throws SQLException {
        String sql = "INSERT INTO peper_person "
                + "(PEPER_CODIGO, PESEX_CODIGO, PEESC_CODIGO, PEPER_NOMBRE, PEPER_APELLIDO, "
                + "PEPER_CEDULA, PEPER_FECHANACI, PEPER_CARGAS, PEPER_DIRECCION, "
                + "PEPER_CELULAR, PEPER_TELDOM, PEPER_EMAIL, PEPER_FOTO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            cargarParametrosPersona(ps, persona);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Connection con, Persona persona) throws SQLException {
        String sql = "UPDATE peper_person SET "
                + "PESEX_CODIGO = ?, PEESC_CODIGO = ?, PEPER_NOMBRE = ?, "
                + "PEPER_APELLIDO = ?, PEPER_CEDULA = ?, PEPER_FECHANACI = ?, "
                + "PEPER_CARGAS = ?, PEPER_DIRECCION = ?, PEPER_CELULAR = ?, "
                + "PEPER_TELDOM = ?, PEPER_EMAIL = ?, PEPER_FOTO = ? "
                + "WHERE PEPER_CODIGO = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, persona.getPesexCodigo());
            setNullableString(ps, 2, persona.getPeescCodigo());
            ps.setString(3, persona.getNombres());
            ps.setString(4, persona.getApellidos());
            setNullableString(ps, 5, persona.getCedula());
            ps.setDate(6, Date.valueOf(persona.getFechaNacimiento()));
            ps.setInt(7, persona.getCargasFamiliares());
            ps.setString(8, persona.getDireccion());
            setNullableString(ps, 9, persona.getCelular());
            setNullableString(ps, 10, persona.getTelefonoDomicilio());
            ps.setString(11, persona.getEmail());
            setNullableString(ps, 12, persona.getFoto());
            ps.setString(13, persona.getPeperCodigo());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(Connection con, String codigoPersona) throws SQLException {
        String sql = "DELETE FROM peper_person WHERE PEPER_CODIGO = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoPersona);
            return ps.executeUpdate() > 0;
        }
    }

    private void cargarParametrosPersona(PreparedStatement ps, Persona persona) throws SQLException {
        ps.setString(1, persona.getPeperCodigo());
        ps.setString(2, persona.getPesexCodigo());
        setNullableString(ps, 3, persona.getPeescCodigo());
        ps.setString(4, persona.getNombres());
        ps.setString(5, persona.getApellidos());
        setNullableString(ps, 6, persona.getCedula());
        ps.setDate(7, Date.valueOf(persona.getFechaNacimiento()));
        ps.setInt(8, persona.getCargasFamiliares());
        ps.setString(9, persona.getDireccion());
        setNullableString(ps, 10, persona.getCelular());
        setNullableString(ps, 11, persona.getTelefonoDomicilio());
        ps.setString(12, persona.getEmail());
        setNullableString(ps, 13, persona.getFoto());
    }

    public Persona mapearPersona(ResultSet rs) throws SQLException {
        Persona persona = new Persona();
        persona.setPeperCodigo(rs.getString("PEPER_CODIGO"));
        persona.setPesexCodigo(rs.getString("PESEX_CODIGO"));
        persona.setSexoDescripcion(rs.getString("PESEX_DESCRI"));
        persona.setPeescCodigo(rs.getString("PEESC_CODIGO"));
        persona.setEstadoCivilDescripcion(rs.getString("PEESC_DESCRI"));
        persona.setNombres(rs.getString("PEPER_NOMBRE"));
        persona.setApellidos(rs.getString("PEPER_APELLIDO"));
        persona.setCedula(rs.getString("PEPER_CEDULA"));

        Date fecha = rs.getDate("PEPER_FECHANACI");
        persona.setFechaNacimiento(fecha != null ? fecha.toString() : "");

        persona.setCargasFamiliares(rs.getInt("PEPER_CARGAS"));
        persona.setDireccion(rs.getString("PEPER_DIRECCION"));
        persona.setCelular(rs.getString("PEPER_CELULAR"));
        persona.setTelefonoDomicilio(rs.getString("PEPER_TELDOM"));
        persona.setEmail(rs.getString("PEPER_EMAIL"));
        persona.setFoto(rs.getString("PEPER_FOTO"));
        return persona;
    }

    private void setNullableString(PreparedStatement ps, int indice, String valor) throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            ps.setNull(indice, Types.VARCHAR);
        } else {
            ps.setString(indice, valor.trim());
        }
    }
}
