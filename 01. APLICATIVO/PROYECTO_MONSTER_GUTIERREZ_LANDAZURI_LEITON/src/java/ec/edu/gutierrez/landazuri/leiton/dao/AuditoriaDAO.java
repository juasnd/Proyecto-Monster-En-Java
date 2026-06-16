package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Auditoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuditoriaDAO extends BaseDAO {

    public void registrar(Auditoria auditoria) {
        try (Connection con = obtenerConexion()) {
            registrar(con, auditoria);
        } catch (SQLException e) {
            System.out.println("No se pudo registrar auditoria: " + e.getMessage());
        }
    }

    public void registrar(Connection con, Auditoria auditoria) {
        if (auditoria == null) {
            return;
        }

        try {
            Set<String> columnasDisponibles = obtenerColumnas(con, "xeaud_auditoria");
            List<String> columnas = new ArrayList<>();
            List<Object> valores = new ArrayList<>();

            agregar(columnasDisponibles, columnas, valores, "XEUSU_LOGIN", auditoria.getLogin());
            agregar(columnasDisponibles, columnas, valores, "XEAUD_TABLA", auditoria.getTabla());
            agregar(columnasDisponibles, columnas, valores, "XEAUD_ACCION", auditoria.getAccion());
            agregar(columnasDisponibles, columnas, valores, "XEAUD_DETALLE", auditoria.getDetalle());
            agregar(columnasDisponibles, columnas, valores, "XEAUD_FECHA", Timestamp.valueOf(LocalDateTime.now()));

            if (columnas.isEmpty()) {
                return;
            }

            String sql = construirInsert(columnas);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 0; i < valores.size(); i++) {
                    ps.setObject(i + 1, valores.get(i));
                }

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("No se pudo registrar auditoria: " + e.getMessage());
        }
    }

    private void agregar(Set<String> disponibles, List<String> columnas, List<Object> valores,
            String columna, Object valor) {

        if (tieneColumna(disponibles, columna) && valor != null) {
            columnas.add(columna);
            valores.add(valor);
        }
    }

    private String construirInsert(List<String> columnas) {
        StringBuilder sql = new StringBuilder("INSERT INTO xeaud_auditoria (");
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

}
