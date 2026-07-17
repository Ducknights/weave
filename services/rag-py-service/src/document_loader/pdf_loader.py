"""PDF 文档加载器

使用 pymupdf4llm 将 PDF 转换为 Markdown。
表格和流程图不做文字提取，直接渲染为图片供 VLM 处理。
按页分块输出，便于后续按内容类型分类处理。
"""
from __future__ import annotations

from dataclasses import dataclass, field
from pathlib import Path

import pymupdf4llm

@dataclass
class PageContent:
    """单页 PDF 的提取结果"""
    page_num: int                        # 页码（0-based）
    text: str = ""                       # 正文 Markdown（不含表格）
    has_tables: bool = False             # 是否包含表格
    has_images: bool = False             # 是否包含图片/流程图
    image_count: int = 0                 # 图片数量


@dataclass
class DocumentContent:
    """整个 PDF 的提取结果"""
    source: str                          # 源文件路径
    pages: list[PageContent] = field(default_factory=list)
    total_pages_with_tables: int = 0
    total_pages_with_images: int = 0

    @property
    def total_pages(self) -> int:
        return len(self.pages)


def load_pdf(pdf_path: str | Path, show_progress: bool = False) -> DocumentContent:
    """
    加载 PDF 文档，提取文本。
    表格和流程图标记为待渲染状态，不做文字提取。

    Args:
        pdf_path: PDF 文件路径
        show_progress: 是否显示处理进度

    Returns:
        DocumentContent: 包含每页内容的文档对象
    """
    pdf_path = Path(pdf_path)
    if not pdf_path.exists():
        raise FileNotFoundError(f"PDF 文件不存在: {pdf_path}")

    # 使用 pymupdf4llm 按页提取 Markdown
    page_chunks = pymupdf4llm.to_markdown(
        str(pdf_path),
        page_chunks=True,
        show_progress=show_progress,
        table_strategy="lines_strict",
        write_images=False,
        ignore_graphics=False,
    )

    doc = DocumentContent(source=str(pdf_path))

    for i, chunk in enumerate(page_chunks):
        text = chunk.get("text", "") if isinstance(chunk, dict) else str(chunk)

        has_tables = _detect_tables(text)
        has_images, image_count = _detect_images(text)

        page = PageContent(
            page_num=i,
            text=_strip_tables(text),
            has_tables=has_tables,
            has_images=has_images,
            image_count=image_count,
        )
        doc.pages.append(page)
        doc.total_pages_with_tables += 1 if has_tables else 0
        doc.total_pages_with_images += 1 if has_images else 0

    return doc


def _detect_tables(text: str) -> bool:
    """检测文本中是否包含表格（Markdown 表格语法）"""
    lines = text.split("\n")
    table_line_count = 0
    for line in lines:
        stripped = line.strip()
        if stripped.startswith("|") and stripped.endswith("|"):
            table_line_count += 1
            if table_line_count >= 2:
                return True
    return False


def _strip_tables(text: str) -> str:
    """从文本中移除表格内容，只保留纯文本"""
    lines = text.split("\n")
    result: list[str] = []
    in_table = False

    for line in lines:
        stripped = line.strip()
        is_table_row = stripped.startswith("|") and stripped.endswith("|")

        if is_table_row:
            in_table = True
            continue
        elif in_table:
            in_table = False

        if not in_table:
            result.append(line)

    return "\n".join(result)


def _detect_images(text: str) -> tuple[bool, int]:
    """
    检测 Markdown 文本中的图片引用。

    pymupdf4llm 在 write_images=False 时可能保留图片占位符，
    也可能完全省略。这里检测常见模式。
    """
    # Markdown 图片语法: ![alt](path)
    import re
    pattern = r"!\[[^\]]*\]\([^)]*\)"
    matches = re.findall(pattern, text)
    return len(matches) > 0, len(matches)
