// src/main/resources/static/js/profile.js

document.addEventListener("DOMContentLoaded", function() {
    const avatarGalleryModal = document.getElementById('avatarGalleryModal');
    const avatarContainer = avatarGalleryModal ? avatarGalleryModal.querySelector('.row.g-3') : null;

    // Aquí están tus 8 avatares fijos. 
    // (Nota: Si quieres cambiar alguno, solo tienes que cambiar la palabra entre comillas. 
    // DiceBear generará un emoji distinto por cada palabra diferente).
    const mis8Avatares = [
        "cool",       // Gafas de sol (como el de tu foto)
        "feliz",      // Sonrisa
        "guiño",      // Guiñando un ojo
        "risa",       // Riéndose a carcajadas
        "amor",       // Ojos de corazón
        "estrella",   // Ojos de estrella
        "sorpresa",   // Cara de asombro
        "gafas"       // Gafas de ver
    ];

    if (avatarGalleryModal && avatarContainer) {
        
        avatarGalleryModal.addEventListener('show.bs.modal', function () {
            // Si ya están cargados, no volvemos a llamar a la API
            if (avatarContainer.innerHTML.trim() !== '') return;

            let htmlAvatars = '';
            
            // Recorremos tu lista de 8 emojis fijos
            mis8Avatares.forEach(seed => {
                // Usamos el estilo 'fun-emoji' que es el que me has enseñado en la captura
                // Le pasamos tu semilla y un fondo transparente o de colores pastel
                const avatarUrl = `https://api.dicebear.com/7.x/fun-emoji/svg?seed=${seed}&backgroundColor=b6e3f4,c0aede,d1d4f9,e2e8f0`;
                
                // Usamos col-md-3 para que quepan 4 por fila (2 filas de 4 = 8 avatares)
                htmlAvatars += `
                    <div class="col-4 col-sm-3 col-md-3 text-center mb-3">
                        <div class="card border-0 shadow-sm rounded-4 avatar-option-card" style="cursor: pointer; transition: transform 0.2s;" onclick="saveAvatar('${avatarUrl}')">
                            <div class="card-body p-2">
                                <img src="${avatarUrl}" class="img-fluid rounded-circle bg-light shadow-sm border" alt="Avatar option">
                            </div>
                        </div>
                    </div>
                `;
            });
            
            avatarContainer.innerHTML = htmlAvatars;
            
            // Añadimos el efecto hover
            const cards = avatarContainer.querySelectorAll('.avatar-option-card');
            cards.forEach(card => {
                card.addEventListener('mouseenter', () => card.style.transform = 'scale(1.15)');
                card.addEventListener('mouseleave', () => card.style.transform = 'scale(1)');
            });
        });
    }
});

// Función para enviar el avatar al backend Java
function saveAvatar(avatarUrl) {
    const encodedUrl = encodeURIComponent(avatarUrl);
    
    fetch('/profile/avatar/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': getCsrfToken()
        },
        body: `avatarUrl=${encodedUrl}`
    })
    .then(response => {
        if (response.ok) {
            window.location.reload();
        } else {
            alert('Error al guardar el avatar.');
        }
    })
    .catch(error => console.error('Error:', error));
}

function getCsrfToken() {
    const tokenElement = document.querySelector('meta[name="_csrf"]');
    return tokenElement ? tokenElement.getAttribute('content') : '';
}