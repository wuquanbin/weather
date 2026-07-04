import math
import sys
from pathlib import Path
from typing import Optional

WORKSPACE = Path(__file__).resolve().parents[1]
if str(WORKSPACE) not in sys.path:
    sys.path.insert(0, str(WORKSPACE))

from PIL import Image, ImageDraw

from scripts.insert_uml_diagrams import (
    ASSET_DIR,
    WHITE,
    INK,
    LIGHT_BORDER,
    dashed_line,
    draw_actor,
    ensure_dirs,
    font,
    wrap_text,
)


OUTPUT_PATH = ASSET_DIR / "cart_sequence.png"

TITLE_FONT = font(34, bold=True)
SMALL_FONT = font(21)
NAME_FONT = font(23, bold=True)

WIDTH = 3200
HEIGHT = 1680
TOP = 86
BOX_HEIGHT = 98
LIFELINE_TOP = TOP + BOX_HEIGHT
LIFELINE_BOTTOM = 1570


def draw_sequence_object(
    draw: ImageDraw.ImageDraw,
    center_x: int,
    name: str,
    stereotype: Optional[str],
    width: int,
) -> None:
    x0 = center_x - width // 2
    x1 = center_x + width // 2
    box = (x0, TOP, x1, TOP + BOX_HEIGHT)
    draw.rectangle(box, fill=WHITE, outline=INK, width=3)

    if stereotype:
        stereo_bbox = draw.textbbox((0, 0), stereotype, font=SMALL_FONT)
        stereo_w = stereo_bbox[2] - stereo_bbox[0]
        draw.text((center_x - stereo_w / 2, TOP + 12), stereotype, font=SMALL_FONT, fill=INK)
        name_y = TOP + 48
    else:
        name_y = TOP + 36

    name_text = f":{name}"
    name_bbox = draw.textbbox((0, 0), name_text, font=NAME_FONT)
    name_w = name_bbox[2] - name_bbox[0]
    name_h = name_bbox[3] - name_bbox[1]
    name_x = center_x - name_w / 2
    draw.text((name_x, name_y), name_text, font=NAME_FONT, fill=INK)
    draw.line((name_x, name_y + name_h + 4, name_x + name_w, name_y + name_h + 4), fill=INK, width=2)

    dashed_line(
        draw,
        (center_x, LIFELINE_TOP),
        (center_x, LIFELINE_BOTTOM),
        fill=LIGHT_BORDER,
        width=2,
        dash=10,
        gap=8,
    )


