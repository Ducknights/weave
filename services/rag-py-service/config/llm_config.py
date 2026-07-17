# LLM 配置
# 支持 OpenAI 及兼容接口（如国内的 DeepSeek、通义千问等）

import os
from dotenv import load_dotenv

# 从 .venv/.env 加载环境变量
load_dotenv(os.path.join(os.path.dirname(__file__), "..", ".venv", ".env"))

LLM_CONFIG = {
    # 模型名称
    "model": "deepseek-chat",

    # API 地址（OpenAI 官方: https://api.openai.com/v1）
    "base_url": "https://api.deepseek.com",

    # API Key（从 .venv/.env 文件读取，也可通过环境变量 OPENAI_API_KEY 设置）
    "api_key": os.getenv("OPENAI_API_KEY", ""),

    # 生成参数
    "temperature": 0.3,
    "max_tokens": 1024,
}
