<%--
    Document   : pagPrincipal
    Created on : 16 may 2026
    Author     : ASUS
--%>

<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.MenuOpcion"%>
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

    private boolean tieneUrl(MenuOpcion opcion) {
        return opcion != null && opcion.getUrl() != null && !opcion.getUrl().trim().isEmpty();
    }

    private boolean esInicio(MenuOpcion opcion) {
        if (!tieneUrl(opcion)) {
            return false;
        }

        String url = opcion.getUrl().trim().toLowerCase();
        return "pagprincipal.jsp".equals(url) || "/pagprincipal.jsp".equals(url);
    }

    private boolean moduloPermitido(MenuOpcion opcion) {
        if (opcion == null) {
            return false;
        }

        String codigo = opcion.getCodigo() == null ? "" : opcion.getCodigo().trim().toUpperCase();

        if ("INI".equals(codigo)
                || "DEP".equals(codigo)
                || "CAR".equals(codigo)
                || "EMP".equals(codigo)
                || "USU".equals(codigo)
                || "PER".equals(codigo)
                || "OCP".equals(codigo)
                || "OPC".equals(codigo)) {
            return true;
        }

        String url = normalizarUrl(opcion.getUrl());
        return "pagprincipal.jsp".equals(url)
                || "departamentos.jsp".equals(url)
                || "departamentocontroller".equals(url)
                || "cargos.jsp".equals(url)
                || "cargocontroller".equals(url)
                || "empleados.jsp".equals(url)
                || "empleadocontroller".equals(url)
                || "usuarios.jsp".equals(url)
                || "usuariocontroller".equals(url)
                || "perfiles.jsp".equals(url)
                || "perfilcontroller".equals(url)
                || "permisos.jsp".equals(url)
                || "permisocontroller".equals(url);
    }

    private String normalizarUrl(String url) {
        String limpia = url == null ? "" : url.trim();
        int query = limpia.indexOf('?');

        if (query >= 0) {
            limpia = limpia.substring(0, query);
        }

        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }

        return limpia.toLowerCase();
    }

    private String icono(MenuOpcion opcion) {
        String texto = opcion == null || opcion.getDescripcion() == null ? "MM" : opcion.getDescripcion().trim();

        if (texto.length() >= 2) {
            return texto.substring(0, 2).toUpperCase();
        }

        return "MM";
    }

    private String iconoModuloArchivo(MenuOpcion opcion) {
        if (opcion == null) {
            return "";
        }

        String codigo = opcion.getCodigo() == null ? "" : opcion.getCodigo().trim().toUpperCase();
        String ruta = normalizarUrl(opcion.getUrl());
        String descripcion = opcion.getDescripcion() == null ? "" : opcion.getDescripcion().trim().toLowerCase();

        if ("DEP".equals(codigo) || ruta.contains("departamento") || descripcion.contains("departamento")) {
            return "departamentos.png";
        }

        if ("CAR".equals(codigo) || ruta.contains("cargo") || descripcion.contains("cargo")) {
            return "cargos.png";
        }

        if ("EMP".equals(codigo) || ruta.contains("empleado") || descripcion.contains("empleado")) {
            return "empleados.png";
        }

        if ("USU".equals(codigo) || ruta.contains("usuario") || descripcion.contains("usuario")) {
            return "usuarios.png";
        }

        if ("PER".equals(codigo) || ruta.contains("perfil") || descripcion.contains("perfil")) {
            return "perfiles.png";
        }

        if ("OCP".equals(codigo)
                || "OPC".equals(codigo)
                || ruta.contains("permiso")
                || descripcion.contains("permiso")) {
            return "permisos.png";
        }

        return "";
    }

    private String ruta(MenuOpcion opcion) {
        if (!tieneUrl(opcion)) {
            return "";
        }

        String url = opcion.getUrl().trim();

        while (url.startsWith("/")) {
            url = url.substring(1);
        }

        String normalizada = url.toLowerCase();

        if ("departamentos.jsp".equals(normalizada)) {
            return "DepartamentoController";
        }

        if ("cargos.jsp".equals(normalizada)) {
            return "CargoController";
        }

        if ("empleados.jsp".equals(normalizada)) {
            return "EmpleadoController";
        }

        if ("usuarios.jsp".equals(normalizada)) {
            return "UsuarioController";
        }

        if ("perfiles.jsp".equals(normalizada)) {
            return "PerfilController";
        }

        if ("permisos.jsp".equals(normalizada)) {
            return "PermisoController";
        }

        return url;
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

    List<MenuOpcion> menuUsuario = (List<MenuOpcion>) session.getAttribute("menuUsuario");
    String perfilNombre = (String) session.getAttribute("perfilNombre");
    String mensaje = request.getParameter("mensaje");
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Panel principal | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260615-seg1">
    </head>

    <body class="body-dashboard">

        <jsp:include page="/WEB-INF/includes/topbar.jsp" />

        <main class="dashboard-simple">

            <section class="bienvenida-dashboard">

                <div class="bienvenida-texto">
                    <h2>Bienvenido al sistema</h2>

                    <p>
                        Master Monster es un sistema de gestion de proyectos.
                        Desde este panel puedes acceder a las opciones permitidas para tu perfil
                        <%= perfilNombre == null ? "" : h(perfilNombre) %>.
                    </p>

                    <% if (perfilNombre != null && !perfilNombre.trim().isEmpty()) { %>
                        <span class="badge-perfil-dashboard">
                            Perfil <%= h(perfilNombre) %>
                        </span>
                    <% } %>

                    <% if ("clave_actualizada".equals(mensaje)) { %>
                        <p class="mensaje-exito-general">Contrasena actualizada correctamente.</p>
                    <% } %>
                </div>

                <div class="bienvenida-imagen">
                    <img
                        src="${pageContext.request.contextPath}/img/inge.monster.png"
                        alt="Personaje Master Monster"
                    >
                </div>

            </section>

            <section class="modulo-unico">

                <% boolean tieneOpciones = false;
                   if (menuUsuario != null) {
                       for (MenuOpcion opcion : menuUsuario) {
                           if (tieneUrl(opcion) && !esInicio(opcion) && moduloPermitido(opcion)) {
                               String iconoModulo = iconoModuloArchivo(opcion);
                               tieneOpciones = true; %>
                                <article class="modulo-card">
                                    <div class="modulo-icono">
                                        <% if (!iconoModulo.isEmpty()) { %>
                                            <img
                                                class="icono-modulo-img"
                                                src="${pageContext.request.contextPath}/img/iconos_master_monster/<%= h(iconoModulo) %>"
                                                alt="<%= h(opcion.getDescripcion()) %>"
                                            >
                                        <% } else { %>
                                            <%= h(icono(opcion)) %>
                                        <% } %>
                                    </div>

                                    <div class="modulo-info">
                                        <h3><%= h(opcion.getDescripcion()) %></h3>
                                        <p>Acceso habilitado para el perfil actual.</p>
                                    </div>

                                    <a href="${pageContext.request.contextPath}/<%= h(ruta(opcion)) %>">
                                        Entrar al modulo
                                    </a>
                                </article>
                <%         }
                       }
                   }

                   if (!tieneOpciones) { %>
                        <article class="modulo-card">
                            <div class="modulo-icono">MM</div>

                            <div class="modulo-info">
                                <h3>Sin opciones asignadas</h3>
                                <p>El perfil actual no tiene opciones de menu configuradas.</p>
                            </div>
                        </article>
                <% } %>

            </section>

            <footer class="footer-dashboard">
                GRUPO 2
            </footer>

        </main>

    </body>
</html>
