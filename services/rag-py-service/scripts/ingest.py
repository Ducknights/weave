"""入库脚本：加载 chunks、向量化、存入 ChromaDB"""
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from src.document_loader.chunker import load_chunks
from src.embedding.embedder import Embedder
from src.vector_store.store import VectorStore


def main():
    # 1. 加载分块
    print("=" * 50)
    print("1. 加载 chunks.json ...")
    chunks = load_chunks()
    print(f"   共 {len(chunks)} 个块")

    # 2. 初始化嵌入模型
    print("\n2. 加载嵌入模型 ...")
    embedder = Embedder()

    # TF-IDF 需要先训练
    if embedder.backend == "tfidf":
        texts = [c["content"] for c in chunks]
        embedder.fit(texts)

    print(f"   后端: {embedder.backend}")
    print(f"   向量维度: {embedder.dim}")

    # 3. 批量向量化
    print("\n3. 向量化文本 ...")
    texts = [c["content"] for c in chunks]
    embeddings = embedder.embed(texts)
    print(f"   生成 {len(embeddings)} 个向量")

    # 4. 存入向量库
    print("\n4. 存入 ChromaDB ...")
    store = VectorStore()
    store.reset()

    ids = [f"chunk_{i}" for i in range(len(chunks))]
    metadatas = [{"source": c["source"], "title": c["title"]} for c in chunks]

    store.add(
        ids=ids,
        embeddings=embeddings,
        documents=texts,
        metadatas=metadatas,
    )
    print(f"   已存入 {store.count} 条")

    # 5. 验证检索
    print("\n" + "=" * 50)
    print("5. 验证检索")
    print("=" * 50)
    queries = [
        "学校的校训是什么？",
        "学分绩点怎么计算？",
        "国家奖学金的金额是多少？",
        "学生如何申请免听课程？",
    ]
    for q in queries:
        q_emb = embedder.embed_query(q)
        results = store.search(q_emb, top_k=3)
        print(f"\n  [Q] {q}")
        for r in results:
            title = r["metadata"].get("title", "?")
            score = round(1 - r["distance"], 4)
            preview = r["document"][:80]
            print(f"    [{score:.3f}] [{title}] {preview}...")

    print("\n" + "=" * 50)
    print("入库完成!")


if __name__ == "__main__":
    main()
