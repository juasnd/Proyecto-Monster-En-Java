package ec.edu.gutierrez.landazuri.leiton.dao;

import ec.edu.gutierrez.landazuri.leiton.modelo.Cargo;
import ec.edu.gutierrez.landazuri.leiton.modelo.Catalogo;
import ec.edu.gutierrez.landazuri.leiton.modelo.Departamento;
import ec.edu.gutierrez.landazuri.leiton.modelo.Empleado;
import ec.edu.gutierrez.landazuri.leiton.modelo.Familiar;
import ec.edu.gutierrez.landazuri.leiton.modelo.Persona;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO extends BaseDAO {

    private final PersonaDAO personaDAO = new PersonaDAO();
    private final FamiliarDAO familiarDAO = new FamiliarDAO();
    private final FormacionDAO formacionDAO = new FormacionDAO();
    private final CodigoDAO codigoDAO = new CodigoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public List<Empleado> listarEmpleados() {
        List<Empleado> lista = new ArrayList<>();

        String sql = consultaEmpleadoBase()
                + "ORDER BY p.PEPER_APELLIDO, p.PEPER_NOMBRE, e.PEEMP_CODIGO";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        }

        return lista;
    }

    public List<Empleado> buscarEmpleados(String criterio, String termino) {
        List<Empleado> lista = new ArrayList<>();
        String columna;

        if ("nombres".equalsIgnoreCase(criterio)) {
            columna = "p.PEPER_NOMBRE";
        } else if ("apellidos".equalsIgnoreCase(criterio)) {
            columna = "p.PEPER_APELLIDO";
        } else if ("cedula".equalsIgnoreCase(criterio)) {
            columna = "p.PEPER_CEDULA";
        } else {
            columna = "e.PEEMP_CODIGO";
        }

        String sql = consultaEmpleadoBase()
                + "WHERE " + columna + " LIKE ? "
                + "ORDER BY p.PEPER_APELLIDO, p.PEPER_NOMBRE, e.PEEMP_CODIGO";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + termino + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearEmpleado(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar empleados: " + e.getMessage());
        }

        return lista;
    }

    public Empleado buscarEmpleado(String codigoEmpleado) {
        String sql = consultaEmpleadoBase()
                + "WHERE e.PEEMP_CODIGO = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empleado empleado = mapearEmpleado(rs);
                    List<Familiar> familiares = familiarDAO.listarPorPersona(
                            empleado.getPersona().getPeperCodigo());
                    empleado.setFamiliares(familiares);
                    empleado.getPersona().setCargasFamiliares(contarCargasFamiliares(familiares));
                    empleado.setFormaciones(formacionDAO.listarPorEmpleado(
                            empleado.getPeempCodigo()));
                    return empleado;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar empleado: " + e.getMessage());
        }

        return null;
    }

    public boolean guardarEmpleado(Empleado empleado) throws SQLException {
        String sqlEmpleado = "INSERT INTO peemp_emplea "
                + "(PEEMP_CODIGO, PEDEP_CODIGO, PECAR_CODIGO, PEPER_CODIGO, PED_PEDEP_CODIGO) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                validarDatosPreviosNuevo(con, empleado);
                completarCodigosAutomaticos(con, empleado);
                empleado.getPersona().setTipo("EMP");
                personaDAO.insertar(con, empleado.getPersona());

                try (PreparedStatement ps = con.prepareStatement(sqlEmpleado)) {
                    cargarParametrosEmpleado(ps, empleado);
                    ps.executeUpdate();
                }

                familiarDAO.reemplazarPorPersona(con,
                        empleado.getPersona().getPeperCodigo(),
                        empleado.getFamiliares());
                formacionDAO.reemplazarPorEmpleado(con,
                        empleado.getPeempCodigo(),
                        empleado.getFormaciones());

                usuarioDAO.crearUsuarioAutomaticoEmpleado(con, empleado.getPersona());
                registrarAuditoriaCreacion(con, empleado);

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void generarCodigosAutomaticos(Empleado empleado) throws SQLException {
        try (Connection con = obtenerConexion()) {
            completarCodigosAutomaticos(con, empleado);
        }
    }

    public boolean actualizarEmpleado(Empleado empleado) throws SQLException {
        String sqlEmpleado = "UPDATE peemp_emplea SET "
                + "PEDEP_CODIGO = ?, PECAR_CODIGO = ?, PED_PEDEP_CODIGO = ? "
                + "WHERE PEEMP_CODIGO = ? AND PEPER_CODIGO = ?";

        try (Connection con = obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                personaDAO.actualizar(con, empleado.getPersona());

                try (PreparedStatement ps = con.prepareStatement(sqlEmpleado)) {
                    ps.setString(1, empleado.getPedepCodigo());
                    ps.setString(2, empleado.getPecarCodigo());
                    ps.setString(3, empleado.getPedPedepCodigo());
                    ps.setString(4, empleado.getPeempCodigo());
                    ps.setString(5, empleado.getPersona().getPeperCodigo());
                    ps.executeUpdate();
                }

                familiarDAO.reemplazarPorPersona(con,
                        empleado.getPersona().getPeperCodigo(),
                        empleado.getFamiliares());
                formacionDAO.reemplazarPorEmpleado(con,
                        empleado.getPeempCodigo(),
                        empleado.getFormaciones());

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public boolean eliminarEmpleado(String codigoEmpleado) throws SQLException {
        Empleado empleado = buscarEmpleado(codigoEmpleado);

        if (empleado == null) {
            return false;
        }

        String sqlEmpleado = "DELETE FROM peemp_emplea WHERE PEEMP_CODIGO = ?";

        try (Connection con = obtenerConexion()) {
            con.setAutoCommit(false);

            try {
                formacionDAO.eliminarPorEmpleado(con, codigoEmpleado);

                try (PreparedStatement ps = con.prepareStatement(sqlEmpleado)) {
                    ps.setString(1, codigoEmpleado);
                    ps.executeUpdate();
                }

                familiarDAO.eliminarPorPersona(con, empleado.getPersona().getPeperCodigo());
                personaDAO.eliminar(con, empleado.getPersona().getPeperCodigo());

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public boolean existeEmpleado(String codigoEmpleado) {
        String sql = "SELECT PEEMP_CODIGO FROM peemp_emplea WHERE PEEMP_CODIGO = ?";
        return existePorSql(sql, codigoEmpleado);
    }

    public boolean existePersona(String codigoPersona) {
        return personaDAO.existe(codigoPersona);
    }

    public boolean cargoPerteneceDepartamento(String codigoDepartamento, String codigoCargo) {
        String sql = "SELECT PECAR_CODIGO FROM pecar_cargo "
                + "WHERE PEDEP_CODIGO = ? AND PECAR_CODIGO = ?";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoDepartamento);
            ps.setString(2, codigoCargo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar cargo por departamento: " + e.getMessage());
        }

        return false;
    }

    public boolean empleadoTieneRelaciones(String codigoEmpleado) {
        String sql = "SELECT COUNT(*) AS total FROM ge_peemp_gepro WHERE PEEMP_CODIGO = ?";
        return contar(sql, codigoEmpleado) > 0;
    }

    public boolean personaTieneRelacionesExternas(String codigoPersona, String codigoEmpleado) {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM xeusu_usuari WHERE PEPER_CODIGO = ?) + "
                + "(SELECT COUNT(*) FROM peemp_emplea WHERE PEPER_CODIGO = ? AND PEEMP_CODIGO <> ?) "
                + "AS total";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoPersona);
            ps.setString(2, codigoPersona);
            ps.setString(3, codigoEmpleado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar relaciones de persona: " + e.getMessage());
        }

        return true;
    }

    public List<Departamento> listarDepartamentos() {
        List<Departamento> lista = new ArrayList<>();
        String sql = "SELECT PEDEP_CODIGO, PEDEP_DESCRI FROM pedep_depart ORDER BY PEDEP_DESCRI";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Departamento(rs.getString("PEDEP_CODIGO"), rs.getString("PEDEP_DESCRI")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar departamentos para empleado: " + e.getMessage());
        }

        return lista;
    }

    public List<Cargo> listarCargos() {
        List<Cargo> lista = new ArrayList<>();
        String sql = "SELECT c.PEDEP_CODIGO, c.PECAR_CODIGO, c.PECAR_DESCRI, d.PEDEP_DESCRI "
                + "FROM pecar_cargo c "
                + "INNER JOIN pedep_depart d ON c.PEDEP_CODIGO = d.PEDEP_CODIGO "
                + "ORDER BY d.PEDEP_DESCRI, c.PECAR_DESCRI";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cargo cargo = new Cargo();
                cargo.setPedepCodigo(rs.getString("PEDEP_CODIGO"));
                cargo.setPecarCodigo(rs.getString("PECAR_CODIGO"));
                cargo.setPecarDescri(rs.getString("PECAR_DESCRI"));
                cargo.setNombreDepartamento(rs.getString("PEDEP_DESCRI"));
                lista.add(cargo);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar cargos para empleado: " + e.getMessage());
        }

        return lista;
    }

    public List<Cargo> listarCargosPorDepartamento(String codigoDepartamento) {
        List<Cargo> lista = new ArrayList<>();
        String sql = "SELECT PEDEP_CODIGO, PECAR_CODIGO, PECAR_DESCRI "
                + "FROM pecar_cargo WHERE PEDEP_CODIGO = ? ORDER BY PECAR_DESCRI";

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigoDepartamento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cargo(
                            rs.getString("PEDEP_CODIGO"),
                            rs.getString("PECAR_CODIGO"),
                            rs.getString("PECAR_DESCRI")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar cargos por departamento: " + e.getMessage());
        }

        return lista;
    }

    public List<Catalogo> listarSexos() {
        return listarCatalogo("SELECT PESEX_CODIGO, PESEX_DESCRI FROM pesex_sexo ORDER BY PESEX_DESCRI");
    }

    public List<Catalogo> listarEstadosCiviles() {
        return listarCatalogo("SELECT PEESC_CODIGO, PEESC_DESCRI FROM peesc_estciv ORDER BY PEESC_DESCRI");
    }

    private List<Catalogo> listarCatalogo(String sql) {
        List<Catalogo> lista = new ArrayList<>();

        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Catalogo(rs.getString(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar catalogo: " + e.getMessage());
        }

        return lista;
    }

    private String consultaEmpleadoBase() {
        return "SELECT e.PEEMP_CODIGO, e.PEDEP_CODIGO AS EMP_PEDEP_CODIGO, "
                + "e.PECAR_CODIGO AS EMP_PECAR_CODIGO, e.PED_PEDEP_CODIGO, "
                + "p.PEPER_CODIGO, p.PESEX_CODIGO, s.PESEX_DESCRI, p.PEESC_CODIGO, "
                + "ec.PEESC_DESCRI, p.PEPER_NOMBRE, p.PEPER_APELLIDO, p.PEPER_CEDULA, "
                + "p.PEPER_FECHANACI, p.PEPER_CARGAS, p.PEPER_DIRECCION, "
                + "p.PEPER_CELULAR, p.PEPER_TELDOM, p.PEPER_EMAIL, p.PEPER_FOTO, "
                + "d.PEDEP_DESCRI AS DEP_DESCRI, dp.PEDEP_DESCRI AS DEP_PADRE_DESCRI, "
                + "c.PECAR_DESCRI "
                + "FROM peemp_emplea e "
                + "INNER JOIN peper_person p ON e.PEPER_CODIGO = p.PEPER_CODIGO "
                + "INNER JOIN pedep_depart d ON e.PEDEP_CODIGO = d.PEDEP_CODIGO "
                + "INNER JOIN pedep_depart dp ON e.PED_PEDEP_CODIGO = dp.PEDEP_CODIGO "
                + "INNER JOIN pecar_cargo c ON e.PEDEP_CODIGO = c.PEDEP_CODIGO "
                + "AND e.PECAR_CODIGO = c.PECAR_CODIGO "
                + "INNER JOIN pesex_sexo s ON p.PESEX_CODIGO = s.PESEX_CODIGO "
                + "LEFT JOIN peesc_estciv ec ON p.PEESC_CODIGO = ec.PEESC_CODIGO ";
    }

    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        empleado.setPeempCodigo(rs.getString("PEEMP_CODIGO"));
        empleado.setPedepCodigo(rs.getString("EMP_PEDEP_CODIGO"));
        empleado.setPecarCodigo(rs.getString("EMP_PECAR_CODIGO"));
        empleado.setPedPedepCodigo(rs.getString("PED_PEDEP_CODIGO"));
        empleado.setNombreDepartamento(rs.getString("DEP_DESCRI"));
        empleado.setNombreDepartamentoPadre(rs.getString("DEP_PADRE_DESCRI"));
        empleado.setNombreCargo(rs.getString("PECAR_DESCRI"));
        empleado.setPersona(personaDAO.mapearPersona(rs));
        return empleado;
    }

    private int contarCargasFamiliares(List<Familiar> familiares) {
        if (familiares == null) {
            return 0;
        }

        return familiares.size();
    }

    private void cargarParametrosEmpleado(PreparedStatement ps, Empleado empleado) throws SQLException {
        ps.setString(1, empleado.getPeempCodigo());
        ps.setString(2, empleado.getPedepCodigo());
        ps.setString(3, empleado.getPecarCodigo());
        ps.setString(4, empleado.getPersona().getPeperCodigo());
        ps.setString(5, empleado.getPedPedepCodigo());
    }

    private void completarCodigosAutomaticos(Connection con, Empleado empleado) throws SQLException {
        if (empleado.getPersona() != null && vacio(empleado.getPersona().getPeperCodigo())) {
            empleado.getPersona().setPeperCodigo(codigoDAO.generarCodigo(con, "PEPER_CODIGO"));
        }

        if (vacio(empleado.getPeempCodigo())) {
            empleado.setPeempCodigo(codigoDAO.generarCodigo(con, "PEEMP_CODIGO"));
        }
    }

    private void validarDatosPreviosNuevo(Connection con, Empleado empleado) throws SQLException {
        if (empleado == null || empleado.getPersona() == null) {
            throw new SQLException("No se recibieron los datos personales del empleado.");
        }

        String cedula = empleado.getPersona().getCedula();

        if (!vacio(cedula) && existeCedula(con, cedula)) {
            throw new SQLException("Ya existe una persona registrada con esta cédula.");
        }

        if (!vacio(cedula) && usuarioDAO.existeLogin(con, cedula)) {
            throw new SQLException("Ya existe un usuario registrado con esta cédula.");
        }
    }

    private boolean existeCedula(Connection con, String cedula) throws SQLException {
        String sql = "SELECT PEPER_CODIGO FROM peper_person WHERE PEPER_CEDULA = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void registrarAuditoriaCreacion(Connection con, Empleado empleado) {
        ec.edu.gutierrez.landazuri.leiton.modelo.Auditoria auditoria =
                new ec.edu.gutierrez.landazuri.leiton.modelo.Auditoria();
        auditoria.setLogin("sistema");
        auditoria.setTabla("PEEMP_EMPLEA");
        auditoria.setAccion("CREAR");
        auditoria.setDetalle("Empleado " + empleado.getPeempCodigo()
                + " y usuario " + empleado.getPersona().getCedula() + " creados automaticamente.");
        auditoriaDAO.registrar(con, auditoria);
    }

    private boolean existePorSql(String sql, String parametro) {
        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, parametro);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar existencia: " + e.getMessage());
        }

        return false;
    }

    private int contar(String sql, String parametro) {
        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, parametro);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al contar relaciones: " + e.getMessage());
        }

        return 1;
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
