document.addEventListener("DOMContentLoaded", function () {
    configurarBusquedaUsuarios();
    configurarLoginDesdeEmpleado();
});

function configurarBusquedaUsuarios() {
    const input = document.getElementById("buscarUsuario");
    const tabla = document.getElementById("tablaUsuarios");

    if (!input || !tabla) {
        return;
    }

    input.addEventListener("input", function () {
        filtrarTabla(tabla, input.value);
    });
}

function configurarLoginDesdeEmpleado() {
    const persona = document.getElementById("personaCodigo");
    const login = document.getElementById("login");

    if (!persona || !login) {
        return;
    }

    persona.addEventListener("change", function () {
        const opcion = persona.options[persona.selectedIndex];
        login.value = opcion && opcion.dataset.cedula ? opcion.dataset.cedula : "";
    });
}

function filtrarTabla(tabla, texto) {
    const filtro = normalizar(texto);
    const filas = tabla.querySelectorAll("tbody tr");

    filas.forEach(function (fila) {
        const contenido = normalizar(fila.textContent);
        fila.style.display = contenido.includes(filtro) ? "" : "none";
    });
}

function normalizar(texto) {
    return (texto || "")
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .trim();
}
