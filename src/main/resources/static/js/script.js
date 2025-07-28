
    // Obtener referencias a los elementos del DOM
    const urlForm = document.getElementById('url-form');
    const longUrlInput = document.getElementById('long-url-input');
    const resultSection = document.getElementById('result-section');
    const shortUrlOutput = document.getElementById('short-url-output');
    const copyBtn = document.getElementById('copy-btn');
    const errorMessage = document.getElementById('error-message');

    // Escuchar el evento 'submit' del formulario
    urlForm.addEventListener('submit', async (event) => {
    // Prevenir el comportamiento por defecto del formulario (que es recargar la página)
    event.preventDefault();

    // Ocultar resultados y errores anteriores
    resultSection.style.display = 'none';
    errorMessage.style.display = 'none';

    const longUrl = longUrlInput.value;

    try {
    // Enviar la petición al backend usando la API Fetch
    const response = await fetch('/shorten', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify({ url: longUrl })
});

    if (!response.ok) {
    throw new Error('La respuesta del servidor no fue exitosa.');
}

    // Procesar la respuesta JSON del servidor
    const data = await response.json();

    // Mostrar el resultado en la página
    shortUrlOutput.value = data.shortUrl;
    resultSection.style.display = 'block';

} catch (error) {
    console.error('Error al acortar la URL:', error);
    errorMessage.innerText = 'Ocurrió un error. Por favor, intenta de nuevo.';
    errorMessage.style.display = 'block';
}
});

    // Funcionalidad para el botón de copiar
    copyBtn.addEventListener('click', () => {
    // Seleccionar el texto del campo de la URL corta
    shortUrlOutput.select();
    // Copiar el texto al portapapeles
    navigator.clipboard.writeText(shortUrlOutput.value);

    // Cambiar temporalmente el texto del botón para dar feedback al usuario
    copyBtn.innerText = '¡Copiado!';
    setTimeout(() => {
    copyBtn.innerText = 'Copiar';
}, 2000);
});
