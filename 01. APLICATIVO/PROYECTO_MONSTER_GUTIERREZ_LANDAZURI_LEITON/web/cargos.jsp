<%-- 
    Document   : cargos
    Created on : 31 may 2026
--%>

<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Cargo"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Departamento"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

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

    Cargo cargoEditar = (Cargo) request.getAttribute("cargoEditar");
    Cargo cargoVer = (Cargo) request.getAttribute("cargoVer");

    List<Cargo> cargos = (List<Cargo>) request.getAttribute("cargos");
    List<Departamento> departamentos = (List<Departamento>) request.getAttribute("departamentos");
    boolean puedeReporteCargos = Boolean.TRUE.equals(request.getAttribute("puedeReporteCargos"));

    if (modo == null) {
        modo = "listar";
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Cargos | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260611-pag1">
    </head>

    <body class="body-dashboard">


        <main class="crud-page">

            <section class="crud-titulo">
                <div>
                    <h2>Gestionar Cargos</h2>
                    <p>Administra los cargos y asígnalos a los departamentos registrados.</p>
                </div>
            </section>

            <% if ("guardado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Cargo guardado correctamente.</p>
            <% } else if ("actualizado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Cargo actualizado correctamente.</p>
            <% } else if ("eliminado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Cargo eliminado correctamente.</p>
            <% } %>

            <% if (error != null) { %>
                <p class="mensaje-error-general"><%= error %></p>
            <% } else if ("en_uso".equals(errorUrl)) { %>
                <p class="mensaje-error-general">No se puede eliminar el cargo porque está asignado a empleados.</p>
            <% } else if ("no_eliminado".equals(errorUrl)) { %>
                <p class="mensaje-error-general">No se pudo eliminar el cargo.</p>
            <% } else if ("no_encontrado".equals(errorUrl)) { %>
                <p class="mensaje-error-general">No se encontró el cargo solicitado.</p>
            <% } %>

            <section class="crud-contenedor">

                <div class="crud-toolbar">
                    <div class="crud-toolbar-left">
                        <a
                            href="${pageContext.request.contextPath}/CargoController?accion=nuevo"
                            class="btn-crud verde"
                        >
                            + Nuevo cargo
                        </a>

                        <a href="${pageContext.request.contextPath}/CargoController" class="btn-crud celeste">
                            Recargar
                        </a>

                        <% if (puedeReporteCargos) { %>
                            <button type="button" class="btn-crud reporte" data-reporte-toggle="reporteCargos">
                                Reportes
                            </button>
                        <% } %>
                    </div>

                    <div class="buscar-registros">
                        <label for="buscarCargo">Buscar:</label>
                        <input type="text" id="buscarCargo" placeholder="Buscar cargo...">
                    </div>
                </div>



                <% if (puedeReporteCargos) { %>
                <section id="reporteCargos"
                         class="reporte-panel"
                         data-reporte-panel
                         data-reporte-tabla="tablaCargos"
                         data-reporte-columnas="0,1,2"
                         data-reporte-encabezados="Departamento|Cod. Cargo|Descripcion"
                         data-reporte-titulo="Reporte de Cargos"
                         data-reporte-archivo="reporte_cargos"
                         data-reporte-codigo="RCA"
                         data-reporte-modulo="Cargos"
                         data-reporte-registro-url="${pageContext.request.contextPath}/ReporteController"
                         hidden>
                    <div class="reporte-panel-header">
                        <div>
                            <h3>Reportes de Cargos</h3>
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
                <% } %>

                <%-- FORMULARIO DE NUEVO CARGO --%>
                <% if ("nuevo".equals(modo)) { %>
                    <div class="form-crud-box">
                        <h3>Crear cargo</h3>

                        <form
                            action="${pageContext.request.contextPath}/CargoController"
                            method="post"
                            class="form-crud-lineal"
                        >
                            <input type="hidden" name="accion" value="guardar">

                            <div class="grupo-campo">
                                <label for="pedepCodigo">Departamento</label>
                                <select id="pedepCodigo" name="pedepCodigo" required>
                                    <option value="">Seleccione un departamento...</option>
                                    <% if (departamentos != null) { 
                                        for (Departamento dep : departamentos) { %>
                                            <option value="<%= dep.getCodigo() %>"><%= dep.getDescripcion() %></option>
                                    <%  } 
                                       } %>
                                </select>
                            </div>

                            <div class="grupo-campo">
                                <label for="pecarCodigo">Código del Cargo</label>
                                <input
                                    type="text"
                                    id="pecarCodigo"
                                    name="pecarCodigo"
                                    maxlength="3"
                                    placeholder="Ej: GER"
                                    required
                                >
                            </div>

                            <div class="grupo-campo">
                                <label for="pecarDescri">Descripción</label>
                                <input
                                    type="text"
                                    id="pecarDescri"
                                    name="pecarDescri"
                                    maxlength="50"
                                    placeholder="Ej: Gerente General"
                                    required
                                >
                            </div>

                            <button type="submit" class="btn-crud-form">Guardar</button>
                        </form>
                    </div>
                <% } %>

                <%-- FORMULARIO DE EDITAR CARGO --%>
                <% if ("editar".equals(modo) && cargoEditar != null) { %>
                    <div class="form-crud-box">
                        <h3>Editar cargo</h3>

                        <form
                            action="${pageContext.request.contextPath}/CargoController"
                            method="post"
                            class="form-crud-lineal"
                        >
                            <input type="hidden" name="accion" value="actualizar">
                            
                            <%-- Enviamos las llaves primarias ocultas porque no deben modificarse --%>
                            <input type="hidden" name="pedepCodigo" value="<%= cargoEditar.getPedepCodigo() %>">
                            <input type="hidden" name="pecarCodigo" value="<%= cargoEditar.getPecarCodigo() %>">

                            <div class="grupo-campo">
                                <label for="departamentoVisible">Departamento</label>
                                <%-- El combobox solo muestra el departamento asociado. --%>
                                <select id="departamentoVisible" disabled>
                                    <% if (departamentos != null) { 
                                        for (Departamento dep : departamentos) { 
                                            String seleccionado = cargoEditar.getPedepCodigo()
                                                    .equals(dep.getCodigo())
                                                    ? "selected"
                                                    : "";
                                    %>
                                            <option
                                                value="<%= dep.getCodigo() %>"
                                                <%= seleccionado %>
                                            >
                                                <%= dep.getDescripcion() %>
                                            </option>
                                    <%  } 
                                       } %>
                                </select>
                            </div>

                            <div class="grupo-campo">
                                <label for="pecarCodigoVisible">Código</label>
                                <input
                                    type="text"
                                    id="pecarCodigoVisible"
                                    value="<%= cargoEditar.getPecarCodigo() %>"
                                    readonly
                                >
                            </div>

                            <div class="grupo-campo">
                                <label for="pecarDescri">Descripción</label>
                                <input
                                    type="text"
                                    id="pecarDescri"
                                    name="pecarDescri"
                                    maxlength="50"
                                    value="<%= cargoEditar.getPecarDescri() %>"
                                    required
                                >
                            </div>

                            <button type="submit" class="btn-crud-form">Actualizar</button>
                        </form>
                    </div>
                <% } %>

                <%-- VISTA DETALLE DEL CARGO --%>
                <% if ("ver".equals(modo) && cargoVer != null) { %>
                    <div class="form-crud-box">
                        <h3>Detalle del cargo</h3>

                        <div class="detalle-crud">
                            <p>
                                <strong>Departamento:</strong>
                                <%= cargoVer.getNombreDepartamento() %>
                                (<%= cargoVer.getPedepCodigo() %>)
                            </p>
                            <p><strong>Código del Cargo:</strong> <%= cargoVer.getPecarCodigo() %></p>
                            <p><strong>Descripción:</strong> <%= cargoVer.getPecarDescri() %></p>
                        </div>
                    </div>
                <% } %>

                <div class="tabla-contenedor tabla-clara">
                    <table
                        class="tabla-crud"
                        id="tablaCargos"
                        data-paginacion="true"
                        data-paginacion-tamanio="3"
                    >
                        <thead>
                            <tr>
                                <th>Departamento</th>
                                <th>Cód. Cargo</th>
                                <th>Descripción</th>
                                <th>Operaciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% if (cargos != null && !cargos.isEmpty()) { %>
                                <% for (Cargo c : cargos) {
                                    String verCargoUrl = request.getContextPath()
                                            + "/CargoController?accion=ver&depCode="
                                            + c.getPedepCodigo()
                                            + "&carCode="
                                            + c.getPecarCodigo();
                                    String editarCargoUrl = request.getContextPath()
                                            + "/CargoController?accion=editar&depCode="
                                            + c.getPedepCodigo()
                                            + "&carCode="
                                            + c.getPecarCodigo();
                                    String eliminarCargoUrl = request.getContextPath()
                                            + "/CargoController?accion=eliminar&depCode="
                                            + c.getPedepCodigo()
                                            + "&carCode="
                                            + c.getPecarCodigo();
                                %>
                                    <tr>
                                        <td><%= c.getNombreDepartamento() %></td>
                                        <td><%= c.getPecarCodigo() %></td>
                                        <td><%= c.getPecarDescri() %></td>
                                        <td>
                                            <div class="acciones-tabla">
                                                
                                                <a
                                                    class="btn-accion-icono accion-ver"
                                                    href="<%= verCargoUrl %>"
                                                    title="Ver"
                                                    aria-label="Ver"
                                                >
                                                    <img
                                                        src="${pageContext.request.contextPath}/img/iconos_master_monster/ver.png"
                                                        alt=""
                                                    >
                                                </a>

                                                <a
                                                    class="btn-accion-icono accion-editar"
                                                    href="<%= editarCargoUrl %>"
                                                    title="Editar"
                                                    aria-label="Editar"
                                                >
                                                    <img
                                                        src="${pageContext.request.contextPath}/img/iconos_master_monster/editar.png"
                                                        alt=""
                                                    >
                                                </a>

                                                <a
                                                    class="btn-accion-icono accion-eliminar"
                                                    href="<%= eliminarCargoUrl %>"
                                                    title="Eliminar"
                                                    aria-label="Eliminar"
                                                    onclick="
                                                        return confirm('¿Seguro que desea eliminar este cargo?');
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
                                    <td colspan="4" class="tabla-vacia">No existen cargos registrados.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <div class="tabla-footer">
                </div>

            </section>
        </main>
        
        
        <script src="${pageContext.request.contextPath}/js/departamentos.js" defer></script>
        <script src="${pageContext.request.contextPath}/js/paginacion.js?v=20260611-pag1" defer></script>
        <script src="${pageContext.request.contextPath}/js/reportes.js?v=20260621-hist1" defer></script>

    </body>
</html>
