<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Acceso denegado | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260615-seg1">
    </head>

    <body class="body-dashboard">
        <jsp:include page="/WEB-INF/includes/topbar.jsp" />

        <main class="dashboard-simple">
            <section class="bienvenida-dashboard">
                <div class="bienvenida-texto">
                    <h2>Acceso denegado</h2>
                    <p>No tienes permiso para acceder a esta opcion. Si necesitas entrar, solicita la asignacion del permiso a un administrador.</p>

                    <div class="botones-dashboard">
                        <a href="${pageContext.request.contextPath}/pagPrincipal.jsp" class="btn-dashboard-principal">Volver al inicio</a>
                        <a href="${pageContext.request.contextPath}/Logout" class="btn-dashboard-secundario">Cerrar sesion</a>
                    </div>
                </div>

                <div class="bienvenida-imagen">
                    <img src="${pageContext.request.contextPath}/img/inge.monster.png" alt="Personaje Master Monster">
                </div>
            </section>
        </main>
    </body>
</html>
