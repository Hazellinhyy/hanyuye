# 导入必要的依赖库
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


# 设置环境变量：禁用TensorFlow支持，降低TensorFlow日志级别
os.environ.setdefault("TRANSFORMERS_NO_TF", "1")
os.environ.setdefault("TF_CPP_MIN_LOG_LEVEL", "3")

# 基础路径配置：获取当前文件所在目录
BASE_DIR = Path(__file__).resolve().parent
# 模型目录配置：从环境变量读取，默认使用本地models目录下的Qwen2.5-0.5B-Instruct模型
MODEL_DIR = Path(os.getenv("LOCAL_AGENT_MODEL_DIR", BASE_DIR / "models" / "qwen2.5-0.5b-instruct"))
MODEL_NAME = os.getenv("LOCAL_AGENT_MODEL_NAME", "qwen2.5-0.5b-instruct")
# 设备配置：优先使用GPU（CUDA），否则使用CPU
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

# 初始化FastAPI应用实例，服务名称为本地通用智能体服务
app = FastAPI(title="Local General Agent Service")

# 初始化Tokenizer和LLM模型
# Tokenizer用于文本编码/解码，trust_remote_code=True允许加载自定义代码
tokenizer = AutoTokenizer.from_pretrained(MODEL_DIR, trust_remote_code=True)
# 加载因果语言模型，GPU使用float16精度以节省显存，CPU使用float32精度
model = AutoModelForCausalLM.from_pretrained(
    MODEL_DIR,
    trust_remote_code=True,
    torch_dtype=torch.float16 if DEVICE == "cuda" else torch.float32,
).to(DEVICE)
# 设置模型为评估模式
model.eval()


# Pydantic数据模型：定义聊天消息结构
class ChatMessage(BaseModel):
    role: str    # 消息角色：system/system/user/assistant
    content: str # 消息内容


# Pydantic数据模型：定义聊天请求结构
class ChatRequest(BaseModel):
    model: Optional[str] = MODEL_NAME      # 使用的模型名称
    messages: List[ChatMessage]            # 消息列表
    temperature: Optional[float] = 0.7     # 生成温度（控制随机性）
    max_tokens: Optional[int] = 700        # 最大生成长度


# 健康检查接口：用于监控服务运行状态
@app.get("/health")
def health() -> Dict[str, str]:
    return {"status": "ok", "model": MODEL_NAME, "device": DEVICE}


# 核心聊天接口：兼容OpenAI API格式的对话补全接口
@app.post("/v1/chat/completions")
def chat_completions(request: ChatRequest) -> Dict[str, Any]:
    # 输入校验：确保消息列表不为空
    if not request.messages:
        raise HTTPException(status_code=400, detail="messages is required")

    # 消息预处理：过滤有效消息并标准化角色
    # 仅保留非空消息，角色不在{system, user, assistant}范围内的统一视为user
    messages = [
        {"role": item.role if item.role in {"system", "user", "assistant"} else "user", "content": item.content}
        for item in request.messages
        if item.content and item.content.strip()
    ]
    # 二次校验：确保过滤后仍有有效消息
    if not messages:
        raise HTTPException(status_code=400, detail="messages is empty")

    try:
        # 构建提示词：应用模型特定的聊天模板
        prompt = tokenizer.apply_chat_template(messages, tokenize=False, add_generation_prompt=True)
        # 文本编码：将提示词转换为模型可接受的张量格式
        inputs = tokenizer([prompt], return_tensors="pt").to(DEVICE)
        
        # 参数处理：限制最大生成长度在32-900之间
        max_new_tokens = max(32, min(int(request.max_tokens or 700), 900))
        # 参数处理：温度参数最小为0.1，避免生成过于确定
        temperature = float(request.temperature or 0.7)
        
        # 模型推理：生成响应文本
        with torch.no_grad():  # 禁用梯度计算以提高性能
            output_ids = model.generate(
                **inputs,
                max_new_tokens=max_new_tokens,      # 最大生成token数
                do_sample=temperature > 0,          # 根据温度决定是否采样
                temperature=max(0.1, temperature),  # 温度参数（控制随机性）
                top_p=0.9,                         # Top-P采样
                repetition_penalty=1.08,           # 重复惩罚，避免重复生成
                pad_token_id=tokenizer.eos_token_id, # padding token设置
            )
        # 解码输出：跳过输入部分和特殊token，获取纯文本响应
        answer = tokenizer.decode(output_ids[0][inputs.input_ids.shape[1]:], skip_special_tokens=True).strip()
    except Exception as exception:
        # 异常处理：捕获并返回500错误
        raise HTTPException(status_code=500, detail=str(exception)) from exception

    # 返回结果：遵循OpenAI API响应格式
    return {
        "id": f"chatcmpl-{uuid.uuid4().hex}",  # 唯一请求ID
        "object": "chat.completion",           # 对象类型
        "created": int(time.time()),           # 创建时间戳
        "model": request.model or MODEL_NAME,  # 使用的模型名称
        "choices": [
            {
                "index": 0,                                    # 选择索引
                "message": {"role": "assistant", "content": answer},  # 助手响应消息
                "finish_reason": "stop",                       # 结束原因
            }
        ],
    }


# 简化聊天接口：GET请求方式，适合简单对话场景
@app.get("/v1/simple-chat")
def simple_chat(message: str, max_tokens: int = 700, temperature: float = 0.7) -> Dict[str, str]:
    # URL解码并去除首尾空格
    content = unquote_plus(message or "").strip()
    # 输入校验：确保消息不为空
    if not content:
        raise HTTPException(status_code=400, detail="message is required")
    
    # 构建完整的ChatRequest请求
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
    
    # 调用核心聊天接口获取响应
    result = chat_completions(request)
    answer = result["choices"][0]["message"]["content"]
    
    # 安全过滤：检查响应是否包含敏感公司名称
    lowered = answer.lower()
    if "anthropic" in lowered or "openai" in lowered or "alibaba" in lowered or "qwen" in lowered:
        answer = "我是本地通用智能体，可以回答学习、写作、生活、系统使用和校园猫认养等问题。你可以直接告诉我想问什么。"
    
    # 返回简化格式的响应
    return {"answer": answer, "model": MODEL_NAME}