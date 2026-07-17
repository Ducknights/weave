"""生成模块

使用 LangChain + LLM 将检索到的上下文生成自然语言回答。
"""
from __future__ import annotations

from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_openai import ChatOpenAI

from config.llm_config import LLM_CONFIG

RAG_PROMPT = ChatPromptTemplate.from_messages([
    ("system", """你是一个基于知识库的问答助手。请根据以下参考资料回答用户的问题。

要求：
1. 如果参考资料中有相关信息，请基于资料内容给出准确、简洁的回答。
2. 如果参考资料中没有相关信息，请回答"根据现有资料，我无法回答这个问题"，不要编造。
3. 回答时不要提及"参考资料"、"根据文档"等字眼，直接给出答案。
4. 用中文回答。

参考资料：
{context}"""),
    ("human", "{question}"),
])


def _create_llm():
    api_key = LLM_CONFIG["api_key"] or None
    return ChatOpenAI(
        model=LLM_CONFIG["model"],
        base_url=LLM_CONFIG["base_url"],
        api_key=api_key,  # type: ignore[arg-type]
        temperature=LLM_CONFIG["temperature"],
        max_tokens=LLM_CONFIG["max_tokens"],
    )


def generate(question: str, context: str) -> str:
    """
    基于上下文生成回答。

    Args:
        question: 用户问题
        context: 检索到的参考文档

    Returns:
        LLM 生成的回答
    """
    llm = _create_llm()
    chain = RAG_PROMPT | llm | StrOutputParser()
    return chain.invoke({"context": context, "question": question})


def stream_generate(question: str, context: str):
    """
    基于上下文流式生成回答。

    Args:
        question: 用户问题
        context: 检索到的参考文档

    Yields:
        str: 逐个文本块
    """
    llm = _create_llm()
    chain = RAG_PROMPT | llm | StrOutputParser()
    for chunk in chain.stream({"context": context, "question": question}):
        yield chunk
