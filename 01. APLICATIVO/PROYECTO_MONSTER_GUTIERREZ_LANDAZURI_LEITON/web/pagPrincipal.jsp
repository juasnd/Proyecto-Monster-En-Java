<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String perfilNombre = (String) session.getAttribute("perfilNombre");
    String mensaje = request.getParameter("mensaje");
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Panel principal | Master Monster</title>

        <link rel="icon" type="image/png" href="<%= request.getContextPath() %>/img/favicon.png">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/CSS/estilos.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body class="body-dashboard body-treeview">

        <jsp:include page="/WEB-INF/includes/topbar.jsp" />

        <div class="layout-treeview">

            <jsp:include page="/WEB-INF/includes/sidebarTree.jsp" />

            <main class="workspace-treeview">

                <section id="panelInicio" class="panel-inicio-treeview">

                    <div class="bienvenida-dashboard bienvenida-dashboard-tree">

                        <div class="bienvenida-texto">
                            <h2>Bienvenido al sistema</h2>

                            <p>
                                Master Monster es un sistema de gestión de proyectos.
                                Desde este panel puedes acceder a las opciones permitidas para tu perfil
                                <%= perfilNombre == null ? "" : perfilNombre %>.
                            </p>

                            <% if (perfilNombre != null && !perfilNombre.trim().isEmpty()) { %>
                                <span class="badge-perfil-dashboard">
                                    Perfil <%= perfilNombre %>
                                </span>
                            <% } %>

                            <% if ("clave_actualizada".equals(mensaje)) { %>
                                <p class="mensaje-exito-general">Contraseña actualizada correctamente.</p>
                            <% } %>
                        </div>

                        <div class="bienvenida-imagen">
                            <img
                                src="<%= request.getContextPath() %>/img/inge.monster.png"
                                alt="Personaje Master Monster">
                        </div>

                    </div>

                </section>

                <section id="panelModulo" class="panel-modulo-treeview" hidden>

                    <iframe
                        id="frameModulo"
                        class="frame-modulo-treeview"
                        title="Contenido del módulo">
                    </iframe>

                </section>

            </main>

        </div>

        <script src="<%= request.getContextPath() %>/js/sidebar-tree.js?v=<%= System.currentTimeMillis() %>"></script>
    </body>
</html>