document.addEventListener("DOMContentLoaded", function () {
    const header = document.getElementById("tabsHeader");
    const content = document.getElementById("tabsContent");

    if (!header || !content) {
        return;
    }

    document.querySelectorAll(".tree-link[data-tab-url]").forEach(function (link) {
        link.addEventListener("click", function (event) {
            event.preventDefault();

            const url = link.getAttribute("data-tab-url");
            const title = link.getAttribute("data-tab-title") || "Módulo";
            const codigo = link.getAttribute("data-codigo") || title;
            const tabId = "tab-" + codigo.toLowerCase().replace(/[^a-z0-9]/g, "-");

            abrirTab(tabId, title, url);
        });
    });

    header.addEventListener("click", function (event) {
        const boton = event.target.closest(".tab-button");
        const cerrar = event.target.closest(".tab-close");

        if (cerrar) {
            event.stopPropagation();
            cerrarTab(cerrar.getAttribute("data-tab-id"));
            return;
        }

        if (boton) {
            activarTab(boton.getAttribute("data-tab-id"));
        }
    });

    function abrirTab(tabId, title, url) {
        let boton = header.querySelector("[data-tab-id='" + tabId + "']");
        let panel = document.getElementById(tabId);

        if (!boton) {
            boton = document.createElement("button");
            boton.type = "button";
            boton.className = "tab-button";
            boton.setAttribute("data-tab-id", tabId);
            boton.innerHTML = "<span>" + escapeHtml(title) + "</span><span class='tab-close' data-tab-id='" + tabId + "'>×</span>";
            header.appendChild(boton);
        }

        if (!panel) {
            panel = document.createElement("section");
            panel.className = "tab-panel";
            panel.id = tabId;
            panel.innerHTML = "<iframe class='tab-frame' src='" + url + "' title='" + escapeHtml(title) + "'></iframe>";
            content.appendChild(panel);
        }

        activarTab(tabId);
    }

    function activarTab(tabId) {
        header.querySelectorAll(".tab-button").forEach(function (btn) {
            btn.classList.toggle("active", btn.getAttribute("data-tab-id") === tabId);
        });

        content.querySelectorAll(".tab-panel").forEach(function (panel) {
            panel.classList.toggle("active", panel.id === tabId);
        });
    }

    function cerrarTab(tabId) {
        if (!tabId || tabId === "tab-inicio") {
            return;
        }

        const boton = header.querySelector("[data-tab-id='" + tabId + "']");
        const panel = document.getElementById(tabId);
        const estabaActivo = boton && boton.classList.contains("active");

        if (boton) {
            boton.remove();
        }

        if (panel) {
            panel.remove();
        }

        if (estabaActivo) {
            activarTab("tab-inicio");
        }
    }

    function escapeHtml(text) {
        return String(text)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
});
