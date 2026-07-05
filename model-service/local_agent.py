import os
import time
import uuid
from pathlib import Path
from typing import Any, Dict, List, Optional
from urllib.parse import unquote_plus

import torch
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from transformers import AutoModelForCausalLM, AutoTokenizer


os.environ.setdefault("TRANSFORMERS_NO_TF", "1")
os.environ.setdefault("TF_CPP_MIN_LOG_LEVEL", "3")

BASE_DIR = Path(__file__).resolve().parent
MODEL_DIR = Path(os.getenv("LOCAL_AGENT_MODEL_DIR", BASE_DIR / "models" / "qwen2.5-0.5b-instruct"))
MODEL_NAME = os.getenv("LOCAL_AGENT_MODEL_NAME", "qwen2.5-0.5b-instruct")
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

app = FastAPI(title="Local General Agent Service")

tokenizer = AutoTokenizer.from_pretrained(MODEL_DIR, trust_remote_code=True)
model = AutoModelForCausalLM.from_pretrained(
    MODEL_DIR,
    trust_remote_code=True,
    torch_dtype=torch.float16 if DEVICE == "cuda" else torch.float32,
).to(DEVICE)
model.eval()


class ChatMessage(BaseModel):
    role: str
    content: str


class ChatRequest(BaseModel):
    model: Optional[str] = MODEL_NAME
    messages: List[ChatMessage]
    temperature: Optional[float] = 0.7
    max_tokens: Optional[int] = 700


@app.get("/health")
def health() -> Dict[str, str]:
    return {"status": "ok", "model": MODEL_NAME, "device": DEVICE}


@app.post("/v1/chat/completions")
def chat_completions(request: ChatRequest) -> Dict[str, Any]:
    if not request.messages:
        raise HTTPException(status_code=400, detail="messages is required")

    messages = [
        {"role": item.role if item.role in {"system", "user", "assistant"} else "user", "content": item.content}
        for item in request.messages
        if item.content and item.content.strip()
    ]
    if not messages:
        raise HTTPException(status_code=400, detail="messages is empty")

    try:
        prompt = tokenizer.apply_chat_template(messages, tokenize=False, add_generation_prompt=True)
        inputs = tokenizer([prompt], return_tensors="pt").to(DEVICE)
        max_new_tokens = max(32, min(int(request.max_tokens or 700), 900))
        temperature = float(request.temperature or 0.7)
        with torch.no_grad():
            output_ids = model.generate(
                **inputs,
                max_new_tokens=max_new_tokens,
                do_sample=temperature > 0,
                temperature=max(0.1, temperature),
                top_p=0.9,
                repetition_penalty=1.08,
                pad_token_id=tokenizer.eos_token_id,
            )
        answer = tokenizer.decode(output_ids[0][inputs.input_ids.shape[1]:], skip_special_tokens=True).strip()
    except Exception as exception:
        raise HTTPException(status_code=500, detail=str(exception)) from exception

    return {
        "id": f"chatcmpl-{uuid.uuid4().hex}",
        "object": "chat.completion",
        "created": int(time.time()),
        "model": request.model or MODEL_NAME,
        "choices": [
            {
                "index": 0,
                "message": {"role": "assistant", "content": answer},
                "finish_reason": "stop",
            }
        ],
    }


@app.get("/v1/simple-chat")
def simple_chat(message: str, max_tokens: int = 700, temperature: float = 0.7) -> Dict[str, str]:
    content = unquote_plus(message or "").strip()
    if not content:
        raise HTTPException(status_code=400, detail="message is required")
    request = ChatRequest(
        model=MODEL_NAME,
        messages=[
            ChatMessage(
                role="system",
                content=(
                    "You are a local general AI assistant for this web system. "
                    "Do not claim to be Anthropic, OpenAI, Alibaba, or any other company. "
                    "If asked who you are, say you are a local general AI assistant. "
                    "Answer in the same language as the user."
                ),
            ),
            ChatMessage(role="user", content=content),
        ],
        max_tokens=max_tokens,
        temperature=temperature,
    )
    result = chat_completions(request)
    answer = result["choices"][0]["message"]["content"]
    lowered = answer.lower()
    if "anthropic" in lowered or "openai" in lowered or "alibaba" in lowered or "qwen" in lowered:
        answer = "我是本地通用智能体，可以回答学习、写作、生活、系统使用和校园猫认养等问题。你可以直接告诉我想问什么。"
    return {"answer": answer, "model": MODEL_NAME}
