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
        return opcion != null
                && opcion.getUrl() != null
                && !opcion.getUrl().trim().isEmpty();
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

        return limpia;
    }

    private String rutaModulo(MenuOpcion opcion) {
        String ruta = normalizarUrl(opcion == null ? "" : opcion.getUrl());
        String rutaLower = ruta.toLowerCase();

        if ("pagprincipal.jsp".equals(rutaLower)) {
            return "";
        }

        if ("departamentos.jsp".equals(rutaLower)) {
            return "DepartamentoController";
        }

        if ("cargos.jsp".equals(rutaLower)) {
            return "CargoController";
        }

        if ("empleados.jsp".equals(rutaLower)) {
            return "EmpleadoController";
        }

        if ("usuarios.jsp".equals(rutaLower)) {
            return "UsuarioController";
        }

        if ("perfiles.jsp".equals(rutaLower)) {
            return "PerfilController";
        }

        if ("permisos.jsp".equals(rutaLower)) {
            return "PermisoController";
        }

        return ruta;
    }

    private boolean esInicio(MenuOpcion opcion) {
        if (!tieneUrl(opcion)) {
            return false;
        }

        String ruta = normalizarUrl(opcion.getUrl()).toLowerCase();
        return "pagprincipal.jsp".equals(ruta);
    }

    private String pintarNodo(MenuOpcion opcion, int nivel, String contextPath) {
        if (opcion == null) {
            return "";
        }

        if (esInicio(opcion)) {
            return "";
        }

        StringBuilder html = new StringBuilder();

        String descripcion = h(opcion.getDescripcion());
        String codigo = h(opcion.getCodigo());
        String padding = String.valueOf(18 + (nivel * 16));

        if (opcion.tieneHijos()) {
            html.append("<li class='tree-item tree-parent nivel-").append(nivel).append("'>");
            html.append("<button type='button' class='tree-toggle' style='padding-left:").append(padding).append("px'>");
            html.append("<span class='tree-arrow'></span>");
            html.append("<span class='tree-text'>").append(descripcion).append("</span>");
            html.append("</button>");

            html.append("<ul class='tree-children'>");

            for (MenuOpcion hijo : opcion.getHijos()) {
                html.append(pintarNodo(hijo, nivel + 1, contextPath));
            }

            html.append("</ul>");
            html.append("</li>");

        } else if (tieneUrl(opcion)) {
            String modulo = rutaModulo(opcion);

            if (modulo == null || modulo.trim().isEmpty()) {
                return "";
            }

            String ruta = contextPath + "/" + modulo;

            html.append("<li class='tree-item tree-leaf nivel-").append(nivel).append("'>");
            html.append("<a href='").append(h(ruta)).append("' ");
            html.append("class='tree-link' ");
            html.append("data-tab-url='").append(h(ruta)).append("' ");
            html.append("data-tab-title='").append(descripcion).append("' ");
            html.append("data-codigo='").append(codigo).append("' ");
            html.append("style='padding-left:").append(padding).append("px'>");
            html.append("<span class='tree-leaf-dot'></span>");
            html.append("<span class='tree-text'>").append(descripcion).append("</span>");
            html.append("</a>");
            html.append("</li>");
        }

        return html.toString();
    }
%>

<%
    List<MenuOpcion> menuTree = (List<MenuOpcion>) session.getAttribute("menuUsuario");
    String contextPath = request.getContextPath();
%>

<aside class="sidebar-tree">
    <div class="sidebar-tree-title">
        <button type="button" id="btnSidebarInicio" class="sidebar-inicio-btn">
            INICIO
        </button>
    </div>
    <nav class="sidebar-nav">
        <ul class="tree-root">
            <% if (menuTree != null && !menuTree.isEmpty()) {
                for (MenuOpcion opcion : menuTree) { %>
                    <%= pintarNodo(opcion, 0, contextPath) %>
            <%  }
            } else { %>
                <li class="tree-empty">No hay opciones asignadas a este perfil.</li>
            <% } %>
        </ul>
    </nav>
</aside>