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

    private String cambioTexto(String cambio) {
        return "S".equalsIgnoreCase(cambio) ? "Obligatorio" : "No";
    }

    private String tipoTexto(String tipo, String tipoDescripcion, String esEmpleado) {
        if (tipoDescripcion != null && !tipoDescripcion.trim().isEmpty()) {
            return tipoDescripcion;
        }

        if ("S".equalsIgnoreCase(esEmpleado)) {
            return "Empleado";
        }

        if ("EMP".equalsIgnoreCase(tipo)) {
            return "Empleado";
        }
        if ("INV".equalsIgnoreCase(tipo)) {
            return "Invitado";
        }
        if ("CLI".equalsIgnoreCase(tipo)) {
            return "Cliente";
        }
        if ("ADM".equalsIgnoreCase(tipo)) {
            return "Administrador";
        }
        if ("RHH".equalsIgnoreCase(tipo)) {
            return "Recursos Humanos";
        }

        return tipo == null || tipo.trim().isEmpty() ? "Persona" : tipo;
    }
%>

<%
    String mensaje = request.getParameter("mensaje");
    String error = (String) request.getAttribute("error");
    String claveTemporal = (String) request.getAttribute("claveTemporal");
    List<UsuarioPerfil> usuarios = (List<UsuarioPerfil>) request.getAttribute("usuarios");
    List<Perfil> perfiles = (List<Perfil>) request.getAttribute("perfiles");
    List<Persona> personasSinUsuario = (List<Persona>) request.getAttribute("personasSinUsuario");

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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260617-usuarios-fase1">
    </head>

    <body class="body-dashboard">


        <main class="crud-page usuario-page">

            <section class="crud-titulo usuarios-titulo-flex">
                <div>
                    <h2>Gestión de Usuarios</h2>
                    <p>Administra cuentas, perfiles y estados de acceso del sistema.</p>
                </div>

                <button type="button" class="btn-crud verde btn-nuevo-usuario" data-modal-open="modalTipoUsuario">
                    + Nuevo usuario
                </button>
            </section>

            <% if ("guardado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Usuario registrado correctamente. Clave temporal: <strong><%= h(claveTemporal) %></strong></p>
            <% } else if ("activado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Usuario activado correctamente.</p>
            <% } else if ("bloqueado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Usuario bloqueado correctamente.</p>
            <% } else if ("reseteado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Contraseña temporal restablecida: <strong><%= h(claveTemporal) %></strong></p>
            <% } else if ("perfil_asignado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Perfil asignado correctamente.</p>
            <% } else if ("no_actualizado".equals(mensaje)) { %>
                <p class="mensaje-error-general">No se pudo actualizar el usuario.</p>
            <% } %>

            <% if (error != null) { %>
                <p class="mensaje-error-general"><%= h(error) %></p>
            <% } %>

            <section class="crud-contenedor usuarios-panel usuarios-panel-limpio usuarios-fase1">
                <div class="crud-toolbar usuarios-toolbar-mejorada">
                    <div class="crud-toolbar-left usuarios-toolbar-left">
                        <a href="${pageContext.request.contextPath}/UsuarioController" class="btn-crud celeste">Recargar</a>
                    </div>

                    <div class="usuarios-filtros">
                        <div class="buscar-registros usuarios-buscar">
                            <label for="buscarUsuario">Buscar:</label>
                            <input type="text" id="buscarUsuario" placeholder="Usuario, nombre, tipo o perfil...">
                        </div>
                    </div>
                </div>

                <div class="tabla-contenedor tabla-clara tabla-seguridad tabla-usuarios-simple">
                    <table class="tabla-crud" id="tablaUsuarios" data-paginacion="true" data-paginacion-tamanio="3">
                        <thead>
                            <tr>
                                <th>Cuenta</th>
                                <th>Tipo</th>
                                <th>Perfil</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            <% if (usuarios != null && !usuarios.isEmpty()) { %>
                                <% for (UsuarioPerfil usuario : usuarios) {
                                    String loginUrl = url(usuario.getLogin());
                                    String personaNombre = (usuario.getApellidos() == null ? "" : usuario.getApellidos())
                                            + " "
                                            + (usuario.getNombres() == null ? "" : usuario.getNombres());
                                    String tipo = tipoTexto(usuario.getTipoPersona(), usuario.getTipoPersonaDescripcion(), usuario.getEsEmpleado());
                                    String estado = estadoTexto(usuario.getEstadoCodigo());
                                    String cambio = cambioTexto(usuario.getCambioClave());
                                %>
                                    <tr
                                        data-usuario-row
                                        data-login="<%= h(usuario.getLogin()) %>"
                                        data-persona="<%= h(personaNombre.trim()) %>"
                                        data-cedula="<%= h(usuario.getCedula()) %>"
                                        data-tipo="<%= h(tipo) %>"
                                        data-codigo-empleado="<%= h(usuario.getCodigoEmpleado()) %>"
                                        data-perfil-codigo="<%= h(usuario.getPerfilCodigo()) %>"
                                        data-perfil="<%= h(usuario.getPerfilDescripcion()) %>"
                                        data-estado-codigo="<%= h(usuario.getEstadoCodigo()) %>"
                                        data-estado="<%= h(estado) %>"
                                        data-cambio="<%= h(cambio) %>"
                                        data-intentos="<%= usuario.getIntentosFallidos() %>"
                                        data-ultimo="<%= h(usuario.getUltimoAcceso()) %>"
                                        data-reset-url="${pageContext.request.contextPath}/UsuarioController?accion=resetear&login=<%= loginUrl %>"
                                        data-estado-url="${pageContext.request.contextPath}/UsuarioController?accion=<%= "A".equalsIgnoreCase(usuario.getEstadoCodigo()) ? "bloquear" : "activar" %>&login=<%= loginUrl %>"
                                        data-estado-accion="<%= "A".equalsIgnoreCase(usuario.getEstadoCodigo()) ? "Bloquear usuario" : "Activar usuario" %>"
                                    >
                                        <td class="usuario-cuenta-cell">
                                            <div class="usuario-cuenta-simple">
                                                <div class="cuenta-linea">
                                                    <strong>Usuario</strong>
                                                    <span><%= h(usuario.getLogin()) %></span>
                                                </div>

                                                <div class="cuenta-linea">
                                                    <strong>Nombre</strong>
                                                    <span><%= h(personaNombre.trim()) %></span>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="estado-badge badge-neutro"><%= h(tipo) %></span>
                                        </td>
                                        <td class="usuario-perfil-cell"><%= h(usuario.getPerfilDescripcion()) %></td>
                                        <td>
                                            <span class="estado-badge <%= estadoClase(usuario.getEstadoCodigo()) %>">
                                                <%= h(estado) %>
                                            </span>
                                        </td>
                                        <td>
                                            <div class="usuario-acciones usuario-acciones-fase1">
                                                <button type="button" class="btn-accion-icono accion-ver" data-usuario-detalle title="Ver detalle" aria-label="Ver detalle">
                                                    <img src="${pageContext.request.contextPath}/img/iconos_master_monster/ver.png" alt="">
                                                </button>

                                                <button type="button" class="btn-accion-icono accion-editar" data-usuario-perfil title="Cambiar perfil" aria-label="Cambiar perfil">
                                                    <img src="${pageContext.request.contextPath}/img/iconos_master_monster/perfiles.png" alt="">
                                                </button>

                                                <button type="button" class="btn-accion-icono accion-seguridad" data-usuario-seguridad title="Seguridad" aria-label="Seguridad">
                                                    <img src="${pageContext.request.contextPath}/img/iconos_master_monster/editar.png" alt="">
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="5" class="tabla-vacia">No existen usuarios registrados.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>

            <footer class="footer-dashboard">
                GUTIÉRREZ - LANDÁZURI - LEITON
            </footer>
        </main>

        <div class="usuario-modal-backdrop" id="modalTipoUsuario" hidden>
            <section class="usuario-modal usuario-modal-eleccion" role="dialog" aria-modal="true" aria-labelledby="tituloModalTipoUsuario">
                <button type="button" class="usuario-modal-cerrar" data-modal-close aria-label="Cerrar">&times;</button>
                <h3 id="tituloModalTipoUsuario">Crear nuevo usuario</h3>
                <p class="usuario-modal-ayuda">Elige cómo deseas crear la cuenta de acceso.</p>

                <div class="usuario-choice-grid">
                    <button type="button" class="usuario-choice-card" data-modal-switch="modalUsuarioExistente">
                        <strong>Persona existente</strong>
                        <span>Usa una persona ya registrada, sea empleado o no.</span>
                    </button>

                    <button type="button" class="usuario-choice-card" data-modal-switch="modalUsuarioExterno">
                        <strong>Usuario externo</strong>
                        <span>Crea una persona básica sin registrarla como empleado.</span>
                    </button>
                </div>
            </section>
        </div>

        <div class="usuario-modal-backdrop" id="modalUsuarioExistente" hidden>
            <section class="usuario-modal usuario-modal-md" role="dialog" aria-modal="true" aria-labelledby="tituloModalUsuarioExistente">
                <button type="button" class="usuario-modal-cerrar" data-modal-close aria-label="Cerrar">&times;</button>
                <h3 id="tituloModalUsuarioExistente">Crear usuario para persona existente</h3>
                <p class="usuario-modal-ayuda">Selecciona una persona sin usuario y asigna su perfil de acceso.</p>

                <form action="${pageContext.request.contextPath}/UsuarioController" method="post" class="form-usuario-grid usuario-modal-form">
                    <input type="hidden" name="accion" value="guardarExistente">

                    <div class="grupo-campo campo-doble-usuario">
                        <label for="personaCodigo">Persona sin usuario</label>
                        <select id="personaCodigo" name="personaCodigo" required>
                            <option value="">Seleccione...</option>
                            <% if (personasSinUsuario != null) {
                                for (Persona persona : personasSinUsuario) { %>
                                    <option
                                        value="<%= h(persona.getPeperCodigo()) %>"
                                        data-cedula="<%= h(persona.getCedula()) %>"
                                        data-email="<%= h(persona.getEmail()) %>"
                                    >
                                        <%= h(persona.getApellidos()) %> <%= h(persona.getNombres()) %>
                                        - <%= h(tipoTexto(persona.getTipo(), persona.getTipoDescripcion(), persona.getEsEmpleado())) %>
                                        <% if (persona.getCedula() != null && !persona.getCedula().trim().isEmpty()) { %>
                                            - <%= h(persona.getCedula()) %>
                                        <% } %>
                                    </option>
                            <%  }
                               } %>
                        </select>
                    </div>

                    <div class="grupo-campo">
                        <label for="loginExistente">Login</label>
                        <input type="text" id="loginExistente" name="loginExistente" maxlength="50" placeholder="Cédula o correo">
                    </div>

                    <div class="grupo-campo">
                        <label for="perfilCodigoExistente">Perfil</label>
                        <select id="perfilCodigoExistente" name="perfilCodigoExistente" required>
                            <option value="">Seleccione...</option>
                            <% if (perfiles != null) {
                                for (Perfil perfil : perfiles) { %>
                                    <option value="<%= h(perfil.getCodigo()) %>"><%= h(perfil.getDescripcion()) %></option>
                            <%  }
                               } %>
                        </select>
                    </div>

                    <div class="usuario-modal-actions campo-doble-usuario">
                        <button type="button" class="btn-ficha btn-cancelar" data-modal-close>Cancelar</button>
                        <button type="submit" class="btn-ficha btn-guardar">Guardar usuario</button>
                    </div>
                </form>
            </section>
        </div>

        <div class="usuario-modal-backdrop" id="modalUsuarioExterno" hidden>
            <section class="usuario-modal usuario-modal-lg" role="dialog" aria-modal="true" aria-labelledby="tituloModalUsuarioExterno">
                <button type="button" class="usuario-modal-cerrar" data-modal-close aria-label="Cerrar">&times;</button>
                <h3 id="tituloModalUsuarioExterno">Crear usuario externo</h3>
                <p class="usuario-modal-ayuda">Crea una persona básica sin registrarla como empleado.</p>

                <form action="${pageContext.request.contextPath}/UsuarioController" method="post" class="form-usuario-grid form-usuario-externo usuario-modal-form">
                    <input type="hidden" name="accion" value="guardarExterno">

                    <div class="grupo-campo">
                        <label for="tipoPersona">Tipo</label>
                        <select id="tipoPersona" name="tipoPersona" required>
                            <option value="INV">Invitado</option>
                            <option value="CLI">Cliente</option>
                            <option value="ADM">Administrador</option>
                            <option value="RHH">Recursos Humanos</option>
                        </select>
                    </div>

                    <div class="grupo-campo">
                        <label for="nombres">Nombres</label>
                        <input type="text" id="nombres" name="nombres" maxlength="15" required>
                    </div>

                    <div class="grupo-campo">
                        <label for="apellidos">Apellidos</label>
                        <input type="text" id="apellidos" name="apellidos" maxlength="15" required>
                    </div>

                    <div class="grupo-campo">
                        <label for="cedula">Cédula</label>
                        <input type="text" id="cedula" name="cedula" maxlength="10" placeholder="Opcional">
                    </div>

                    <div class="grupo-campo">
                        <label for="email">Correo</label>
                        <input type="email" id="email" name="email" maxlength="100" placeholder="Opcional">
                    </div>

                    <div class="grupo-campo">
                        <label for="celular">Celular</label>
                        <input type="text" id="celular" name="celular" maxlength="10" placeholder="Opcional">
                    </div>

                    <div class="grupo-campo campo-doble-usuario">
                        <label for="direccion">Dirección</label>
                        <input type="text" id="direccion" name="direccion" maxlength="100" placeholder="Opcional">
                    </div>

                    <div class="grupo-campo">
                        <label for="loginExterno">Login</label>
                        <input type="text" id="loginExterno" name="loginExterno" maxlength="50" placeholder="Cédula, correo o usuario" required>
                    </div>

                    <div class="grupo-campo">
                        <label for="perfilCodigoExterno">Perfil</label>
                        <select id="perfilCodigoExterno" name="perfilCodigoExterno" required>
                            <option value="">Seleccione...</option>
                            <% if (perfiles != null) {
                                for (Perfil perfil : perfiles) { %>
                                    <option value="<%= h(perfil.getCodigo()) %>"><%= h(perfil.getDescripcion()) %></option>
                            <%  }
                               } %>
                        </select>
                    </div>

                    <div class="usuario-modal-actions campo-doble-usuario">
                        <button type="button" class="btn-ficha btn-cancelar" data-modal-close>Cancelar</button>
                        <button type="submit" class="btn-ficha btn-guardar">Crear usuario</button>
                    </div>
                </form>
            </section>
        </div>

        <div class="usuario-modal-backdrop" id="modalCambiarPerfil" hidden>
            <section class="usuario-modal usuario-modal-sm" role="dialog" aria-modal="true" aria-labelledby="tituloModalCambiarPerfil">
                <button type="button" class="usuario-modal-cerrar" data-modal-close aria-label="Cerrar">&times;</button>
                <h3 id="tituloModalCambiarPerfil">Cambiar perfil</h3>
                <p class="usuario-modal-ayuda" id="perfilUsuarioResumen">Selecciona el nuevo perfil del usuario.</p>

                <form action="${pageContext.request.contextPath}/UsuarioController" method="post" class="form-usuario-grid usuario-modal-form">
                    <input type="hidden" name="accion" value="asignarPerfil">
                    <input type="hidden" name="login" id="perfilLogin">

                    <div class="grupo-campo campo-doble-usuario">
                        <label for="perfilCodigoModal">Nuevo perfil</label>
                        <select id="perfilCodigoModal" name="perfilCodigo" required>
                            <% if (perfiles != null) {
                                for (Perfil perfil : perfiles) { %>
                                    <option value="<%= h(perfil.getCodigo()) %>"><%= h(perfil.getDescripcion()) %></option>
                            <%  }
                               } %>
                        </select>
                    </div>

                    <div class="usuario-modal-actions campo-doble-usuario">
                        <button type="button" class="btn-ficha btn-cancelar" data-modal-close>Cancelar</button>
                        <button type="submit" class="btn-ficha btn-guardar">Guardar cambios</button>
                    </div>
                </form>
            </section>
        </div>

        <div class="usuario-modal-backdrop" id="modalDetalleUsuario" hidden>
            <section class="usuario-modal usuario-modal-sm" role="dialog" aria-modal="true" aria-labelledby="tituloModalDetalleUsuario">
                <button type="button" class="usuario-modal-cerrar" data-modal-close aria-label="Cerrar">&times;</button>
                <h3 id="tituloModalDetalleUsuario">Detalle del usuario</h3>

                <div class="usuario-detalle-grid">
                    <div><span>Usuario</span><strong id="detalleLogin"></strong></div>
                    <div><span>Nombre</span><strong id="detallePersona"></strong></div>
                    <div><span>Cédula</span><strong id="detalleCedula"></strong></div>
                    <div><span>Tipo</span><strong id="detalleTipo"></strong></div>
                    <div><span>Código empleado</span><strong id="detalleCodigoEmpleado"></strong></div>
                    <div><span>Perfil</span><strong id="detallePerfil"></strong></div>
                    <div><span>Estado</span><strong id="detalleEstado"></strong></div>
                    <div><span>Cambio clave</span><strong id="detalleCambio"></strong></div>
                    <div><span>Intentos</span><strong id="detalleIntentos"></strong></div>
                    <div class="detalle-wide"><span>Último acceso</span><strong id="detalleUltimo"></strong></div>
                </div>

                <div class="usuario-modal-actions">
                    <button type="button" class="btn-ficha btn-cancelar" data-modal-close>Cerrar</button>
                </div>
            </section>
        </div>

        <div class="usuario-modal-backdrop" id="modalSeguridadUsuario" hidden>
            <section class="usuario-modal usuario-modal-sm" role="dialog" aria-modal="true" aria-labelledby="tituloModalSeguridadUsuario">
                <button type="button" class="usuario-modal-cerrar" data-modal-close aria-label="Cerrar">&times;</button>
                <h3 id="tituloModalSeguridadUsuario">Seguridad de cuenta</h3>
                <p class="usuario-modal-ayuda" id="seguridadUsuarioResumen">Elige una acción de seguridad para el usuario.</p>

                <div class="usuario-seguridad-grid">
                    <a href="#" class="usuario-seguridad-card" id="resetUsuarioLink">
                        <strong>Resetear clave</strong>
                        <span>Asigna nuevamente la contraseña temporal.</span>
                    </a>
                    <a href="#" class="usuario-seguridad-card peligro" id="estadoUsuarioLink">
                        <strong id="estadoUsuarioAccion">Bloquear usuario</strong>
                        <span id="estadoUsuarioDescripcion">Cambia el estado de acceso de la cuenta.</span>
                    </a>
                </div>

                <div class="usuario-modal-actions">
                    <button type="button" class="btn-ficha btn-cancelar" data-modal-close>Cerrar</button>
                </div>
            </section>
        </div>

        <script src="${pageContext.request.contextPath}/js/usuarios.js?v=20260617-usuarios-fase1" defer></script>
        <script src="${pageContext.request.contextPath}/js/paginacion.js?v=20260611-pag1" defer></script>
    </body>
</html>
