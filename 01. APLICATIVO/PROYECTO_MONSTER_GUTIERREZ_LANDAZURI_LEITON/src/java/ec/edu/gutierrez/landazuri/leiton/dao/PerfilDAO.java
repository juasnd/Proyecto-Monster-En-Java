package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Perfil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerfilDAO extends BaseDAO {

    public List<Perfil> listarPerfiles() {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT p.XEPER_CODIGO, p.XEPER_DESCRI, "
                + "p.XEPER_ESTADO AS XEEST_CODIGO, e.XEEST_DESCRI "
                + "FROM xeper_perfil p "
                + "LEFT JOIN xeest_estado e ON p.XEPER_ESTADO = e.XEEST_CODIGO "
                + "ORDER BY p.XEPER_DESCRI";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                perfiles.add(mapearPerfil(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar perfiles: " + e.getMessage());
        }

        return perfiles;
    }

    public Perfil buscarPorCodigo(String codigo) {
        String sql = "SELECT p.XEPER_CODIGO, p.XEPER_DESCRI, "
                + "p.XEPER_ESTADO AS XEEST_CODIGO, e.XEEST_DESCRI "
                + "FROM xeper_perfil p "
                + "LEFT JOIN xeest_estado e ON p.XEPER_ESTADO = e.XEEST_CODIGO "
                + "WHERE p.XEPER_CODIGO = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPerfil(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar perfil: " + e.getMessage());
        }

        return null;
    }

    public String buscarCodigoPorNombre(Connection con, String descripcion) throws SQLException {
        String sql = "SELECT XEPER_CODIGO FROM xeper_perfil "
                + "WHERE UPPER(XEPER_DESCRI) = UPPER(?) OR UPPER(XEPER_CODIGO) = UPPER(?) "
                + "ORDER BY XEPER_CODIGO LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ps.setString(2, descripcion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("XEPER_CODIGO");
                }
            }
        }

        return "";
    }

    public boolean guardar(Perfil perfil) {
        String sql = "INSERT INTO xeper_perfil (XEPER_CODIGO, XEPER_DESCRI, XEPER_ESTADO) "
                + "VALUES (?, ?, ?)";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, perfil.getCodigo());
            ps.setString(2, perfil.getDescripcion());
            ps.setString(3, perfil.getEstadoCodigo() == null ? "A" : perfil.getEstadoCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar perfil: " + e.getMessage());
        }

        return false;
    }

    public boolean actualizar(Perfil perfil) {
        String sql = "UPDATE xeper_perfil SET XEPER_DESCRI = ?, XEPER_ESTADO = ? "
                + "WHERE XEPER_CODIGO = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, perfil.getDescripcion());
            ps.setString(2, perfil.getEstadoCodigo() == null ? "A" : perfil.getEstadoCodigo());
            ps.setString(3, perfil.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar perfil: " + e.getMessage());
        }

        return false;
    }

    public boolean cambiarEstado(String codigo, String estado) {
        String sql = "UPDATE xeper_perfil SET XEPER_ESTADO = ? WHERE XEPER_CODIGO = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado de perfil: " + e.getMessage());
        }

        return false;
    }

    private Perfil mapearPerfil(ResultSet rs) throws SQLException {
        Perfil perfil = new Perfil();
        perfil.setCodigo(obtenerTexto(rs, "XEPER_CODIGO"));
        perfil.setDescripcion(obtenerTexto(rs, "XEPER_DESCRI", "PERFIL", "DESCRIPCION"));
        perfil.setEstadoCodigo(obtenerTexto(rs, "XEEST_CODIGO"));
        perfil.setEstadoDescripcion(obtenerTexto(rs, "XEEST_DESCRI", "ESTADO"));
        return perfil;
    }
}
