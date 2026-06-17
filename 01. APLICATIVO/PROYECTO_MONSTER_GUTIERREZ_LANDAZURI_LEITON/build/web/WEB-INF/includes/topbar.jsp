<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.MenuOpcion"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%!
    private String topbarH(Object valor) {
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

    private boolean topbarTieneUrl(MenuOpcion opcion) {
        return opcion != null && opcion.getUrl() != null && !opcion.getUrl().trim().isEmpty();
    }

    private String topbarNormalizarRuta(String ruta) {
        String limpia = ruta == null ? "" : ruta.trim();
        int query = limpia.indexOf('?');

        if (query >= 0) {
            limpia = limpia.substring(0, query);
        }

        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }

        return limpia.toLowerCase();
    }

    private boolean topbarEsInicio(MenuOpcion opcion) {
        if (!topbarTieneUrl(opcion)) {
            return false;
        }

        String codigo = opcion.getCodigo() == null ? "" : opcion.getCodigo().trim().toUpperCase();
        String ruta = topbarNormalizarRuta(opcion.getUrl());
        return "INI".equals(codigo) || "pagprincipal.jsp".equals(ruta);
    }

    private boolean topbarEsAdministracion(MenuOpcion opcion) {
        if (opcion == null) {
            return false;
        }

        String codigo = opcion.getCodigo() == null ? "" : opcion.getCodigo().trim().toUpperCase();
        String ruta = topbarTieneUrl(opcion) ? topbarNormalizarRuta(opcion.getUrl()) : "";
        String descripcion = opcion.getDescripcion() == null ? "" : opcion.getDescripcion().trim().toLowerCase();

        return "USU".equals(codigo)
                || "PER".equals(codigo)
                || "OCP".equals(codigo)
                || "OPC".equals(codigo)
                || "usuarios.jsp".equals(ruta)
                || "usuariocontroller".equals(ruta)
                || "perfiles.jsp".equals(ruta)
                || "perfilcontroller".equals(ruta)
                || "permisos.jsp".equals(ruta)
                || "permisocontroller".equals(ruta)
                || descripcion.contains("usuario")
                || descripcion.contains("perfil")
                || descripcion.contains("permiso");
    }

    private boolean topbarModuloPrincipal(MenuOpcion opcion) {
        if (opcion == null || topbarEsInicio(opcion) || topbarEsAdministracion(opcion)) {
            return false;
        }

        String codigo = opcion.getCodigo() == null ? "" : opcion.getCodigo().trim().toUpperCase();
        String ruta = topbarTieneUrl(opcion) ? topbarNormalizarRuta(opcion.getUrl()) : "";

        return "DEP".equals(codigo)
                || "CAR".equals(codigo)
                || "EMP".equals(codigo)
                || "departamentos.jsp".equals(ruta)
                || "departamentocontroller".equals(ruta)
                || "cargos.jsp".equals(ruta)
                || "cargocontroller".equals(ruta)
                || "empleados.jsp".equals(ruta)
                || "empleadocontroller".equals(ruta);
    }

    private String topbarRuta(MenuOpcion opcion) {
        if (!topbarTieneUrl(opcion)) {
            return "#";
        }

        String ruta = topbarNormalizarRuta(opcion.getUrl());

        if ("departamentos.jsp".equals(ruta)) {
            return "DepartamentoController";
        }

        if ("cargos.jsp".equals(ruta)) {
            return "CargoController";
        }

        if ("empleados.jsp".equals(ruta)) {
            return "EmpleadoController";
        }

        if ("usuarios.jsp".equals(ruta)) {
            return "UsuarioController";
        }

        if ("perfiles.jsp".equals(ruta)) {
            return "PerfilController";
        }

        if ("permisos.jsp".equals(ruta)) {
            return "PermisoController";
        }

        return ruta;
    }

    private String topbarTexto(MenuOpcion opcion) {
        if (opcion == null) {
            return "";
        }

        String codigo = opcion.getCodigo() == null ? "" : opcion.getCodigo().trim().toUpperCase();

        if ("DEP".equals(codigo)) {
            return "Departamentos";
        }

        if ("CAR".equals(codigo)) {
            return "Cargos";
        }

        if ("EMP".equals(codigo)) {
            return "Empleados";
        }

        if ("USU".equals(codigo)) {
            return "Usuarios";
        }

        if ("PER".equals(codigo)) {
            return "Perfiles";
        }

        if ("OCP".equals(codigo) || "OPC".equals(codigo)) {
            return "Permisos";
        }

        return opcion.getDescripcion() == null ? "" : opcion.getDescripcion();
    }
