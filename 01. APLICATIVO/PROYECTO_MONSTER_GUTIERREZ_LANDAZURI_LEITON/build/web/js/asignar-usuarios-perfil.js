document.addEventListener("DOMContentLoaded", function () {

    const formPerfil
            = document.getElementById(
                    "formSeleccionPerfil"
            );

    const selectPerfil
            = document.getElementById(
                    "perfilCodigo"
            );

    if (formPerfil && selectPerfil) {

        selectPerfil.addEventListener(
                "change",
                function () {

                    formPerfil.submit();
                }
        );
    }

    const formulario
            = document.getElementById(
                    "formAsignacionUsuarios"
            );

    const disponibles
            = document.getElementById(
                    "usuariosDisponibles"
            );

    const asignados
            = document.getElementById(
                    "usuariosAsignados"
            );

    const aviso
            = document.getElementById(
                    "avisoSeleccion"
            );

    if (
            !formulario
            || !disponibles
            || !asignados
    ) {
        return;
    }

    let datosArrastre = null;

    const ultimoSeleccionado
            = new WeakMap();

    configurarLista(disponibles);
    configurarLista(asignados);

    configurarZonaDestino(disponibles);
    configurarZonaDestino(asignados);

    configurarBotones();

    configurarBusqueda(
            "buscarDisponibles",
            disponibles
    );

    configurarBusqueda(
            "buscarAsignados",
            asignados
    );

    function configurarLista(lista) {

        lista.addEventListener(
                "click",
                function (evento) {

                    const item
                            = evento.target.closest(
                                    ".asignacion-usuario-item"
                            );

                    if (
                            !item
                            || !lista.contains(item)
                    ) {
                        return;
                    }

                    seleccionarItem(
                            lista,
                            item,
                            evento
                    );
                }
        );

        lista.addEventListener(
                "dblclick",
                function (evento) {

                    const item
                            = evento.target.closest(
                                    ".asignacion-usuario-item"
                            );

                    if (
                            !item
                            || !lista.contains(item)
                    ) {
                        return;
                    }

                    if (
                            !item.classList.contains(
                                    "seleccionado"
                            )
                    ) {

                        limpiarSeleccion(lista);

                        marcarSeleccionado(
                                item,
                                true
                        );
                    }

                    enviarSeleccionados(lista);
                }
        );

        lista.addEventListener(
                "keydown",
                function (evento) {

                    const item
                            = evento.target.closest(
                                    ".asignacion-usuario-item"
                            );

                    if (
                            (
                                    evento.ctrlKey
                                    || evento.metaKey
                            )
                            && evento.key
                                    .toLowerCase() === "a"
                    ) {

                        evento.preventDefault();

                        obtenerItemsVisibles(lista)
                                .forEach(
                                        function (usuario) {

                                            marcarSeleccionado(
                                                    usuario,
                                                    true
                                            );
                                        }
                                );

                        return;
                    }

                    if (!item) {
                        return;
                    }

                    if (
                            evento.key === " "
                            || evento.key === "Spacebar"
                    ) {

                        evento.preventDefault();

                        marcarSeleccionado(
                                item,
                                !item.classList.contains(
                                        "seleccionado"
                                )
                        );
                    }

                    if (evento.key === "Enter") {

                        evento.preventDefault();

                        if (
                                !item.classList.contains(
                                        "seleccionado"
                                )
                        ) {

                            limpiarSeleccion(lista);

                            marcarSeleccionado(
                                    item,
                                    true
                            );
                        }

                        enviarSeleccionados(lista);
                    }
                }
        );

        lista.querySelectorAll(
                ".asignacion-usuario-item"
        ).forEach(
                function (item) {

                    item.addEventListener(
                            "dragstart",
                            function (evento) {

                                if (
                                        !item.classList.contains(
                                                "seleccionado"
                                        )
                                ) {

                                    limpiarSeleccion(lista);

                                    marcarSeleccionado(
                                            item,
                                            true
                                    );
                                }

                                const seleccionados
                                        = obtenerSeleccionados(
                                                lista
                                        );

                                const logins
                                        = seleccionados.map(
                                                function (usuario) {

                                                    return usuario
                                                            .dataset
                                                            .login;
                                                }
                                        );

                                datosArrastre = {
                                    origenId: lista.id,
                                    logins: logins
                                };

                                seleccionados.forEach(
                                        function (usuario) {

                                            usuario.classList.add(
                                                    "arrastrando"
                                            );
                                        }
                                );

                                evento.dataTransfer
                                        .effectAllowed = "move";

                                evento.dataTransfer.setData(
                                        "text/plain",
                                        JSON.stringify(
                                                datosArrastre
                                        )
                                );
                            }
                    );

                    item.addEventListener(
                            "dragend",
                            function () {

                                document.querySelectorAll(
                                        ".asignacion-usuario-item.arrastrando"
                                ).forEach(
                                        function (usuario) {

                                            usuario.classList.remove(
                                                    "arrastrando"
                                            );
                                        }
                                );

                                disponibles.classList.remove(
                                        "recibiendo"
                                );

                                asignados.classList.remove(
                                        "recibiendo"
                                );

                                datosArrastre = null;
                            }
                    );
                }
        );
    }

    function seleccionarItem(
            lista,
            item,
            evento
    ) {

        const items
                = obtenerItemsVisibles(lista);

        const indiceActual
                = items.indexOf(item);

        const seleccionMultiple
                = evento.ctrlKey
                || evento.metaKey;

        if (
                evento.shiftKey
                && items.length > 0
        ) {

            evento.preventDefault();

            const ultimo
                    = ultimoSeleccionado.get(lista);

            const indiceInicial
                    = ultimo
                    && items.includes(ultimo)
                    ? items.indexOf(ultimo)
                    : indiceActual;

            if (!seleccionMultiple) {
                limpiarSeleccion(lista);
            }

            const desde
                    = Math.min(
                            indiceInicial,
                            indiceActual
                    );

            const hasta
                    = Math.max(
                            indiceInicial,
                            indiceActual
                    );

            items.slice(
                    desde,
                    hasta + 1
            ).forEach(
                    function (usuario) {

                        marcarSeleccionado(
                                usuario,
                                true
                        );
                    }
            );

            ultimoSeleccionado.set(
                    lista,
                    item
            );

            return;
        }

        if (seleccionMultiple) {

            marcarSeleccionado(
                    item,
                    !item.classList.contains(
                            "seleccionado"
                    )
            );

            ultimoSeleccionado.set(
                    lista,
                    item
            );

            return;
        }

        const eraUnicoSeleccionado
                = item.classList.contains(
                        "seleccionado"
                )
                && obtenerSeleccionados(
                        lista
                ).length === 1;

        limpiarSeleccion(lista);

        marcarSeleccionado(
                item,
                !eraUnicoSeleccionado
        );

        ultimoSeleccionado.set(
                lista,
                item
        );
    }

    function configurarZonaDestino(destino) {

        destino.addEventListener(
                "dragover",
                function (evento) {

                    if (
                            !datosArrastre
                            || datosArrastre.origenId
                            === destino.id
                    ) {
                        return;
                    }

                    evento.preventDefault();

                    evento.dataTransfer
                            .dropEffect = "move";

                    destino.classList.add(
                            "recibiendo"
                    );
                }
        );

        destino.addEventListener(
                "dragleave",
                function (evento) {

                    if (
                            !destino.contains(
                                    evento.relatedTarget
                            )
                    ) {

                        destino.classList.remove(
                                "recibiendo"
                        );
                    }
                }
        );

        destino.addEventListener(
                "drop",
                function (evento) {

                    evento.preventDefault();

                    destino.classList.remove(
                            "recibiendo"
                    );

                    let datos = datosArrastre;

                    if (!datos) {

                        try {

                            datos = JSON.parse(
                                    evento.dataTransfer
                                            .getData(
                                                    "text/plain"
                                            )
                            );

                        } catch (error) {

                            datos = null;
                        }
                    }

                    if (
                            !datos
                            || !Array.isArray(
                                    datos.logins
                            )
                            || datos.logins.length === 0
                    ) {
                        return;
                    }

                    if (
                            datos.origenId
                            === destino.id
                    ) {
                        return;
                    }

                    const origen
                            = document.getElementById(
                                    datos.origenId
                            );

                    if (!origen) {
                        return;
                    }

                    const accion
                            = datos.origenId
                            === "usuariosDisponibles"
                            ? "asignarSeleccionados"
                            : "retirarSeleccionados";

                    enviarFormulario(
                            accion,
                            origen.dataset.inputName,
                            datos.logins
                    );
                }
        );
    }

    function configurarBotones() {

        formulario.querySelectorAll(
                "button[data-accion]"
        ).forEach(
                function (boton) {

                    boton.addEventListener(
                            "click",
                            function () {

                                const accion
                                        = boton.dataset.accion;

                                const origenId
                                        = boton.dataset.origen;

                                /*
                                 * Los botones de mover todos
                                 * no necesitan usuarios seleccionados.
                                 */
                                if (!origenId) {

                                    enviarFormulario(
                                            accion,
                                            "",
                                            []
                                    );

                                    return;
                                }

                                const origen
                                        = document.getElementById(
                                                origenId
                                        );

                                const logins
                                        = obtenerSeleccionados(
                                                origen
                                        ).map(
                                                function (item) {

                                                    return item
                                                            .dataset
                                                            .login;
                                                }
                                        );

                                if (logins.length === 0) {

                                    mostrarAviso();

                                    return;
                                }

                                enviarFormulario(
                                        accion,
                                        origen.dataset.inputName,
                                        logins
                                );
                            }
                    );
                }
        );
    }

    function enviarSeleccionados(lista) {

        const logins
                = obtenerSeleccionados(
                        lista
                ).map(
                        function (item) {

                            return item
                                    .dataset
                                    .login;
                        }
                );

        if (logins.length === 0) {

            mostrarAviso();

            return;
        }

        const accion
                = lista.id
                === "usuariosDisponibles"
                ? "asignarSeleccionados"
                : "retirarSeleccionados";

        enviarFormulario(
                accion,
                lista.dataset.inputName,
                logins
        );
    }

    function enviarFormulario(
            accion,
            nombreCampo,
            valores
    ) {

        if (
                formulario.dataset.enviando
                === "true"
        ) {
            return;
        }

        formulario.dataset.enviando = "true";

        formulario.setAttribute(
                "aria-busy",
                "true"
        );

        formulario.querySelectorAll(
                ".campo-transferencia"
        ).forEach(
                function (campo) {

                    campo.remove();
                }
        );

        agregarCampoOculto(
                "accion",
                accion
        );

        valores.forEach(
                function (valor) {

                    agregarCampoOculto(
                            nombreCampo,
                            valor
                    );
                }
        );

        formulario.querySelectorAll(
                "button"
        ).forEach(
                function (boton) {

                    boton.disabled = true;
                }
        );

        formulario.submit();
    }

    function agregarCampoOculto(
            nombre,
            valor
    ) {

        if (!nombre) {
            return;
        }

        const campo
                = document.createElement(
                        "input"
                );

        campo.type = "hidden";
        campo.name = nombre;
        campo.value = valor;

        campo.className
                = "campo-transferencia";

        formulario.appendChild(campo);
    }

    function configurarBusqueda(
            idBuscador,
            lista
    ) {

        const buscador
                = document.getElementById(
                        idBuscador
                );

        if (!buscador) {
            return;
        }

        buscador.addEventListener(
                "input",
                function () {

                    const termino
                            = normalizar(
                                    buscador.value
                            );

                    lista.querySelectorAll(
                            ".asignacion-usuario-item"
                    ).forEach(
                            function (item) {

                                const texto
                                        = item.dataset.search
                                        || item.textContent;

                                item.classList.toggle(
                                        "oculto",
                                        !normalizar(texto)
                                                .includes(
                                                        termino
                                                )
                                );
                            }
                    );
                }
        );
    }

    function obtenerItemsVisibles(lista) {

        return Array.from(
                lista.querySelectorAll(
                        ".asignacion-usuario-item"
                )
        ).filter(
                function (item) {

                    return !item.classList.contains(
                            "oculto"
                    );
                }
        );
    }

    function obtenerSeleccionados(lista) {

        return Array.from(
                lista.querySelectorAll(
                        ".asignacion-usuario-item.seleccionado"
                )
        );
    }

    function limpiarSeleccion(lista) {

        obtenerSeleccionados(
                lista
        ).forEach(
                function (item) {

                    marcarSeleccionado(
                            item,
                            false
                    );
                }
        );
    }

    function marcarSeleccionado(
            item,
            seleccionado
    ) {

        item.classList.toggle(
                "seleccionado",
                seleccionado
        );

        item.setAttribute(
                "aria-selected",
                seleccionado
                        ? "true"
                        : "false"
        );
    }

    function mostrarAviso() {

        if (!aviso) {
            return;
        }

        aviso.hidden = false;

        aviso.setAttribute(
                "tabindex",
                "-1"
        );

        aviso.focus();

        window.clearTimeout(
                mostrarAviso.temporizador
        );

        mostrarAviso.temporizador
                = window.setTimeout(
                        function () {

                            aviso.hidden = true;
                        },
                        3000
                );
    }

    function normalizar(texto) {

        return (texto || "")
                .toLowerCase()
                .normalize("NFD")
                .replace(
                        /[\u0300-\u036f]/g,
                        ""
                )
                .trim();
    }
});