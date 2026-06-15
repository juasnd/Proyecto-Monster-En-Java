package ec.edu.gutierrez.landazuri.leiton.controlador;

import ec.edu.gutierrez.landazuri.leiton.dao.EmpleadoDAO;
import ec.edu.gutierrez.landazuri.leiton.dao.FamiliarDAO;
import ec.edu.gutierrez.landazuri.leiton.modelo.Cargo;
import ec.edu.gutierrez.landazuri.leiton.modelo.Empleado;
import ec.edu.gutierrez.landazuri.leiton.modelo.Familiar;
import ec.edu.gutierrez.landazuri.leiton.modelo.Formacion;
import ec.edu.gutierrez.landazuri.leiton.modelo.Parentesco;
import ec.edu.gutierrez.landazuri.leiton.modelo.Persona;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "EmpleadoController", urlPatterns = {"/EmpleadoController"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 3 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class EmpleadoController extends HttpServlet {

    private static final long FOTO_MAX_BYTES = 2L * 1024L * 1024L;
    private static final String FOTO_DIR = "uploads/empleados";
    private static final List<String> FOTO_EXT_VALIDAS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private static final List<String> FOTO_MIME_VALIDOS = Arrays.asList("image/jpeg", "image/png", "image/webp");

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final FamiliarDAO familiarDAO = new FamiliarDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            procesar(request, response);
        } catch (IllegalStateException e) {
            request.setAttribute("modo", "nuevo");
            request.setAttribute("error", "La foto no debe superar 2 MB.");
            cargarDatosBase(request);
            request.getRequestDispatcher("empleados.jsp").forward(request, response);
        }
    }

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        if (accion == null) {
            accion = "listar";
        }

        if ("cargosPorDepartamento".equals(accion)) {
            responderCargosPorDepartamento(request, response);
            return;
        }

        switch (accion) {
            case "nuevo":
                nuevo(request, response);
                break;

            case "guardar":
                guardar(request, response);
                break;

            case "editar":
                editar(request, response);
                break;

            case "ver":
                ver(request, response);
                break;

            case "actualizar":
                actualizar(request, response);
                break;

            case "eliminar":
                eliminar(request, response);
                break;

            case "buscar":
                buscar(request, response);
                break;

            default:
                listar(request, response);
                break;
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("modo", "listar");
        cargarDatosBase(request);
        request.getRequestDispatcher("empleados.jsp").forward(request, response);
    }

    private void buscar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String criterio = valor(request, "criterioBusqueda");
        String termino = valor(request, "terminoBusqueda");

        request.setAttribute("modo", "listar");
        request.setAttribute("criterioBusqueda", criterio);
        request.setAttribute("terminoBusqueda", termino);

        if (termino.isEmpty()) {
            request.setAttribute("empleados", empleadoDAO.listarEmpleados());
        } else {
            request.setAttribute("empleados", empleadoDAO.buscarEmpleados(criterio, termino));
        }

        cargarDatosBase(request);
        request.getRequestDispatcher("empleados.jsp").forward(request, response);
    }

    private void nuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("modo", "nuevo");
        request.setAttribute("empleadoForm", new Empleado());
        cargarDatosBase(request);
        request.getRequestDispatcher("empleados.jsp").forward(request, response);
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado empleado = construirEmpleado(request);
        List<String> errores = new ArrayList<>();
        empleado.setFamiliares(construirFamiliares(request,
                empleado.getPersona().getPeperCodigo(), errores));
        empleado.setFormaciones(construirFormaciones(request, empleado.getPeempCodigo()));
        actualizarCargasFamiliares(empleado);

        Part fotoPart = obtenerFotoPart(request);
        errores.addAll(validarEmpleado(empleado, true));
        errores.addAll(validarFamiliares(empleado.getFamiliares()));
        errores.addAll(validarFormaciones(empleado.getFormaciones()));
        String errorFoto = validarFoto(fotoPart);

        if (errorFoto != null) {
            errores.add(errorFoto);
        }

        if (empleadoDAO.existeEmpleado(empleado.getPeempCodigo())) {
            errores.add("Ya existe un empleado con ese codigo.");
        }

        if (empleadoDAO.existePersona(empleado.getPersona().getPeperCodigo())) {
            errores.add("Ya existe una persona con ese codigo.");
        }

        if (!errores.isEmpty()) {
            volverAlFormulario(request, response, "nuevo", empleado, unirErrores(errores));
            return;
        }

        String fotoGuardada = null;

        try {
            if (fotoSeleccionada(fotoPart)) {
                fotoGuardada = guardarFoto(fotoPart, empleado.getPersona().getPeperCodigo());
                empleado.getPersona().setFoto(fotoGuardada);
            }

            empleadoDAO.guardarEmpleado(empleado);
            response.sendRedirect(request.getContextPath() + "/EmpleadoController?mensaje=guardado");
        } catch (SQLException | IOException e) {
            eliminarFotoSiExiste(fotoGuardada);
            volverAlFormulario(request, response, "nuevo", empleado,
                    "No se pudo guardar el empleado. Verifique las claves, relaciones y foto seleccionada.");
        }
    }

    private void editar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");
        Empleado empleado = empleadoDAO.buscarEmpleado(codigo);

        if (empleado == null) {
            response.sendRedirect(request.getContextPath() + "/EmpleadoController?error=no_encontrado");
            return;
        }

        request.setAttribute("modo", "editar");
        request.setAttribute("empleadoForm", empleado);
        cargarDatosBase(request);
        request.getRequestDispatcher("empleados.jsp").forward(request, response);
    }

    private void ver(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");
        Empleado empleado = empleadoDAO.buscarEmpleado(codigo);

        if (empleado == null) {
            response.sendRedirect(request.getContextPath() + "/EmpleadoController?error=no_encontrado");
            return;
        }

        request.setAttribute("modo", "ver");
        request.setAttribute("empleadoForm", empleado);
        cargarDatosBase(request);
        request.getRequestDispatcher("empleados.jsp").forward(request, response);
    }

    private void actualizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado empleado = construirEmpleado(request);
        List<String> errores = new ArrayList<>();
        empleado.setFamiliares(construirFamiliares(request,
                empleado.getPersona().getPeperCodigo(), errores));
        empleado.setFormaciones(construirFormaciones(request, empleado.getPeempCodigo()));
        actualizarCargasFamiliares(empleado);

        Empleado empleadoActual = empleadoDAO.buscarEmpleado(empleado.getPeempCodigo());
        String fotoAnterior = empleadoActual != null && empleadoActual.getPersona() != null
                ? empleadoActual.getPersona().getFoto()
                : empleado.getPersona().getFoto();
        empleado.getPersona().setFoto(fotoAnterior);

        Part fotoPart = obtenerFotoPart(request);
        errores.addAll(validarEmpleado(empleado, false));
        errores.addAll(validarFamiliares(empleado.getFamiliares()));
        errores.addAll(validarFormaciones(empleado.getFormaciones()));
        String errorFoto = validarFoto(fotoPart);

        if (errorFoto != null) {
            errores.add(errorFoto);
        }

        if (!empleadoDAO.existeEmpleado(empleado.getPeempCodigo())) {
            errores.add("No se encontro el empleado que desea actualizar.");
        }

        if (!empleadoDAO.existePersona(empleado.getPersona().getPeperCodigo())) {
            errores.add("No se encontro la persona vinculada al empleado.");
        }

        if (!errores.isEmpty()) {
            volverAlFormulario(request, response, "editar", empleado, unirErrores(errores));
            return;
        }

        String fotoGuardada = null;

        try {
            if (fotoSeleccionada(fotoPart)) {
                fotoGuardada = guardarFoto(fotoPart, empleado.getPersona().getPeperCodigo());
                empleado.getPersona().setFoto(fotoGuardada);
            }

            empleadoDAO.actualizarEmpleado(empleado);

            if (fotoGuardada != null) {
                eliminarFotoAnterior(fotoAnterior, fotoGuardada);
            }

            response.sendRedirect(request.getContextPath() + "/EmpleadoController?mensaje=actualizado");
        } catch (SQLException | IOException e) {
            eliminarFotoSiExiste(fotoGuardada);
            volverAlFormulario(request, response, "editar", empleado,
                    "No se pudo actualizar el empleado. Verifique las claves, relaciones y foto seleccionada.");
        }
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");
        Empleado empleado = empleadoDAO.buscarEmpleado(codigo);

        if (empleado == null) {
            response.sendRedirect(request.getContextPath() + "/EmpleadoController?error=no_encontrado");
            return;
        }

        if (empleadoDAO.empleadoTieneRelaciones(codigo)
                || empleadoDAO.personaTieneRelacionesExternas(empleado.getPersona().getPeperCodigo(), codigo)) {
            response.sendRedirect(request.getContextPath() + "/EmpleadoController?error=en_uso");
            return;
        }

        try {
            if (empleadoDAO.eliminarEmpleado(codigo)) {
                response.sendRedirect(request.getContextPath() + "/EmpleadoController?mensaje=eliminado");
            } else {
                response.sendRedirect(request.getContextPath() + "/EmpleadoController?error=no_eliminado");
            }
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/EmpleadoController?error=en_uso");
        }
    }

    private Empleado construirEmpleado(HttpServletRequest request) {
        Persona persona = new Persona();
        persona.setPeperCodigo(valor(request, "codigoPersona").toUpperCase());
        persona.setCedula(valor(request, "cedula"));
        persona.setNombres(valor(request, "nombres"));
        persona.setApellidos(valor(request, "apellidos"));
        persona.setFechaNacimiento(valor(request, "fechaNacimiento"));
        persona.setPesexCodigo(valor(request, "sexoCodigo"));
        persona.setPeescCodigo(valor(request, "estadoCivilCodigo"));
        persona.setCargasFamiliares(entero(request, "cargasFamiliares"));
        persona.setDireccion(valor(request, "direccion"));
        persona.setCelular(valor(request, "celular"));
        persona.setTelefonoDomicilio(valor(request, "telefonoDomicilio"));
        persona.setEmail(valor(request, "email"));
        persona.setFoto(valor(request, "fotoActual"));

        Empleado empleado = new Empleado();
        empleado.setPersona(persona);
        empleado.setPeempCodigo(valor(request, "codigoEmpleado").toUpperCase());
        empleado.setPedepCodigo(valor(request, "departamentoCodigo").toUpperCase());
        empleado.setPecarCodigo(valor(request, "cargoCodigo").toUpperCase());
        empleado.setPedPedepCodigo(empleado.getPedepCodigo());

        return empleado;
    }

    private List<Familiar> construirFamiliares(HttpServletRequest request,
            String codigoPersona, List<String> errores) {
        List<Familiar> familiares = new ArrayList<>();
        String json = valor(request, "familiaresJson");

        if (json.isEmpty()) {
            return familiares;
        }

        try {
            List<Map<String, String>> objetos = parsearListaJson(json);

            for (Map<String, String> item : objetos) {
                Familiar familiar = new Familiar();
                familiar.setCodigo(valorMapa(item, "codigo").toUpperCase());
                familiar.setCodigoPersona(codigoPersona);
                familiar.setCodigoParentesco(valorMapa(item, "codigoParentesco").toUpperCase());
                familiar.setDescripcionParentesco(valorMapa(item, "descripcionParentesco"));
                familiar.setNombre(valorMapa(item, "nombre"));
                familiar.setApellido(valorMapa(item, "apellido"));
                familiar.setFechaNacimiento(valorMapa(item, "fechaNacimiento"));
                familiar.setTelefono(valorMapa(item, "telefono"));
                familiar.setCargaFamiliar(normalizarCargaFamiliar(valorMapa(item, "cargaFamiliar")));
                familiar.setObservacion(valorMapa(item, "observacion"));
                familiares.add(familiar);
            }
        } catch (IllegalArgumentException e) {
            errores.add("La informacion familiar enviada no es valida.");
        }

        return familiares;
    }

    private List<Formacion> construirFormaciones(HttpServletRequest request, String codigoEmpleado) {
        List<Formacion> formaciones = new ArrayList<>();
        Formacion formacion = new Formacion();
        formacion.setCodigoEmpleado(codigoEmpleado);
        formacion.setNivel(valor(request, "formacionNivel"));
        formacion.setTitulo(valor(request, "formacionTitulo"));
        formacion.setInstitucion(valor(request, "formacionInstitucion"));
        formacion.setFechaInicio(valor(request, "formacionInicio"));
        formacion.setFechaFin(valor(request, "formacionFin"));
        formacion.setObservacion(valor(request, "formacionObservacion"));

        if (formacionTieneDatos(formacion)) {
            formaciones.add(formacion);
        }

        return formaciones;
    }

    private boolean formacionTieneDatos(Formacion formacion) {
        return formacion != null
                && (!vacio(formacion.getNivel())
                || !vacio(formacion.getTitulo())
                || !vacio(formacion.getInstitucion())
                || !vacio(formacion.getFechaInicio())
                || !vacio(formacion.getFechaFin())
                || !vacio(formacion.getObservacion()));
    }

    private List<String> validarFamiliares(List<Familiar> familiares) {
        List<String> errores = new ArrayList<>();

        if (familiares == null || familiares.isEmpty()) {
            return errores;
        }

        Set<String> codigosParentesco = new HashSet<>();

        for (Parentesco parentesco : familiarDAO.listarParentescos()) {
            if (parentesco.getCodigo() != null) {
                codigosParentesco.add(parentesco.getCodigo().trim().toUpperCase());
            }
        }

        for (int i = 0; i < familiares.size(); i++) {
            Familiar familiar = familiares.get(i);
            String prefijo = "Familiar " + (i + 1) + ": ";

            validarObligatorio(errores, familiar.getNombre(), prefijo + "los nombres son obligatorios.");
            validarObligatorio(errores, familiar.getApellido(), prefijo + "los apellidos son obligatorios.");
            validarObligatorio(errores, familiar.getCodigoParentesco(), prefijo + "debe seleccionar el parentesco.");
            validarObligatorio(
                    errores,
                    familiar.getFechaNacimiento(),
                    prefijo + "la fecha de nacimiento es obligatoria."
            );
            validarObligatorio(errores, familiar.getTelefono(), prefijo + "el telefono es obligatorio.");
            validarObligatorio(errores, familiar.getCargaFamiliar(), prefijo + "debe indicar si es carga familiar.");

            validarLongitud(errores, familiar.getNombre(), 30, prefijo + "los nombres no deben superar 30 caracteres.");
            validarLongitud(
                    errores,
                    familiar.getApellido(),
                    30,
                    prefijo + "los apellidos no deben superar 30 caracteres."
            );
            validarLongitud(
                    errores,
                    familiar.getObservacion(),
                    200,
                    prefijo + "la observacion no debe superar 200 caracteres."
            );

            if (!familiar.getNombre().isEmpty()
                    && !familiar.getNombre().matches("^[\\p{L} ]+$")) {
                errores.add(prefijo + "los nombres solo deben contener letras y espacios.");
            }

            if (!familiar.getApellido().isEmpty()
                    && !familiar.getApellido().matches("^[\\p{L} ]+$")) {
                errores.add(prefijo + "los apellidos solo deben contener letras y espacios.");
            }

            if (!familiar.getCodigoParentesco().isEmpty()
                    && !codigosParentesco.contains(familiar.getCodigoParentesco())) {
                errores.add(prefijo + "el parentesco seleccionado no existe.");
            }

            if (!familiar.getFechaNacimiento().isEmpty()) {
                validarFechaFamiliar(errores, familiar.getFechaNacimiento(), prefijo);
            }

            if (!familiar.getTelefono().isEmpty()
                    && !familiar.getTelefono().matches("^(09|0[2-7])\\d{8}$")) {
                errores.add(prefijo + "el telefono debe tener 10 digitos e iniciar con 09, 02, 03, 04, 05, 06 o 07.");
            }

            if (!familiar.getCargaFamiliar().isEmpty()
                    && !("S".equals(familiar.getCargaFamiliar()) || "N".equals(familiar.getCargaFamiliar()))) {
                errores.add(prefijo + "carga familiar solo permite S o N.");
            }
        }

        return errores;
    }

    private List<String> validarFormaciones(List<Formacion> formaciones) {
        List<String> errores = new ArrayList<>();

        if (formaciones == null || formaciones.isEmpty()) {
            return errores;
        }

        for (int i = 0; i < formaciones.size(); i++) {
            Formacion formacion = formaciones.get(i);
            String prefijo = "Formacion " + (i + 1) + ": ";

            if (!formacionTieneDatos(formacion)) {
                continue;
            }

            validarObligatorio(errores, formacion.getNivel(), prefijo + "el nivel es obligatorio.");
            validarObligatorio(errores, formacion.getTitulo(), prefijo + "el titulo es obligatorio.");
            validarObligatorio(errores, formacion.getInstitucion(), prefijo + "la institucion es obligatoria.");
            validarObligatorio(errores, formacion.getFechaInicio(), prefijo + "la fecha de inicio es obligatoria.");

            validarLongitud(errores, formacion.getNivel(), 40, prefijo + "el nivel no debe superar 40 caracteres.");
            validarLongitud(errores, formacion.getTitulo(), 80, prefijo + "el titulo no debe superar 80 caracteres.");
            validarLongitud(errores, formacion.getInstitucion(), 80,
                    prefijo + "la institucion no debe superar 80 caracteres.");
            validarLongitud(errores, formacion.getObservacion(), 200,
                    prefijo + "la observacion no debe superar 200 caracteres.");

            validarFechasFormacion(errores, formacion, prefijo);
        }

        return errores;
    }

    private void validarFechasFormacion(List<String> errores, Formacion formacion, String prefijo) {
        LocalDate fechaInicio = null;
        LocalDate fechaFin = null;

        if (!vacio(formacion.getFechaInicio())) {
            try {
                fechaInicio = LocalDate.parse(formacion.getFechaInicio());
            } catch (DateTimeParseException e) {
                errores.add(prefijo + "la fecha de inicio debe tener formato AAAA-MM-DD.");
            }
        }

        if (!vacio(formacion.getFechaFin())) {
            try {
                fechaFin = LocalDate.parse(formacion.getFechaFin());
            } catch (DateTimeParseException e) {
                errores.add(prefijo + "la fecha de fin debe tener formato AAAA-MM-DD.");
            }
        }

        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            errores.add(prefijo + "la fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }

    private void validarFechaFamiliar(List<String> errores, String fechaNacimiento, String prefijo) {
        try {
            LocalDate fecha = LocalDate.parse(fechaNacimiento);

            if (fecha.isAfter(LocalDate.now())) {
                errores.add(prefijo + "la fecha de nacimiento no puede ser futura.");
            }
        } catch (DateTimeParseException e) {
            errores.add(prefijo + "la fecha de nacimiento debe tener formato AAAA-MM-DD.");
        }
    }

    private void actualizarCargasFamiliares(Empleado empleado) {
        if (empleado == null || empleado.getPersona() == null) {
            return;
        }

        empleado.getPersona().setCargasFamiliares(contarCargasFamiliares(empleado.getFamiliares()));
    }

    private int contarCargasFamiliares(List<Familiar> familiares) {
        if (familiares == null) {
            return 0;
        }

        int total = 0;

        for (Familiar familiar : familiares) {
            if (familiar != null && "S".equals(normalizarCargaFamiliar(familiar.getCargaFamiliar()))) {
                total++;
            }
        }

        return total;
    }

    private String normalizarCargaFamiliar(String valor) {
        String carga = valor == null ? "" : valor.trim().toUpperCase(Locale.ROOT);

        if ("SI".equals(carga) || "SÍ".equals(carga)) {
            return "S";
        }

        if ("NO".equals(carga)) {
            return "N";
        }

        return carga;
    }

    private String valorMapa(Map<String, String> mapa, String clave) {
        String valor = mapa.get(clave);
        return valor == null ? "" : valor.trim();
    }

    private Part obtenerFotoPart(HttpServletRequest request) throws IOException, ServletException {
        try {
            return request.getPart("fotoEmpleado");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private boolean fotoSeleccionada(Part fotoPart) {
        return fotoPart != null && fotoPart.getSize() > 0;
    }

    private String validarFoto(Part fotoPart) {
        if (!fotoSeleccionada(fotoPart)) {
            return null;
        }

        if (fotoPart.getSize() > FOTO_MAX_BYTES) {
            return "La foto no debe superar 2 MB.";
        }

        String nombre = fotoPart.getSubmittedFileName();
        String extension = obtenerExtension(nombre);

        if (!FOTO_EXT_VALIDAS.contains(extension)) {
            return "La foto debe ser una imagen jpg, jpeg, png o webp.";
        }

        String contentType = fotoPart.getContentType();

        if (contentType == null || !FOTO_MIME_VALIDOS.contains(contentType.toLowerCase(Locale.ROOT))) {
            return "El tipo de archivo de la foto no es valido.";
        }

        return null;
    }

    private String guardarFoto(Part fotoPart, String codigoPersona) throws IOException {
        String realPath = getServletContext().getRealPath("/" + FOTO_DIR);

        if (realPath == null) {
            throw new IOException("No se pudo resolver la carpeta de subida de fotos.");
        }

        Path directorio = Paths.get(realPath).toAbsolutePath().normalize();
        Files.createDirectories(directorio);

        String extension = obtenerExtension(fotoPart.getSubmittedFileName());
        String nombreArchivo = codigoPersona + "_" + System.currentTimeMillis() + "." + extension;
        Path destino = directorio.resolve(nombreArchivo).normalize();

        if (!destino.startsWith(directorio)) {
            throw new IOException("Ruta de foto no valida.");
        }

        try (InputStream entrada = fotoPart.getInputStream()) {
            Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
        }

        return FOTO_DIR + "/" + nombreArchivo;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null) {
            return "";
        }

        int punto = nombreArchivo.lastIndexOf('.');

        if (punto < 0 || punto == nombreArchivo.length() - 1) {
            return "";
        }

        return nombreArchivo.substring(punto + 1).toLowerCase(Locale.ROOT);
    }

    private void eliminarFotoAnterior(String fotoAnterior, String fotoNueva) {
        if (fotoAnterior == null || fotoAnterior.trim().isEmpty() || fotoAnterior.equals(fotoNueva)) {
            return;
        }

        eliminarFotoSiExiste(fotoAnterior);
    }

    private void eliminarFotoSiExiste(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.trim().isEmpty() || !rutaRelativa.startsWith(FOTO_DIR + "/")) {
            return;
        }

        String rootPath = getServletContext().getRealPath("/");

        if (rootPath == null) {
            return;
        }

        try {
            Path root = Paths.get(rootPath).toAbsolutePath().normalize();
            Path carpetaFotos = root.resolve(FOTO_DIR).normalize();
            Path archivo = root.resolve(rutaRelativa).normalize();

            if (archivo.startsWith(carpetaFotos)) {
                Files.deleteIfExists(archivo);
            }
        } catch (IOException e) {
            System.out.println("No se pudo eliminar foto anterior: " + e.getMessage());
        }
    }

    private List<String> validarEmpleado(Empleado empleado, boolean nuevo) {
        List<String> errores = new ArrayList<>();
        Persona persona = empleado.getPersona();

        validarObligatorio(errores, persona.getPeperCodigo(), "El codigo de persona es obligatorio.");
        validarObligatorio(errores, empleado.getPeempCodigo(), "El codigo de empleado es obligatorio.");
        validarObligatorio(errores, persona.getCedula(), "La cedula es obligatoria.");
        validarObligatorio(errores, persona.getNombres(), "Los nombres son obligatorios.");
        validarObligatorio(errores, persona.getApellidos(), "Los apellidos son obligatorios.");
        validarObligatorio(errores, persona.getFechaNacimiento(), "La fecha de nacimiento es obligatoria.");
        validarObligatorio(errores, persona.getPesexCodigo(), "Debe seleccionar el sexo.");
        validarObligatorio(errores, persona.getDireccion(), "La direccion es obligatoria.");
        validarObligatorio(errores, persona.getEmail(), "El email es obligatorio.");
        validarObligatorio(errores, empleado.getPedepCodigo(), "Debe seleccionar el departamento.");
        validarObligatorio(errores, empleado.getPecarCodigo(), "Debe seleccionar el cargo.");

        validarLongitud(errores, persona.getPeperCodigo(), 10, "El codigo de persona no debe superar 10 caracteres.");
        validarLongitud(errores, empleado.getPeempCodigo(), 10, "El codigo de empleado no debe superar 10 caracteres.");
        validarLongitud(
                errores,
                empleado.getPedepCodigo(),
                3,
                "El codigo de departamento debe tener maximo 3 caracteres."
        );
        validarLongitud(errores, empleado.getPecarCodigo(), 3, "El codigo de cargo debe tener maximo 3 caracteres.");
        validarLongitud(errores, persona.getNombres(), 15, "Los nombres no deben superar 15 caracteres.");
        validarLongitud(errores, persona.getApellidos(), 15, "Los apellidos no deben superar 15 caracteres.");
        validarLongitud(errores, persona.getDireccion(), 100, "La direccion no debe superar 100 caracteres.");
        validarLongitud(errores, persona.getEmail(), 100, "El email no debe superar 100 caracteres.");

        if (!persona.getPeperCodigo().isEmpty() && !persona.getPeperCodigo().matches("^[A-Z0-9]+$")) {
            errores.add("El codigo de persona solo debe contener letras y numeros, sin espacios.");
        }

        if (!empleado.getPeempCodigo().isEmpty() && !empleado.getPeempCodigo().matches("^[A-Z0-9]+$")) {
            errores.add("El codigo de empleado solo debe contener letras y numeros, sin espacios.");
        }

        if (!persona.getCedula().isEmpty() && !cedulaEcuatorianaValida(persona.getCedula())) {
            errores.add("La cedula ingresada no es valida.");
        }

        if (!persona.getNombres().isEmpty() && !persona.getNombres().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,15}$")) {
            errores.add("Los nombres solo deben contener letras y espacios.");
        }

        if (!persona.getApellidos().isEmpty() && !persona.getApellidos().matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,15}$")) {
            errores.add("Los apellidos solo deben contener letras y espacios.");
        }

        if (!persona.getFechaNacimiento().isEmpty()) {
            validarFechaNacimiento(errores, persona.getFechaNacimiento());
        }

        if (persona.getDireccion() != null && persona.getDireccion().trim().isEmpty()) {
            errores.add("La direccion no puede contener solo espacios.");
        }

        if (!persona.getCelular().isEmpty() && !persona.getCelular().matches("^09\\d{8}$")) {
            errores.add("El celular debe tener 10 digitos e iniciar con 09.");
        }

        if (!persona.getTelefonoDomicilio().isEmpty()
                && !persona.getTelefonoDomicilio().matches("^0[2-7]\\d{8}$")) {
            errores.add("El telefono de domicilio debe tener 10 digitos e iniciar con 02, 03, 04, 05, 06 o 07.");
        }

        if (!persona.getEmail().isEmpty()
                && !persona.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errores.add("Ingrese un email valido.");
        }

        if (persona.getCargasFamiliares() < 0 || persona.getCargasFamiliares() > 99) {
            errores.add("Las cargas familiares deben estar entre 0 y 99.");
        }

        if (!empleado.getPedepCodigo().isEmpty()
                && !empleado.getPecarCodigo().isEmpty()
                && !empleadoDAO.cargoPerteneceDepartamento(empleado.getPedepCodigo(), empleado.getPecarCodigo())) {
            errores.add("El cargo seleccionado no pertenece al departamento indicado.");
        }

        return errores;
    }

    private void cargarDatosBase(HttpServletRequest request) {
        if (request.getAttribute("empleados") == null) {
            request.setAttribute("empleados", empleadoDAO.listarEmpleados());
        }

        request.setAttribute("departamentos", empleadoDAO.listarDepartamentos());
        request.setAttribute("cargos", empleadoDAO.listarCargos());
        request.setAttribute("sexos", empleadoDAO.listarSexos());
        request.setAttribute("estadosCiviles", empleadoDAO.listarEstadosCiviles());
        request.setAttribute("parentescos", familiarDAO.listarParentescos());
    }

    private void volverAlFormulario(HttpServletRequest request, HttpServletResponse response,
            String modo, Empleado empleado, String error) throws ServletException, IOException {
        request.setAttribute("modo", modo);
        request.setAttribute("empleadoForm", empleado);
        request.setAttribute("error", error);
        cargarDatosBase(request);
        request.getRequestDispatcher("empleados.jsp").forward(request, response);
    }

    private void responderCargosPorDepartamento(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String codigoDepartamento = valor(request, "departamento");
        List<Cargo> cargos = empleadoDAO.listarCargosPorDepartamento(codigoDepartamento);

        response.setContentType("application/json;charset=UTF-8");
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < cargos.size(); i++) {
            Cargo cargo = cargos.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{\"departamento\":\"").append(escaparJson(cargo.getPedepCodigo())).append("\",")
                    .append("\"codigo\":\"").append(escaparJson(cargo.getPecarCodigo())).append("\",")
                    .append("\"descripcion\":\"").append(escaparJson(cargo.getPecarDescri())).append("\"}");
        }

        json.append("]");
        response.getWriter().write(json.toString());
    }

    private List<Map<String, String>> parsearListaJson(String json) {
        List<Map<String, String>> lista = new ArrayList<>();
        int[] pos = {0};

        saltarEspacios(json, pos);
        esperarCaracter(json, pos, '[');
        saltarEspacios(json, pos);

        if (leerSi(json, pos, ']')) {
            return lista;
        }

        while (pos[0] < json.length()) {
            lista.add(parsearObjetoJson(json, pos));
            saltarEspacios(json, pos);

            if (leerSi(json, pos, ',')) {
                continue;
            }

            if (leerSi(json, pos, ']')) {
                saltarEspacios(json, pos);

                if (pos[0] != json.length()) {
                    throw new IllegalArgumentException("JSON con contenido extra.");
                }

                return lista;
            }

            throw new IllegalArgumentException("JSON de familiares invalido.");
        }

        throw new IllegalArgumentException("JSON de familiares incompleto.");
    }

    private Map<String, String> parsearObjetoJson(String json, int[] pos) {
        Map<String, String> mapa = new LinkedHashMap<>();

        saltarEspacios(json, pos);
        esperarCaracter(json, pos, '{');
        saltarEspacios(json, pos);

        if (leerSi(json, pos, '}')) {
            return mapa;
        }

        while (pos[0] < json.length()) {
            String clave = parsearCadenaJson(json, pos);
            saltarEspacios(json, pos);
            esperarCaracter(json, pos, ':');
            saltarEspacios(json, pos);
            String valor = parsearValorJson(json, pos);
            mapa.put(clave, valor);
            saltarEspacios(json, pos);

            if (leerSi(json, pos, ',')) {
                continue;
            }

            if (leerSi(json, pos, '}')) {
                return mapa;
            }

            throw new IllegalArgumentException("Objeto JSON invalido.");
        }

        throw new IllegalArgumentException("Objeto JSON incompleto.");
    }

    private String parsearValorJson(String json, int[] pos) {
        if (pos[0] >= json.length()) {
            throw new IllegalArgumentException("Valor JSON incompleto.");
        }

        if (json.charAt(pos[0]) == '"') {
            return parsearCadenaJson(json, pos);
        }

        if (json.startsWith("null", pos[0])) {
            pos[0] += 4;
            return "";
        }

        throw new IllegalArgumentException("Valor JSON no soportado.");
    }

    private String parsearCadenaJson(String json, int[] pos) {
        esperarCaracter(json, pos, '"');
        StringBuilder resultado = new StringBuilder();

        while (pos[0] < json.length()) {
            char actual = json.charAt(pos[0]++);

            if (actual == '"') {
                return resultado.toString();
            }

            if (actual != '\\') {
                resultado.append(actual);
                continue;
            }

            if (pos[0] >= json.length()) {
                throw new IllegalArgumentException("Escape JSON incompleto.");
            }

            char escape = json.charAt(pos[0]++);

            switch (escape) {
                case '"':
                case '\\':
                case '/':
                    resultado.append(escape);
                    break;
                case 'b':
                    resultado.append('\b');
                    break;
                case 'f':
                    resultado.append('\f');
                    break;
                case 'n':
                    resultado.append('\n');
                    break;
                case 'r':
                    resultado.append('\r');
                    break;
                case 't':
                    resultado.append('\t');
                    break;
                case 'u':
                    resultado.append(parsearUnicodeJson(json, pos));
                    break;
                default:
                    throw new IllegalArgumentException("Escape JSON no valido.");
            }
        }

        throw new IllegalArgumentException("Cadena JSON incompleta.");
    }

    private char parsearUnicodeJson(String json, int[] pos) {
        if (pos[0] + 4 > json.length()) {
            throw new IllegalArgumentException("Unicode JSON incompleto.");
        }

        String hex = json.substring(pos[0], pos[0] + 4);
        pos[0] += 4;

        try {
            return (char) Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unicode JSON invalido.");
        }
    }

    private void esperarCaracter(String json, int[] pos, char esperado) {
        saltarEspacios(json, pos);

        if (pos[0] >= json.length() || json.charAt(pos[0]) != esperado) {
            throw new IllegalArgumentException("Se esperaba " + esperado + ".");
        }

        pos[0]++;
    }

    private boolean leerSi(String json, int[] pos, char esperado) {
        saltarEspacios(json, pos);

        if (pos[0] < json.length() && json.charAt(pos[0]) == esperado) {
            pos[0]++;
            return true;
        }

        return false;
    }

    private void saltarEspacios(String json, int[] pos) {
        while (pos[0] < json.length() && Character.isWhitespace(json.charAt(pos[0]))) {
            pos[0]++;
        }
    }

    private String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private int entero(HttpServletRequest request, String nombre) {
        try {
            return Integer.parseInt(valor(request, nombre));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void validarObligatorio(List<String> errores, String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            errores.add(mensaje);
        }
    }

    private void validarLongitud(List<String> errores, String valor, int maximo, String mensaje) {
        if (valor != null && valor.length() > maximo) {
            errores.add(mensaje);
        }
    }

    private void validarFechaNacimiento(List<String> errores, String fechaNacimiento) {
        try {
            LocalDate fecha = LocalDate.parse(fechaNacimiento);
            LocalDate hoy = LocalDate.now();

            if (fecha.isAfter(hoy)) {
                errores.add("La fecha de nacimiento no puede ser futura.");
                return;
            }

            if (Period.between(fecha, hoy).getYears() < 18) {
                errores.add("El empleado debe tener al menos 18 anios.");
            }
        } catch (DateTimeParseException e) {
            errores.add("La fecha de nacimiento debe tener formato AAAA-MM-DD.");
        }
    }

    private boolean cedulaEcuatorianaValida(String cedula) {
        if (cedula == null || !cedula.matches("^\\d{10}$")) {
            return false;
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));

        if (provincia < 1 || provincia > 24 || tercerDigito >= 6) {
            return false;
        }

        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        for (int i = 0; i < coeficientes.length; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];

            if (valor > 9) {
                valor -= 9;
            }

            suma += valor;
        }

        int decenaSuperior = ((suma + 9) / 10) * 10;
        int digitoVerificador = decenaSuperior - suma;

        if (digitoVerificador == 10) {
            digitoVerificador = 0;
        }

        return digitoVerificador == Character.getNumericValue(cedula.charAt(9));
    }

    private String unirErrores(List<String> errores) {
        StringBuilder mensaje = new StringBuilder();

        for (String error : errores) {
            if (mensaje.length() > 0) {
                mensaje.append(" ");
            }

            mensaje.append(error);
        }

        return mensaje.toString();
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
