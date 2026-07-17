"""向量数据库模块

基于 ChromaDB 持久化存储文档向量，支持相似度检索。
"""
from __future__ import annotations

import json
from pathlib import Path
from typing import Optional

import chromadb
from chromadb.config import Settings


class VectorStore:
    """ChromaDB 向量数据库封装"""

    def __init__(self, persist_dir: str = "data/vector_store/chroma", collection_name: str = "rag_docs"):
        self.persist_dir = Path(persist_dir)
        self.collection_name = collection_name
        self._client: Optional[chromadb.PersistentClient] = None
        self._collection: Optional[chromadb.Collection] = None

    @property
    def client(self) -> chromadb.PersistentClient:
        if self._client is None:
            self.persist_dir.mkdir(parents=True, exist_ok=True)
            self._client = chromadb.PersistentClient(
                path=str(self.persist_dir),
                settings=Settings(anonymized_telemetry=False),
            )
        return self._client

    @property
    def collection(self) -> chromadb.Collection:
        if self._collection is None:
            self._collection = self.client.get_or_create_collection(
                name=self.collection_name,
                metadata={"hnsw:space": "cosine"},
            )
        return self._collection

    @property
    def count(self) -> int:
        return self.collection.count()

    def add(
        self,
        ids: list[str],
        embeddings: list[list[float]],
        documents: list[str],
        metadatas: list[dict],
    ) -> None:
        """批量添加文档向量"""
        self.collection.add(
            ids=ids,
            embeddings=embeddings,
            documents=documents,
            metadatas=metadatas,
        )

    def search(self, query_embedding: list[float], top_k: int = 5) -> list[dict]:
        """
        相似度检索。

        Returns:
            [{id, document, metadata, distance}, ...]
        """
        results = self.collection.query(
            query_embeddings=[query_embedding],
            n_results=top_k,
        )

        hits = []
        for i in range(len(results["ids"][0])):
            hits.append({
                "id": results["ids"][0][i],
                "document": results["documents"][0][i],
                "metadata": results["metadatas"][0][i],
                "distance": results["distances"][0][i],
            })
        return hits

    def reset(self) -> None:
        """清空 collection（重新入库时使用）"""
        try:
            self.client.delete_collection(self.collection_name)
        except Exception:
            pass
        self._collection = None
