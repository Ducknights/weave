"""文档分块器

将提取的纯文本内容按标题层级分块：
  - 以标题为边界，每个标题及其下属内容构成一个块
  - 如果块内容过长，在标题级别内按段落进一步切分

每个 chunk 带有元数据（标题、层级等），便于后续检索时追溯来源。
分块结果可持久化为 JSON 文件，避免每次重新处理。
"""
from __future__ import annotations

import json
import re
from dataclasses import dataclass, field, asdict
from pathlib import Path

from src.document_loader.pdf_loader import DocumentContent


# DocumentContent 仅在 chunk_document 中按需导入，避免依赖 pymupdf4llm


@dataclass
class Chunk:
    """文本块"""
    content: str                          # 块内容
    source: str = ""                     # 源文件
    page_num: int = -1                   # 起始页码
    metadata: dict = field(default_factory=dict)

    def to_dict(self) -> dict:
        return asdict(self)

    @classmethod
    def from_dict(cls, d: dict) -> "Chunk":
        return cls(**d)


def chunk_document(
    doc: "DocumentContent",
    max_chunk_size: int = 1500,
    min_chunk_size: int = 50,
) -> list[Chunk]:
    """
    将文档内容按标题层级分块。

    策略：
      1. 合并所有页面文本
      2. 按 Markdown 标题识别段落边界（#、##、###）
      3. 每个标题 + 其下属内容构成一个块
      4. 如果块内容过长，在标题级别内按段落进一步切分

    Args:
        doc: 文档提取结果
        max_chunk_size: 单块最大字符数
        min_chunk_size: 单块最小字符数

    Returns:
        Chunk 列表
    """
    full_text, page_map = _merge_pages(doc)
    sections = _parse_sections(full_text, page_map)
    chunks = _split_sections(sections, max_chunk_size, min_chunk_size)

    for chunk in chunks:
        chunk.source = doc.source

    return chunks


def _merge_pages(doc: DocumentContent) -> tuple[str, dict[int, int]]:
    """
    合并所有页面的文本，并记录每个字符位置对应的页码。

    Returns:
        (合并后的文本, {字符位置: 页码})
    """
    full_text = ""
    page_map: dict[int, int] = {}
    pos = 0

    for page in doc.pages:
        text = page.text
        if text:
            full_text += text + "\n\n"
            # 记录该页开始位置的页码
            page_map[pos] = page.page_num
            pos += len(text) + 2

    return full_text, page_map


def _parse_sections(text: str, page_map: dict[int, int]) -> list[dict]:
    """
    按标题层级解析文本，返回章节列表。

    Returns:
        [{"level": 1, "title": "...", "content": "...", "start_pos": int}]
    """
    lines = text.split("\n")
    sections: list[dict] = []
    current_section = None

    title_pattern = re.compile(r"^(#{1,3})\s+(.+)")

    for line in lines:
        match = title_pattern.match(line)
        if match:
            if current_section:
                sections.append(current_section)

            level = len(match.group(1))
            title = match.group(2).strip()

            current_section = {
                "level": level,
                "title": title,
                "content": line + "\n",
                "start_pos": text.find(line),
            }
        elif current_section:
            current_section["content"] += line + "\n"

    if current_section:
        sections.append(current_section)

    for sec in sections:
        sec["content"] = sec["content"].strip()

    return sections


def _split_sections(sections: list[dict], max_size: int, min_size: int) -> list[Chunk]:
    """
    将章节切分为符合大小限制的 chunk。

    如果章节内容过长，在段落级别进一步切分。
    """
    chunks: list[Chunk] = []

    for sec in sections:
        content = sec["content"]
        title = sec["title"]
        level = sec["level"]
        start_pos = sec["start_pos"]

        if len(content) <= max_size:
            if len(content) >= min_size:
                chunks.append(Chunk(
                    content=content,
                    page_num=sec.get("page_num", -1),
                    metadata={
                        "title": title,
                        "level": level,
                        "section_type": "full",
                    },
                ))
            continue

        paragraphs = content.split("\n\n")
        current_chunk = []
        current_length = 0

        for para in paragraphs:
            para = para.strip()
            if not para:
                continue

            para_length = len(para) + 2

            if current_length + para_length > max_size and current_chunk:
                chunk_content = "\n\n".join(current_chunk).strip()
                if len(chunk_content) >= min_size:
                    chunks.append(Chunk(
                        content=chunk_content,
                        page_num=sec.get("page_num", -1),
                        metadata={
                            "title": title,
                            "level": level,
                            "section_type": "partial",
                        },
                    ))
                current_chunk = []
                current_length = 0

            current_chunk.append(para)
            current_length += para_length

        if current_chunk:
            chunk_content = "\n\n".join(current_chunk).strip()
            if len(chunk_content) >= min_size:
                chunks.append(Chunk(
                    content=chunk_content,
                    page_num=sec.get("page_num", -1),
                    metadata={
                        "title": title,
                        "level": level,
                        "section_type": "partial",
                    },
                ))

    return chunks


