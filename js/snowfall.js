document.addEventListener('DOMContentLoaded', () => {
    const container = document.getElementById('snowfall-container');

    function createSnowflake() {
        const snowflake = document.createElement('div');
        snowflake.className = 'snowflake';
        snowflake.textContent = '❄'; // Snowflake symbol

        // Position the snowflake randomly
        snowflake.style.left = Math.random() * 100 + 'vw';
        snowflake.style.animationDuration = Math.random() * (80 - 60) + 10 + 's'; // Fall duration: 5-15 seconds
        snowflake.style.fontSize = Math.random() * 1 + 1 + 'em'; // Random size: 1em to 2em

        container.appendChild(snowflake);

        // Remove the snowflake after it finishes falling
        snowflake.addEventListener('animationend', () => {
            snowflake.remove();
        });
    }

    // Create new snowflakes every x miliseconds
    setInterval(createSnowflake, 8000);
});