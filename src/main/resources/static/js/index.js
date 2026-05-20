// index.js
document.addEventListener("DOMContentLoaded", function() {
    
    // ==========================================
    // 1. BANNER PROMOCIONAL GEOLOCALIZADO (Sin CORS)
    // ==========================================
    
    // Candado sincrónico: si ya ha empezado a cargar en otra instancia del script, paramos aquí.
    if (window.geoBannerCargado) return;
    window.geoBannerCargado = true;

    fetch('https://get.geojs.io/v1/ip/geo.json')
        .then(response => {
            if (!response.ok) throw new Error("Fallo en la API de GeoJS");
            return response.json();
        })
        .then(data => {
            // Doble comprobación asíncrona: nos aseguramos de que no se haya pintado ya
            if (data.city && !document.getElementById('geo-banner')) {
                const promoBanner = document.createElement("div");
                promoBanner.id = "geo-banner";
                promoBanner.className = "alert bg-primary text-white text-center py-2 mb-0 rounded-0 shadow-sm border-0 geo-banner-text";
                
                promoBanner.innerHTML = `
                    <span class="fw-bold">🌟 ¡Oferta Especial!</span> 
                    Envíos gratuitos en 24h para todos los pedidos realizados desde <strong>${data.city}</strong>.
                    <button type="button" class="btn-close btn-close-white float-end" aria-label="Close" style="font-size: 0.75rem; margin-top: 2px;" onclick="this.parentElement.remove();"></button>
                `;
                
                const mainTag = document.querySelector("main");
                if (mainTag) {
                    mainTag.parentNode.insertBefore(promoBanner, mainTag);
                } else {
                    document.body.insertBefore(promoBanner, document.body.children[1]);
                }
            }
        })
        .catch(error => console.log("Banner geolocalizado ignorado: ", error.message));

    // ==========================================
    // 2. TEMA OSCURO / CLARO
    // ==========================================
    const htmlElement = document.documentElement; 
    const botonTema = document.getElementById('themeToggle'); 
    const temaGuardado = localStorage.getItem('mi_tema_tienda') || 'light';

    htmlElement.setAttribute('data-bs-theme', temaGuardado);
    if (botonTema) {
        botonTema.textContent = temaGuardado === 'dark' ? '☀️' : '🌙';
        botonTema.addEventListener('click', () => {
            const temaActual = htmlElement.getAttribute('data-bs-theme');
            const nuevoTema = temaActual === 'dark' ? 'light' : 'dark';
            htmlElement.setAttribute('data-bs-theme', nuevoTema);
            localStorage.setItem('mi_tema_tienda', nuevoTema);
            botonTema.textContent = nuevoTema === 'dark' ? '☀️' : '🌙';
        });
    }
});