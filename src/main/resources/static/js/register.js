// register.js
document.addEventListener("DOMContentLoaded", function() {
    
    // ==========================================
    // 1. API: AUTOCOMPLETADO DE CÓDIGO POSTAL
    // ==========================================
    const inputPostalCode = document.getElementById("postalCode");
    const inputCity = document.getElementById("city");
    const inputProvince = document.getElementById("province");

    if (inputPostalCode) {
        inputPostalCode.addEventListener("input", function() {
            const cp = inputPostalCode.value.trim();
            if (cp.length === 5) {
                fetch(`https://api.zippopotam.us/es/${cp}`)
                    .then(response => {
                        if (response.ok) return response.json();
                        throw new Error("Código postal no encontrado");
                    })
                    .then(data => {
                        inputCity.value = data.places[0]["place name"];
                        inputProvince.value = data.places[0]["state"];
                    })
                    .catch(error => console.log("Info autocompletado: ", error.message));
            }
        });
    }

    // ==========================================
    // 2. API: LISTADO DINÁMICO DE PAÍSES
    // ==========================================
    const selectCountry = document.getElementById("country");
    
    if (selectCountry) {
        // Llamamos a RestCountries pidiendo solo el nombre y las traducciones para optimizar la carga
        fetch('https://restcountries.com/v3.1/all?fields=name,translations')
            .then(response => response.json())
            .then(data => {
                // Ordenamos el array de países alfabéticamente usando el nombre en Español ('spa')
                data.sort((a, b) => a.translations.spa.common.localeCompare(b.translations.spa.common));
                
                // Limpiamos el texto de "Cargando..."
                selectCountry.innerHTML = '<option value="">Selecciona un país...</option>';
                
                // Inyectamos cada país como una opción en el HTML
                data.forEach(country => {
                    const option = document.createElement('option');
                    option.value = country.translations.spa.common;
                    option.textContent = country.translations.spa.common;
                    
                    // Si estás editando un usuario y ya tenía España, lo seleccionamos por defecto
                    if (selectCountry.getAttribute("value") === country.translations.spa.common) {
                        option.selected = true;
                    }
                    
                    selectCountry.appendChild(option);
                });
            })
            .catch(error => console.error("Error cargando países:", error));
    }

});