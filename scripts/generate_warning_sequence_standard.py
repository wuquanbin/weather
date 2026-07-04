from pathlib import Path
import math
from typing import Optional

from PIL import Image, ImageDraw, ImageFont


WORKSPACE = Path(r"D:\微软oneDriver\OneDrive\Desktop\weather")
OUT = WORKSPACE / "generated_assets" / "uml_diagrams" / "warning_sequence.png"
PUML = WORKSPACE / "generated_assets" / "uml_diagrams" / "puml" / "warning_sequence.puml"

WHITE = "#FFFFFF"
INK = "#1F2937"
LIFELINE = "#94A3B8"
ACTIVE = "#E5E7EB"

WIDTH = 3300
HEIGHT = 2220
TOP = 116
BOX_H = 106
LIFE_TOP = TOP + BOX_H
LIFE_BOTTOM = 2100


def font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont:
    candidates = []
    if bold:
        candidates.extend(
            [
                r"C:\Windows\Fonts\msyhbd.ttc",
                r"C:\Windows\Fonts\simhei.ttf",
                r"C:\Windows\Fonts\arialbd.ttf",
            ]
        )
    candidates.extend(
        [
            r"C:\Windows\Fonts\msyh.ttc",
            r"C:\Windows\Fonts\simhei.ttf",
            r"C:\Windows\Fonts\arial.ttf",
        ]
    )
    for candidate in candidates:
        if Path(candidate).exists():
            return ImageFont.truetype(candidate, size)
    return ImageFont.load_default()


TITLE = font(40, True)
NAME = font(25, True)
TEXT = font(23)
SMALL = font(20)


img = Image.new("RGB", (WIDTH, HEIGHT), WHITE)
draw = ImageDraw.Draw(img)


def text_size(text: str, text_font: ImageFont.FreeTypeFont) -> tuple[int, int]:
    bbox = draw.textbbox((0, 0), text, font=text_font)
    return bbox[2] - bbox[0], bbox[3] - bbox[1]


def wrap_text(text: str, text_font: ImageFont.FreeTypeFont, max_width: int) -> list[str]:
    lines: list[str] = []
    for block in text.split("\n"):
        current = ""
        for ch in block:
            candidate = current + ch
            if text_size(candidate, text_font)[0] <= max_width or not current:
                current = candidate
            else:
                lines.append(current)
                current = ch
        lines.append(current)
    return lines


def dashed(start: tuple[int, int], end: tuple[int, int], *, width: int = 2, fill: str = LIFELINE, dash: int = 10, gap: int = 8) -> None:
    x1, y1 = start
    x2, y2 = end
    length = math.hypot(x2 - x1, y2 - y1)
    if length == 0:
        return
    dx = (x2 - x1) / length
    dy = (y2 - y1) / length
    dist = 0.0
    while dist < length:
        seg = min(dash, length - dist)
        draw.line(
            (
                x1 + dx * dist,
                y1 + dy * dist,
                x1 + dx * (dist + seg),
                y1 + dy * (dist + seg),
            ),
            fill=fill,
            width=width,
        )
        dist += dash + gap


def draw_actor(cx: int, top: int, label: str) -> None:
    r = 28
    draw.ellipse((cx - r, top, cx + r, top + 2 * r), outline=INK, width=4)
    body_top = top + 2 * r
    body_bottom = body_top + 78
    draw.line((cx, body_top, cx, body_bottom), fill=INK, width=4)
    draw.line((cx - 42, body_top + 24, cx + 42, body_top + 24), fill=INK, width=4)
    draw.line((cx, body_bottom, cx - 34, body_bottom + 50), fill=INK, width=4)
    draw.line((cx, body_bottom, cx + 34, body_bottom + 50), fill=INK, width=4)
    w, _ = text_size(label, NAME)
    draw.text((cx - w / 2, body_bottom + 62), label, font=NAME, fill=INK)
    dashed((cx, body_bottom + 112), (cx, LIFE_BOTTOM), width=2, fill=LIFELINE)


