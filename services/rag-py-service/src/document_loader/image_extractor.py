"""流程图/图片提取器

检测 PDF 中的图片区域、矢量图形和表格，将含表格或流程图的页面渲染为图片，
供视觉语言模型 (VLM) 生成文字描述。

策略：
  1. 用 PyMuPDF (fitz) 扫描每页的图片和矢量图形
  2. 图形密集的页面判定为"含流程图"
  3. 包含表格的页面判定为"含表格"
  4. 将这些页面渲染为 PNG 图片，返回路径列表供 VLM 处理
"""
from __future__ import annotations

from dataclasses import dataclass, field
from pathlib import Path

import fitz  # PyMuPDF


@dataclass
class ImageRegion:
    """PDF 中的图片区域"""
    page_num: int          # 所在页码（0-based）
    bbox: tuple[float, float, float, float]  # (x0, y0, x1, y1)
    image_path: str = ""  # 渲染后的图片路径
    is_flowchart: bool = False  # 是否判定为流程图
    is_table: bool = False      # 是否判定为表格


@dataclass
class ExtractionResult:
    """图片提取结果（表格 + 流程图）"""
    source: str
    page_images: list[str] = field(default_factory=list)  # 渲染后的页面图片路径
    regions: list[ImageRegion] = field(default_factory=list)
    flowchart_pages: list[int] = field(default_factory=list)  # 含流程图的页码
    table_pages: list[int] = field(default_factory=list)      # 含表格的页码


def extract_visual_elements(
    pdf_path: str | Path,
    output_dir: str | Path = "data/vector_store/page_images",
    dpi: int = 200,
    min_graphics_count: int = 10,
    min_image_area_ratio: float = 0.15,
) -> ExtractionResult:
    """
    扫描 PDF，提取表格和流程图页面并渲染为图片。

    判定规则：
      - 矢量图形数量 >= min_graphics_count，或图片面积占比 >= min_image_area_ratio → 流程图
      - 页面包含表格（通过扫描线条检测）→ 表格

    Args:
        pdf_path: PDF 文件路径
        output_dir: 渲染图片输出目录
        dpi: 渲染分辨率
        min_graphics_count: 判定为流程图的最少矢量图形数
        min_image_area_ratio: 图片面积占比阈值

    Returns:
        ExtractionResult: 提取结果
    """
    pdf_path = Path(pdf_path)
    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    result = ExtractionResult(source=str(pdf_path))
    doc = fitz.open(str(pdf_path))

    for page_num in range(len(doc)):
        page = doc[page_num]

        drawings = page.get_drawings()
        images = page.get_images(full=True)

        page_area = page.rect.width * page.rect.height
        image_area = 0.0
        for img_info in images:
            xref = img_info[0]
            for img_rect in page.get_image_rects(xref):
                image_area += img_rect.width * img_rect.height

        image_ratio = image_area / page_area if page_area > 0 else 0

        is_flowchart = (
            len(drawings) >= min_graphics_count
            or image_ratio >= min_image_area_ratio
        )
        is_table = _detect_table_on_page(page)

        if is_flowchart or is_table:
            img_path = _render_page(page, page_num, output_dir, dpi)
            result.page_images.append(img_path)

            if is_flowchart:
                result.flowchart_pages.append(page_num)
            if is_table:
                result.table_pages.append(page_num)

            region = ImageRegion(
                page_num=page_num,
                bbox=(0, 0, page.rect.width, page.rect.height),
                image_path=img_path,
                is_flowchart=is_flowchart,
                is_table=is_table,
            )
            result.regions.append(region)

    doc.close()
    return result


def _detect_table_on_page(page: fitz.Page) -> bool:
    """通过检测页面中的线条结构判定是否包含表格"""
    drawings = page.get_drawings()
    if not drawings:
        return False

    horizontal_lines = 0
    vertical_lines = 0

    for draw in drawings:
        for item in draw.get("items", []):
            if isinstance(item, list) and len(item) >= 2 and item[0] == "l":
                try:
                    _, x0, y0, x1, y1 = item[:5]
                except ValueError:
                    continue

                length = ((x1 - x0) ** 2 + (y1 - y0) ** 2) ** 0.5
                if length < 10:
                    continue

                dx = abs(x1 - x0)
                dy = abs(y1 - y0)
                if dx > dy * 3:
                    horizontal_lines += 1
                elif dy > dx * 3:
                    vertical_lines += 1

    return horizontal_lines >= 3 and vertical_lines >= 3


def _render_page(
    page: fitz.Page,
    page_num: int,
    output_dir: Path,
    dpi: int = 200,
) -> str:
    """将单页渲染为 PNG 图片"""
    zoom = dpi / 72  # 72 DPI 是 PDF 默认分辨率
    matrix = fitz.Matrix(zoom, zoom)
    pix = page.get_pixmap(matrix=matrix)
    img_path = str(output_dir / f"page_{page_num:04d}.png")
    pix.save(img_path)
    return img_path


def render_page_range(
    pdf_path: str | Path,
    page_nums: list[int],
    output_dir: str | Path = "data/vector_store/page_images",
    dpi: int = 200,
) -> list[str]:
    """
    渲染指定页码范围为图片（供 VLM 处理）。

    Args:
        pdf_path: PDF 文件路径
        page_nums: 要渲染的页码列表（0-based）
        output_dir: 输出目录
        dpi: 渲染分辨率

    Returns:
        图片路径列表
    """
    pdf_path = Path(pdf_path)
    output_dir = Path(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    doc = fitz.open(str(pdf_path))
    paths: list[str] = []

    for page_num in page_nums:
        if 0 <= page_num < len(doc):
            img_path = _render_page(doc[page_num], page_num, output_dir, dpi)
            paths.append(img_path)

    doc.close()
    return paths
