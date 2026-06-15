package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.modelo.Cargo;
import ec.edu.gutierrez.landazuri.leiton.modelo.Departamento;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BDController {

    public static final String URL = "jdbc:mysql://localhost:3306/PROYECTO MONSTER?useSSL=false&serverTimezone=UTC";
    public static final String USER = "root";
    public static final String CLAVE = "";

    private Connection con = null;

    public BDController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, CLAVE);
            System.out.println("Conexión exitosa a la base de datos real");
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    public boolean isRegistered(String username, String password) {
        String sql = "SELECT * FROM XEUSU_USUARI "
                   + "WHERE XEUSU_LOGIN = ? AND XEUSU_PASWD = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, Controller.getMD5(password));

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("Error al validar usuario: " + e.getMessage());
        }

        return false;
    }

    public boolean usuarioExiste(String username) {
        String sql = "SELECT XEUSU_LOGIN FROM XEUSU_USUARI WHERE XEUSU_LOGIN = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("Error al verificar usuario: " + e.getMessage());
        }

        return false;
    }

    public boolean usuarioBloqueado(String username) {
        String sql = "SELECT XEEST_CODIGO FROM XEUSU_USUARI WHERE XEUSU_LOGIN = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return "B".equalsIgnoreCase(rs.getString("XEEST_CODIGO"));
            }

        } catch (Exception e) {
            System.out.println("Error al verificar bloqueo: " + e.getMessage());
        }

        return false;
    }

    public void aumentarIntentosFallidos(String username) {
        String sql = "UPDATE XEUSU_USUARI "
                   + "SET XEUSU_INTENTOS = XEUSU_INTENTOS + 1 "
                   + "WHERE XEUSU_LOGIN = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error al aumentar intentos: " + e.getMessage());
        }
    }

    public int obtenerIntentosFallidos(String username) {
        String sql = "SELECT XEUSU_INTENTOS FROM XEUSU_USUARI WHERE XEUSU_LOGIN = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("XEUSU_INTENTOS");
            }

        } catch (Exception e) {
            System.out.println("Error al obtener intentos: " + e.getMessage());
        }

        return 0;
    }

    public void bloquearUsuario(String username) {
        String sql = "UPDATE XEUSU_USUARI "
                   + "SET XEEST_CODIGO = 'B' "
                   + "WHERE XEUSU_LOGIN = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error al bloquear usuario: " + e.getMessage());
        }
    }

    public void reiniciarIntentos(String username) {
        String sql = "UPDATE XEUSU_USUARI "
                   + "SET XEUSU_INTENTOS = 0, XEEST_CODIGO = 'A' "
                   + "WHERE XEUSU_LOGIN = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error al reiniciar intentos: " + e.getMessage());
        }
    }

    public String obtenerCorreoPorUsuario(String username) {
        String sql = "SELECT p.PEPER_EMAIL "
                   + "FROM XEUSU_USUARI u "
                   + "INNER JOIN PEPER_PERSON p ON u.PEPER_CODIGO = p.PEPER_CODIGO "
                   + "WHERE u.XEUSU_LOGIN = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("PEPER_EMAIL");
            }

        } catch (Exception e) {
            System.out.println("Error al obtener correo: " + e.getMessage());
        }

        return null;
    }

    public List<Departamento> listarDepartamentos() {
        List<Departamento> lista = new ArrayList<>();

        String sql = "SELECT PEDEP_CODIGO, PEDEP_DESCRI "
                   + "FROM PEDEP_DEPART "
                   + "ORDER BY PEDEP_CODIGO";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Departamento dep = new Departamento();
                dep.setCodigo(rs.getString("PEDEP_CODIGO"));
                dep.setDescripcion(rs.getString("PEDEP_DESCRI"));
                lista.add(dep);
            }

        } catch (Exception e) {
            System.out.println("Error al listar departamentos: " + e.getMessage());
        }

        return lista;
    }

    public Departamento buscarDepartamento(String codigo) {
        String sql = "SELECT PEDEP_CODIGO, PEDEP_DESCRI "
                   + "FROM PEDEP_DEPART "
                   + "WHERE PEDEP_CODIGO = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, codigo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Departamento dep = new Departamento();
                dep.setCodigo(rs.getString("PEDEP_CODIGO"));
                dep.setDescripcion(rs.getString("PEDEP_DESCRI"));
                return dep;
            }

        } catch (Exception e) {
            System.out.println("Error al buscar departamento: " + e.getMessage());
        }

        return null;
    }

    public boolean insertarDepartamento(Departamento dep) {
        String sql = "INSERT INTO PEDEP_DEPART (PEDEP_CODIGO, PEDEP_DESCRI) "
                   + "VALUES (?, ?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, dep.getCodigo());
            ps.setString(2, dep.getDescripcion());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al insertar departamento: " + e.getMessage());
        }

        return false;
    }

    public boolean actualizarDepartamento(Departamento dep) {
        String sql = "UPDATE PEDEP_DEPART "
                   + "SET PEDEP_DESCRI = ? "
                   + "WHERE PEDEP_CODIGO = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, dep.getDescripcion());
            ps.setString(2, dep.getCodigo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al actualizar departamento: " + e.getMessage());
        }

        return false;
    }

    public boolean departamentoEnUso(String codigo) {
        String sql = "SELECT "
                   + "(SELECT COUNT(*) FROM PECAR_CARGO WHERE PEDEP_CODIGO = ?) + "
                   + "(SELECT COUNT(*) FROM GEPRO_PROYEC WHERE PEDEP_CODIGO = ?) + "
                   + "(SELECT COUNT(*) FROM PEEMP_EMPLEA WHERE PEDEP_CODIGO = ? OR PED_PEDEP_CODIGO = ?) "
                   + "AS total";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, codigo);
            ps.setString(2, codigo);
            ps.setString(3, codigo);
            ps.setString(4, codigo);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (Exception e) {
            System.out.println("Error al verificar uso del departamento: " + e.getMessage());
        }

        return true;
    }

    public boolean eliminarDepartamento(String codigo) {
        if (departamentoEnUso(codigo)) {
            return false;
        }

        String sql = "DELETE FROM PEDEP_DEPART WHERE PEDEP_CODIGO = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, codigo);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al eliminar departamento: " + e.getMessage());
        }

        return false;
    }

    public List<Cargo> listarCargos() {
        List<Cargo> lista = new ArrayList<>();
        
        String sql = "SELECT c.PEDEP_CODIGO, c.PECAR_CODIGO, c.PECAR_DESCRI, d.PEDEP_DESCRI "
                   + "FROM pecar_cargo c "
                   + "INNER JOIN pedep_depart d ON c.PEDEP_CODIGO = d.PEDEP_CODIGO "
                   + "ORDER BY d.PEDEP_DESCRI, c.PECAR_DESCRI";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Cargo cargo = new Cargo();
                cargo.setPedepCodigo(rs.getString("PEDEP_CODIGO"));
                cargo.setPecarCodigo(rs.getString("PECAR_CODIGO"));
                cargo.setPecarDescri(rs.getString("PECAR_DESCRI"));
                cargo.setNombreDepartamento(rs.getString("PEDEP_DESCRI")); // El nombre real
                lista.add(cargo);
            }
        } catch (Exception e) {
            System.out.println("Error al listar cargos: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertarCargo(Cargo cargo) {
        String sql = "INSERT INTO pecar_cargo (PEDEP_CODIGO, PECAR_CODIGO, PECAR_DESCRI) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cargo.getPedepCodigo());
            ps.setString(2, cargo.getPecarCodigo());
            ps.setString(3, cargo.getPecarDescri());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al insertar cargo: " + e.getMessage());
        }
        return false;
    }

    public Cargo buscarCargo(String pedepCodigo, String pecarCodigo) {
        String sql = "SELECT PEDEP_CODIGO, PECAR_CODIGO, PECAR_DESCRI FROM pecar_cargo "
                   + "WHERE PEDEP_CODIGO = ? AND PECAR_CODIGO = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, pedepCodigo);
            ps.setString(2, pecarCodigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Cargo(
                        rs.getString("PEDEP_CODIGO"),
                        rs.getString("PECAR_CODIGO"),
                        rs.getString("PECAR_DESCRI")
                );
            }
        } catch (Exception e) {
            System.out.println("Error al buscar cargo: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarCargo(Cargo cargo) {
        String sql = "UPDATE pecar_cargo SET PECAR_DESCRI = ? WHERE PEDEP_CODIGO = ? AND PECAR_CODIGO = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cargo.getPecarDescri());
            ps.setString(2, cargo.getPedepCodigo());
            ps.setString(3, cargo.getPecarCodigo());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar cargo: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarCargo(String pedepCodigo, String pecarCodigo) {
        // Nota: Deberías hacer un método cargoEnUso() similar al de departamentos 
        // revisando la tabla peemp_emplea antes de permitir eliminar.
        String sql = "DELETE FROM pecar_cargo WHERE PEDEP_CODIGO = ? AND PECAR_CODIGO = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, pedepCodigo);
            ps.setString(2, pecarCodigo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar cargo: " + e.getMessage());
        }
        return false;
    }
}

