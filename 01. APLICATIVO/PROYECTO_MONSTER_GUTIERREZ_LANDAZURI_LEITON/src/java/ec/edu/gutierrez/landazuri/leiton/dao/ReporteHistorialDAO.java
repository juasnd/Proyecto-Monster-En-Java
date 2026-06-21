package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.ReporteHistorial;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ReporteHistorialDAO extends BaseDAO {

    private static final String[] TABLAS_CANDIDATAS = {
        "xerep_reporte_historial",
        "xereph_reportes_historial",
        "xerep_historial",
        "xerep_reportes",
        "reporte_historial",
        "reportes_historial",
        "historial_reportes"
    };

    private static final String[] COLUMNAS_ID = {
        "XERHI_CODIGO", "REPH_CODIGO", "REHIS_CODIGO",
        "REPORTE_HISTORIAL_CODIGO", "HISTORIAL_CODIGO"
    };

    private static final String[] COLUMNAS_FECHA = {
        "XERHI_FECHA", "XEREP_FECHA", "REPH_FECHA", "FECHA_GENERACION",
        "FECHA", "CREATED_AT", "XEREP_FECGEN"
    };

    public List<ReporteHistorial> listarHistorial() {
        List<ReporteHistorial> historial = new ArrayList<>();

        try (Connection con = obtenerConexion()) {
            String tabla = resolverTabla(con);

            if (tabla == null) {
                return historial;
            }

            Set<String> columnas = obtenerColumnas(con, tabla);
            String columnaFecha = primeraColumna(columnas, COLUMNAS_FECHA);
            StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tabla);

            if (columnaFecha != null) {
                sql.append(" ORDER BY ").append(columnaFecha).append(" DESC");
            }

            try (PreparedStatement ps = con.prepareStatement(sql.toString());
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    historial.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("No se pudo listar historial de reportes: " + e.getMessage());
        }

        return historial;
    }

    public ReporteHistorial buscarPorCodigo(String codigo) {
        if (vacio(codigo)) {
            return null;
        }

        try (Connection con = obtenerConexion()) {
            String tabla = resolverTabla(con);

            if (tabla == null) {
                return null;
            }

            Set<String> columnas = obtenerColumnas(con, tabla);
            String columnaId = primeraColumna(columnas, COLUMNAS_ID);

            if (columnaId == null) {
                return null;
            }

            String sql = "SELECT * FROM " + tabla + " WHERE " + columnaId + " = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, codigo);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("No se pudo buscar historial de reporte: " + e.getMessage());
        }

        return null;
    }

    public boolean registrar(ReporteHistorial reporte) {
        if (reporte == null) {
            return false;
        }

        try (Connection con = obtenerConexion()) {
            String tabla = resolverTabla(con);

            if (tabla == null) {
                System.out.println("No existe tabla de historial de reportes disponible.");
                return false;
            }

            Set<String> disponibles = obtenerColumnas(con, tabla);
            List<String> columnas = new ArrayList<>();
            List<Object> valores = new ArrayList<>();

            agregarPrimera(disponibles, columnas, valores, generarCodigo(), COLUMNAS_ID);
            agregarPrimera(disponibles, columnas, valores, Timestamp.valueOf(LocalDateTime.now()), COLUMNAS_FECHA);
            agregarPrimera(disponibles, columnas, valores, reporte.getUsuario(),
                    "XEUSU_LOGIN", "USUARIO_LOGIN", "USUARIO", "XEREP_USUARIO", "REPH_USUARIO");
            agregarPrimera(disponibles, columnas, valores, reporte.getPerfil(),
                    "XEPER_CODIGO", "PERFIL_CODIGO", "PERFIL", "XEREP_PERFIL", "REPH_PERFIL");
            agregarPrimera(disponibles, columnas, valores, reporte.getCodigoReporte(),
                    "XEOPC_CODIGO", "CODIGO_REPORTE", "REPORTE_CODIGO", "XEREP_CODIGO_REPORTE", "REPH_REPORTE");
            agregarPrimera(disponibles, columnas, valores, reporte.getModulo(),
                    "MODULO", "REPORTE_MODULO", "XEREP_MODULO", "REPH_MODULO");
            agregarPrimera(disponibles, columnas, valores, reporte.getTipoReporte(),
                    "TIPO_REPORTE", "REPORTE_TIPO", "XEREP_TIPO_REPORTE", "XEREP_TIPO", "REPH_TIPO");
            agregarPrimera(disponibles, columnas, valores, reporte.getFormato(),
                    "FORMATO", "REPORTE_FORMATO", "XEREP_FORMATO", "REPH_FORMATO");
            agregarPrimera(disponibles, columnas, valores, reporte.getTotalRegistros(),
                    "TOTAL_REGISTROS", "REPORTE_TOTAL_REGISTROS", "XEREP_TOTAL_REGISTROS", "TOTAL", "REGISTROS");
            agregarPrimera(disponibles, columnas, valores, reporte.getFiltros(),
                    "FILTROS", "FILTROS_USADOS", "REPORTE_FILTROS", "XEREP_FILTROS", "REPH_FILTROS");

            if (columnas.isEmpty()) {
                return false;
            }

            String sql = construirInsert(tabla, columnas);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 0; i < valores.size(); i++) {
                    ps.setObject(i + 1, valores.get(i));
                }

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("No se pudo registrar historial de reporte: " + e.getMessage());
        }

        return false;
    }

    public boolean eliminar(String codigo) {
        if (vacio(codigo)) {
            return false;
        }

        try (Connection con = obtenerConexion()) {
            String tabla = resolverTabla(con);

            if (tabla == null) {
                return false;
            }

            Set<String> columnas = obtenerColumnas(con, tabla);
            String columnaId = primeraColumna(columnas, COLUMNAS_ID);

            if (columnaId == null) {
                return false;
            }

            String sql = "DELETE FROM " + tabla + " WHERE " + columnaId + " = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, codigo);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("No se pudo eliminar historial de reporte: " + e.getMessage());
        }

        return false;
    }

    private ReporteHistorial mapear(ResultSet rs) throws SQLException {
        ReporteHistorial reporte = new ReporteHistorial();
        reporte.setCodigo(obtenerTexto(rs, "XERHI_CODIGO", "REPH_CODIGO", "REHIS_CODIGO",
                "REPORTE_HISTORIAL_CODIGO", "HISTORIAL_CODIGO"));
        reporte.setFecha(formatearFecha(obtenerTexto(rs, "XERHI_FECHA", "XEREP_FECHA", "REPH_FECHA",
                "FECHA_GENERACION", "FECHA", "CREATED_AT", "XEREP_FECGEN")));
        reporte.setUsuario(obtenerTexto(rs, "XEUSU_LOGIN", "USUARIO_LOGIN", "USUARIO",
                "XEREP_USUARIO", "REPH_USUARIO"));
        reporte.setPerfil(obtenerTexto(rs, "XEPER_CODIGO", "PERFIL_CODIGO", "PERFIL",
                "XEREP_PERFIL", "REPH_PERFIL"));
        reporte.setCodigoReporte(obtenerTexto(rs, "XEOPC_CODIGO", "CODIGO_REPORTE", "REPORTE_CODIGO",
                "XEREP_CODIGO_REPORTE", "REPH_REPORTE"));
        reporte.setModulo(obtenerTexto(rs, "MODULO", "REPORTE_MODULO", "XEREP_MODULO", "REPH_MODULO"));
        reporte.setTipoReporte(obtenerTexto(rs, "TIPO_REPORTE", "REPORTE_TIPO", "XEREP_TIPO_REPORTE",
                "XEREP_TIPO", "REPH_TIPO"));
        reporte.setFormato(obtenerTexto(rs, "FORMATO", "REPORTE_FORMATO", "XEREP_FORMATO", "REPH_FORMATO"));
        reporte.setTotalRegistros(obtenerEntero(rs, "TOTAL_REGISTROS", "REPORTE_TOTAL_REGISTROS",
                "XEREP_TOTAL_REGISTROS", "TOTAL", "REGISTROS"));
        reporte.setFiltros(obtenerTexto(rs, "FILTROS", "FILTROS_USADOS", "REPORTE_FILTROS",
                "XEREP_FILTROS", "REPH_FILTROS"));
        return reporte;
    }

    private String resolverTabla(Connection con) throws SQLException {
        for (String candidata : TABLAS_CANDIDATAS) {
            String tabla = buscarTabla(con, candidata);

            if (tabla != null) {
                return tabla;
            }
        }

        DatabaseMetaData metaData = con.getMetaData();

        try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String tabla = rs.getString("TABLE_NAME");
                String normalizada = tabla == null ? "" : tabla.toUpperCase(Locale.ROOT);

                if (normalizada.contains("REP")
                        && (normalizada.contains("HIST") || normalizada.contains("LOG"))) {
                    return tabla;
                }
            }
        }

        return null;
    }

    private String buscarTabla(Connection con, String tabla) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        String[] variantes = {tabla, tabla.toUpperCase(Locale.ROOT), tabla.toLowerCase(Locale.ROOT)};

        for (String variante : variantes) {
            try (ResultSet rs = metaData.getTables(null, null, variante, null)) {
                if (rs.next()) {
                    return rs.getString("TABLE_NAME");
                }
            }
        }

        return null;
    }

    private void agregarPrimera(Set<String> disponibles, List<String> columnas, List<Object> valores,
            Object valor, String... candidatas) {

        if (valor == null) {
            return;
        }

        for (String columna : candidatas) {
            if (tieneColumna(disponibles, columna) && !columnas.contains(columna)) {
                columnas.add(columna);
                valores.add(valor);
                return;
            }
        }
    }

    private String primeraColumna(Set<String> disponibles, String... candidatas) {
        for (String columna : candidatas) {
            if (tieneColumna(disponibles, columna)) {
                return columna;
            }
        }

        return null;
    }

    private String construirInsert(String tabla, List<String> columnas) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tabla).append(" (");
        StringBuilder marcas = new StringBuilder();

        for (int i = 0; i < columnas.size(); i++) {
            if (i > 0) {
                sql.append(", ");
                marcas.append(", ");
            }

            sql.append(columnas.get(i));
            marcas.append("?");
        }

        sql.append(") VALUES (").append(marcas).append(")");
        return sql.toString();
    }

    private String generarCodigo() {
        return "REP" + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
    }

    private String formatearFecha(String fecha) {
        return fecha == null ? "" : fecha;
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}