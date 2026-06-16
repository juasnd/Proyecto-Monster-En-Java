document.addEventListener("DOMContentLoaded", function () {
    configurarFiltroPermisos();
    configurarAccionesPermisos();
});

function configurarFiltroPermisos() {
    const input = document.getElementById("buscarPermiso");
    const tabla = document.getElementById("tablaPermisos");

    if (!input || !tabla) {
        return;
    }

    input.addEventListener("input", function () {
        const filtro = normalizar(input.value);

        tabla.querySelectorAll("tbody tr").forEach(function (fila) {
            fila.style.display = normalizar(fila.textContent).includes(filtro) ? "" : "none";
        });
    });
}

function configurarAccionesPermisos() {
    const marcar = document.getElementById("btnMarcarPermisos");
    const limpiar = document.getElementById("btnLimpiarPermisos");

    if (marcar) {
        marcar.addEventListener("click", function () {
            actualizarChecks(true);
        });
    }

    if (limpiar) {
        limpiar.addEventListener("click", function () {
            actualizarChecks(false);
        });
    }
}

function actualizarChecks(valor) {
    document.querySelectorAll("#tablaPermisos input[type='checkbox']").forEach(function (check) {
        if (check.closest("tr").style.display !== "none") {
            check.checked = valor;
        }
    });
}

function normalizar(texto) {
    return (texto || "")
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .trim();
}
