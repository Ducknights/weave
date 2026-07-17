"""检索模块

将用户查询向量化，从 ChromaDB 中检索最相关的文档片段。
"""
from __future__ import annotations

from src.embedding.embedder import Embedder
from src.vector_store.store import VectorStore


class Retriever:
    """RAG 检索器"""

    def __init__(self, top_k: int = 5):
        self.top_k = top_k
        self._embedder: Embedder | None = None
        self._store: VectorStore | None = None

    @property
    def embedder(self) -> Embedder:
        if self._embedder is None:
            self._embedder = Embedder()
            # TF-IDF 需要预先训练，BGE 不需要
            if self._embedder.backend == "tfidf":
                raise RuntimeError(
                    "TF-IDF 嵌入器未训练，请先运行 python scripts/ingest.py"
                )
        return self._embedder

    @property
    def store(self) -> VectorStore:
        if self._store is None:
            self._store = VectorStore()
        return self._store

    def retrieve(self, query: str) -> list[dict]:
        """
        检索与查询最相关的文档片段。

        Args:
            query: 用户查询文本

        Returns:
            [{content, title, source, score}, ...]
        """
        q_emb = self.embedder.embed_query(query)
        results = self.store.search(q_emb, top_k=self.top_k)

        hits = []
        for r in results:
            hits.append({
                "content": r["document"],
                "title": r["metadata"].get("title", ""),
                "source": r["metadata"].get("source", ""),
                "score": round(1 - r["distance"], 4),
            })
        return hits

    def retrieve_as_context(self, query: str) -> str:
        """
        检索并格式化为 LLM 上下文。

        Returns:
            格式化的上下文字符串
        """
        hits = self.retrieve(query)
        if not hits:
            return "未找到相关文档。"

        lines = []
        for i, hit in enumerate(hits, 1):
            lines.append(
                f"[参考{i}] 标题: {hit['title']}\n"
                f"内容: {hit['content']}"
            )
        return "\n\n".join(lines)
