"""下载 BGE 嵌入模型到本地

从 hf-mirror.com 下载 bge-small-zh-v1.5 模型文件。
下载完成后，Embedder 将自动使用本地模型而非 TF-IDF。

运行方式：
    python scripts/download_model.py
"""
import os
import sys
import json
import urllib.request
from pathlib import Path

MODEL_NAME = "BAAI/bge-small-zh-v1.5"
MIRROR = "https://hf-mirror.com"
LOCAL_DIR = Path("models/bge-small-zh-v1.5")

# 不需要下载的文件
SKIP = {".gitattributes", "README.md", "config.json", "tokenizer_config.json",
        "special_tokens_map.json", "vocab.txt", "sentence_bert_config.json",
        "modules.json", "config_sentence_transformers.json"}
SKIP_PREFIXES = ("onnx/",)


def main():
    LOCAL_DIR.mkdir(parents=True, exist_ok=True)

    # 1. 获取文件列表
    print(f"1. 获取文件列表: {MIRROR}/api/models/{MODEL_NAME}")
    url = f"{MIRROR}/api/models/{MODEL_NAME}"
    r = urllib.request.urlopen(url, timeout=10)
    info = json.loads(r.read())
    siblings = info.get("siblings", [])
    files = [s["rfilename"] for s in siblings
             if s["rfilename"] not in SKIP
             and not any(s["rfilename"].startswith(p) for p in SKIP_PREFIXES)]
    print(f"   共 {len(files)} 个文件需要下载")

    # 2. 逐文件下载
    for i, fname in enumerate(files, 1):
        fpath = LOCAL_DIR / fname
        if fpath.exists():
            print(f"   [{i}/{len(files)}] {fname} (已存在，跳过)")
            continue

        fpath.parent.mkdir(parents=True, exist_ok=True)
        furl = f"{MIRROR}/{MODEL_NAME}/resolve/main/{fname}"
        print(f"   [{i}/{len(files)}] 下载 {fname} ...")
        urllib.request.urlretrieve(furl, str(fpath))

    print(f"\n下载完成: {LOCAL_DIR.resolve()}")

    # 3. 复制缺失的小文件（config 等从缓存找或手动下载）
    extra_files = ["config.json", "tokenizer_config.json", "special_tokens_map.json",
                   "vocab.txt", "modules.json", "config_sentence_transformers.json"]
    for fname in extra_files:
        fpath = LOCAL_DIR / fname
        if not fpath.exists():
            url = f"{MIRROR}/{MODEL_NAME}/resolve/main/{fname}"
            print(f"  补充下载: {fname}")
            urllib.request.urlretrieve(url, str(fpath))

    print(f"\n文件列表: {sorted(p.name for p in LOCAL_DIR.iterdir())}")
    print("\n下一步: 运行 python scripts/ingest.py 即可使用 BGE 模型")


if __name__ == "__main__":
    main()
