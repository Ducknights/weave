"""嵌入模型模块

支持两种嵌入方式：
  1. TF-IDF（离线，无需下载模型）
  2. BGE 中文模型（在线，需从 HuggingFace 下载）

默认使用 TF-IDF，可传入 model_name 切换。
"""
from __future__ import annotations

import pickle
from pathlib import Path

import numpy as np


class TfidfEmbedder:
    """基于 sklearn TfidfVectorizer 的轻量嵌入器"""

    def __init__(self):
        self._vectorizer = None
        self._fitted = False

    def fit(self, texts: list[str]) -> None:
        from sklearn.feature_extraction.text import TfidfVectorizer
        self._vectorizer = TfidfVectorizer(
            max_features=512,
            analyzer="char_wb",
            ngram_range=(2, 4),
        )
        self._vectorizer.fit(texts)
        self._fitted = True

    @property
    def dim(self) -> int:
        if not self._fitted:
            raise RuntimeError("请先调用 fit() 训练嵌入器")
        return len(self._vectorizer.get_feature_names_out())

    def encode(self, texts: list[str], normalize: bool = True) -> np.ndarray:
        if not self._fitted:
            raise RuntimeError("请先调用 fit() 训练嵌入器")
        vec = self._vectorizer.transform(texts).toarray().astype(np.float32)
        if normalize:
            norms = np.linalg.norm(vec, axis=1, keepdims=True)
            norms[norms == 0] = 1e-10
            vec = vec / norms
        return vec

    def save(self, path: str | Path) -> None:
        path = Path(path)
        path.parent.mkdir(parents=True, exist_ok=True)
        with path.open("wb") as f:
            pickle.dump(self._vectorizer, f)

    def load(self, path: str | Path) -> None:
        path = Path(path)
        with path.open("rb") as f:
            self._vectorizer = pickle.load(f)
        self._fitted = True


class SentenceEmbedder:
    """基于 sentence-transformers 的神经嵌入器"""

    def __init__(self, model_name: str = "BAAI/bge-small-zh-v1.5", device: str = "cpu"):
        self.model_name = model_name
        self.device = device
        self._model = None

    @property
    def model(self):
        if self._model is None:
            from sentence_transformers import SentenceTransformer
            self._model = SentenceTransformer(self.model_name, device=self.device)
        return self._model

    @property
    def dim(self) -> int:
        try:
            return self.model.get_sentence_embedding_dimension()
        except AttributeError:
            return self.model.get_embedding_dimension()

    def encode(self, texts: list[str], normalize: bool = True) -> np.ndarray:
        return self.model.encode(
            texts,
            normalize_embeddings=normalize,
            show_progress_bar=len(texts) > 50,
        )


class Embedder:
    """统一嵌入器：优先尝试 BGE，失败则回退 TF-IDF"""

    def __init__(
        self,
        model_name: str = "BAAI/bge-small-zh-v1.5",
        tfidf_path: str = "data/vector_store/tfidf_vectorizer.pkl",
        device: str = "cpu",
    ):
        self.model_name = model_name
        self.tfidf_path = Path(tfidf_path)
        self.device = device
        self._backend = None    # "sentence" or "tfidf"
        self._embedder = None

    @property
    def dim(self) -> int:
        return self.embedder.dim

    @property
    def embedder(self):
        if self._embedder is None:
            self._init_embedder()
        return self._embedder

    def _init_embedder(self):
        # 优先级: 本地 BGE > HuggingFace BGE > TF-IDF

        # 1. 尝试加载本地模型
        local_path = Path("models/bge-small-zh-v1.5")
        if local_path.exists():
            try:
                embedder = SentenceEmbedder(str(local_path), self.device)
                dim = embedder.dim
                self._embedder = embedder
                self._backend = "sentence"
                print(f"[embedder] 已加载本地 BGE 模型 ({dim}维)")
                return
            except Exception as e:
                print(f"[embedder] 本地模型加载失败: {e}")

        # 2. 尝试从 HuggingFace 下载
        try:
            embedder = SentenceEmbedder(self.model_name, self.device)
            dim = embedder.dim
            self._embedder = embedder
            self._backend = "sentence"
            print(f"[embedder] 已加载 {self.model_name} ({dim}维)")
            return
        except Exception as e:
            print(f"[embedder] BGE 模型加载失败: {e}")
            print(f"[embedder] 提示: 运行 python scripts/download_model.py 下载本地模型")

        # 3. 回退：加载已训练的 TF-IDF
        if self.tfidf_path.exists():
            tfidf = TfidfEmbedder()
            try:
                tfidf.load(self.tfidf_path)
                self._embedder = tfidf
                self._backend = "tfidf"
                print(f"[embedder] 已加载 TF-IDF 嵌入器 ({tfidf.dim}维)")
                return
            except Exception:
                pass

        # 4. 最后回退：新建 TF-IDF（需调用 fit）
        self._embedder = TfidfEmbedder()
        self._backend = "tfidf"
        print(f"[embedder] 回退到 TF-IDF（需 fit 训练）")

    @property
    def backend(self) -> str:
        if self._embedder is None:
            self._init_embedder()
        return self._backend

    def fit(self, texts: list[str]) -> None:
        """TF-IDF 模式：训练向量化器"""
        if self._embedder is None:
            self._init_embedder()
        if self._backend == "tfidf":
            self._embedder.fit(texts)
            self._embedder.save(self.tfidf_path)
            print(f"[embedder] TF-IDF 训练完成 ({self._embedder.dim}维)")

    def embed(self, texts: str | list[str]) -> list[list[float]]:
        """批量嵌入"""
        if isinstance(texts, str):
            texts = [texts]
        vec = self.embedder.encode(texts)
        if isinstance(vec, np.ndarray):
            vec = vec.tolist()
        return vec

    def embed_query(self, query: str) -> list[float]:
        """查询嵌入"""
        if self._backend == "sentence" and "bge" in self.model_name.lower():
            query = f"为这个句子生成表示以用于检索相关文章：{query}"
        return self.embed(query)[0]
