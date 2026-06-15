<%-- 
    Document   : pagPrincipal
    Created on : 16 may 2026
    Author     : ASUS
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Panel principal | Master Monster</title>
        <link rel="icon" type="image/png" href="img/favicon.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilos.css?v=20260610-soft1">
    </head>

    <body class="body-dashboard">

        <header class="topbar">
            <div class="topbar-logo">
                <img 
                    src="${pageContext.request.contextPath}/img/Monsters-Inc.-Symbol.png" 
                    alt="Logo Master Monster"
                >

                <div>
                    <h1>Master Monster</h1>
                    <span>Sistema de gestión de proyectos</span>
                </div>
            </div>

            <nav class="topbar-menu">
                <a href="${pageContext.request.contextPath}/pagPrincipal.jsp">Inicio</a>
                <a href="${pageContext.request.contextPath}/DepartamentoController">Departamentos</a>
                <a href="${pageContext.request.contextPath}/CargoController">Cargos</a>
                <a href="${pageContext.request.contextPath}/EmpleadoController">Empleados</a>
                <a href="${pageContext.request.contextPath}/Logout" class="btn-salir">Cerrar sesión</a>
            </nav>
        </header>

        <main class="dashboard-simple">

            <section class="bienvenida-dashboard">

                <div class="bienvenida-texto">
                    <h2>Bienvenido al sistema</h2>

                    <p>
                        Master Monster es un sistema de gestión de proyectos.
                        Desde este panel puedes administrar los departamentos, cargos y empleados registrados.
                    </p>

                    <div class="botones-dashboard">
                        <a
                            href="${pageContext.request.contextPath}/DepartamentoController"
                            class="btn-dashboard-principal"
                        >
                            Gestionar departamentos
                        </a>
                        
                        <a href="${pageContext.request.contextPath}/CargoController" class="btn-dashboard-principal">
                            Gestionar cargos
                        </a>

                        <a href="${pageContext.request.contextPath}/EmpleadoController" class="btn-dashboard-principal">
                            Gestionar empleados
                        </a>

                        <a href="${pageContext.request.contextPath}/Logout" class="btn-dashboard-secundario">
                            Cerrar sesión
                        </a>
                    </div>
                </div>

                <div class="bienvenida-imagen">
                    <img 
                        src="${pageContext.request.contextPath}/img/inge.monster.png" 
                        alt="Personaje Master Monster"
                    >
                </div>

            </section>

            <section class="modulo-unico">
                
                <article class="modulo-card">
                    <div class="modulo-icono">📁</div>

                    <div class="modulo-info">
                        <h3>Departamentos</h3>
                        <p>
                            Administra, registra, edita y consulta los departamentos del sistema.
                        </p>
                    </div>

                    <a href="${pageContext.request.contextPath}/DepartamentoController">
                        Entrar al módulo
                    </a>
                </article>

                <article class="modulo-card">
                    <div class="modulo-icono">💼</div>

                    <div class="modulo-info">
                        <h3>Cargos</h3>
                        <p>
                            Administra, registra, edita y consulta los cargos y sus departamentos.
                        </p>
                    </div>

                    <a href="${pageContext.request.contextPath}/CargoController">
                        Entrar al módulo
                    </a>
                </article>

                <article class="modulo-card">
                    <div class="modulo-icono">EM</div>

                    <div class="modulo-info">
                        <h3>Empleados</h3>
                        <p>
                            Administra la ficha personal, departamento y cargo de los empleados.
                        </p>
                    </div>

                    <a href="${pageContext.request.contextPath}/EmpleadoController">
                        Entrar al modulo
                    </a>
                </article>

            </section>

            <footer class="footer-dashboard">
                GRUPO 2
            </footer>

        </main>

    </body>
</html>     
