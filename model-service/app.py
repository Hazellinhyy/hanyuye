# 导入必要的依赖库
import os
from io import BytesIO

import torch
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from PIL import Image
from transformers import CLIPModel, CLIPProcessor


# 模型配置：从环境变量读取模型名称，默认使用OpenAI的CLIP-ViT-B/32模型
MODEL_NAME = os.getenv("RECOGNITION_MODEL_NAME", "openai/clip-vit-base-patch32")
# 设备配置：优先使用GPU（CUDA），否则使用CPU
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

# 初始化FastAPI应用实例，服务名称为猫咪识别嵌入服务
app = FastAPI(title="Cat Recognition Embedding Service")

# 初始化CLIP处理器和模型
# CLIPProcessor用于图像预处理（缩放、归一化等）
processor = CLIPProcessor.from_pretrained(MODEL_NAME)
# CLIPModel是预训练的图像-文本匹配模型，加载到指定设备上
model = CLIPModel.from_pretrained(MODEL_NAME).to(DEVICE)
# 设置模型为评估模式（关闭训练相关的操作如Dropout）
model.eval()


# 健康检查接口：用于监控服务状态
@app.get("/health")
def health():
    return {"status": "ok", "model": MODEL_NAME, "device": DEVICE}


# 图像嵌入生成接口：接收图像文件，返回CLIP模型提取的图像特征向量
@app.post("/v1/image-embedding")
async def image_embedding(
        model_name: str = Form(default=MODEL_NAME, alias="model"),
        image: UploadFile = File(...)):
    # 模型名称校验：确保请求使用的模型与服务加载的模型一致
    if model_name != MODEL_NAME:
        raise HTTPException(status_code=400, detail=f"Loaded model is {MODEL_NAME}, got {model_name}")
    
    # 读取上传的图像文件内容
    content = await image.read()
    
    # 图像格式解析与转换：将二进制内容转换为PIL图像对象并转为RGB格式
    try:
        pil_image = Image.open(BytesIO(content)).convert("RGB")
    except Exception as exception:
        raise HTTPException(status_code=400, detail="Invalid image") from exception

    # 图像预处理：使用CLIP处理器将图像转换为模型可接受的张量格式，并移动到指定设备
    inputs = processor(images=pil_image, return_tensors="pt").to(DEVICE)
    
    # 模型推理：提取图像特征向量
    with torch.no_grad():  # 禁用梯度计算以提高推理速度并节省内存
        features = model.get_image_features(**inputs)  # 获取图像特征
        features = features / features.norm(p=2, dim=-1, keepdim=True)  # L2归一化处理

    # 返回结果：包含使用的模型名称和归一化后的特征向量
    return {
        "model": MODEL_NAME,
        "embedding": features.squeeze(0).cpu().tolist()
    }