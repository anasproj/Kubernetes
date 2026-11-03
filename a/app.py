import os
import requests
from flask import Flask

app = Flask(__name__)

@app.route('/')
def call_b():
    # On récupère l'adresse de B via la variable d'environnement
    service_b_url = os.environ.get('SERVICE_B_URL')
    response = requests.get(service_b_url)
    return f"A -> {response.text}"

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)