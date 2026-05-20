/**
 * Lógica para la gestión de imágenes en new-product.html
 * Alterna entre subida de archivo y URL externa.
 */
document.addEventListener("DOMContentLoaded", function() {
    const radioInputs = document.querySelectorAll('input[name="imageSource"]');
    
    function toggleImageInputs() {
        const isUrl = document.getElementById('sourceUrl').checked;
        const fileContainer = document.getElementById('fileInputContainer');
        const urlContainer = document.getElementById('urlInputContainer');

        if (isUrl) {
            fileContainer.classList.add('d-none');
            urlContainer.classList.remove('d-none');
            // Limpiamos el valor del input file al cambiar a URL
            fileContainer.querySelector('input').value = '';
        } else {
            urlContainer.classList.add('d-none');
            fileContainer.classList.remove('d-none');
            // Limpiamos el valor del input url al cambiar a archivo
            urlContainer.querySelector('input').value = '';
        }
    }

    // Escuchador de eventos para los radio buttons
    radioInputs.forEach(radio => {
        radio.addEventListener('change', toggleImageInputs);
    });
});