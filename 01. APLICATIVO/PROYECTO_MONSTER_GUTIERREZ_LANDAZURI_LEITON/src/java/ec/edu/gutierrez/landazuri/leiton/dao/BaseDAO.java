package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.controlador.BDController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {

    protected Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(BDController.URL, BDController.USER, BDController.CLAVE);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver de MySQL.", e);
        }
    }
}
