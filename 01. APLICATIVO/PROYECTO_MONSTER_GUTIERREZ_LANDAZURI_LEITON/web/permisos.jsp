<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Opcion"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Perfil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
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

    private boolean mismoCodigo(String a, String b) {
        String x = a == null ? "" : a.trim();
        String y = b == null ? "" : b.trim();
        return x.equalsIgnoreCase(y);
    }

    private boolean esSubsistema(Opcion opcion) {
        if (opcion == null) {
            return false;
        }

        return "S".equalsIgnoreCase(opcion.getTipo()) || opcion.getNivel() == 1;
    }

    private boolean esGrupo(Opcion opcion) {
        if (opcion == null) {
            return false;
        }

        return "G".equalsIgnoreCase(opcion.getTipo()) || opcion.getNivel() == 2;
    }

    private boolean esOpcionFinal(Opcion opcion) {
        if (opcion == null) {
            return false;
        }

        return !esSubsistema(opcion) && !esGrupo(opcion);
    }

    private List<Opcion> ordenar(List<Opcion> lista) {
        Collections.sort(lista, new Comparator<Opcion>() {
            @Override
            public int compare(Opcion a, Opcion b) {
                int ordenA = a == null ? 0 : a.getOrden();
                int ordenB = b == null ? 0 : b.getOrden();

                if (ordenA != ordenB) {
                    return Integer.compare(ordenA, ordenB);
                }

                String textoA = a == null || a.getDescripcion() == null ? "" : a.getDescripcion();
                String textoB = b == null || b.getDescripcion() == null ? "" : b.getDescripcion();

                return textoA.compareToIgnoreCase(textoB);
            }
        });

        return lista;
    }

    private List<Opcion> obtenerSubsistemas(List<Opcion> opciones) {
        List<Opcion> resultado = new ArrayList<Opcion>();

        if (opciones == null) {
            return resultado;
        }

        for (Opcion opcion : opciones) {
            if (esSubsistema(opcion)) {
                resultado.add(opcion);
            }
        }

        return ordenar(resultado);
    }

    private List<Opcion> obtenerGrupos(List<Opcion> opciones, String codigoPadre) {
        List<Opcion> resultado = new ArrayList<Opcion>();

        if (opciones == null) {
            return resultado;
        }

        for (Opcion opcion : opciones) {
            if (esGrupo(opcion) && mismoCodigo(opcion.getCodigoPadre(), codigoPadre)) {
                resultado.add(opcion);
            }
        }

        return ordenar(resultado);
    }

    private List<Opcion> obtenerOpcionesFinales(List<Opcion> opciones, String codigoPadre) {
        List<Opcion> resultado = new ArrayList<Opcion>();

        if (opciones == null) {
            return resultado;
        }

        for (Opcion opcion : opciones) {
            if (esOpcionFinal(opcion) && mismoCodigo(opcion.getCodigoPadre(), codigoPadre)) {
                resultado.add(opcion);
            }
        }

        return ordenar(resultado);
    }

    private boolean grupoTieneOpciones(List<Opcion> opciones, String codigoGrupo) {
        return !obtenerOpcionesFinales(opciones, codigoGrupo).isEmpty();
    }
%>

