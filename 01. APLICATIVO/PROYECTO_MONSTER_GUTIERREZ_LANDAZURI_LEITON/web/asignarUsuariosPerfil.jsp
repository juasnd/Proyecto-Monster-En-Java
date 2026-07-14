<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Perfil"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.UsuarioPerfil"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%!
    private String h(Object valor) {
        if (valor == null) {
            return "";
        }

        return String.valueOf(valor)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String selected(String actual, String opcion) {
        return actual != null && actual.equals(opcion) ? "selected" : "";
    }

    private String nombreCompleto(UsuarioPerfil usuario) {
        if (usuario == null) {
            return "Usuario";
        }

        String nombres = usuario.getNombres() == null ? "" : usuario.getNombres().trim();
        String apellidos = usuario.getApellidos() == null ? "" : usuario.getApellidos().trim();
        String nombre = (apellidos + " " + nombres).trim();

        return nombre.isEmpty() ? usuario.getLogin() : nombre;
    }

    private String textoDisponible(UsuarioPerfil usuario) {
        String perfilActual = usuario.getPerfilDescripcion();

        if (perfilActual == null || perfilActual.trim().isEmpty()) {
            perfilActual = "Sin perfil";
        }

        return nombreCompleto(usuario)
                + " | " + usuario.getLogin()
                + " | " + perfilActual;
    }

    private String textoAsignado(UsuarioPerfil usuario) {
        return nombreCompleto(usuario) + " | " + usuario.getLogin();
    }
%>

<%
    String perfilSeleccionado = (String) request.getAttribute("perfilSeleccionado");
    String mensaje = request.getParameter("mensaje");

    List<Perfil> perfiles = (List<Perfil>) request.getAttribute("perfiles");
    List<UsuarioPerfil> disponibles = (List<UsuarioPerfil>) request.getAttribute("usuariosDisponibles");
    List<UsuarioPerfil> asignados = (List<UsuarioPerfil>) request.getAttribute("usuariosAsignados");

    if (perfiles == null) {
        perfiles = Collections.emptyList();
    }

    if (disponibles == null) {
        disponibles = Collections.emptyList();
    }

    if (asignados == null) {
        asignados = Collections.emptyList();
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Asignar usuarios a perfil | Master Monster</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260714-asignacion-usuarios">
    </head>

    <body class="body-dashboard">
        <main class="crud-page asignacion-usuario-page">

            <section class="crud-titulo asignacion-usuario-titulo">
                <div>
                    <h2>Asignar usuarios a perfil</h2>
                    <p>Selecciona un perfil y administra sus usuarios mediante los dos listados.</p>
                </div>
            </section>

            <% if ("asignados".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Los usuarios seleccionados fueron asignados correctamente.</p>
            <% } else if ("asignados_todos".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Todos los usuarios disponibles fueron asignados correctamente.</p>
            <% } else if ("retirados".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Los usuarios seleccionados fueron retirados correctamente.</p>
            <% } else if ("retirados_todos".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Todos los usuarios fueron retirados del perfil.</p>
            <% } else if ("seleccione_perfil".equals(mensaje)) { %>
                <p class="mensaje-error-general">Selecciona un perfil para continuar.</p>
            <% } else if ("seleccione_usuario".equals(mensaje)) { %>
                <p class="mensaje-error-general">Selecciona al menos un usuario.</p>
            <% } else if ("error".equals(mensaje) || "accion_invalida".equals(mensaje)) { %>
                <p class="mensaje-error-general">No se pudo completar la operación.</p>
            <% } %>

            <section class="crud-contenedor asignacion-usuario-panel">

                <form id="formSeleccionPerfil"
                      action="${pageContext.request.contextPath}/AsignacionUsuarioPerfilController"
                      method="get"
                      class="asignacion-perfil-form">

                    <label for="perfilCodigo">Perfil</label>
                    <select id="perfilCodigo" name="perfilCodigo" required>
                        <option value="">Seleccione un perfil...</option>

                        <% for (Perfil perfil : perfiles) { %>
                            <option value="<%= h(perfil.getCodigo()) %>"
                                    <%= selected(perfilSeleccionado, perfil.getCodigo()) %>>
                                <%= h(perfil.getDescripcion()) %>
                            </option>
                        <% } %>
                    </select>
                </form>

                <% if (perfilSeleccionado != null && !perfilSeleccionado.trim().isEmpty()) { %>

                    <form id="formAsignacionUsuarios"
                          action="${pageContext.request.contextPath}/AsignacionUsuarioPerfilController"
                          method="post"
                          class="asignacion-listas-form">

                        <input type="hidden" name="perfilCodigo" value="<%= h(perfilSeleccionado) %>">

                        <div class="asignacion-listas-grid">

                            <section class="asignacion-lista-columna">
                                <div class="asignacion-lista-encabezado">
                                    <label for="usuariosDisponibles">Usuarios disponibles</label>
                                    <span><%= disponibles.size() %></span>
                                </div>

                                <input type="search"
                                       id="buscarDisponibles"
                                       class="asignacion-buscador"
                                       placeholder="Buscar usuario..."
                                       autocomplete="off">

                                <select id="usuariosDisponibles"
                                        name="usuariosDisponibles"
                                        class="asignacion-listbox"
                                        multiple
                                        size="12"
                                        aria-label="Usuarios disponibles">

                                    <% for (UsuarioPerfil usuario : disponibles) { %>
                                        <option value="<%= h(usuario.getLogin()) %>"
                                                draggable="true"
                                                title="<%= h(textoDisponible(usuario)) %>">
                                            <%= h(textoDisponible(usuario)) %>
                                        </option>
                                    <% } %>
                                </select>

                                <% if (disponibles.isEmpty()) { %>
                                    <p class="asignacion-lista-vacia">No hay usuarios disponibles.</p>
                                <% } %>
                            </section>

                            <div class="asignacion-botones" aria-label="Acciones de asignación">
                                <button type="submit"
                                        name="accion"
                                        value="asignarSeleccionados"
                                        class="asignacion-boton"
                                        data-requiere-seleccion="usuariosDisponibles"
                                        title="Asignar seleccionados">&gt;</button>

                                <button type="submit"
                                        name="accion"
                                        value="asignarTodos"
                                        class="asignacion-boton"
                                        title="Asignar todos">&gt;&gt;</button>

                                <button type="submit"
                                        name="accion"
                                        value="retirarSeleccionados"
                                        class="asignacion-boton"
                                        data-requiere-seleccion="usuariosAsignados"
                                        title="Retirar seleccionados">&lt;</button>

                                <button type="submit"
                                        name="accion"
                                        value="retirarTodos"
                                        class="asignacion-boton"
                                        title="Retirar todos">&lt;&lt;</button>
                            </div>

                            <section class="asignacion-lista-columna">
                                <div class="asignacion-lista-encabezado">
                                    <label for="usuariosAsignados">Usuarios asignados</label>
                                    <span><%= asignados.size() %></span>
                                </div>

                                <input type="search"
                                       id="buscarAsignados"
                                       class="asignacion-buscador"
                                       placeholder="Buscar usuario..."
                                       autocomplete="off">

                                <select id="usuariosAsignados"
                                        name="usuariosAsignados"
                                        class="asignacion-listbox"
                                        multiple
                                        size="12"
                                        aria-label="Usuarios asignados">

                                    <% for (UsuarioPerfil usuario : asignados) { %>
                                        <option value="<%= h(usuario.getLogin()) %>"
                                                draggable="true"
                                                title="<%= h(textoAsignado(usuario)) %>">
                                            <%= h(textoAsignado(usuario)) %>
                                        </option>
                                    <% } %>
                                </select>

                                <% if (asignados.isEmpty()) { %>
                                    <p class="asignacion-lista-vacia">Este perfil no tiene usuarios asignados.</p>
                                <% } %>
                            </section>

                        </div>
                    </form>

                    <p id="mensajeSeleccionUsuario" class="asignacion-aviso" hidden>
                        Selecciona al menos un usuario para realizar esta acción.
                    </p>

                <% } else { %>
                    <div class="asignacion-estado-inicial">
                        Selecciona un perfil para cargar los usuarios disponibles y asignados.
                    </div>
                <% } %>

            </section>
        </main>

        <script src="${pageContext.request.contextPath}/js/asignar-usuarios-perfil.js?v=20260714"></script>
    </body>
</html>
