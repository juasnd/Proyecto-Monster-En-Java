<%--
    Document   : empleados
    CRUD de empleados basado en peemp_emplea y peper_person.
--%>

<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Cargo"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Catalogo"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Departamento"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Empleado"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Familiar"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Formacion"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Parentesco"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Persona"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%!
    private String h(Object valor) {
        if (valor == null) {
            return "";
        }

        String texto = String.valueOf(valor);
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String selected(String actual, String opcion) {
        if (actual == null || opcion == null) {
            return "";
        }

        return actual.equals(opcion) ? "selected" : "";
    }

    private String url(Object valor) {
        try {
            return URLEncoder.encode(valor == null ? "" : String.valueOf(valor), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private String edad(String fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) {
            return "";
        }

        try {
            java.time.LocalDate fecha = java.time.LocalDate.parse(fechaNacimiento);
            return String.valueOf(java.time.Period.between(fecha, java.time.LocalDate.now()).getYears());
        } catch (Exception e) {
            return "";
        }
    }

    private String jsonFamiliares(List<Familiar> familiares) {
        if (familiares == null || familiares.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < familiares.size(); i++) {
            Familiar familiar = familiares.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"codigo\":\"").append(json(familiar.getCodigo())).append("\",")
                    .append("\"codigoParentesco\":\"").append(json(familiar.getCodigoParentesco())).append("\",")
                    .append("\"descripcionParentesco\":\"")
                    .append(json(familiar.getDescripcionParentesco()))
                    .append("\",")
                    .append("\"nombre\":\"").append(json(familiar.getNombre())).append("\",")
                    .append("\"apellido\":\"").append(json(familiar.getApellido())).append("\",")
                    .append("\"fechaNacimiento\":\"").append(json(familiar.getFechaNacimiento())).append("\",")
                    .append("\"telefono\":\"").append(json(familiar.getTelefono())).append("\",")
                    .append("\"cargaFamiliar\":\"").append(json(familiar.getCargaFamiliar())).append("\",")
                    .append("\"observacion\":\"").append(json(familiar.getObservacion())).append("\"")
                    .append("}");
        }

        json.append("]");
        return json.toString();
    }

    private String json(Object valor) {
        if (valor == null) {
            return "";
        }

        return String.valueOf(valor)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String textoCarga(String carga) {
        if ("S".equalsIgnoreCase(carga)) {
            return "S\u00ed";
        }

        if ("N".equalsIgnoreCase(carga)) {
            return "No";
        }

        return "";
    }
%>

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String modo = (String) request.getAttribute("modo");
    String mensaje = request.getParameter("mensaje");
    String errorUrl = request.getParameter("error");
    String error = (String) request.getAttribute("error");
    String criterioBusqueda = (String) request.getAttribute("criterioBusqueda");
    String terminoBusqueda = (String) request.getAttribute("terminoBusqueda");

    if (modo == null) {
        modo = "listar";
    }

    if (criterioBusqueda == null) {
        criterioBusqueda = request.getParameter("criterioBusqueda");
    }

    if (terminoBusqueda == null) {
        terminoBusqueda = request.getParameter("terminoBusqueda");
    }

    if (criterioBusqueda == null || criterioBusqueda.trim().isEmpty()) {
        criterioBusqueda = "codigo";
    }

    if (terminoBusqueda == null) {
        terminoBusqueda = "";
    }

    Empleado empleadoForm = (Empleado) request.getAttribute("empleadoForm");

    if (empleadoForm == null) {
        empleadoForm = new Empleado();
    }

    Persona personaForm = empleadoForm.getPersona();

    if (personaForm == null) {
        personaForm = new Persona();
        empleadoForm.setPersona(personaForm);
    }

    List<Empleado> empleados = (List<Empleado>) request.getAttribute("empleados");
    List<Departamento> departamentos = (List<Departamento>) request.getAttribute("departamentos");
    List<Cargo> cargos = (List<Cargo>) request.getAttribute("cargos");
    List<Catalogo> sexos = (List<Catalogo>) request.getAttribute("sexos");
    List<Catalogo> estadosCiviles = (List<Catalogo>) request.getAttribute("estadosCiviles");
    List<Parentesco> parentescos = (List<Parentesco>) request.getAttribute("parentescos");
    List<Familiar> familiaresForm = empleadoForm.getFamiliares();
    List<Formacion> formacionesForm = empleadoForm.getFormaciones();

    if (familiaresForm == null) {
        familiaresForm = java.util.Collections.emptyList();
    }

    if (formacionesForm == null) {
        formacionesForm = java.util.Collections.emptyList();
    }

    Formacion formacionForm = formacionesForm.isEmpty() ? new Formacion() : formacionesForm.get(0);

    boolean lectura = "ver".equals(modo);
    boolean edicion = "editar".equals(modo);
    boolean nuevo = "nuevo".equals(modo);
    boolean mostrarFormulario = nuevo || edicion || (error != null && !lectura);
    boolean mostrarModalVer = lectura;
    String disabled = "";
    String readonlyCodigo = "readonly";
    String accionFormulario = edicion ? "actualizar" : "guardar";
    String tituloFormulario = lectura ? "Detalle de empleado" : (edicion ? "Editar empleado" : "Añadir empleado");
    String fotoActual = personaForm.getFoto();
    boolean tieneFotoActual = fotoActual != null && !fotoActual.trim().isEmpty();
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Empleados | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260616-empleados1">
    </head>

    <body class="body-dashboard">


        <main class="crud-page empleado-page">

            <section class="crud-titulo">
                <div>
                    <h2>Gestión de Empleados</h2>
                    <p>Administra la ficha personal, cargo y departamento de cada empleado.</p>
                </div>
            </section>

            <% if ("guardado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Empleado guardado correctamente.</p>
            <% } else if ("actualizado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Empleado actualizado correctamente.</p>
            <% } else if ("eliminado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Empleado eliminado correctamente.</p>
            <% } %>

            <% if (error != null) { %>
                <p class="mensaje-error-general"><%= h(error) %></p>
            <% } else if ("en_uso".equals(errorUrl)) { %>
                <p class="mensaje-error-general">
                    No se puede eliminar el empleado porque tiene registros relacionados.
                </p>
            <% } else if ("no_eliminado".equals(errorUrl)) { %>
                <p class="mensaje-error-general">No se pudo eliminar el empleado.</p>
            <% } else if ("no_encontrado".equals(errorUrl)) { %>
                <p class="mensaje-error-general">No se encontró el empleado solicitado.</p>
            <% } %>

            <section class="crud-contenedor empleados-panel">

                <form
                    class="empleado-busqueda"
                    action="${pageContext.request.contextPath}/EmpleadoController"
                    method="get"
                >
                    <input type="hidden" name="accion" value="buscar">

                    <div class="grupo-campo">
                        <label for="criterioBusqueda">Criterio</label>
                        <select id="criterioBusqueda" name="criterioBusqueda">
                            <option value="codigo" <%= selected(criterioBusqueda, "codigo") %>>Código empleado</option>
                            <option value="nombres" <%= selected(criterioBusqueda, "nombres") %>>Nombres</option>
                            <option value="apellidos" <%= selected(criterioBusqueda, "apellidos") %>>Apellidos</option>
                            <option value="cedula" <%= selected(criterioBusqueda, "cedula") %>>Cédula</option>
                        </select>
                    </div>

                    <div class="grupo-campo busqueda-input">
                        <label for="terminoBusqueda">Búsqueda</label>
                        <input
                            type="text"
                            id="terminoBusqueda"
                            name="terminoBusqueda"
                            value="<%= h(terminoBusqueda) %>"
                            placeholder="Buscar empleado..."
                        >
                    </div>

                    <div class="busqueda-acciones">
                        <button type="submit" class="btn-crud-form">Buscar</button>
                        <a
                            href="${pageContext.request.contextPath}/EmpleadoController"
                            class="btn-crud celeste"
                        >
                            Mostrar todos
                        </a>

                        <button type="button" class="btn-crud reporte" data-reporte-toggle="reporteEmpleados">
                            Reportes
                        </button>
                    </div>
                </form>



                <section id="reporteEmpleados"
                         class="reporte-panel"
                         data-reporte-panel
                         data-reporte-tabla="tablaEmpleados"
                         data-reporte-columnas="0,1,2,3,4"
                         data-reporte-encabezados="Nombre completo|Celular|Correo|Departamento|Cargo"
                         data-reporte-titulo="Reporte de Empleados"
                         data-reporte-archivo="reporte_empleados"
                         hidden>
                    <div class="reporte-panel-header">
                        <div>
                            <h3>Reportes de Empleados</h3>
                            <p>Vista previa generada con la informacion visible del modulo.</p>
                        </div>
                        <div class="reporte-meta">
                            <span>Registros: <strong data-reporte-total>0</strong></span>
                            <span>Fecha: <strong data-reporte-fecha></strong></span>
                        </div>
                    </div>

                    <div class="reporte-acciones">
                        <button
                            type="button"
                            class="btn-crud celeste"
                            data-reporte-accion="actualizar"
                        >
                            Vista previa
                        </button>
                        <button type="button" class="btn-crud reporte" data-reporte-accion="pdf">PDF</button>
                        <button type="button" class="btn-crud reporte" data-reporte-accion="excel">Excel</button>
                        <button type="button" class="btn-crud reporte" data-reporte-accion="csv">CSV</button>
                    </div>

                    <div class="tabla-contenedor tabla-clara reporte-preview">
                        <table class="tabla-crud" data-reporte-preview>
                            <thead></thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </section>

                <div class="tabla-contenedor tabla-clara tabla-empleados">
                    <table
                        class="tabla-crud"
                        id="tablaEmpleados"
                        data-paginacion="true"
                        data-paginacion-tamanio="3"
                    >
                        <thead>
                            <tr>
                                <th>Nombre completo</th>
                                <th>Celular</th>
                                <th>Correo</th>
                                <th>Departamento</th>
                                <th>Cargo</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% if (empleados != null && !empleados.isEmpty()) { %>
                                <% for (Empleado empleado : empleados) {
                                    Persona persona = empleado.getPersona();
                                    String nombreTabla = persona != null
                                            ? h(persona.getNombres()) + " " + h(persona.getApellidos())
                                            : "";
                                    String codigoEmpleadoUrl = url(empleado.getPeempCodigo());
                                    String editarEmpleadoUrl = request.getContextPath()
                                            + "/EmpleadoController?accion=editar&codigo="
                                            + codigoEmpleadoUrl;
                                    String verEmpleadoUrl = request.getContextPath()
                                            + "/EmpleadoController?accion=ver&codigo="
                                            + codigoEmpleadoUrl;
                                    String eliminarEmpleadoUrl = request.getContextPath()
                                            + "/EmpleadoController?accion=eliminar&codigo="
                                            + codigoEmpleadoUrl;
                                %>
                                    <tr 
                                        data-codigo="<%= h(empleado.getPeempCodigo()) %>"
                                        data-nombres="<%= h(persona != null ? persona.getNombres() : "") %>"
                                        data-apellidos="<%= h(persona != null ? persona.getApellidos() : "") %>"
                                        data-cedula="<%= h(persona != null ? persona.getCedula() : "") %>"
                                    >
                                        <td class="empleado-col-nombre">
                                            <strong><%= nombreTabla %></strong>
                                        </td>
                                        <td><%= h(persona != null ? persona.getCelular() : "") %></td>
                                        <td><%= h(persona != null ? persona.getEmail() : "") %></td>
                                        <td><%= h(empleado.getNombreDepartamento()) %></td>
                                        <td><%= h(empleado.getNombreCargo()) %></td>
                                        <td>
                                            <div class="acciones-tabla">
                                                <a
                                                    class="btn-accion-icono accion-editar"
                                                    href="<%= editarEmpleadoUrl %>"
                                                    title="Editar"
                                                    aria-label="Editar"
                                                >
                                                    <img
                                                        src="${pageContext.request.contextPath}/img/iconos_master_monster/editar.png"
                                                        alt=""
                                                    >
                                                </a>

                                                <a
                                                    class="btn-accion-icono accion-ver"
                                                    href="<%= verEmpleadoUrl %>"
                                                    title="Ver"
                                                    aria-label="Ver"
                                                >
                                                    <img
                                                        src="${pageContext.request.contextPath}/img/iconos_master_monster/ver.png"
                                                        alt=""
                                                    >
                                                </a>

                                                <a
                                                    class="btn-accion-icono accion-eliminar"
                                                    href="<%= eliminarEmpleadoUrl %>"
                                                    title="Eliminar"
                                                    aria-label="Eliminar"
                                                    onclick="
                                                        return confirm('¿Seguro que desea eliminar este empleado?');
                                                    "
                                                >
                                                    <img
                                                        src="${pageContext.request.contextPath}/img/iconos_master_monster/eliminar.png"
                                                        alt=""
                                                    >
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="6" class="tabla-vacia">No existen empleados registrados.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <div class="empleado-add-row">
                    <button type="button" id="btnMostrarFormularioEmpleado" class="btn-crud verde">
                        + Añadir empleado
                    </button>
                </div>

                <section 
                    id="panelFormularioEmpleado" 
                    class="panel-empleado-form <%= mostrarFormulario ? "abierto" : "" %>"
                    data-modo="<%= h(modo) %>"
                    role="dialog"
                    aria-modal="true"
                    aria-labelledby="tituloFormularioEmpleado"
                    <%= mostrarFormulario ? "" : "hidden" %>
                >
                    <form
                        id="formEmpleado"
                        action="${pageContext.request.contextPath}/EmpleadoController"
                        method="post"
                        enctype="multipart/form-data"
                        data-tabs-scope
                        novalidate
                    >
                        <input type="hidden" id="accionEmpleado" name="accion" value="<%= h(accionFormulario) %>">
                        <input type="hidden" id="fotoActual" name="fotoActual" value="<%= h(fotoActual) %>">
                        <input
                            type="hidden"
                            id="codigoPersona"
                            name="codigoPersona"
                            value="<%= h(personaForm.getPeperCodigo()) %>"
                        >
                        <input
                            type="hidden"
                            id="cargasFamiliares"
                            name="cargasFamiliares"
                            value="<%= familiaresForm == null ? 0 : familiaresForm.size() %>"
                        >
                        <input
                            type="hidden"
                            id="familiaresJson"
                            name="familiaresJson"
                            value="<%= h(jsonFamiliares(familiaresForm)) %>"
                        >

                        <button
                            type="button"
                            id="btnCerrarFormularioEmpleado"
                            class="form-modal-cerrar"
                            aria-label="Cerrar formulario de empleado"
                            title="Cerrar"
                        >
                            &times;
                        </button>

                        <div class="ficha-empleado-header compacta">
                            <div class="ficha-resumen ficha-identidad">
                                <h3 id="tituloFormularioEmpleado"><%= h(tituloFormulario) %></h3>
                                <p class="ficha-ayuda">
                                    Complete los datos obligatorios y seleccione primero el
                                    departamento para cargar sus cargos.
                                </p>

                                <div class="identidad-campos">
                                    <input
                                        type="hidden"
                                        id="codigoEmpleado"
                                        name="codigoEmpleado"
                                        value="<%= h(empleadoForm.getPeempCodigo()) %>"
                                    >

                                    <div class="grupo-campo">
                                        <label for="nombres">Nombres</label>
                                        <input
                                            type="text"
                                            id="nombres"
                                            name="nombres"
                                            maxlength="15"
                                            value="<%= h(personaForm.getNombres()) %>"
                                            <%= disabled %>
                                            required
                                        >
                                        <small class="field-error-message" data-error-for="nombres"></small>
                                    </div>

                                    <div class="grupo-campo">
                                        <label for="apellidos">Apellidos</label>
                                        <input
                                            type="text"
                                            id="apellidos"
                                            name="apellidos"
                                            maxlength="15"
                                            value="<%= h(personaForm.getApellidos()) %>"
                                            <%= disabled %>
                                            required
                                        >
                                        <small class="field-error-message" data-error-for="apellidos"></small>
                                    </div>
                                </div>
                            </div>

                            <div class="foto-empleado">
                                <div class="foto-marco foto-marco-preview <%= tieneFotoActual ? "con-foto" : "" %>">
                                    <img 
                                        id="previewFotoEmpleado"
                                        src="<%= tieneFotoActual
                                                ? request.getContextPath() + "/" + h(fotoActual)
                                                : "" %>"
                                        alt="Foto del empleado"
                                        <%= tieneFotoActual ? "" : "hidden" %>
                                    >
                                    <span id="textoSinFoto" <%= tieneFotoActual ? "hidden" : "" %>>Sin foto</span>
                                </div>

                                <% if (!lectura) { %>
                                    <label for="fotoEmpleado" class="foto-upload-label">Seleccionar foto</label>
                                    <input 
                                        type="file" 
                                        id="fotoEmpleado" 
                                        name="fotoEmpleado"
                                        accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp"
                                    >
                                    <small class="field-error-message" data-error-for="fotoEmpleado"></small>
                                <% } %>
                            </div>

                        </div>

                        <div class="tabs-empleado" role="tablist" aria-label="Secciones de empleado">
                            <button
                                type="button"
                                class="tab-empleado activo"
                                data-tab="datosGenerales"
                            >
                                Datos Generales
                            </button>
                            <button type="button" class="tab-empleado" data-tab="formacion">Formación</button>
                            <button type="button" class="tab-empleado" data-tab="familia">Información Familiar</button>
                        </div>

                        <section class="tab-panel activo" id="datosGenerales">
                            <div class="form-grid-empleado">
                                <div class="grupo-campo">
                                    <label for="cedula">Cédula</label>
                                    <input
                                        type="text"
                                        id="cedula"
                                        name="cedula"
                                        maxlength="10"
                                        value="<%= h(personaForm.getCedula()) %>"
                                        <%= disabled %>
                                        required
                                    >
                                    <small class="field-error-message" data-error-for="cedula"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="fechaNacimiento">Fecha nacimiento</label>
                                    <input
                                        type="date"
                                        id="fechaNacimiento"
                                        name="fechaNacimiento"
                                        value="<%= h(personaForm.getFechaNacimiento()) %>"
                                        <%= disabled %>
                                        required
                                    >
                                    <small class="field-error-message" data-error-for="fechaNacimiento"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="sexoCodigo">Sexo</label>
                                    <select id="sexoCodigo" name="sexoCodigo" <%= disabled %> required>
                                        <option value="">Seleccione...</option>
                                        <% if (sexos != null) {
                                            for (Catalogo sexo : sexos) { %>
                                                <option
                                                    value="<%= h(sexo.getCodigo()) %>"
                                                    <%= selected(
                                                            personaForm.getPesexCodigo(),
                                                            sexo.getCodigo()
                                                    ) %>
                                                >
                                                    <%= h(sexo.getDescripcion()) %>
                                                </option>
                                        <%  }
                                           } %>
                                    </select>
                                    <small class="field-error-message" data-error-for="sexoCodigo"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="estadoCivilCodigo">Estado civil</label>
                                    <select id="estadoCivilCodigo" name="estadoCivilCodigo" <%= disabled %>>
                                        <option value="">Seleccione...</option>
                                        <% if (estadosCiviles != null) {
                                            for (Catalogo estado : estadosCiviles) { %>
                                                <option
                                                    value="<%= h(estado.getCodigo()) %>"
                                                    <%= selected(
                                                            personaForm.getPeescCodigo(),
                                                            estado.getCodigo()
                                                    ) %>
                                                >
                                                    <%= h(estado.getDescripcion()) %>
                                                </option>
                                        <%  }
                                           } %>
                                    </select>
                                    <small class="field-error-message" data-error-for="estadoCivilCodigo"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="departamentoCodigo">Departamento</label>
                                    <select id="departamentoCodigo" name="departamentoCodigo" <%= disabled %> required>
                                        <option value="">Seleccione...</option>
                                        <% if (departamentos != null) {
                                            for (Departamento dep : departamentos) { %>
                                                <option
                                                    value="<%= h(dep.getCodigo()) %>"
                                                    <%= selected(
                                                            empleadoForm.getPedepCodigo(),
                                                            dep.getCodigo()
                                                    ) %>
                                                >
                                                    <%= h(dep.getDescripcion()) %>
                                                </option>
                                        <%  }
                                           } %>
                                    </select>
                                    <small class="field-error-message" data-error-for="departamentoCodigo"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="cargoCodigo">Cargo</label>
                                    <select
                                        id="cargoCodigo"
                                        name="cargoCodigo"
                                        data-selected="<%= h(empleadoForm.getPecarCodigo()) %>"
                                        <%= disabled %>
                                        required
                                    >
                                        <option value="">Seleccione...</option>
                                        <% if (cargos != null) {
                                            for (Cargo cargo : cargos) {
                                                boolean seleccionadoCargo = cargo.getPecarCodigo() != null
                                                        && cargo.getPecarCodigo().equals(empleadoForm.getPecarCodigo())
                                                        && cargo.getPedepCodigo() != null
                                                        && cargo.getPedepCodigo().equals(empleadoForm.getPedepCodigo());
                                        %>
                                                <option 
                                                    value="<%= h(cargo.getPecarCodigo()) %>"
                                                    data-departamento="<%= h(cargo.getPedepCodigo()) %>"
                                                    <%= seleccionadoCargo ? "selected" : "" %>
                                                >
                                                    <%= h(cargo.getPecarDescri()) %>
                                                </option>
                                        <%  }
                                           } %>
                                    </select>
                                    <small class="field-error-message" data-error-for="cargoCodigo"></small>
                                </div>

                                <div class="grupo-campo campo-full">
                                    <label for="direccion">Dirección</label>
                                    <input
                                        type="text"
                                        id="direccion"
                                        name="direccion"
                                        maxlength="100"
                                        value="<%= h(personaForm.getDireccion()) %>"
                                        <%= disabled %>
                                        required
                                    >
                                    <small class="field-error-message" data-error-for="direccion"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="celular">Celular</label>
                                    <input
                                        type="text"
                                        id="celular"
                                        name="celular"
                                        maxlength="10"
                                        value="<%= h(personaForm.getCelular()) %>"
                                        <%= disabled %>
                                    >
                                    <small class="field-error-message" data-error-for="celular"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="telefonoDomicilio">Teléfono domicilio</label>
                                    <input
                                        type="text"
                                        id="telefonoDomicilio"
                                        name="telefonoDomicilio"
                                        maxlength="10"
                                        value="<%= h(personaForm.getTelefonoDomicilio()) %>"
                                        <%= disabled %>
                                    >
                                    <small class="field-error-message" data-error-for="telefonoDomicilio"></small>
                                </div>

                                <div class="grupo-campo campo-doble">
                                    <label for="email">Email</label>
                                    <input
                                        type="email"
                                        id="email"
                                        name="email"
                                        maxlength="100"
                                        value="<%= h(personaForm.getEmail()) %>"
                                        <%= disabled %>
                                        required
                                    >
                                    <small class="field-error-message" data-error-for="email"></small>
                                </div>
                            </div>
                        </section>

                        <section class="tab-panel" id="formacion">
                            <div class="form-grid-empleado formacion-grid">
                                <div class="grupo-campo">
                                    <label for="formacionNivel">Nivel de formación</label>
                                    <input
                                        type="text"
                                        id="formacionNivel"
                                        name="formacionNivel"
                                        maxlength="40"
                                        value="<%= h(formacionForm.getNivel()) %>"
                                        <%= disabled %>
                                    >
                                    <small class="field-error-message" data-error-for="formacionNivel"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="formacionTitulo">Título obtenido</label>
                                    <input
                                        type="text"
                                        id="formacionTitulo"
                                        name="formacionTitulo"
                                        maxlength="80"
                                        value="<%= h(formacionForm.getTitulo()) %>"
                                        <%= disabled %>
                                    >
                                    <small class="field-error-message" data-error-for="formacionTitulo"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="formacionInstitucion">Institución</label>
                                    <input
                                        type="text"
                                        id="formacionInstitucion"
                                        name="formacionInstitucion"
                                        maxlength="80"
                                        value="<%= h(formacionForm.getInstitucion()) %>"
                                        <%= disabled %>
                                    >
                                    <small class="field-error-message" data-error-for="formacionInstitucion"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="formacionInicio">Fecha de inicio</label>
                                    <input
                                        type="date"
                                        id="formacionInicio"
                                        name="formacionInicio"
                                        value="<%= h(formacionForm.getFechaInicio()) %>"
                                        <%= disabled %>
                                    >
                                    <small class="field-error-message" data-error-for="formacionInicio"></small>
                                </div>

                                <div class="grupo-campo">
                                    <label for="formacionFin">Fecha de finalización</label>
                                    <input
                                        type="date"
                                        id="formacionFin"
                                        name="formacionFin"
                                        value="<%= h(formacionForm.getFechaFin()) %>"
                                        <%= disabled %>
                                    >
                                    <small class="field-error-message" data-error-for="formacionFin"></small>
                                </div>

                                <div class="grupo-campo campo-full">
                                    <label for="formacionObservacion">Observación</label>
                                    <input
                                        type="text"
                                        id="formacionObservacion"
                                        name="formacionObservacion"
                                        maxlength="200"
                                        value="<%= h(formacionForm.getObservacion()) %>"
                                        <%= disabled %>
                                    >
                                    <small class="field-error-message" data-error-for="formacionObservacion"></small>
                                </div>
                            </div>
                        </section>

                        <section class="tab-panel" id="familia">
                            <div class="familia-card">
                                <div class="familia-card-header">
                                    <div>
                                        <h4>Información Familiar</h4>
                                        <p>Registra los familiares asociados al empleado.</p>
                                    </div>
                                </div>

                                <div class="form-grid-empleado familia-grid">
                                    <div class="grupo-campo">
                                        <label for="familiarNombres">Nombres del familiar</label>
                                        <input type="text" id="familiarNombres" maxlength="30" <%= disabled %>>
                                        <small class="field-error-message" data-error-for="familiarNombres"></small>
                                    </div>

                                    <div class="grupo-campo">
                                        <label for="familiarApellidos">Apellidos del familiar</label>
                                        <input type="text" id="familiarApellidos" maxlength="30" <%= disabled %>>
                                        <small class="field-error-message" data-error-for="familiarApellidos"></small>
                                    </div>

                                    <div class="grupo-campo">
                                        <label for="familiarParentesco">Parentesco</label>
                                        <select id="familiarParentesco" <%= disabled %>>
                                            <option value="">Seleccione...</option>
                                            <% if (parentescos != null) {
                                                for (Parentesco parentesco : parentescos) { %>
                                                    <option value="<%= h(parentesco.getCodigo()) %>">
                                                        <%= h(parentesco.getDescripcion()) %>
                                                    </option>
                                            <%  }
                                               } %>
                                        </select>
                                        <small class="field-error-message" data-error-for="familiarParentesco"></small>
                                    </div>

                                    <div class="grupo-campo">
                                        <label for="familiarFechaNacimiento">Fecha de nacimiento</label>
                                        <input type="date" id="familiarFechaNacimiento" <%= disabled %>>
                                        <small
                                            class="field-error-message"
                                            data-error-for="familiarFechaNacimiento"
                                        ></small>
                                    </div>

                                    <div class="grupo-campo">
                                        <label for="familiarTelefono">Teléfono</label>
                                        <input type="text" id="familiarTelefono" maxlength="10" <%= disabled %>>
                                        <small class="field-error-message" data-error-for="familiarTelefono"></small>
                                    </div>

                                    <div class="grupo-campo campo-full">
                                        <label for="familiarObservacion">Observación</label>
                                        <input type="text" id="familiarObservacion" maxlength="200" <%= disabled %>>
                                        <small class="field-error-message" data-error-for="familiarObservacion"></small>
                                    </div>
                                </div>

                                <div class="familia-acciones">
                                    <button
                                        type="button"
                                        id="btnAgregarFamiliar"
                                        class="btn-crud celeste"
                                        <%= disabled %>
                                    >
                                        + Añadir familiar
                                    </button>
                                    <button
                                        type="button"
                                        id="btnCancelarEdicionFamiliar"
                                        class="btn-familiar-secundario"
                                        hidden
                                    >
                                        Cancelar edición
                                    </button>
                                    <small class="field-error-message" data-error-for="familiares"></small>
                                </div>
                            </div>

                            <div class="tabla-contenedor tabla-clara tabla-familiar">
                                <table class="tabla-crud tabla-familiar-compacta">
                                    <thead>
                                        <tr>
                                            <th>Nombres</th>
                                            <th>Apellidos</th>
                                            <th>Parentesco</th>
                                            <th>Fecha nacimiento</th>
                                            <th>Teléfono</th>
                                            <th>Observación</th>
                                            <th>Acciones</th>
                                        </tr>
                                    </thead>

                                    <tbody id="tablaFamiliaresBody">
                                        <tr>
                                            <td colspan="7" class="tabla-vacia">Sin familiares agregados.</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </section>

                        <div class="empleado-modal-actions">
                            <button type="button" id="btnCancelarFormulario" class="btn-ficha btn-cancelar">
                                Cancelar
                            </button>

                            <% if (!lectura) { %>
                                <button type="submit" id="btnSubmitEmpleado" class="btn-ficha btn-guardar">
                                    <%= edicion ? "Actualizar" : "Guardar" %>
                                </button>
                            <% } %>
                        </div>
                    </form>
                </section>
            </section>

            <% if (mostrarModalVer) { %>
                <div
                    class="empleado-modal-backdrop"
                    role="dialog"
                    aria-modal="true"
                    aria-labelledby="tituloModalEmpleado"
                >
                    <section class="empleado-modal">
                        <div class="empleado-modal-header">
                            <div class="empleado-modal-identidad">
                                <div class="empleado-foto-grande">
                                    <% if (tieneFotoActual) { %>
                                        <img
                                            src="<%= request.getContextPath() + "/" + h(fotoActual) %>"
                                            alt="Foto del empleado"
                                        >
                                    <% } else { %>
                                        <span>Sin foto</span>
                                    <% } %>
                                </div>

                                <div>
                                    <h3 id="tituloModalEmpleado">
                                        <%= h(personaForm.getNombres()) %>
                                        <%= h(personaForm.getApellidos()) %>
                                    </h3>
                                    <p>
                                        <%= h(empleadoForm.getPeempCodigo()) %>
                                        ·
                                        <%= h(empleadoForm.getNombreCargo()) %>
                                    </p>
                                </div>
                            </div>

                            <a
                                href="${pageContext.request.contextPath}/EmpleadoController"
                                class="empleado-modal-cerrar"
                                aria-label="Cerrar detalle del empleado"
                                title="Cerrar"
                            >
                                &times;
                            </a>
                        </div>

                        <div class="empleado-modal-body" data-tabs-scope>
                            <div
                                class="tabs-empleado modal-tabs-empleado"
                                role="tablist"
                                aria-label="Secciones de consulta del empleado"
                            >
                                <button
                                    type="button"
                                    class="tab-empleado activo"
                                    data-tab="modalDatosGenerales"
                                >
                                    Datos Generales
                                </button>
                                <button
                                    type="button"
                                    class="tab-empleado"
                                    data-tab="modalFormacion"
                                >
                                    Formación
                                </button>
                                <button
                                    type="button"
                                    class="tab-empleado"
                                    data-tab="modalFamilia"
                                >
                                    Información Familiar
                                </button>
                            </div>

                            <section class="tab-panel activo detalle-empleado-bloque" id="modalDatosGenerales">
                                <h4>Datos generales</h4>
                                <div class="detalle-empleado-grid">
                                    <div>
                                        <span>Código empleado</span>
                                        <strong><%= h(empleadoForm.getPeempCodigo()) %></strong>
                                    </div>
                                    <div>
                                        <span>Cédula</span>
                                        <strong><%= h(personaForm.getCedula()) %></strong>
                                    </div>
                                    <div>
                                        <span>Nombres</span>
                                        <strong><%= h(personaForm.getNombres()) %></strong>
                                    </div>
                                    <div>
                                        <span>Apellidos</span>
                                        <strong><%= h(personaForm.getApellidos()) %></strong>
                                    </div>
                                    <div>
                                        <span>Fecha nacimiento</span>
                                        <strong><%= h(personaForm.getFechaNacimiento()) %></strong>
                                    </div>
                                    <div>
                                        <span>Edad</span>
                                        <strong><%= h(edad(personaForm.getFechaNacimiento())) %></strong>
                                    </div>
                                    <div>
                                        <span>Sexo</span>
                                        <strong><%= h(personaForm.getSexoDescripcion()) %></strong>
                                    </div>
                                    <div>
                                        <span>Estado civil</span>
                                        <strong><%= h(personaForm.getEstadoCivilDescripcion()) %></strong>
                                    </div>
                                    <div>
                                        <span>Cargas familiares</span>
                                        <strong><%= h(personaForm.getCargasFamiliares()) %></strong>
                                    </div>
                                    <div class="detalle-wide">
                                        <span>Dirección</span>
                                        <strong><%= h(personaForm.getDireccion()) %></strong>
                                    </div>
                                    <div>
                                        <span>Celular</span>
                                        <strong><%= h(personaForm.getCelular()) %></strong>
                                    </div>
                                    <div>
                                        <span>Teléfono domicilio</span>
                                        <strong><%= h(personaForm.getTelefonoDomicilio()) %></strong>
                                    </div>
                                    <div class="detalle-wide">
                                        <span>Email</span>
                                        <strong><%= h(personaForm.getEmail()) %></strong>
                                    </div>
                                </div>

                                <h4 class="detalle-subtitulo">Datos laborales</h4>
                                <div class="detalle-empleado-grid">
                                    <div>
                                        <span>Departamento</span>
                                        <strong><%= h(empleadoForm.getNombreDepartamento()) %></strong>
                                    </div>
                                    <div>
                                        <span>Cargo</span>
                                        <strong><%= h(empleadoForm.getNombreCargo()) %></strong>
                                    </div>
                                </div>
                            </section>

                            <section class="tab-panel detalle-empleado-bloque" id="modalFormacion">
                                <h4>Formación académica</h4>
                                <div class="tabla-contenedor tabla-clara tabla-formacion modal-formaciones">
                                    <table class="tabla-crud tabla-formacion-compacta">
                                        <thead>
                                            <tr>
                                                <th>Nivel</th>
                                                <th>Título</th>
                                                <th>Institución</th>
                                                <th>Fecha inicio</th>
                                                <th>Fecha fin</th>
                                                <th>Observación</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            <% if (formacionesForm != null && !formacionesForm.isEmpty()) { %>
                                                <% for (Formacion formacion : formacionesForm) { %>
                                                    <tr>
                                                        <td><%= h(formacion.getNivel()) %></td>
                                                        <td><%= h(formacion.getTitulo()) %></td>
                                                        <td><%= h(formacion.getInstitucion()) %></td>
                                                        <td><%= h(formacion.getFechaInicio()) %></td>
                                                        <td><%= h(formacion.getFechaFin()) %></td>
                                                        <td><%= h(formacion.getObservacion()) %></td>
                                                    </tr>
                                                <% } %>
                                            <% } else { %>
                                                <tr>
                                                    <td colspan="6" class="tabla-vacia">
                                                        No existe formación registrada.
                                                    </td>
                                                </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            </section>

                            <section class="tab-panel detalle-empleado-bloque" id="modalFamilia">
                                <h4>Información familiar</h4>
                                <div class="tabla-contenedor tabla-clara tabla-familiar modal-familiares">
                                    <table class="tabla-crud tabla-familiar-compacta">
                                        <thead>
                                            <tr>
                                                <th>Nombres</th>
                                                <th>Apellidos</th>
                                                <th>Parentesco</th>
                                                <th>Fecha nacimiento</th>
                                                <th>Teléfono</th>
                                                <th>Observación</th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            <% if (familiaresForm != null && !familiaresForm.isEmpty()) { %>
                                                <% for (Familiar familiar : familiaresForm) { %>
                                                    <tr>
                                                        <td><%= h(familiar.getNombre()) %></td>
                                                        <td><%= h(familiar.getApellido()) %></td>
                                                        <td><%= h(familiar.getDescripcionParentesco()) %></td>
                                                        <td><%= h(familiar.getFechaNacimiento()) %></td>
                                                        <td><%= h(familiar.getTelefono()) %></td>
                                                        <td><%= h(familiar.getObservacion()) %></td>
                                                    </tr>
                                                <% } %>
                                            <% } else { %>
                                                <tr>
                                                    <td colspan="6" class="tabla-vacia">
                                                        No existen familiares registrados.
                                                    </td>
                                                </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            </section>
                        </div>
                    </section>
                </div>
            <% } %>
        </main>

        <script src="${pageContext.request.contextPath}/js/empleados.js?v=20260616-empleados1" defer></script>
        <script src="${pageContext.request.contextPath}/js/paginacion.js?v=20260611-pag1" defer></script>
        <script src="${pageContext.request.contextPath}/js/reportes.js?v=20260611-pag1" defer></script>
    </body>
</html>