def draw_activation(
    draw: ImageDraw.ImageDraw,
    center_x: int,
    y0: int,
    y1: int,
    width: int = 18,
    offset: int = 0,
) -> None:
    draw.rectangle(
        (center_x - width // 2 + offset, y0, center_x + width // 2 + offset, y1),
        fill="#E5E7EB",
        outline=INK,
        width=2,
    )


def draw_open_arrow(
    draw: ImageDraw.ImageDraw,
    start: tuple[int, int],
    end: tuple[int, int],
    dashed: bool = False,
) -> None:
    if dashed:
        dashed_line(draw, start, end, fill=INK, width=3, dash=12, gap=8)
    else:
        draw.line((start, end), fill=INK, width=3)

    x1, y1 = start
    x2, y2 = end
    angle = math.atan2(y2 - y1, x2 - x1)
    arrow_len = 16
    arrow_w = 7
    p1 = (
        x2 - arrow_len * math.cos(angle) + arrow_w * math.sin(angle),
        y2 - arrow_len * math.sin(angle) - arrow_w * math.cos(angle),
    )
    p2 = (
        x2 - arrow_len * math.cos(angle) - arrow_w * math.sin(angle),
        y2 - arrow_len * math.sin(angle) + arrow_w * math.cos(angle),
    )
    draw.line((x2, y2, p1[0], p1[1]), fill=INK, width=3)
    draw.line((x2, y2, p2[0], p2[1]), fill=INK, width=3)


def draw_message(
    draw: ImageDraw.ImageDraw,
    start_x: int,
    end_x: int,
    y: int,
    label: str,
    *,
    dashed: bool = False,
    label_dx: int = 0,
    label_dy: int = -32,
    max_width: int = 420,
) -> None:
    draw_open_arrow(draw, (start_x, y), (end_x, y), dashed=dashed)

    lines = wrap_text(draw, label, SMALL_FONT, max_width)
    widths = [draw.textbbox((0, 0), line or "A", font=SMALL_FONT)[2] for line in lines]
    heights = [draw.textbbox((0, 0), line or "A", font=SMALL_FONT)[3] for line in lines]
    box_w = max(widths) + 18
    box_h = sum(heights) + max(0, len(lines) - 1) * 4 + 10

    cx = (start_x + end_x) / 2 + label_dx
    cy = y + label_dy
    box = (cx - box_w / 2, cy - box_h / 2, cx + box_w / 2, cy + box_h / 2)
    draw.rectangle(box, fill=WHITE)

    text_y = box[1] + 5
    for line, h, w in zip(lines, heights, widths):
        draw.text((cx - w / 2, text_y), line, font=SMALL_FONT, fill=INK)
        text_y += h + 4


def build_diagram() -> Path:
    ensure_dirs()

    image = Image.new("RGB", (WIDTH, HEIGHT), WHITE)
    draw = ImageDraw.Draw(image)

    title = "含有视图层的加入购物车顺序图"
    title_bbox = draw.textbbox((0, 0), title, font=TITLE_FONT)
    title_w = title_bbox[2] - title_bbox[0]
    draw.text(((WIDTH - title_w) / 2, 18), title, font=TITLE_FONT, fill=INK)

    actor_x = 120
    draw_actor(draw, actor_x, 70, "Customer")
    dashed_line(
        draw,
        (actor_x, 198),
        (actor_x, LIFELINE_BOTTOM),
        fill=LIGHT_BORDER,
        width=2,
        dash=10,
        gap=8,
    )

    participants = [
        ("SearchItemWindow", "<<view>>", 430, 250),
        ("AddItemWindow", "<<view>>", 760, 240),
        ("DisplayItemWindow", "<<view>>", 1090, 260),
        ("ViewAccessWindow", "<<view>>", 1420, 255),
        ("AddAccessWindow", "<<view>>", 1750, 250),
        ("DisplayItemAccessWindow", "<<view>>", 2090, 320),
        ("CustLoginWindow", "<<view>>", 2430, 250),
        ("CartHandler", "<<control>>", 2770, 230),
    ]
    for name, stereotype, center_x, width in participants:
        draw_sequence_object(draw, center_x, name, stereotype, width)

    xs = {
        "customer": actor_x,
        "search": 430,
        "add_item": 760,
        "display_item": 1090,
        "view_access": 1420,
        "add_access": 1750,
        "display_item_access": 2090,
        "cust_login": 2430,
        "cart_handler": 2770,
    }

    draw_activation(draw, xs["search"], 282, 392)
    draw_activation(draw, xs["add_item"], 412, 826)
    draw_activation(draw, xs["cust_login"], 644, 714)
    draw_activation(draw, xs["cart_handler"], 548, 812)
    draw_activation(draw, xs["display_item"], 836, 916)
    draw_activation(draw, xs["view_access"], 970, 1080)
    draw_activation(draw, xs["add_access"], 1100, 1430)
    draw_activation(draw, xs["cart_handler"], 1236, 1412, offset=10)
    draw_activation(draw, xs["display_item_access"], 1450, 1532)

    draw_message(
        draw,
        xs["customer"],
        xs["search"],
        300,
        "addItemToCart(promoNo, productId, size, color, qty)",
        label_dx=18,
        label_dy=-34,
        max_width=520,
    )
    draw_message(
        draw,
        xs["search"],
        xs["add_item"],
        430,
        "addItemToCart(promoNo, productId, size, color, qty)",
        label_dy=-46,
        max_width=360,
    )
    draw_message(
        draw,
        xs["add_item"],
        xs["cart_handler"],
        560,
        "addItemToCart(promoNo, productId, size, color, qty)",
        label_dx=40,
        label_dy=-34,
        max_width=640,
    )
    draw_message(
        draw,
        xs["cart_handler"],
        xs["cust_login"],
        670,
        "custInfo := requestCustomerId()",
        label_dy=-34,
        max_width=340,
    )
    draw_message(
        draw,
        xs["cust_login"],
        xs["cart_handler"],
        736,
        "custInfo",
        dashed=True,
        label_dy=30,
        max_width=160,
    )
    draw_message(
        draw,
        xs["cart_handler"],
        xs["display_item"],
        852,
        "description, price, extendedPrice",
        dashed=True,
        label_dx=-60,
        label_dy=-36,
        max_width=420,
    )
    draw_message(
        draw,
        xs["display_item"],
        xs["customer"],
        934,
        "description, price, extendedPrice",
        dashed=True,
        label_dx=-20,
        label_dy=28,
        max_width=420,
    )

    draw_message(
        draw,
        xs["customer"],
        xs["view_access"],
        990,
        "addAccessoryToCart(promoNo, productId, size, color, qty)",
        label_dx=50,
        label_dy=-36,
        max_width=620,
    )
    draw_message(
        draw,
        xs["view_access"],
        xs["add_access"],
        1120,
        "addAccessoryToCart(promoNo, productId, size, color, qty)",
        label_dy=-46,
        max_width=380,
    )
    draw_message(
        draw,
        xs["add_access"],
        xs["cart_handler"],
        1248,
        "addAccessoryToCart(promoNo, productId, size, color, qty)",
        label_dx=30,
        label_dy=-36,
        max_width=620,
    )
    draw_message(
        draw,
        xs["cart_handler"],
        xs["display_item_access"],
        1458,
        "description, price, extendedPrice",
        dashed=True,
        label_dx=-10,
        label_dy=-36,
        max_width=420,
    )
    draw_message(
        draw,
        xs["display_item_access"],
        xs["customer"],
        1540,
        "description, price, extendedPrice",
        dashed=True,
        label_dx=10,
        label_dy=28,
        max_width=420,
    )

    image.save(OUTPUT_PATH)
    return OUTPUT_PATH


if __name__ == "__main__":
    path = build_diagram()
    print(path)