<%
    String mensaje = request.getParameter("mensaje");
    String perfilSeleccionado = (String) request.getAttribute("perfilSeleccionado");
    List<Perfil> perfiles = (List<Perfil>) request.getAttribute("perfiles");
    List<Opcion> opciones = (List<Opcion>) request.getAttribute("opciones");
    Set<String> permisosSeleccionados = (Set<String>) request.getAttribute("permisosSeleccionados");

    List<Opcion> subsistemas = obtenerSubsistemas(opciones);
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Opciones por Perfil | Master Monster</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=<%= System.currentTimeMillis() %>">
    </head>

    <body class="body-dashboard">

        <main class="crud-page permisos-page permisos-matriz-page">

            <section class="crud-titulo permisos-titulo">
                <div>
                    <h2>Opciones por Perfil</h2>
                    <p>Activa o desactiva las opciones del menú que puede usar cada perfil del sistema.</p>
                </div>
            </section>

            <% if ("guardado".equals(mensaje)) { %>
                <p class="mensaje-exito-general">Permisos guardados correctamente.</p>
            <% } else if ("no_guardado".equals(mensaje)) { %>
                <p class="mensaje-error-general">No se pudieron guardar los permisos.</p>
            <% } %>

            <section class="crud-contenedor permisos-contenedor permisos-matriz-contenedor">

                <div class="form-crud-box permisos-filtro-box">
                    <h3>Seleccionar perfil</h3>

                    <form action="${pageContext.request.contextPath}/PermisoController" method="get" class="permisos-filtro-form">
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

                    <form action="${pageContext.request.contextPath}/PermisoController" method="post" class="permisos-form">
                        <input type="hidden" name="perfilCodigo" value="<%= h(perfilSeleccionado) %>">

                        <div class="crud-toolbar permisos-toolbar">
                            <div class="crud-toolbar-left">
                                <button type="submit" class="btn-crud verde">Guardar permisos</button>
                                <button type="button" class="btn-crud celeste" id="btnMarcarPermisos">Activar todos</button>
                                <button type="button" class="btn-crud reporte" id="btnLimpiarPermisos">Desactivar todos</button>
                            </div>
                        </div>

                        <% if (subsistemas != null && !subsistemas.isEmpty()) { %>

                            <div class="permisos-sistemas-matriz">

                                <% for (Opcion subsistema : subsistemas) {
                                    List<Opcion> grupos = obtenerGrupos(opciones, subsistema.getCodigo());
                                    List<Opcion> directas = obtenerOpcionesFinales(opciones, subsistema.getCodigo());
                                %>

                                    <section class="matriz-sistema-card abierto">

                                        <div class="matriz-sistema-header">
                                            <button type="button" class="matriz-toggle matriz-toggle-sistema">
                                                <span class="matriz-flecha">▾</span>
                                                <strong><%= h(subsistema.getDescripcion()) %></strong>
                                            </button>

                                            <label class="matriz-switch" title="Activar o desactivar subsistema">
                                                <input
                                                    type="checkbox"
                                                    name="opciones"
                                                    class="matriz-sistema-check"
                                                    value="<%= h(subsistema.getCodigo()) %>"
                                                    <%= checked(permisosSeleccionados, subsistema.getCodigo()) %>
                                                >
                                                <span class="matriz-slider"></span>
                                            </label>
                                        </div>

                                        <div class="matriz-sistema-body">

                                            <%
                                                boolean imprimioContenido = false;

                                                if (grupos != null && !grupos.isEmpty()) {
                                                    for (Opcion grupo : grupos) {
                                                        List<Opcion> opcionesGrupo = obtenerOpcionesFinales(opciones, grupo.getCodigo());

                                                        if (opcionesGrupo == null || opcionesGrupo.isEmpty()) {
                                                            continue;
                                                        }

                                                        imprimioContenido = true;
                                            %>

                                                        <div class="matriz-grupo-card abierto">

                                                            <div class="matriz-grupo-header">
                                                                <button type="button" class="matriz-toggle matriz-toggle-grupo">
                                                                    <span class="matriz-flecha">▾</span>
                                                                    <strong><%= h(grupo.getDescripcion()) %></strong>
                                                                </button>

                                                                <label class="matriz-switch matriz-switch-sm" title="Activar o desactivar grupo">
                                                                    <input
                                                                        type="checkbox"
                                                                        name="opciones"
                                                                        class="matriz-grupo-check"
                                                                        value="<%= h(grupo.getCodigo()) %>"
                                                                        <%= checked(permisosSeleccionados, grupo.getCodigo()) %>
                                                                    >
                                                                    <span class="matriz-slider"></span>
                                                                </label>
                                                            </div>

                                                            <div class="matriz-grupo-body">

                                                                <% for (Opcion opcionFinal : opcionesGrupo) { %>

                                                                    <div class="matriz-opcion-row">
                                                                        <span class="matriz-opcion-nombre">
                                                                            <%= h(opcionFinal.getDescripcion()) %>
                                                                        </span>

                                                                        <label class="matriz-switch matriz-switch-sm" title="Activar o desactivar opción">
                                                                            <input
                                                                                type="checkbox"
                                                                                name="opciones"
                                                                                class="matriz-opcion-check"
                                                                                value="<%= h(opcionFinal.getCodigo()) %>"
                                                                                <%= checked(permisosSeleccionados, opcionFinal.getCodigo()) %>
                                                                            >
                                                                            <span class="matriz-slider"></span>
                                                                        </label>
                                                                    </div>

                                                                <% } %>

                                                            </div>
                                                        </div>

                                            <%      }
                                                }

                                                if (directas != null && !directas.isEmpty()) {
                                                    imprimioContenido = true;
                                            %>

                                                    <div class="matriz-grupo-card abierto">
                                                        <div class="matriz-grupo-header">
                                                            <button type="button" class="matriz-toggle matriz-toggle-grupo">
                                                                <span class="matriz-flecha">▾</span>
                                                                <strong>Opciones directas</strong>
                                                            </button>
                                                        </div>

                                                        <div class="matriz-grupo-body">

                                                            <% for (Opcion opcionDirecta : directas) { %>

                                                                <div class="matriz-opcion-row">
                                                                    <span class="matriz-opcion-nombre">
                                                                        <%= h(opcionDirecta.getDescripcion()) %>
                                                                    </span>

                                                                    <label class="matriz-switch matriz-switch-sm" title="Activar o desactivar opción">
                                                                        <input
                                                                            type="checkbox"
                                                                            name="opciones"
                                                                            class="matriz-opcion-check"
                                                                            value="<%= h(opcionDirecta.getCodigo()) %>"
                                                                            <%= checked(permisosSeleccionados, opcionDirecta.getCodigo()) %>
                                                                        >
                                                                        <span class="matriz-slider"></span>
                                                                    </label>
                                                                </div>

                                                            <% } %>

                                                        </div>
                                                    </div>

                                            <%  }

                                                if (!imprimioContenido) { %>

                                                    <div class="matriz-card-vacia">
                                                        No hay opciones implementadas para este subsistema.
                                                    </div>

                                            <%  } %>

                                        </div>
                                    </section>

                                <% } %>

                            </div>

                        <% } else { %>

                            <p class="tabla-vacia">No existen opciones registradas.</p>

                        <% } %>

                    </form>

                <% } else { %>

                    <div class="permisos-empty">
                        Selecciona un perfil y presiona <strong>Cargar</strong> para administrar sus opciones.
                    </div>

                <% } %>

            </section>

        </main>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const marcar = document.getElementById("btnMarcarPermisos");
                const limpiar = document.getElementById("btnLimpiarPermisos");
                const checks = document.querySelectorAll("input[name='opciones']");

                if (marcar) {
                    marcar.addEventListener("click", function () {
                        checks.forEach(function (check) {
                            check.checked = true;
                        });
                    });
                }

                if (limpiar) {
                    limpiar.addEventListener("click", function () {
                        checks.forEach(function (check) {
                            check.checked = false;
                        });
                    });
                }

                document.querySelectorAll(".matriz-toggle").forEach(function (boton) {
                    boton.addEventListener("click", function () {
                        const sistema = boton.closest(".matriz-sistema-card");
                        const grupo = boton.closest(".matriz-grupo-card");

                        if (boton.classList.contains("matriz-toggle-grupo") && grupo) {
                            grupo.classList.toggle("abierto");
                            return;
                        }

                        if (boton.classList.contains("matriz-toggle-sistema") && sistema) {
                            sistema.classList.toggle("abierto");
                        }
                    });
                });

                document.querySelectorAll(".matriz-sistema-check").forEach(function (checkSistema) {
                    checkSistema.addEventListener("change", function () {
                        const card = checkSistema.closest(".matriz-sistema-card");

                        if (!card) {
                            return;
                        }

                        card.querySelectorAll("input[name='opciones']").forEach(function (check) {
                            check.checked = checkSistema.checked;
                        });
                    });
                });

                document.querySelectorAll(".matriz-grupo-check").forEach(function (checkGrupo) {
                    checkGrupo.addEventListener("change", function () {
                        const card = checkGrupo.closest(".matriz-grupo-card");

                        if (!card) {
                            return;
                        }

                        card.querySelectorAll(".matriz-opcion-check").forEach(function (check) {
                            check.checked = checkGrupo.checked;
                        });
                    });
                });
            });
        </script>

    </body>
</html>