def object_box(cx: int, class_name: str, stereotype: str, box_w: int) -> None:
    x0 = cx - box_w // 2
    x1 = cx + box_w // 2
    draw.rectangle((x0, TOP, x1, TOP + BOX_H), fill=WHITE, outline=INK, width=3)
    sw, _ = text_size(stereotype, SMALL)
    draw.text((cx - sw / 2, TOP + 14), stereotype, font=SMALL, fill=INK)

    name = f":{class_name}"
    nw, nh = text_size(name, NAME)
    nx = cx - nw / 2
    ny = TOP + 55
    draw.text((nx, ny), name, font=NAME, fill=INK)
    draw.line((nx, ny + nh + 5, nx + nw, ny + nh + 5), fill=INK, width=2)
    dashed((cx, LIFE_TOP), (cx, LIFE_BOTTOM), width=2, fill=LIFELINE)


def activation(cx: int, y0: int, y1: int, *, offset: int = 0, w: int = 18) -> None:
    draw.rectangle((cx - w // 2 + offset, y0, cx + w // 2 + offset, y1), fill=ACTIVE, outline=INK, width=2)


def open_arrow(start: tuple[int, int], end: tuple[int, int], *, is_return: bool = False) -> None:
    if is_return:
        dashed(start, end, width=3, fill=INK, dash=14, gap=9)
    else:
        draw.line((start, end), fill=INK, width=3)

    x1, y1 = start
    x2, y2 = end
    angle = math.atan2(y2 - y1, x2 - x1)
    al = 18
    aw = 8
    p1 = (
        x2 - al * math.cos(angle) + aw * math.sin(angle),
        y2 - al * math.sin(angle) - aw * math.cos(angle),
    )
    p2 = (
        x2 - al * math.cos(angle) - aw * math.sin(angle),
        y2 - al * math.sin(angle) + aw * math.cos(angle),
    )
    draw.line((x2, y2, p1[0], p1[1]), fill=INK, width=3)
    draw.line((x2, y2, p2[0], p2[1]), fill=INK, width=3)


def label(cx: float, cy: float, text: str, *, max_width: int = 520) -> None:
    lines = wrap_text(text, TEXT, max_width)
    widths = [text_size(line or "A", TEXT)[0] for line in lines]
    heights = [text_size(line or "A", TEXT)[1] for line in lines]
    bw = max(widths) + 20
    bh = sum(heights) + max(0, len(lines) - 1) * 5 + 12
    draw.rectangle((cx - bw / 2, cy - bh / 2, cx + bw / 2, cy + bh / 2), fill=WHITE)
    y = cy - bh / 2 + 6
    for line, width, height in zip(lines, widths, heights):
        draw.text((cx - width / 2, y), line, font=TEXT, fill=INK)
        y += height + 5


def message(
    frm: int,
    to: int,
    y: int,
    text: str,
    *,
    cy: Optional[int] = None,
    dx: int = 0,
    max_width: int = 520,
    is_return: bool = False,
) -> None:
    open_arrow((frm, y), (to, y), is_return=is_return)
    if cy is None:
        cy = y - 36 if not is_return else y + 36
    label((frm + to) / 2 + dx, cy, text, max_width=max_width)


def self_message(cx: int, y: int, text: str, *, box_width: int = 132) -> None:
    x2 = cx + box_width
    draw.line((cx, y, x2, y), fill=INK, width=3)
    draw.line((x2, y, x2, y + 58), fill=INK, width=3)
    open_arrow((x2, y + 58), (cx + 10, y + 58), is_return=False)
    label(cx + box_width / 2 + 72, y - 25, text, max_width=410)


def build() -> None:
    OUT.parent.mkdir(parents=True, exist_ok=True)
    PUML.parent.mkdir(parents=True, exist_ok=True)

    title = "气象预警发布顺序图"
    tw, _ = text_size(title, TITLE)
    draw.text(((WIDTH - tw) / 2, 28), title, font=TITLE, fill=INK)

    xs = {
        "source": 130,
        "page": 440,
        "service": 820,
        "repo": 1225,
        "filter": 1660,
        "wechat": 2120,
        "mini": 2580,
        "warning_page": 3020,
    }

    draw_actor(xs["source"], 92, "预警来源")
    object_box(xs["page"], "AdminPage", "<<boundary>>", 300)
    object_box(xs["service"], "WarningNoticeService", "<<control>>", 370)
    object_box(xs["repo"], "WarningNoticeRepository", "<<entity>>", 390)
    object_box(xs["filter"], "SubscriptionFilterService", "<<control>>", 430)
    object_box(xs["wechat"], "WechatSubscriptionApi", "<<utility>>", 400)
    object_box(xs["mini"], "MiniProgramPage", "<<boundary>>", 330)
    object_box(xs["warning_page"], "WarningCenterPage", "<<boundary>>", 380)

    activation(xs["page"], 360, 2028)
    activation(xs["service"], 520, 1970)
    activation(xs["service"], 665, 795, offset=14)
    activation(xs["repo"], 875, 1025)
    activation(xs["filter"], 1160, 1320)
    activation(xs["wechat"], 1450, 1740)
    activation(xs["mini"], 1620, 1720)
    activation(xs["warning_page"], 1850, 1960)

    message(
        xs["source"],
        xs["page"],
        380,
        "submitWarningInfo(warningData)",
        cy=336,
        max_width=360,
    )
    message(
        xs["page"],
        xs["service"],
        540,
        "createWarningNotice(warningData)",
        cy=498,
        max_width=420,
    )
    self_message(xs["service"], 680, "validatedWarning := validateWarning(warningData)")
    message(
        xs["service"],
        xs["repo"],
        900,
        "savedWarning := saveWarningNotice(validatedWarning)",
        cy=852,
        max_width=640,
    )
    message(
        xs["repo"],
        xs["service"],
        1045,
        "savedWarning",
        cy=1085,
        max_width=180,
        is_return=True,
    )
    message(
        xs["service"],
        xs["filter"],
        1185,
        "subscribedUsers := filterSubscribers(impactArea, severity)",
        cy=1138,
        max_width=720,
    )
    message(
        xs["filter"],
        xs["service"],
        1345,
        "subscribedUsers",
        cy=1385,
        max_width=220,
        is_return=True,
    )
    message(
        xs["service"],
        xs["wechat"],
        1480,
        "pushResult := sendSubscriptionMessage(subscribedUsers, savedWarning)",
        cy=1432,
        dx=70,
        max_width=760,
    )
    message(
        xs["wechat"],
        xs["mini"],
        1645,
        "deliverWarningNotice(savedWarning)",
        cy=1600,
        max_width=420,
    )
    message(
        xs["mini"],
        xs["wechat"],
        1755,
        "deliveryStatus",
        cy=1795,
        max_width=210,
        is_return=True,
    )
    message(
        xs["service"],
        xs["warning_page"],
        1875,
        "syncActiveWarnings(savedWarning)",
        cy=1830,
        dx=90,
        max_width=430,
    )
    message(
        xs["warning_page"],
        xs["service"],
        1990,
        "activeWarningView",
        cy=2030,
        max_width=260,
        is_return=True,
    )
    message(
        xs["service"],
        xs["page"],
        2070,
        "publishResult",
        cy=2110,
        max_width=210,
        is_return=True,
    )

    img.save(OUT)
    PUML.write_text(
        """@startuml
title 气象预警发布顺序图
actor "预警来源" as Source
boundary ":AdminPage" as Page
control ":WarningNoticeService" as Service
entity ":WarningNoticeRepository" as Repo
control ":SubscriptionFilterService" as Filter
participant ":WechatSubscriptionApi" <<utility>> as Wechat
boundary ":MiniProgramPage" as Mini
boundary ":WarningCenterPage" as WarningPage

Source -> Page : submitWarningInfo(warningData)
activate Page
Page -> Service : createWarningNotice(warningData)
activate Service
Service -> Service : validatedWarning := validateWarning(warningData)
Service -> Repo : savedWarning := saveActiveWarning(validatedWarning)
activate Repo
Repo --> Service : savedWarning
deactivate Repo
Service -> Filter : subscribedUsers := filterSubscribedUsers(impactArea, severity)
activate Filter
Filter --> Service : subscribedUsers
deactivate Filter
Service -> Wechat : pushResult := sendWarningMessage(subscribedUsers, savedWarning)
activate Wechat
Wechat -> Mini : deliverWarningNotice(savedWarning)
activate Mini
Mini --> Wechat : deliveryStatus
deactivate Mini
Service -> WarningPage : syncActiveWarnings(savedWarning)
activate WarningPage
WarningPage --> Service : activeWarningView
deactivate WarningPage
Service --> Page : publishResult
deactivate Service
deactivate Page
@enduml
""",
        encoding="utf-8",
    )


if __name__ == "__main__":
    build()
    print(OUT)
