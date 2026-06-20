<%-- 
    Document   : departamentos
    Created on : 20 may 2026, 8:45:52
    Author     : ASUS
--%>

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

    Departamento departamentoEditar = (Departamento) request.getAttribute("departamentoEditar");
    Departamento departamentoVer = (Departamento) request.getAttribute("departamentoVer");

    List<Departamento> departamentos = (List<Departamento>) request.getAttribute("departamentos");

    if (modo == null) {
        modo = "listar";
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Departamentos | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260611-pag1">
    </head>

    <body class="body-dashboard">


        <main class="crud-page">

            <section class="crud-titulo">
                <div>
                    <h2>Gestionar Departamentos</h2>
                    <p>Administra los departamentos registrados dentro del sistema.</p>
                </div>
            </section>

            <% if ("guardado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Departamento guardado correctamente.</p>
            <% } else if ("actualizado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Departamento actualizado correctamente.</p>
            <% } else if ("eliminado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Departamento eliminado correctamente.</p>
            <% } %>

            <% if (error != null) { %>
                <p class="mensaje-error-general"><%= error %></p>
            <% } else if ("en_uso".equals(errorUrl)) { %>
                <p class="mensaje-error-general">
                    No se puede eliminar el departamento porque está relacionado
                    con cargos, proyectos o empleados.
                </p>
            <% } else if ("no_eliminado".equals(errorUrl)) { %>
                <p class="mensaje-error-general">No se pudo eliminar el departamento.</p>
            <% } else if ("no_encontrado".equals(errorUrl)) { %>
                <p class="mensaje-error-general">No se encontró el departamento solicitado.</p>
            <% } %>

            <section class="crud-contenedor">

                <div class="crud-toolbar">
                    <div class="crud-toolbar-left">
                        <a
                            href="${pageContext.request.contextPath}/DepartamentoController?accion=nuevo"
                            class="btn-crud verde"
                        >
                            + Nuevo departamento
                        </a>

                        <a href="${pageContext.request.contextPath}/DepartamentoController" class="btn-crud celeste">
                            Recargar
                        </a>

                        <button type="button" class="btn-crud reporte" data-reporte-toggle="reporteDepartamentos">
                            Reportes
                        </button>
                    </div>

                    <div class="buscar-registros">
                        <label for="buscarDepartamento">Buscar:</label>
                        <input type="text" id="buscarDepartamento" placeholder="Buscar departamento...">
                    </div>
                </div>



                <section id="reporteDepartamentos"
                         class="reporte-panel"
                         data-reporte-panel
                         data-reporte-tabla="tablaDepartamentos"
                         data-reporte-columnas="0,1"
                         data-reporte-encabezados="Codigo|Descripcion"
                         data-reporte-titulo="Reporte de Departamentos"
                         data-reporte-archivo="reporte_departamentos"
                         hidden>
                    <div class="reporte-panel-header">
                        <div>
                            <h3>Reportes de Departamentos</h3>
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

                <% if ("nuevo".equals(modo)) { %>
                    <div class="form-crud-box">
                        <h3>Crear departamento</h3>

                        <form
                            action="${pageContext.request.contextPath}/DepartamentoController"
                            method="post"
                            class="form-crud-lineal"
                        >
                            <input type="hidden" name="accion" value="guardar">

                            <div class="grupo-campo">
                                <label for="codigo">Código</label>
                                <input
                                    type="text"
                                    id="codigo"
                                    name="codigo"
                                    maxlength="3"
                                    placeholder="Ej: ADM"
                                    required
                                >
                            </div>

                            <div class="grupo-campo">
                                <label for="descripcion">Descripción</label>
                                <input
                                    type="text"
                                    id="descripcion"
                                    name="descripcion"
                                    maxlength="50"
                                    placeholder="Ej: Administración"
                                    required
                                >
                            </div>

                            <button type="submit" class="btn-crud-form">Guardar</button>
                        </form>
                    </div>
                <% } %>

                <% if ("editar".equals(modo) && departamentoEditar != null) { %>
                    <div class="form-crud-box">
                        <h3>Editar departamento</h3>

                        <form
                            action="${pageContext.request.contextPath}/DepartamentoController"
                            method="post"
                            class="form-crud-lineal"
                        >
                            <input type="hidden" name="accion" value="actualizar">

                            <div class="grupo-campo">
                                <label for="codigo">Código</label>
                                <input
                                    type="text"
                                    id="codigo"
                                    name="codigo"
                                    value="<%= departamentoEditar.getCodigo() %>"
                                    readonly
                                >
                            </div>

                            <div class="grupo-campo">
                                <label for="descripcion">Descripción</label>
                                <input
                                    type="text"
                                    id="descripcion"
                                    name="descripcion"
                                    maxlength="50"
                                    value="<%= departamentoEditar.getDescripcion() %>"
                                    required
                                >
                            </div>

                            <button type="submit" class="btn-crud-form">Actualizar</button>
                        </form>
                    </div>
                <% } %>

                <% if ("ver".equals(modo) && departamentoVer != null) { %>
                    <div class="form-crud-box">
                        <h3>Detalle del departamento</h3>

                        <div class="detalle-crud">
                            <p><strong>Código:</strong> <%= departamentoVer.getCodigo() %></p>
                            <p><strong>Descripción:</strong> <%= departamentoVer.getDescripcion() %></p>
                        </div>
                    </div>
                <% } %>

                <div class="tabla-contenedor tabla-clara">
                    <table
                        class="tabla-crud"
                        id="tablaDepartamentos"
                        data-paginacion="true"
                        data-paginacion-tamanio="3"
                    >
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Descripción</th>
                                <th>Operaciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% if (departamentos != null && !departamentos.isEmpty()) { %>
                                <% for (Departamento dep : departamentos) {
                                    String verDepartamentoUrl = request.getContextPath()
                                            + "/DepartamentoController?accion=ver&codigo="
                                            + dep.getCodigo();
                                    String editarDepartamentoUrl = request.getContextPath()
                                            + "/DepartamentoController?accion=editar&codigo="
                                            + dep.getCodigo();
                                    String eliminarDepartamentoUrl = request.getContextPath()
                                            + "/DepartamentoController?accion=eliminar&codigo="
                                            + dep.getCodigo();
                                %>
                                    <tr>
                                        <td><%= dep.getCodigo() %></td>
                                        <td><%= dep.getDescripcion() %></td>
                                        <td>
                                            <div class="acciones-tabla">
                                                <a
                                                    class="btn-accion-icono accion-ver"
                                                    href="<%= verDepartamentoUrl %>"
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
                                                    href="<%= editarDepartamentoUrl %>"
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
                                                    href="<%= eliminarDepartamentoUrl %>"
                                                    title="Eliminar"
                                                    aria-label="Eliminar"
                                                    onclick="
                                                        return confirm(
                                                            '¿Seguro que desea eliminar este departamento?'
                                                        );
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
                                    <td colspan="3" class="tabla-vacia">No existen departamentos registrados.</td>
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
        <script src="${pageContext.request.contextPath}/js/reportes.js?v=20260611-pag1" defer></script>

    </body>
</html>
