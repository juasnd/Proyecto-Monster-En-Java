<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Usuario"%>
<%@page import="ec.edu.gutierrez.landazuri.leiton.modelo.Perfil"%>
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

    private boolean textoValido(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }

        String limpio = texto.trim();

        if (limpio.startsWith("ec.edu.")) {
            return false;
        }

        if (limpio.contains("@") && limpio.contains("modelo")) {
            return false;
        }

        return true;
    }

    private String obtenerTexto(Object valor) {
        if (valor == null) {
            return "";
        }

        String texto = String.valueOf(valor).trim();

        if (!textoValido(texto)) {
            return "";
        }

        return texto;
    }
%>

<%
    String usuarioNombre = "";

    Object usuarioNombreSesion = session.getAttribute("usuarioNombre");
    usuarioNombre = obtenerTexto(usuarioNombreSesion);

    if (usuarioNombre.isEmpty()) {
        Object usuarioLogueado = session.getAttribute("usuarioLogueado");

        if (usuarioLogueado instanceof Usuario) {
            Usuario u = (Usuario) usuarioLogueado;
            usuarioNombre = obtenerTexto(u.getUsuario());
        } else {
            usuarioNombre = obtenerTexto(usuarioLogueado);
        }
    }

    if (usuarioNombre.isEmpty()) {
        Object usuarioSesion = session.getAttribute("usuario");

        if (usuarioSesion instanceof Usuario) {
            Usuario u = (Usuario) usuarioSesion;
            usuarioNombre = obtenerTexto(u.getUsuario());
        } else {
            usuarioNombre = obtenerTexto(usuarioSesion);
        }
    }

    if (usuarioNombre.isEmpty()) {
        usuarioNombre = "Usuario";
    }


    String perfilNombre = "";

    Object perfilNombreSesion = session.getAttribute("perfilNombre");
    perfilNombre = obtenerTexto(perfilNombreSesion);

    if (perfilNombre.isEmpty()) {
        Object perfilSesion = session.getAttribute("perfilUsuario");

        if (perfilSesion instanceof Perfil) {
            Perfil p = (Perfil) perfilSesion;
            perfilNombre = obtenerTexto(p.getDescripcion());
        }
    }

    if (perfilNombre.isEmpty()) {
        Object usuarioSesion = session.getAttribute("usuario");

        if (usuarioSesion instanceof Usuario) {
            Usuario u = (Usuario) usuarioSesion;

            if (u.getPerfil() != null) {
                perfilNombre = obtenerTexto(u.getPerfil().getDescripcion());
            }
        }
    }

    if (perfilNombre.isEmpty()) {
        perfilNombre = "Perfil";
    }
%>

<header class="topbar topbar-monster-simple">

    <a class="topbar-logo-simple"
       href="${pageContext.request.contextPath}/pagPrincipal.jsp"
       target="_top"
       aria-label="Inicio Master Monster">

        <img
            src="${pageContext.request.contextPath}/img/Monsters-Inc.-Symbol.png"
            alt="Logo Master Monster"
        >

        <div class="topbar-brand-text">
            <h1>Master Monster</h1>
            <span>Sistema de gestión de proyectos</span>
        </div>
    </a>

    <!-- GIF / IMAGEN ANIMADA EN EL CENTRO DE LA TOPBAR -->
    <div class="topbar-gif-center">
        <img
            src="${pageContext.request.contextPath}/img/topbar.gif"
            alt="Animación Master Monster"
        >
    </div>

    <div class="topbar-right-simple">

        <div class="topbar-user-card-simple" title="<%= h(usuarioNombre) %> - <%= h(perfilNombre) %>">
            <strong><%= h(usuarioNombre) %></strong>
            <small><%= h(perfilNombre) %></small>
        </div>

        <a href="${pageContext.request.contextPath}/Logout" class="btn-salir-simple" target="_top">
            Cerrar sesión
        </a>

    </div>

</header>