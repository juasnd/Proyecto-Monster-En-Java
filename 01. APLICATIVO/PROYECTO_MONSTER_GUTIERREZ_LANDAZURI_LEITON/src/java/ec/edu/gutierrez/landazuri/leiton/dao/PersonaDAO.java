package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Persona;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PersonaDAO extends BaseDAO {

    public Persona buscarPorCodigo(String codigo) {
        String sql = "SELECT p.PEPER_CODIGO, p.PEPER_TIPO, tp.PETIP_DESCRI AS PEPER_TIPO_DESCRI, "
                + "p.PESEX_CODIGO, s.PESEX_DESCRI, p.PEESC_CODIGO, e.PEESC_DESCRI, "
                + "p.PEPER_NOMBRE, p.PEPER_APELLIDO, p.PEPER_CEDULA, p.PEPER_FECHANACI, "
                + "p.PEPER_CARGAS, p.PEPER_DIRECCION, p.PEPER_CELULAR, p.PEPER_TELDOM, "
                + "p.PEPER_EMAIL, p.PEPER_FOTO "
                + "FROM peper_person p "
                + "LEFT JOIN petip_persona tp ON p.PEPER_TIPO = tp.PETIP_CODIGO "
                + "LEFT JOIN pesex_sexo s ON p.PESEX_CODIGO = s.PESEX_CODIGO "
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

    public boolean existeCedula(Connection con, String cedula) throws SQLException {
        if (vacio(cedula)) {
            return false;
        }

        String sql = "SELECT PEPER_CODIGO FROM peper_person WHERE PEPER_CEDULA = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insertar(Connection con, Persona persona) throws SQLException {
        Set<String> columnasDisponibles = obtenerColumnas(con, "peper_person");
        List<String> columnas = new ArrayList<>();
        List<Object> valores = new ArrayList<>();

        agregar(columnasDisponibles, columnas, valores, "PEPER_CODIGO", persona.getPeperCodigo());
        agregar(columnasDisponibles, columnas, valores, "PEPER_TIPO", normalizarTipo(persona.getTipo()));
        agregar(columnasDisponibles, columnas, valores, "PESEX_CODIGO", valorONull(persona.getPesexCodigo()));
        agregar(columnasDisponibles, columnas, valores, "PEESC_CODIGO", valorONull(persona.getPeescCodigo()));
        agregar(columnasDisponibles, columnas, valores, "PEPER_NOMBRE", requerido(persona.getNombres(), "Sin nombre"));
        agregar(columnasDisponibles, columnas, valores, "PEPER_APELLIDO", requerido(persona.getApellidos(), "Sin apellido"));
        agregar(columnasDisponibles, columnas, valores, "PEPER_CEDULA", valorONull(persona.getCedula()));
        agregar(columnasDisponibles, columnas, valores, "PEPER_FECHANACI", fechaONull(persona.getFechaNacimiento()));
        agregar(columnasDisponibles, columnas, valores, "PEPER_CARGAS", Integer.valueOf(persona.getCargasFamiliares()));
        agregar(columnasDisponibles, columnas, valores, "PEPER_DIRECCION", requerido(persona.getDireccion(), "Sin direccion"));
        agregar(columnasDisponibles, columnas, valores, "PEPER_CELULAR", valorONull(persona.getCelular()));
        agregar(columnasDisponibles, columnas, valores, "PEPER_TELDOM", valorONull(persona.getTelefonoDomicilio()));
        agregar(columnasDisponibles, columnas, valores, "PEPER_EMAIL", requerido(persona.getEmail(), ""));
        agregar(columnasDisponibles, columnas, valores, "PEPER_FOTO", valorONull(persona.getFoto()));

        String sql = construirInsert("peper_person", columnas);

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Connection con, Persona persona) throws SQLException {
        Set<String> columnasDisponibles = obtenerColumnas(con, "peper_person");
        List<String> sets = new ArrayList<>();
        List<Object> valores = new ArrayList<>();

        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_TIPO", valorONull(persona.getTipo()));
        agregarUpdate(columnasDisponibles, sets, valores, "PESEX_CODIGO", valorONull(persona.getPesexCodigo()));
        agregarUpdate(columnasDisponibles, sets, valores, "PEESC_CODIGO", valorONull(persona.getPeescCodigo()));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_NOMBRE", requerido(persona.getNombres(), "Sin nombre"));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_APELLIDO", requerido(persona.getApellidos(), "Sin apellido"));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_CEDULA", valorONull(persona.getCedula()));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_FECHANACI", fechaONull(persona.getFechaNacimiento()));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_CARGAS", Integer.valueOf(persona.getCargasFamiliares()));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_DIRECCION", requerido(persona.getDireccion(), "Sin direccion"));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_CELULAR", valorONull(persona.getCelular()));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_TELDOM", valorONull(persona.getTelefonoDomicilio()));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_EMAIL", requerido(persona.getEmail(), ""));
        agregarUpdate(columnasDisponibles, sets, valores, "PEPER_FOTO", valorONull(persona.getFoto()));

        if (sets.isEmpty()) {
            throw new SQLException("No hay columnas disponibles para actualizar persona.");
        }

        StringBuilder sql = new StringBuilder("UPDATE peper_person SET ");
        for (int i = 0; i < sets.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(sets.get(i));
        }
        sql.append(" WHERE PEPER_CODIGO = ?");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }
            ps.setString(valores.size() + 1, persona.getPeperCodigo());
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

    public Persona mapearPersona(ResultSet rs) throws SQLException {
        Persona persona = new Persona();
        persona.setPeperCodigo(obtenerTexto(rs, "PEPER_CODIGO", "PERSONA_CODIGO"));
        persona.setTipo(obtenerTexto(rs, "PEPER_TIPO"));
        persona.setTipoDescripcion(obtenerTexto(rs, "PEPER_TIPO_DESCRI", "PETIP_DESCRI", "TIPO_DESCRI"));
        persona.setPesexCodigo(obtenerTexto(rs, "PESEX_CODIGO"));
        persona.setSexoDescripcion(obtenerTexto(rs, "PESEX_DESCRI"));
        persona.setPeescCodigo(obtenerTexto(rs, "PEESC_CODIGO"));
        persona.setEstadoCivilDescripcion(obtenerTexto(rs, "PEESC_DESCRI"));
        persona.setNombres(obtenerTexto(rs, "PEPER_NOMBRE", "NOMBRES", "NOMBRE"));
        persona.setApellidos(obtenerTexto(rs, "PEPER_APELLIDO", "APELLIDOS", "APELLIDO"));
        persona.setCedula(obtenerTexto(rs, "PEPER_CEDULA", "CEDULA"));

        Date fecha = null;
        if (existeFecha(rs, "PEPER_FECHANACI")) {
            fecha = rs.getDate("PEPER_FECHANACI");
        }
        persona.setFechaNacimiento(fecha != null ? fecha.toString() : "");

        persona.setCargasFamiliares(obtenerEntero(rs, "PEPER_CARGAS"));
        persona.setDireccion(obtenerTexto(rs, "PEPER_DIRECCION"));
        persona.setCelular(obtenerTexto(rs, "PEPER_CELULAR"));
        persona.setTelefonoDomicilio(obtenerTexto(rs, "PEPER_TELDOM"));
        persona.setEmail(obtenerTexto(rs, "PEPER_EMAIL"));
        persona.setFoto(obtenerTexto(rs, "PEPER_FOTO"));
        persona.setEsEmpleado(obtenerTexto(rs, "ES_EMPLEADO"));
        persona.setCodigoEmpleado(obtenerTexto(rs, "PEEMP_CODIGO"));
        return persona;
    }

    private void agregar(Set<String> disponibles, List<String> columnas, List<Object> valores,
            String columna, Object valor) {

        if (tieneColumna(disponibles, columna)) {
            columnas.add(columna);
            valores.add(valor);
        }
    }

    private void agregarUpdate(Set<String> disponibles, List<String> sets, List<Object> valores,
            String columna, Object valor) {

        if (tieneColumna(disponibles, columna)) {
            sets.add(columna + " = ?");
            valores.add(valor);
        }
    }

    private String construirInsert(String tabla, List<String> columnas) throws SQLException {
        if (columnas == null || columnas.isEmpty()) {
            throw new SQLException("No hay columnas disponibles para insertar en " + tabla + ".");
        }

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

    private Object fechaONull(String fecha) {
        if (vacio(fecha)) {
            return null;
        }

        try {
            return Date.valueOf(fecha.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Object valorONull(String valor) {
        return vacio(valor) ? null : valor.trim();
    }

    private String requerido(String valor, String defecto) {
        return vacio(valor) ? defecto : valor.trim();
    }

    private String normalizarTipo(String tipo) {
        return vacio(tipo) ? "INV" : tipo.trim().toUpperCase();
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private boolean existeFecha(ResultSet rs, String columna) {
        try {
            rs.findColumn(columna);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    private void setNullableString(PreparedStatement ps, int indice, String valor) throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            ps.setNull(indice, Types.VARCHAR);
        } else {
            ps.setString(indice, valor.trim());
        }
    }
}
