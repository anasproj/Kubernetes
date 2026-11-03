const express = require('express');
const fs = require('fs');
const app = express();

const dataFile = (process.env.DATA_DIR || '/data') + '/counter.txt';

app.get('/', (req, res) => {
    let count = 0;
    try {
        if (fs.existsSync(dataFile)) {
            count = parseInt(fs.readFileSync(dataFile, 'utf8')) || 0;
        }
        count++;
        fs.writeFileSync(dataFile, count.toString());
    } catch (err) {
        console.error("Erreur avec le volume:", err);
    }
    res.send(`C (Compteur: ${count})`);
});

app.listen(3000, () => console.log('Service C démarré sur le port 3000'));