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

    private boolean topbarEsInicio(MenuOpcion opcion) {
        if (!topbarTieneUrl(opcion)) {
            return false;
        }

        String ruta = topbarNormalizarRuta(opcion.getUrl());
        return "pagprincipal.jsp".equals(ruta);
    }

    private boolean topbarModuloPermitido(MenuOpcion opcion) {
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

        return topbarRutaPermitida(topbarNormalizarRuta(opcion.getUrl()));
    }

    private boolean topbarRutaPermitida(String ruta) {
        return "pagprincipal.jsp".equals(ruta)
                || "departamentos.jsp".equals(ruta)
                || "departamentocontroller".equals(ruta)
                || "cargos.jsp".equals(ruta)
                || "cargocontroller".equals(ruta)
                || "empleados.jsp".equals(ruta)
                || "empleadocontroller".equals(ruta)
                || "usuarios.jsp".equals(ruta)
                || "usuariocontroller".equals(ruta)
                || "perfiles.jsp".equals(ruta)
                || "perfilcontroller".equals(ruta)
                || "permisos.jsp".equals(ruta)
                || "permisocontroller".equals(ruta);
    }

    private String topbarRuta(MenuOpcion opcion) {
        if (!topbarTieneUrl(opcion)) {
            return "";
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
%>

<%
    List<MenuOpcion> topbarMenu = (List<MenuOpcion>) session.getAttribute("menuUsuario");
    Object topbarUsuarioObj = session.getAttribute("usuarioLogueado");
    Object topbarPerfilObj = session.getAttribute("perfilNombre");
    String topbarUsuario = topbarUsuarioObj == null ? "" : String.valueOf(topbarUsuarioObj);
    String topbarPerfil = topbarPerfilObj == null ? "" : String.valueOf(topbarPerfilObj);
%>

<header class="topbar topbar-dinamica">
    <div class="topbar-logo">
        <img
            src="${pageContext.request.contextPath}/img/Monsters-Inc.-Symbol.png"
            alt="Logo Master Monster"
        >

        <div>
            <h1>Master Monster</h1>
            <span>Sistema de gestion de proyectos</span>
        </div>
    </div>

    <nav class="topbar-menu topbar-menu-centro">
        <a href="${pageContext.request.contextPath}/pagPrincipal.jsp">Inicio</a>
        <% if (topbarMenu != null) {
            for (MenuOpcion opcion : topbarMenu) {
                if (topbarTieneUrl(opcion) && !topbarEsInicio(opcion) && topbarModuloPermitido(opcion)) { %>
                    <a href="${pageContext.request.contextPath}/<%= topbarH(topbarRuta(opcion)) %>">
                        <%= topbarH(opcion.getDescripcion()) %>
                    </a>
        <%      }
            }
           } %>
    </nav>

    <div class="topbar-user">
        <div class="topbar-user-info">
            <strong><%= topbarH(topbarUsuario) %></strong>
            <span><%= topbarH(topbarPerfil) %></span>
        </div>

        <a href="${pageContext.request.contextPath}/Logout" class="btn-salir">Cerrar sesion</a>
    </div>
</header>
