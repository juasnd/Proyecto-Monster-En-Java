<%-- 
    Document   : index
    Created on : 16 may 2026, 20:45:36
    Author     : ASUS
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Inicio de sesión | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260610-soft1">
        <script src="${pageContext.request.contextPath}/js/validaciones.js" defer></script>
    </head>

    <body>
        <main class="contenedor-login">

            <section class="login-panel">

                <div class="login-izquierda">

                    <!-- LOGO MASTER MONSTER -->
                    <div class="logo-monster">
                        <img 
                            src="${pageContext.request.contextPath}/img/Monsters-Inc.-Symbol.png" 
                            alt="Logo Master Monster"
                        >
                    </div>

                    <h1>Master Monster</h1>
                    <p class="subtitulo">Inicio de sesión</p>

                    <%
                        String error = request.getParameter("error");
                        String num = request.getParameter("num");
                    %>

                    <% if ("usuario_no_existe".equals(error)) { %>
                        <p class="mensaje-error-general">El usuario ingresado no existe.</p>
                    <% } else if ("bloqueado".equals(error)) { %>
                        <p class="mensaje-error-general">La cuenta está bloqueada por superar los 3 intentos.</p>
                    <% } else if ("inactivo".equals(error)) { %>
                        <p class="mensaje-error-general">La cuenta se encuentra inactiva.</p>
                    <% } else if ("intentos".equals(error)) { %>
                        <p class="mensaje-error-general">
                            Usuario o contraseña incorrectos. Intento <%= num %> de 3.
                        </p>
                    <% } %>

                    <form
                        id="formLogin"
                        action="${pageContext.request.contextPath}/Controller"
                        method="post"
                        novalidate
                    >

                        <div class="grupo-campo">
                            <label for="usuario">Usuario</label>
                            <input 
                                type="text" 
                                id="usuario" 
                                name="usuario" 
                                placeholder="Ingrese su usuario"
                                autocomplete="username"
                            >
                            <small class="mensaje-error" id="errorUsuario"></small>
                        </div>

                        <div class="grupo-campo">
                            <label for="password">Contraseña</label>

                            <div class="campo-password">
                                <input 
                                    type="password" 
                                    id="password" 
                                    name="password" 
                                    placeholder="Ingrese su contraseña"
                                    autocomplete="current-password"
                                >
                            </div>

                            <small class="mensaje-error" id="errorPassword"></small>
                        </div>

                        <button type="submit" class="btn-ingresar">
                            Ingresar
                        </button>
                    </form>

                    <a href="#" class="olvido-password">¿Olvidaste tu contraseña?</a>

                    <div class="crear-cuenta">
                        <span>¿No tienes una cuenta?</span>
                        <a href="#">Crear cuenta</a>
                    </div>

                    <p class="pie">
                        GUTIÉRREZ - LANDÁZURI - LEITON
                    </p>
                </div>

                <div class="login-derecha">
                    <div class="contenido-derecha">

                        <div class="logo-derecha">
                            <img 
                                src="${pageContext.request.contextPath}/img/inge.monster.png" 
                                alt="Personaje Master Monster"
                            >
                        </div>

                        <h2>Sistema de gestión de proyectos</h2>

                        <p>
                            Master Monster permite administrar proyectos, usuarios e información 
                            de forma organizada, segura y sencilla.
                        </p>
                    </div>
                </div>

            </section>

        </main>
    </body>
</html>
