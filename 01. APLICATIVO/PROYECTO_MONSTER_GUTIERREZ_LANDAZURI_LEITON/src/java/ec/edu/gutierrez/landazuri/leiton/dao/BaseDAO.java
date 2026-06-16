package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.controlador.BDController;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public abstract class BaseDAO {

    protected Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(BDController.URL, BDController.USER, BDController.CLAVE);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver de MySQL.", e);
        }
    }

    protected Set<String> obtenerColumnas(Connection con, String tabla) throws SQLException {
        Set<String> columnas = new HashSet<>();
        DatabaseMetaData metaData = con.getMetaData();

        cargarColumnas(metaData, columnas, tabla);
        cargarColumnas(metaData, columnas, tabla.toUpperCase(Locale.ROOT));
        cargarColumnas(metaData, columnas, tabla.toLowerCase(Locale.ROOT));

        return columnas;
    }

    private void cargarColumnas(DatabaseMetaData metaData, Set<String> columnas, String tabla)
            throws SQLException {

        try (ResultSet rs = metaData.getColumns(null, null, tabla, null)) {
            while (rs.next()) {
                columnas.add(rs.getString("COLUMN_NAME").toUpperCase(Locale.ROOT));
            }
        }
    }

    protected boolean tieneColumna(Set<String> columnas, String columna) {
        return columnas != null && columnas.contains(columna.toUpperCase(Locale.ROOT));
    }

    protected String obtenerTexto(ResultSet rs, String... columnas) throws SQLException {
        for (String columna : columnas) {
            if (existeColumna(rs, columna)) {
                return rs.getString(columna);
            }
        }

        return "";
    }

    protected int obtenerEntero(ResultSet rs, String... columnas) throws SQLException {
        for (String columna : columnas) {
            if (existeColumna(rs, columna)) {
                return rs.getInt(columna);
            }
        }

        return 0;
    }

    private boolean existeColumna(ResultSet rs, String columna) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String label = metaData.getColumnLabel(i);
            String name = metaData.getColumnName(i);

            if (columna.equalsIgnoreCase(label) || columna.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }
}
