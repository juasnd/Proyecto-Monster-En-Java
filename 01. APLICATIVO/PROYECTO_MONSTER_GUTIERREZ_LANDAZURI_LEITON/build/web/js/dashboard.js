const modulos = [
    {
        titulo: "Departamentos",
        descripcion: "Administra los departamentos registrados dentro del sistema.",
        icono: "",
        enlace: contextPath + "/DepartamentoController",
        boton: "Gestionar departamentos"
    }
];

const contenedorModulos = document.getElementById("contenedorModulos");

modulos.forEach(modulo => {
    const tarjeta = document.createElement("article");
    tarjeta.classList.add("modulo-card");

    tarjeta.innerHTML = `
        <div class="modulo-icono">${modulo.icono}</div>
        <h3>${modulo.titulo}</h3>
        <p>${modulo.descripcion}</p>
        <a href="${modulo.enlace}">${modulo.boton}</a>
    `;

    contenedorModulos.appendChild(tarjeta);
});