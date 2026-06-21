<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Perfil"%>
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
        return actual != null && actual.equals(opcion) ? "selected" : "";
    }

    private String estadoTexto(String estado) {
        if ("A".equalsIgnoreCase(estado)) {
            return "Activo";
        }

        if ("I".equalsIgnoreCase(estado)) {
            return "Inactivo";
        }

        return estado == null || estado.trim().isEmpty() ? "Sin estado" : estado;
    }

    private String estadoClase(Perfil perfil) {
        return perfil != null && perfil.estaActivo() ? "badge-ok" : "badge-neutro";
    }
%>

<%
    String modo = (String) request.getAttribute("modo");
    String mensaje = request.getParameter("mensaje");
    String error = (String) request.getAttribute("error");
    Perfil perfilEditar = (Perfil) request.getAttribute("perfilEditar");
    List<Perfil> perfiles = (List<Perfil>) request.getAttribute("perfiles");
    boolean puedeReportePerfiles = Boolean.TRUE.equals(request.getAttribute("puedeReportePerfiles"));

    if (modo == null) {
        modo = "listar";
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Perfiles | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260615-seg1">
    </head>

    <body class="body-dashboard">

        <main class="crud-page">
            <section class="crud-titulo">
                <div>
                    <h2>Gestion de Perfiles</h2>
                    <p>Crea, edita y activa los perfiles de seguridad.</p>
                </div>
            </section>

            <% if ("guardado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Perfil guardado correctamente.</p>
            <% } else if ("actualizado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Perfil actualizado correctamente.</p>
            <% } else if ("no_actualizado".equals(mensaje)) { %>
                <p class="mensaje-error-general">No se pudo actualizar el perfil.</p>
            <% } %>

            <% if (error != null) { %>
                <p class="mensaje-error-general"><%= h(error) %></p>
            <% } %>

            <section class="crud-contenedor">
                <div class="crud-toolbar">
                    <div class="crud-toolbar-left">
                        <a href="${pageContext.request.contextPath}/PerfilController?accion=nuevo" class="btn-crud verde">+ Nuevo perfil</a>
                        <a href="${pageContext.request.contextPath}/PerfilController" class="btn-crud celeste">Recargar</a>
                        <% if (puedeReportePerfiles) { %>
                            <button type="button" class="btn-crud reporte" data-reporte-toggle="reportePerfiles">
                                Reportes
                            </button>
                        <% } %>
                    </div>

                    <div class="buscar-registros">
                        <label for="buscarPerfil">Buscar:</label>
                        <input type="text" id="buscarPerfil" placeholder="Buscar perfil...">
                    </div>
                </div>

                <% if ("nuevo".equals(modo)) { %>
                    <div class="form-crud-box">
                        <h3>Crear perfil</h3>
                        <form action="${pageContext.request.contextPath}/PerfilController" method="post" class="form-crud-lineal">
                            <input type="hidden" name="accion" value="guardar">
                            <div class="grupo-campo">
                                <label for="codigo">Codigo</label>
                                <input type="text" id="codigo" name="codigo" maxlength="20" placeholder="Ej: ADMIN" required>
                            </div>
                            <div class="grupo-campo">
                                <label for="descripcion">Descripcion</label>
                                <input type="text" id="descripcion" name="descripcion" maxlength="80" placeholder="Ej: Administrador" required>
                            </div>
                            <input type="hidden" name="estado" value="A">
                            <button type="submit" class="btn-crud-form">Guardar</button>
                        </form>
                    </div>
                <% } %>

                <% if ("editar".equals(modo) && perfilEditar != null) { %>
                    <div class="form-crud-box">
                        <h3>Editar perfil</h3>
                        <form action="${pageContext.request.contextPath}/PerfilController" method="post" class="form-crud-lineal">
                            <input type="hidden" name="accion" value="actualizar">
                            <div class="grupo-campo">
                                <label for="codigo">Codigo</label>
                                <input type="text" id="codigo" name="codigo" value="<%= h(perfilEditar.getCodigo()) %>" readonly>
                            </div>
                            <div class="grupo-campo">
                                <label for="descripcion">Descripcion</label>
                                <input type="text" id="descripcion" name="descripcion" maxlength="80" value="<%= h(perfilEditar.getDescripcion()) %>" required>
                            </div>
                            <div class="grupo-campo">
                                <label for="estado">Estado</label>
                                <select id="estado" name="estado">
                                    <option value="A" <%= selected(perfilEditar.getEstadoCodigo(), "A") %>>Activo</option>
                                    <option value="I" <%= selected(perfilEditar.getEstadoCodigo(), "I") %>>Inactivo</option>
                                </select>
                            </div>
                            <button type="submit" class="btn-crud-form">Actualizar</button>
                        </form>
                    </div>
                <% } %>


                <% if (puedeReportePerfiles) { %>
                    <section id="reportePerfiles"
                             class="reporte-panel"
                             data-reporte-panel
                             data-reporte-tabla="tablaPerfiles"
                             data-reporte-columnas="0,1,2"
                             data-reporte-encabezados="Codigo|Descripcion|Estado"
                             data-reporte-titulo="Reporte de Perfiles"
                             data-reporte-archivo="reporte_perfiles"
                             data-reporte-codigo="RPF"
                             data-reporte-modulo="Perfiles"
                             data-reporte-registro-url="${pageContext.request.contextPath}/ReporteController"
                             hidden>
                        <div class="reporte-panel-header">
                            <div>
                                <h3>Reportes de Perfiles</h3>
                                <p>Vista previa generada con la informacion visible del modulo.</p>
                            </div>
                            <div class="reporte-meta">
                                <span>Registros: <strong data-reporte-total>0</strong></span>
                                <span>Fecha: <strong data-reporte-fecha></strong></span>
                            </div>
                        </div>

                        <div class="reporte-acciones">
                            <button type="button" class="btn-crud celeste" data-reporte-accion="actualizar">
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
                <div class="tabla-contenedor tabla-clara">
                    <table class="tabla-crud" id="tablaPerfiles" data-paginacion="true" data-paginacion-tamanio="5">
                        <thead>
                            <tr>
                                <th>Codigo</th>
                                <th>Descripcion</th>
                                <th>Estado</th>
                                <th>Operaciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (perfiles != null && !perfiles.isEmpty()) { %>
                                <% for (Perfil perfil : perfiles) { %>
                                    <tr>
                                        <td><%= h(perfil.getCodigo()) %></td>
                                        <td><%= h(perfil.getDescripcion()) %></td>
                                        <td>
                                            <span class="estado-badge <%= estadoClase(perfil) %>">
                                                <%= h(estadoTexto(perfil.getEstadoCodigo())) %>
                                            </span>
                                        </td>
                                        <td>
                                            <div class="acciones-tabla">
                                                <a
                                                    class="btn-accion-icono accion-editar"
                                                    href="${pageContext.request.contextPath}/PerfilController?accion=editar&codigo=<%= h(perfil.getCodigo()) %>"
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
                                                    href="${pageContext.request.contextPath}/PerfilController?accion=activar&codigo=<%= h(perfil.getCodigo()) %>"
                                                    title="Activar"
                                                    aria-label="Activar"
                                                >
                                                    <img
                                                        src="${pageContext.request.contextPath}/img/iconos_master_monster/ver.png"
                                                        alt=""
                                                    >
                                                </a>
                                                <a
                                                    class="btn-accion-icono accion-eliminar"
                                                    href="${pageContext.request.contextPath}/PerfilController?accion=inactivar&codigo=<%= h(perfil.getCodigo()) %>"
                                                    title="Inactivar"
                                                    aria-label="Inactivar"
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
                                    <td colspan="4" class="tabla-vacia">No existen perfiles registrados.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>

        <script src="${pageContext.request.contextPath}/js/perfiles.js?v=20260615-seg1" defer></script>
        <script src="${pageContext.request.contextPath}/js/paginacion.js?v=20260611-pag1" defer></script>
        <script src="${pageContext.request.contextPath}/js/reportes.js?v=20260621-hist1" defer></script>
    </body>
</html>