%>

<%
    List<MenuOpcion> topbarMenu = (List<MenuOpcion>) session.getAttribute("menuUsuario");
    Object topbarUsuarioObj = session.getAttribute("usuarioLogueado");
    Object topbarPerfilObj = session.getAttribute("perfilNombre");
    String topbarUsuario = topbarUsuarioObj == null ? "" : String.valueOf(topbarUsuarioObj);
    String topbarPerfil = topbarPerfilObj == null ? "" : String.valueOf(topbarPerfilObj);

    boolean topbarTieneMenu = topbarMenu != null && !topbarMenu.isEmpty();
    boolean topbarTieneAdministracion = false;

    if (topbarTieneMenu) {
        for (MenuOpcion opcion : topbarMenu) {
            if (topbarTieneUrl(opcion) && topbarEsAdministracion(opcion)) {
                topbarTieneAdministracion = true;
                break;
            }
        }
    }
%>

<header class="topbar topbar-monster-clean">
    <a class="topbar-logo" href="${pageContext.request.contextPath}/pagPrincipal.jsp" aria-label="Ir al inicio">
        <img
            src="${pageContext.request.contextPath}/img/Monsters-Inc.-Symbol.png"
            alt="Logo Master Monster"
        >

        <div class="topbar-brand-text">
            <h1>Master Monster</h1>
            <span>Sistema de gestion de proyectos</span>
        </div>
    </a>

    <nav class="topbar-nav" aria-label="Menu principal">
        <a class="topbar-link" href="${pageContext.request.contextPath}/pagPrincipal.jsp">Inicio</a>

        <% if (topbarTieneMenu) {
            for (MenuOpcion opcion : topbarMenu) {
                if (topbarTieneUrl(opcion) && topbarModuloPrincipal(opcion)) { %>
                    <a class="topbar-link" href="${pageContext.request.contextPath}/<%= topbarH(topbarRuta(opcion)) %>">
                        <%= topbarH(topbarTexto(opcion)) %>
                    </a>
        <%      }
            }
           } else { %>
            <a class="topbar-link" href="${pageContext.request.contextPath}/DepartamentoController">Departamentos</a>
            <a class="topbar-link" href="${pageContext.request.contextPath}/CargoController">Cargos</a>
            <a class="topbar-link" href="${pageContext.request.contextPath}/EmpleadoController">Empleados</a>
        <% } %>

        <% if (topbarTieneAdministracion) { %>
            <div class="topbar-dropdown">
                <button class="topbar-link topbar-dropdown-toggle" type="button" aria-haspopup="true" aria-expanded="false">
                    Administración
                    <span aria-hidden="true">▾</span>
                </button>

                <div class="topbar-dropdown-menu" role="menu">
                    <% for (MenuOpcion opcion : topbarMenu) {
                        if (topbarTieneUrl(opcion) && topbarEsAdministracion(opcion)) { %>
                            <a role="menuitem" href="${pageContext.request.contextPath}/<%= topbarH(topbarRuta(opcion)) %>">
                                <%= topbarH(topbarTexto(opcion)) %>
                            </a>
                    <%  }
                       } %>
                </div>
            </div>
        <% } %>
    </nav>

    <div class="topbar-user">
        <div class="topbar-user-info" title="<%= topbarH(topbarUsuario) %> - <%= topbarH(topbarPerfil) %>">
            <strong><%= topbarH(topbarUsuario) %></strong>
            <span><%= topbarH(topbarPerfil) %></span>
        </div>

        <a href="${pageContext.request.contextPath}/Logout" class="btn-salir">Cerrar sesion</a>
    </div>
</header>