# ---------- 纯文本/TXT 文件处理 ----------

def chunk_text_content(
    text: str,
    source: str = "",
    max_chunk_size: int = 1500,
    min_chunk_size: int = 50,
) -> list[Chunk]:
    """
    对纯文本内容按标题层级分块，适用于 VLM 生成的 TXT 描述文件。
    TXT 文件中常见的 *X、...* 标题会被转换为 ### 。
    """
    text = _normalize_txt_headings(text)
    sections = _parse_sections(text, {})
    chunks = _split_sections(sections, max_chunk_size, min_chunk_size)
    for chunk in chunks:
        chunk.source = source
        chunk.metadata["doc_type"] = "vlm_description"
    return chunks


def _normalize_txt_headings(text: str) -> str:
    """将 TXT 文件中 *X、...* / *X....* 等标题格式转换为 ### 标题"""
    # 匹配：行首 *一、...* 或 *1....* 或 *一....* 格式的标题
    # 仅当整行被 * 包裹（或行尾有 *）
    lines = text.split("\n")
    result: list[str] = []
    for line in lines:
        stripped = line.strip()
        # 匹配 *开头、中文或数字序号 的标题行
        if re.match(r"^\*\s*[一二三四五六七八九十\d]+[、.．]\s*[^*]+[*]?\s*$", stripped):
            # 去掉首尾的 * 号，加上 ### 前缀
            content = stripped.strip("*")
            result.append("### " + content.strip())
        else:
            result.append(line)
    return "\n".join(result)


def chunk_txt_file(
    txt_path: str | Path,
    max_chunk_size: int = 1500,
    min_chunk_size: int = 50,
) -> list[Chunk]:
    """加载 TXT 文件并按标题层级分块。"""
    txt_path = Path(txt_path)
    if not txt_path.exists():
        raise FileNotFoundError(f"TXT 文件不存在: {txt_path}")
    text = txt_path.read_text(encoding="utf-8")
    chunks = chunk_text_content(text, source=txt_path.name, max_chunk_size=max_chunk_size, min_chunk_size=min_chunk_size)
    print(f"[chunker] {txt_path.name}: {len(chunks)} 个块")
    return chunks


def chunk_txt_directory(
    dir_path: str | Path,
    max_chunk_size: int = 1500,
    min_chunk_size: int = 50,
) -> list[Chunk]:
    """加载目录下所有 TXT 文件并按标题层级分块。"""
    dir_path = Path(dir_path)
    if not dir_path.is_dir():
        raise NotADirectoryError(f"目录不存在: {dir_path}")
    all_chunks: list[Chunk] = []
    for txt_file in sorted(dir_path.glob("*.txt")):
        chunks = chunk_txt_file(txt_file, max_chunk_size, min_chunk_size)
        all_chunks.extend(chunks)
    print(f"[chunker] 目录 {dir_path}: 共 {len(all_chunks)} 个块 ({len(list(dir_path.glob('*.txt')))} 个 TXT)")
    return all_chunks


# ---------- 持久化 ----------

DEFAULT_CHUNKS_PATH = "data/vector_store/chunks.json"


def save_chunks(chunks: list[Chunk], path: str | Path = DEFAULT_CHUNKS_PATH) -> str:
    """
    将分块结果保存为 JSON 文件。

    Args:
        chunks: Chunk 列表
        path: 输出路径

    Returns:
        保存的文件路径
    """
    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True)

    data = {
        "total": len(chunks),
        "chunks": [c.to_dict() for c in chunks],
    }
    with path.open("w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    level_counts: dict[int, int] = {}
    for c in chunks:
        level = c.metadata.get("level", 0)
        level_counts[level] = level_counts.get(level, 0) + 1

    print(f"[chunker] 已保存 {len(chunks)} 个块到 {path}")
    if level_counts:
        print(f"          层级分布: {level_counts}")
    return str(path)


def load_chunks(path: str | Path = DEFAULT_CHUNKS_PATH) -> list[dict]:
    """
    从 JSON 文件加载分块结果。

    Args:
        path: JSON 文件路径

    Returns:
        字典列表，每个字典含 content, source, title
    """
    path = Path(path)
    if not path.exists():
        raise FileNotFoundError(f"分块文件不存在: {path}")

    with path.open("r", encoding="utf-8") as f:
        data = json.load(f)

    chunks = data.get("chunks", [])
    print(f"[chunker] 已加载 {len(chunks)} 个块 (来自 {path})")
    return chunks