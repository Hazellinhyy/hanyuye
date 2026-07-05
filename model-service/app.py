import os
from io import BytesIO

import torch
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from PIL import Image
from transformers import CLIPModel, CLIPProcessor


MODEL_NAME = os.getenv("RECOGNITION_MODEL_NAME", "openai/clip-vit-base-patch32")
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

app = FastAPI(title="Cat Recognition Embedding Service")

processor = CLIPProcessor.from_pretrained(MODEL_NAME)
model = CLIPModel.from_pretrained(MODEL_NAME).to(DEVICE)
model.eval()


@app.get("/health")
def health():
    return {"status": "ok", "model": MODEL_NAME, "device": DEVICE}


@app.post("/v1/image-embedding")
async def image_embedding(
        model_name: str = Form(default=MODEL_NAME, alias="model"),
        image: UploadFile = File(...)):
    if model_name != MODEL_NAME:
        raise HTTPException(status_code=400, detail=f"Loaded model is {MODEL_NAME}, got {model_name}")
    content = await image.read()
    try:
        pil_image = Image.open(BytesIO(content)).convert("RGB")
    except Exception as exception:
        raise HTTPException(status_code=400, detail="Invalid image") from exception

    inputs = processor(images=pil_image, return_tensors="pt").to(DEVICE)
    with torch.no_grad():
        features = model.get_image_features(**inputs)
        features = features / features.norm(p=2, dim=-1, keepdim=True)

    return {
        "model": MODEL_NAME,
        "embedding": features.squeeze(0).cpu().tolist()
    }
