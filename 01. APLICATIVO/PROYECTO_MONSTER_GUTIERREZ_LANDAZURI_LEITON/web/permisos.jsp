<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Opcion"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Perfil"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
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

    private String checked(Set<String> seleccionados, String codigo) {
        return seleccionados != null && seleccionados.contains(codigo) ? "checked" : "";
    }
%>

<%
    String mensaje = request.getParameter("mensaje");
    String perfilSeleccionado = (String) request.getAttribute("perfilSeleccionado");
    List<Perfil> perfiles = (List<Perfil>) request.getAttribute("perfiles");
    List<Opcion> opciones = (List<Opcion>) request.getAttribute("opciones");
    Set<String> permisosSeleccionados = (Set<String>) request.getAttribute("permisosSeleccionados");
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Permisos | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260615-seg1">
    </head>

    <body class="body-dashboard">
        <jsp:include page="/WEB-INF/includes/topbar.jsp" />

        <main class="crud-page">
            <section class="crud-titulo">
                <div>
                    <h2>Permisos por Perfil</h2>
                    <p>Marca las opciones del menu permitidas para cada perfil.</p>
                </div>
            </section>

            <% if ("guardado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Permisos guardados correctamente.</p>
            <% } else if ("no_guardado".equals(mensaje)) { %>
                <p class="mensaje-error-general">No se pudieron guardar los permisos.</p>
            <% } %>

            <section class="crud-contenedor">
                <div class="form-crud-box">
                    <h3>Seleccionar perfil</h3>
                    <form action="${pageContext.request.contextPath}/PermisoController" method="get" class="form-crud-lineal">
                        <div class="grupo-campo">
                            <label for="perfilCodigoFiltro">Perfil</label>
                            <select id="perfilCodigoFiltro" name="perfilCodigo" required>
                                <option value="">Seleccione...</option>
                                <% if (perfiles != null) {
                                    for (Perfil perfil : perfiles) { %>
                                        <option value="<%= h(perfil.getCodigo()) %>" <%= selected(perfilSeleccionado, perfil.getCodigo()) %>>
                                            <%= h(perfil.getDescripcion()) %>
                                        </option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                        <button type="submit" class="btn-crud-form">Cargar</button>
                    </form>
                </div>

                <% if (perfilSeleccionado != null && !perfilSeleccionado.trim().isEmpty()) { %>
                    <form action="${pageContext.request.contextPath}/PermisoController" method="post">
                        <input type="hidden" name="perfilCodigo" value="<%= h(perfilSeleccionado) %>">

                        <div class="crud-toolbar">
                            <div class="crud-toolbar-left">
                                <button type="submit" class="btn-crud verde">Guardar permisos</button>
                                <button type="button" class="btn-crud celeste" id="btnMarcarPermisos">Marcar todos</button>
                                <button type="button" class="btn-crud reporte" id="btnLimpiarPermisos">Limpiar</button>
                            </div>

                            <div class="buscar-registros">
                                <label for="buscarPermiso">Buscar:</label>
                                <input type="text" id="buscarPermiso" placeholder="Buscar opcion...">
                            </div>
                        </div>

                        <div class="tabla-contenedor tabla-clara tabla-permisos">
                            <table class="tabla-crud" id="tablaPermisos">
                                <thead>
                                    <tr>
                                        <th>Permitir</th>
                                        <th>Codigo</th>
                                        <th>Opcion</th>
                                        <th>URL</th>
                                        <th>Orden</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (opciones != null && !opciones.isEmpty()) { %>
                                        <% for (Opcion opcion : opciones) { %>
                                            <tr>
                                                <td>
                                                    <label class="check-permiso">
                                                        <input
                                                            type="checkbox"
                                                            name="opciones"
                                                            value="<%= h(opcion.getCodigo()) %>"
                                                            <%= checked(permisosSeleccionados, opcion.getCodigo()) %>
                                                        >
                                                    </label>
                                                </td>
                                                <td><%= h(opcion.getCodigo()) %></td>
                                                <td><%= h(opcion.getDescripcion()) %></td>
                                                <td><%= h(opcion.getUrl()) %></td>
                                                <td><%= opcion.getOrden() %></td>
                                            </tr>
                                        <% } %>
                                    <% } else { %>
                                        <tr>
                                            <td colspan="5" class="tabla-vacia">No existen opciones registradas.</td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </form>
                <% } %>
            </section>

            <footer class="footer-dashboard">
                GUTIERREZ - LANDAZURI - LEITON
            </footer>
        </main>

        <script src="${pageContext.request.contextPath}/js/permisos.js?v=20260615-seg1" defer></script>
    </body>
</html>
