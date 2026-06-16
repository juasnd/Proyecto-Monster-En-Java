package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Opcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class OpcionDAO extends BaseDAO {

    public List<Opcion> listarOpciones() {
        List<Opcion> opciones = new ArrayList<>();
        String sql = "SELECT * FROM xeopc_opcion";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Opcion opcion = mapearOpcion(rs);

                if (!"I".equalsIgnoreCase(opcion.getEstadoCodigo()) && opcionPermitida(opcion)) {
                    opciones.add(opcion);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar opciones: " + e.getMessage());
        }

        opciones.sort(Comparator.comparingInt(Opcion::getOrden)
                .thenComparing(Opcion::getDescripcion, String.CASE_INSENSITIVE_ORDER));
        return opciones;
    }

    private boolean opcionPermitida(Opcion opcion) {
        if (opcion == null) {
            return false;
        }

        String codigo = opcion.getCodigo() == null
                ? ""
                : opcion.getCodigo().trim().toUpperCase(Locale.ROOT);

        if ("INI".equals(codigo)
                || "DEP".equals(codigo)
                || "CAR".equals(codigo)
                || "EMP".equals(codigo)
                || "USU".equals(codigo)
                || "PER".equals(codigo)
                || "OCP".equals(codigo)
                || "OPC".equals(codigo)) {
            return true;
        }

        return rutaPermitida(normalizarRuta(opcion.getUrl()));
    }

    private boolean rutaPermitida(String ruta) {
        return "pagprincipal.jsp".equals(ruta)
                || "departamentos.jsp".equals(ruta)
                || "departamentocontroller".equals(ruta)
                || "cargos.jsp".equals(ruta)
                || "cargocontroller".equals(ruta)
                || "empleados.jsp".equals(ruta)
                || "empleadocontroller".equals(ruta)
                || "usuarios.jsp".equals(ruta)
                || "usuariocontroller".equals(ruta)
                || "perfiles.jsp".equals(ruta)
                || "perfilcontroller".equals(ruta)
                || "permisos.jsp".equals(ruta)
                || "permisocontroller".equals(ruta);
    }

    private String normalizarRuta(String ruta) {
        String limpia = ruta == null ? "" : ruta.trim();
        int query = limpia.indexOf('?');

        if (query >= 0) {
            limpia = limpia.substring(0, query);
        }

        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }

        return limpia.toLowerCase(Locale.ROOT);
    }

    private Opcion mapearOpcion(ResultSet rs) throws SQLException {
        Opcion opcion = new Opcion();
        opcion.setCodigo(obtenerTexto(rs, "XEOPC_CODIGO", "OPCION_CODIGO", "CODIGO"));
        opcion.setSistemaCodigo(obtenerTexto(rs, "XESIS_CODIGO"));
        opcion.setDescripcion(obtenerTexto(rs, "XEOPC_DESCRI", "XEOPC_DESCRIPCION", "DESCRIPCION", "OPCION"));
        opcion.setUrl(obtenerTexto(rs, "XEOPC_URL", "URL", "RUTA"));
        opcion.setIcono(obtenerTexto(rs, "XEOPC_ICONO", "ICONO"));
        opcion.setCodigoPadre(obtenerTexto(rs, "XEOPC_PADRE", "XEOPC_CODIGO_PADRE", "PADRE"));
        opcion.setOrden(obtenerEntero(rs, "XEOPC_ORDEN", "ORDEN"));
        opcion.setEstadoCodigo(obtenerTexto(rs, "XEOPC_ESTADO", "XEEST_CODIGO", "ESTADO_CODIGO"));
        return opcion;
    }
}
