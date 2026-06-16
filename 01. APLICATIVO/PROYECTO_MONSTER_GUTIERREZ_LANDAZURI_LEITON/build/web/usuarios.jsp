<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Perfil"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Persona"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.UsuarioPerfil"%>
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

    private String selected(String actual, String opcion) {
        return actual != null && actual.equals(opcion) ? "selected" : "";
    }

    private String estadoTexto(String estado) {
        if ("A".equalsIgnoreCase(estado)) {
            return "Activo";
        }

        if ("B".equalsIgnoreCase(estado)) {
            return "Bloqueado";
        }

        if ("I".equalsIgnoreCase(estado)) {
            return "Inactivo";
        }

        return estado == null || estado.trim().isEmpty() ? "Sin estado" : estado;
    }

    private String estadoClase(String estado) {
        if ("A".equalsIgnoreCase(estado)) {
            return "badge-ok";
        }

        if ("B".equalsIgnoreCase(estado)) {
            return "badge-error";
        }

        return "badge-neutro";
    }

    private String cambioClase(String cambio) {
        return "S".equalsIgnoreCase(cambio) ? "badge-alerta" : "badge-ok";
    }

    private String cambioTexto(String cambio) {
        return "S".equalsIgnoreCase(cambio) ? "Obligatorio" : "No";
    }
%>

