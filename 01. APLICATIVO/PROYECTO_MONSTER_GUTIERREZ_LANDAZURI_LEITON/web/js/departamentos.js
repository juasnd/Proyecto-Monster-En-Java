document.addEventListener("DOMContentLoaded", function () {
    const inputBuscar = document.getElementById("buscarDepartamento");
    const tabla = document.getElementById("tablaDepartamentos");

    if (inputBuscar && tabla) {
        inputBuscar.addEventListener("keyup", function () {
            const texto = this.value.toLowerCase().trim();
            const filas = tabla.querySelectorAll("tbody tr");

            filas.forEach(function (fila) {
                const contenidoFila = fila.textContent.toLowerCase();
                fila.style.display = contenidoFila.includes(texto) ? "" : "none";
            });
        });
    }
});