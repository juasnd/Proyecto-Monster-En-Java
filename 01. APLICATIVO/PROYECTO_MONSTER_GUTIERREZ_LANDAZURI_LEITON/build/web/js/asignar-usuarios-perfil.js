document.addEventListener("DOMContentLoaded", function () {
    const selectorPerfil = document.getElementById("perfilCodigo");
    const formPerfil = document.getElementById("formSeleccionPerfil");
    const formAsignacion = document.getElementById("formAsignacionUsuarios");
    const disponibles = document.getElementById("usuariosDisponibles");
    const asignados = document.getElementById("usuariosAsignados");
    const aviso = document.getElementById("mensajeSeleccionUsuario");

    if (selectorPerfil && formPerfil) {
        selectorPerfil.addEventListener("change", function () {
            formPerfil.submit();
        });
    }

    configurarBusqueda("buscarDisponibles", disponibles);
    configurarBusqueda("buscarAsignados", asignados);

    if (!formAsignacion) {
        return;
    }

    formAsignacion.querySelectorAll("button[data-requiere-seleccion]").forEach(function (boton) {
        boton.addEventListener("click", function (evento) {
            const idLista = boton.getAttribute("data-requiere-seleccion");
            const lista = document.getElementById(idLista);

            if (!lista || lista.selectedOptions.length === 0) {
                evento.preventDefault();
                mostrarAviso(aviso);
            }
        });
    });

    configurarDobleClic(disponibles, "asignarSeleccionados", formAsignacion);
    configurarDobleClic(asignados, "retirarSeleccionados", formAsignacion);

    configurarArrastre(disponibles, asignados, "asignarSeleccionados", formAsignacion);
    configurarArrastre(asignados, disponibles, "retirarSeleccionados", formAsignacion);
});

function configurarBusqueda(idBuscador, lista) {
    const buscador = document.getElementById(idBuscador);

    if (!buscador || !lista) {
        return;
    }

    buscador.addEventListener("input", function () {
        const termino = normalizar(buscador.value);

        Array.from(lista.options).forEach(function (opcion) {
            const coincide = normalizar(opcion.textContent).includes(termino);
            opcion.hidden = !coincide;
        });
    });
}

function configurarDobleClic(lista, accion, formulario) {
    if (!lista) {
        return;
    }

    lista.addEventListener("dblclick", function () {
        if (lista.selectedOptions.length > 0) {
            enviarAccion(formulario, accion);
        }
    });
}

function configurarArrastre(origen, destino, accion, formulario) {
    if (!origen || !destino) {
        return;
    }

    Array.from(origen.options).forEach(function (opcion) {
        opcion.addEventListener("dragstart", function (evento) {
            evento.dataTransfer.setData("text/plain", opcion.value);
            evento.dataTransfer.effectAllowed = "move";
            opcion.selected = true;
        });
    });

    destino.addEventListener("dragover", function (evento) {
        evento.preventDefault();
        evento.dataTransfer.dropEffect = "move";
        destino.classList.add("recibiendo");
    });

    destino.addEventListener("dragleave", function () {
        destino.classList.remove("recibiendo");
    });

    destino.addEventListener("drop", function (evento) {
        evento.preventDefault();
        destino.classList.remove("recibiendo");

        const login = evento.dataTransfer.getData("text/plain");
        const opcion = Array.from(origen.options).find(function (item) {
            return item.value === login;
        });

        if (opcion) {
            opcion.selected = true;
            enviarAccion(formulario, accion);
        }
    });
}

function enviarAccion(formulario, accion) {
    const temporal = document.createElement("input");
    temporal.type = "hidden";
    temporal.name = "accion";
    temporal.value = accion;
    formulario.appendChild(temporal);
    formulario.submit();
}

function mostrarAviso(aviso) {
    if (!aviso) {
        return;
    }

    aviso.hidden = false;
    aviso.focus();

    window.setTimeout(function () {
        aviso.hidden = true;
    }, 3000);
}

function normalizar(texto) {
    return (texto || "")
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .trim();
}
