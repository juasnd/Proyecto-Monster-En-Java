<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.ReporteHistorial"%>
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

    private String url(Object valor) {
        try {
            return URLEncoder.encode(valor == null ? "" : String.valueOf(valor), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private String texto(Object valor, String alterno) {
        String texto = valor == null ? "" : String.valueOf(valor).trim();
        return texto.isEmpty() ? alterno : texto;
    }

    private String rutaRegenerar(String codigoReporte) {
        if ("RDE".equalsIgnoreCase(codigoReporte)) {
            return "DepartamentoController";
        }

        if ("RCA".equalsIgnoreCase(codigoReporte)) {
            return "CargoController";
        }

        if ("REM".equalsIgnoreCase(codigoReporte)) {
            return "EmpleadoController";
        }

        if ("RUS".equalsIgnoreCase(codigoReporte)) {
            return "UsuarioController";
        }

        if ("RPF".equalsIgnoreCase(codigoReporte)) {
            return "PerfilController";
        }

        return "ReporteController";
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

    String mensaje = request.getParameter("mensaje");
    List<ReporteHistorial> historialReportes = (List<ReporteHistorial>) request.getAttribute("historialReportes");
    ReporteHistorial reporteDetalle = (ReporteHistorial) request.getAttribute("reporteDetalle");
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Reportes | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260621-reportes1">
    </head>

    <body class="body-dashboard">
        <main class="crud-page reportes-page">
            <section class="crud-titulo">
                <div>
                    <h2>Reportes</h2>
                    <p>Histórico de reportes generados en los módulos del sistema.</p>
                </div>
            </section>

            <% if ("eliminado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Registro de historial eliminado correctamente.</p>
            <% } else if ("no_eliminado".equals(mensaje)) { %>
                <p class="mensaje-error-general">No se pudo eliminar el registro del historial.</p>
            <% } %>

            <section class="crud-contenedor">
                <div class="crud-toolbar">
                    <div class="crud-toolbar-left">
                        <a href="${pageContext.request.contextPath}/ReporteController" class="btn-crud celeste">Recargar</a>
                    </div>

                    <div class="buscar-registros">
                        <label for="buscarReporteHistorial">Buscar:</label>
                        <input type="text" id="buscarReporteHistorial" placeholder="Fecha, usuario, perfil, módulo o formato...">
                    </div>
                </div>

                <% if (reporteDetalle != null) { %>
                    <div class="form-crud-box reporte-historial-detalle">
                        <h3>Detalle del reporte</h3>
                        <div class="detalle-crud reporte-detalle-grid">
                            <p><strong>Fecha:</strong> <%= h(texto(reporteDetalle.getFecha(), "Sin fecha")) %></p>
                            <p><strong>Usuario:</strong> <%= h(texto(reporteDetalle.getUsuario(), "Sin usuario")) %></p>
                            <p><strong>Perfil:</strong> <%= h(texto(reporteDetalle.getPerfil(), "Sin perfil")) %></p>
                            <p><strong>Módulo:</strong> <%= h(texto(reporteDetalle.getModulo(), "Sin módulo")) %></p>
                            <p><strong>Tipo:</strong> <%= h(texto(reporteDetalle.getTipoReporte(), "Reporte")) %></p>
                            <p><strong>Formato:</strong> <%= h(texto(reporteDetalle.getFormato(), "Sin formato")) %></p>
                            <p><strong>Total registros:</strong> <%= reporteDetalle.getTotalRegistros() %></p>
                            <p class="detalle-wide"><strong>Filtros usados:</strong> <%= h(texto(reporteDetalle.getFiltros(), "Sin filtros")) %></p>
                        </div>
                    </div>
                <% } %>

                <div class="tabla-contenedor tabla-clara">
                    <table class="tabla-crud" id="tablaHistorialReportes" data-paginacion="true" data-paginacion-tamanio="6">
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Usuario</th>
                                <th>Perfil</th>
                                <th>Módulo</th>
                                <th>Tipo de reporte</th>
                                <th>Formato</th>
                                <th>Total</th>
                                <th>Filtros usados</th>
                                <th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (historialReportes != null && !historialReportes.isEmpty()) { %>
                                <% for (ReporteHistorial reporte : historialReportes) {
                                    String codigo = reporte.getCodigo();
                                    String codigoUrl = url(codigo);
                                    String regenerarUrl = request.getContextPath() + "/" + rutaRegenerar(reporte.getCodigoReporte());
                                %>
                                    <tr>
                                        <td><%= h(texto(reporte.getFecha(), "Sin fecha")) %></td>
                                        <td><%= h(texto(reporte.getUsuario(), "Sin usuario")) %></td>
                                        <td><%= h(texto(reporte.getPerfil(), "Sin perfil")) %></td>
                                        <td><%= h(texto(reporte.getModulo(), "Sin módulo")) %></td>
                                        <td><%= h(texto(reporte.getTipoReporte(), "Reporte")) %></td>
                                        <td><%= h(texto(reporte.getFormato(), "Sin formato")) %></td>
                                        <td><%= reporte.getTotalRegistros() %></td>
                                        <td><%= h(texto(reporte.getFiltros(), "Sin filtros")) %></td>
                                        <td>
                                            <div class="acciones-tabla acciones-reportes">
                                                <% if (codigo != null && !codigo.trim().isEmpty()) { %>
                                                    <a class="btn-accion-icono accion-ver"
                                                       href="${pageContext.request.contextPath}/ReporteController?accion=detalle&codigo=<%= codigoUrl %>"
                                                       title="Ver detalle"
                                                       aria-label="Ver detalle">
                                                        <img src="${pageContext.request.contextPath}/img/iconos_master_monster/ver.png" alt="">
                                                    </a>
                                                <% } %>

                                                <a class="btn-accion-icono accion-editar"
                                                   href="<%= regenerarUrl %>"
                                                   title="Regenerar"
                                                   aria-label="Regenerar">
                                                    <img src="${pageContext.request.contextPath}/img/iconos_master_monster/resetear.png" alt="">
                                                </a>

                                                <% if (codigo != null && !codigo.trim().isEmpty()) { %>
                                                    <form action="${pageContext.request.contextPath}/ReporteController" method="post" class="form-eliminar-historial">
                                                        <input type="hidden" name="accion" value="eliminar">
                                                        <input type="hidden" name="codigo" value="<%= h(codigo) %>">
                                                        <button type="submit"
                                                                class="btn-accion-icono accion-eliminar"
                                                                title="Eliminar historial"
                                                                aria-label="Eliminar historial"
                                                                onclick="return confirm('¿Seguro que desea eliminar este registro del historial?');">
                                                            <img src="${pageContext.request.contextPath}/img/iconos_master_monster/eliminar.png" alt="">
                                                        </button>
                                                    </form>
                                                <% } %>
                                            </div>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="9" class="tabla-vacia">No existen reportes registrados en el historial.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>

        <script src="${pageContext.request.contextPath}/js/paginacion.js?v=20260611-pag1" defer></script>
        <script src="${pageContext.request.contextPath}/js/reportes-historial.js?v=20260621-reportes1" defer></script>
    </body>
</html>