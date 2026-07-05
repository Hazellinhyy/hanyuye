# Cat Recognition Model Service

This service exposes CLIP image embeddings for the Java backend.

```powershell
cd model-service
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 9000
```

Health check:

```text
GET http://localhost:9000/health
```

Embedding endpoint used by the Java app:

```text
POST http://localhost:9000/v1/image-embedding
multipart/form-data: model, image
```
