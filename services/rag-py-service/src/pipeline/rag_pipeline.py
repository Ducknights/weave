"""RAG Pipeline - 核心流水线模块

使用 LangChain 串联完整 RAG 流程：
  1. Retrieval  → 检索相关文档片段
  2. Generation → LLM 生成自然语言回答
"""
from __future__ import annotations

from src.retrieval.retriever import Retriever
from src.generation.generator import generate as llm_generate
from src.generation.generator import stream_generate as llm_stream_generate


_retriever: Retriever | None = None


def _get_retriever() -> Retriever:
    global _retriever
    if _retriever is None:
        _retriever = Retriever()
    return _retriever


def ask(question: str) -> str:
    """
    RAG 查询入口。

    流程：用户问题 → 检索文档 → LLM 生成回答
    """
    if not question.strip():
        return "请输入问题。"

    # Step 1: 检索
    retriever = _get_retriever()
    hits = retriever.retrieve(question)

    if not hits:
        return "未找到相关文档，请尝试换一种问法。"

    # Step 2: 构建上下文
    lines = []
    for i, hit in enumerate(hits, 1):
        lines.append(f"[参考{i}] 标题: {hit['title']}\n内容: {hit['content']}")
    context = "\n\n".join(lines)

    # Step 3: LLM 生成
    try:
        return llm_generate(question, context)
    except Exception as e:
        # LLM 不可用时的回退：返回检索结果
        error_msg = str(e)
        if "api_key" in error_msg.lower() or "credential" in error_msg.lower():
            fallback = [f"问题: {question}\n"]
            fallback.append("(LLM 未配置，以下为检索到的原始资料)\n")
            for i, hit in enumerate(hits, 1):
                fallback.append(
                    f"--- 参考 {i} (相关度: {hit['score']:.2%}) ---\n"
                    f"标题: {hit['title']}\n"
                    f"内容: {hit['content']}\n"
                )
            return "\n".join(fallback)
        return f"生成回答失败: {e}"


def ask_stream(question: str):
    """
    RAG 流式查询入口。

    流程与 ask() 一致，但 LLM 生成阶段逐个产出文本块。
    """
    if not question.strip():
        yield "请输入问题。"
        return

    retriever = _get_retriever()
    hits = retriever.retrieve(question)

    if not hits:
        yield "未找到相关文档，请尝试换一种问法。"
        return

    lines = []
    for i, hit in enumerate(hits, 1):
        lines.append(f"[参考{i}] 标题: {hit['title']}\n内容: {hit['content']}")
    context = "\n\n".join(lines)

    try:
        yield from llm_stream_generate(question, context)
    except Exception as e:
        yield f"生成回答失败: {e}"
