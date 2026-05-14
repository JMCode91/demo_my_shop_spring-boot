// index.js
document.addEventListener("DOMContentLoaded", function() {
    
    // ==========================================
    // API: GEOLOCALIZACIÓN PARA BANNER PROMOCIONAL
    // ==========================================
    
    // Hacemos la petición a la API gratuita de IP
    fetch('https://ipapi.co/json/')
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error("No se pudo obtener la geolocalización");
        })
        .then(data => {
            // Verificamos que la API nos haya devuelto el nombre de una ciudad
            if (data.city) {
                // 1. Creamos el elemento contenedor del banner
                const promoBanner = document.createElement("div");
                
                // 2. Le añadimos clases de Bootstrap para que luzca profesional
                promoBanner.className = "alert bg-primary text-white text-center py-2 mb-0 rounded-0 shadow-sm border-0";
                promoBanner.style.fontSize = "0.95rem";
                
                // 3. Insertamos el mensaje dinámico usando la ciudad obtenida
                promoBanner.innerHTML = `
                    <span class="fw-bold">🌟 ¡Oferta Especial!</span> 
                    Envíos gratuitos en 24h para todos los pedidos realizados desde <strong>${data.city}</strong>.
                    <button type="button" class="btn-close btn-close-white float-end" aria-label="Close" style="font-size: 0.75rem; margin-top: 2px;" onclick="this.parentElement.remove();"></button>
                `;
                
                // 4. Inyectamos el banner justo después del Header (al principio del <main> o del <body>)
                // Buscamos la etiqueta <main> que es donde suele empezar el contenido de tu web
                const mainTag = document.querySelector("main");
                
                if (mainTag) {
                    mainTag.parentNode.insertBefore(promoBanner, mainTag);
                } else {
                    // Plan B por si no usas <main>: insertarlo directamente en el body después del header
                    document.body.insertBefore(promoBanner, document.body.children[1]);
                }
            }
        })
        .catch(error => {
            // Fallo silencioso: si un AdBlock lo bloquea, no hacemos nada y el usuario navega normal
            console.log("Información banner geolocalizado: ", error.message);
        });

});