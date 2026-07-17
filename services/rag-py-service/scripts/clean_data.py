"""数据清洗脚本：分析并清除 chunks.json 中的冗余信息。"""
import json
import re
from pathlib import Path


def analyze(chunks, print_samples=True):
    """分析冗余模式"""
    print(f"总块数: {len(chunks)}\n")

    patterns = {
        "含换行符 \\n": 0,
        "含 Markdown 标题前缀": 0,
        "含 page_num 字段": 0,
    }
    for c in chunks:
        text = c["content"]
        if "\n" in text:
            patterns["含换行符 \\n"] += 1
        if re.match(r"^#{1,3}\s", text):
            patterns["含 Markdown 标题前缀"] += 1
        if "page_num" in c:
            patterns["含 page_num 字段"] += 1

    print("=== 冗余模式统计 ===")
    for k, v in patterns.items():
        print(f"  {k}: {v} 个块")

    sizes = [len(c["content"]) for c in chunks]
    print(f"\n块大小: 最小={min(sizes)}, 最大={max(sizes)}, 平均={sum(sizes)//len(sizes)}")

    tiny = [c for c in chunks if len(c["content"]) < 50]
    big = [c for c in chunks if len(c["content"]) > 3000]
    print(f"过小块 (<50字符): {len(tiny)} 个")
    print(f"过大块 (>3000字符): {len(big)} 个")


def clean(chunks):
    """执行清洗规则"""
    removed = 0
    cleaned = []
    rules_applied = {
        "empty": 0, "tiny": 0, "toc": 0,
        "strip_title_prefix": 0, "collapse_newlines": 0,
        "fix_cn_spaces": 0, "removed_page_num": 0,
    }

    for c in chunks:
        text = c["content"]
        # 兼容新旧两种格式
        if "metadata" in c:
            title = c["metadata"].get("title", "")
        else:
            title = c.get("title", "")

        # 1. 去除 HTML 注释
        text = re.sub(r"<!--.*?-->", "", text)

        # 2. 去除 <br>
        text = text.replace("<br>", "")

        # 3. 去除页码标记
        text = re.sub(r"·\d+·", "", text)

        # 4. 去除 content 中的 Markdown 标题前缀（信息已在 metadata.title）
        text = re.sub(r"^#{1,3}\s+.+?(?=\n|$)\s*", "", text, count=1)
        if re.match(r"^#{1,3}\s", c["content"]):
            rules_applied["strip_title_prefix"] += 1

        # 5. 把换行符和全角空格替换为普通空格
        text = text.replace("\u3000", " ")  # 全角空格
        text = text.replace("\n", " ")

        # 6. 修复中文文本中的断字空格（循环直到无变化）
        while True:
            fixed = re.sub(r"([\u4e00-\u9fff])\s+([\u4e00-\u9fff])", r"\1\2", text)
            if fixed == text:
                break
            text = fixed

        # 7. 去除中文标点符号前的多余空格
        text = re.sub(r"\s+([，。！？；：、""''）】》」』])", r"\1", text)

        # 8. 压缩多个空格为一个
        text = re.sub(r"\s{2,}", " ", text).strip()

        if "\n" in c["content"] and "\n" not in text:
            rules_applied["collapse_newlines"] += 1

        if re.search(r"[\u4e00-\u9fff]\s+[\u4e00-\u9fff]", c["content"]):
            rules_applied["fix_cn_spaces"] += 1

        # 空块检查
        if not text.strip():
            rules_applied["empty"] += 1
            removed += 1
            continue

        # 过小块
        if len(text) < 30:
            rules_applied["tiny"] += 1
            removed += 1
            continue

        # 目录
        if title in ("目录", "Contents"):
            rules_applied["toc"] += 1
            removed += 1
            continue

        # 移除 page_num 字段
        if "page_num" in c:
            del c["page_num"]
            rules_applied["removed_page_num"] += 1

        # 精简字段：只保留 content, source, title
        cleaned.append({
            "content": text,
            "source": c.get("source", ""),
            "title": title,
        })

    return cleaned, rules_applied, removed


if __name__ == "__main__":
    chunks_path = Path("data/vector_store/chunks.json")

    with chunks_path.open("r", encoding="utf-8") as f:
        data = json.load(f)

    chunks = data["chunks"]

    print("=" * 50)
    print("清洗前分析")
    print("=" * 50)
    analyze(chunks)

    cleaned_chunks, rules, removed = clean(chunks)

    print("\n" + "=" * 50)
    print("清洗结果")
    print("=" * 50)
    print(f"移除: {removed} 个块")
    print(f"保留: {len(cleaned_chunks)} 个块")
    print(f"规则明细: {rules}")

    print("\n" + "=" * 50)
    print("清洗后分析")
    print("=" * 50)
    analyze(cleaned_chunks, print_samples=False)

    # 输出几个样本
    print("\n=== 清洗后样本 ===")
    for c in cleaned_chunks[:3]:
        title = c.get("title", "?")
        print(f"\n[{title}] ({len(c['content'])}字符)")
        print(c["content"][:150])
        print(f"字段: {list(c.keys())}")

    # 保存
    data["chunks"] = cleaned_chunks
    data["total"] = len(cleaned_chunks)
    with chunks_path.open("w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print(f"\n已保存: {chunks_path}")