<%
    String mensaje = request.getParameter("mensaje");
    String error = (String) request.getAttribute("error");
    String claveTemporal = (String) request.getAttribute("claveTemporal");
    List<UsuarioPerfil> usuarios = (List<UsuarioPerfil>) request.getAttribute("usuarios");
    List<Perfil> perfiles = (List<Perfil>) request.getAttribute("perfiles");
    List<Persona> empleadosSinUsuario = (List<Persona>) request.getAttribute("empleadosSinUsuario");

    if (claveTemporal == null) {
        claveTemporal = "Monster2026";
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Usuarios | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260615-seg1">
    </head>

    <body class="body-dashboard">

        <jsp:include page="/WEB-INF/includes/topbar.jsp" />

        <main class="crud-page">

            <section class="crud-titulo">
                <div>
                    <h2>Gestion de Usuarios</h2>
                    <p>Administra accesos, perfiles, intentos y bloqueo de cuentas.</p>
                </div>
            </section>

            <% if ("guardado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Usuario registrado correctamente. Clave temporal: <strong><%= h(claveTemporal) %></strong></p>
            <% } else if ("activado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Usuario activado correctamente.</p>
            <% } else if ("bloqueado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Usuario bloqueado correctamente.</p>
            <% } else if ("reseteado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Contrasena temporal restablecida: <strong><%= h(claveTemporal) %></strong></p>
            <% } else if ("perfil_asignado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Perfil asignado correctamente.</p>
            <% } else if ("no_actualizado".equals(mensaje)) { %>
                <p class="mensaje-error-general">No se pudo actualizar el usuario.</p>
            <% } %>

            <% if (error != null) { %>
                <p class="mensaje-error-general"><%= h(error) %></p>
            <% } %>

            <section class="crud-contenedor">
                <div class="crud-toolbar">
                    <div class="crud-toolbar-left">
                        <a href="${pageContext.request.contextPath}/UsuarioController" class="btn-crud celeste">Recargar</a>
                    </div>

                    <div class="buscar-registros">
                        <label for="buscarUsuario">Buscar:</label>
                        <input type="text" id="buscarUsuario" placeholder="Buscar usuario...">
                    </div>
                </div>

                <div class="form-crud-box">
                    <h3>Crear usuario para empleado existente</h3>

                    <form action="${pageContext.request.contextPath}/UsuarioController" method="post" class="form-crud-lineal">
                        <input type="hidden" name="accion" value="guardar">

                        <div class="grupo-campo">
                            <label for="personaCodigo">Empleado sin usuario</label>
                            <select id="personaCodigo" name="personaCodigo" required>
                                <option value="">Seleccione...</option>
                                <% if (empleadosSinUsuario != null) {
                                    for (Persona persona : empleadosSinUsuario) { %>
                                        <option
                                            value="<%= h(persona.getPeperCodigo()) %>"
                                            data-cedula="<%= h(persona.getCedula()) %>"
                                        >
                                            <%= h(persona.getApellidos()) %> <%= h(persona.getNombres()) %> - <%= h(persona.getCedula()) %>
                                        </option>
                                <%  }
                                   } %>
                            </select>
                        </div>

                        <div class="grupo-campo">
                            <label for="login">Login</label>
                            <input type="text" id="login" name="login" maxlength="50" placeholder="Cedula del empleado" required>
                        </div>

                        <div class="grupo-campo">
                            <label for="perfilCodigo">Perfil</label>
                            <select id="perfilCodigo" name="perfilCodigo" required>
                                <option value="">Seleccione...</option>
                                <% if (perfiles != null) {
                                    for (Perfil perfil : perfiles) { %>
                                        <option value="<%= h(perfil.getCodigo()) %>"><%= h(perfil.getDescripcion()) %></option>
                                <%  }
                                   } %>
                            </select>
                        </div>

                        <button type="submit" class="btn-crud-form">Guardar</button>
                    </form>
                </div>

                <div class="tabla-contenedor tabla-clara tabla-seguridad">
                    <table class="tabla-crud" id="tablaUsuarios" data-paginacion="true" data-paginacion-tamanio="5">
                        <thead>
                            <tr>
                                <th>Usuario</th>
                                <th>Empleado</th>
                                <th>Perfil</th>
                                <th>Estado</th>
                                <th>Cambio clave</th>
                                <th>Intentos</th>
                                <th>Ultimo acceso</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% if (usuarios != null && !usuarios.isEmpty()) { %>
                                <% for (UsuarioPerfil usuario : usuarios) {
                                    String loginUrl = url(usuario.getLogin());
                                %>
                                    <tr>
                                        <td><strong><%= h(usuario.getLogin()) %></strong></td>
                                        <td>
                                            <%= h(usuario.getApellidos()) %> <%= h(usuario.getNombres()) %><br>
                                            <small><%= h(usuario.getCedula()) %></small>
                                        </td>
                                        <td><%= h(usuario.getPerfilDescripcion()) %></td>
                                        <td>
                                            <span class="estado-badge <%= estadoClase(usuario.getEstadoCodigo()) %>">
                                                <%= h(estadoTexto(usuario.getEstadoCodigo())) %>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="estado-badge <%= cambioClase(usuario.getCambioClave()) %>">
                                                <%= h(cambioTexto(usuario.getCambioClave())) %>
                                            </span>
                                        </td>
                                        <td><%= usuario.getIntentosFallidos() %></td>
                                        <td><%= h(usuario.getUltimoAcceso()) %></td>
                                        <td>
                                            <div class="acciones-tabla acciones-seguridad">
                                                <a
                                                    class="btn-accion-icono accion-ver"
                                                    href="${pageContext.request.contextPath}/UsuarioController?accion=activar&login=<%= loginUrl %>"
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
                                                    href="${pageContext.request.contextPath}/UsuarioController?accion=bloquear&login=<%= loginUrl %>"
                                                    title="Bloquear"
                                                    aria-label="Bloquear"
                                                >
                                                    <img
                                                        src="${pageContext.request.contextPath}/img/iconos_master_monster/eliminar.png"
                                                        alt=""
                                                    >
                                                </a>
                                                <a
                                                    class="btn-accion-icono accion-editar"
                                                    href="${pageContext.request.contextPath}/UsuarioController?accion=resetear&login=<%= loginUrl %>"
                                                    title="Resetear"
                                                    aria-label="Resetear"
                                                >
                                                    <img
                                                        src="${pageContext.request.contextPath}/img/iconos_master_monster/editar.png"
                                                        alt=""
                                                    >
                                                </a>
                                            </div>

                                            <form action="${pageContext.request.contextPath}/UsuarioController" method="post" class="form-asignar-perfil">
                                                <input type="hidden" name="accion" value="asignarPerfil">
                                                <input type="hidden" name="login" value="<%= h(usuario.getLogin()) %>">
                                                <select name="perfilCodigo" required>
                                                    <% if (perfiles != null) {
                                                        for (Perfil perfil : perfiles) { %>
                                                            <option
                                                                value="<%= h(perfil.getCodigo()) %>"
                                                                <%= selected(usuario.getPerfilCodigo(), perfil.getCodigo()) %>
                                                            >
                                                                <%= h(perfil.getDescripcion()) %>
                                                            </option>
                                                    <%  }
                                                       } %>
                                                </select>
                                                <button type="submit" class="btn-crud-form">Asignar</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="8" class="tabla-vacia">No existen usuarios registrados.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>

            <footer class="footer-dashboard">
                GUTIERREZ - LANDAZURI - LEITON
            </footer>
        </main>

        <script src="${pageContext.request.contextPath}/js/usuarios.js?v=20260615-seg1" defer></script>
        <script src="${pageContext.request.contextPath}/js/paginacion.js?v=20260611-pag1" defer></script>
    </body>
</html>
