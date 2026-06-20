package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Opcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OpcionDAO extends BaseDAO {

    public List<Opcion> listarOpciones() {
        List<Opcion> opciones = new ArrayList<>();

        String sql = "SELECT * FROM xeopc_opcion "
                + "WHERE COALESCE(XEOPC_ESTADO, 'A') = 'A' "
                + "ORDER BY XEOPC_NIVEL, XEOPC_PADRE, XEOPC_ORDEN, XEOPC_DESCRI";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                opciones.add(mapearOpcion(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar opciones: " + e.getMessage());
        }

        opciones.sort(Comparator.comparingInt(Opcion::getNivel)
                .thenComparing(Opcion::getCodigoPadre, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))
                .thenComparingInt(Opcion::getOrden)
                .thenComparing(Opcion::getDescripcion, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

        return opciones;
    }

    private Opcion mapearOpcion(ResultSet rs) throws SQLException {
        Opcion opcion = new Opcion();

        opcion.setCodigo(obtenerTexto(rs, "XEOPC_CODIGO", "OPCION_CODIGO", "CODIGO"));
        opcion.setSistemaCodigo(obtenerTexto(rs, "XESIS_CODIGO"));
        opcion.setDescripcion(obtenerTexto(rs, "XEOPC_DESCRI", "XEOPC_DESCRIPCION", "DESCRIPCION", "OPCION"));
        opcion.setUrl(obtenerTexto(rs, "XEOPC_URL", "URL", "RUTA"));
        opcion.setIcono(obtenerTexto(rs, "XEOPC_ICONO", "ICONO"));
        opcion.setCodigoPadre(obtenerTexto(rs, "XEOPC_PADRE", "XEOPC_CODIGO_PADRE", "PADRE"));
        opcion.setTipo(obtenerTexto(rs, "XEOPC_TIPO", "TIPO"));
        opcion.setNivel(obtenerEntero(rs, "XEOPC_NIVEL", "NIVEL"));
        opcion.setOrden(obtenerEntero(rs, "XEOPC_ORDEN", "ORDEN"));
        opcion.setEstadoCodigo(obtenerTexto(rs, "XEOPC_ESTADO", "XEEST_CODIGO", "ESTADO_CODIGO"));

        return opcion;
    }
}