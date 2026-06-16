<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Cambiar contrasena | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260615-seg1">
    </head>

    <body>
        <main class="contenedor-login">
            <section class="login-panel">
                <div class="login-izquierda">
                    <div class="logo-monster">
                        <img src="${pageContext.request.contextPath}/img/Monsters-Inc.-Symbol.png" alt="Logo Master Monster">
                    </div>

                    <h1>Master Monster</h1>
                    <p class="subtitulo">Cambio obligatorio de contrasena</p>

                    <% if (error != null) { %>
                        <p class="mensaje-error-general"><%= error %></p>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/CambiarClaveController" method="post" novalidate>
                        <div class="grupo-campo">
                            <label for="claveActual">Contrasena actual</label>
                            <input type="password" id="claveActual" name="claveActual" placeholder="Contrasena actual" autocomplete="current-password" required>
                        </div>

                        <div class="grupo-campo">
                            <label for="claveNueva">Nueva contrasena</label>
                            <input type="password" id="claveNueva" name="claveNueva" placeholder="Nueva contrasena" autocomplete="new-password" required>
                        </div>

                        <div class="grupo-campo">
                            <label for="claveConfirmar">Confirmar contrasena</label>
                            <input type="password" id="claveConfirmar" name="claveConfirmar" placeholder="Confirmar nueva contrasena" autocomplete="new-password" required>
                        </div>

                        <button type="submit" class="btn-ingresar">Guardar contrasena</button>
                    </form>

                    <p class="pie">GUTIERREZ - LANDAZURI - LEITON</p>
                </div>

                <div class="login-derecha">
                    <div class="contenido-derecha">
                        <div class="logo-derecha">
                            <img src="${pageContext.request.contextPath}/img/inge.monster.png" alt="Personaje Master Monster">
                        </div>

                        <h2>Protege tu cuenta</h2>
                        <p>Despues de guardar la nueva contrasena podras continuar al panel principal.</p>
                    </div>
                </div>
            </section>
        </main>
    </body>
</html>
