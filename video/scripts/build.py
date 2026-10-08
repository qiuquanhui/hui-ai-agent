# -*- coding: utf-8 -*-
"""STUDY.md 阶段讲解视频生成器。

流水线：内容(content.py) → PIL 渲染幻灯片 PNG → edge-tts 语音旁白 MP3
        → ffmpeg 合成每页片段 → concat 成单个阶段 MP4。

用法：
    python build.py --all            # 生成全部 12 个阶段
    python build.py --stage 0        # 只生成阶段 0
    python build.py --stage 3 --force  # 强制重新生成（忽略已有产物）
产物：video/stage-{NN}-{slug}.mp4；中间文件在 video/.work/（可整目录删除）。
"""
import argparse
import subprocess
import sys
import time
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont

sys.path.insert(0, str(Path(__file__).parent))
from content import STAGES  # noqa: E402

ROOT = Path(__file__).resolve().parents[2]   # 项目根
VIDEO = ROOT / "video"
WORK = VIDEO / ".work"

# ---------------- 视觉 ----------------
W, H = 1280, 720
BG = (11, 18, 32)
PANEL = (30, 41, 59)
ACCENT = (56, 189, 248)
GREEN = (52, 211, 153)
TITLE_C = (248, 250, 252)
TEXT_C = (226, 232, 240)
MUTED = (148, 163, 184)
FAINT = (71, 85, 105)
DARK_NUM = (20, 32, 54)

FONT_DIR = Path("C:/Windows/Fonts")
F_REG = str(FONT_DIR / "msyh.ttc")
F_BOLD = str(FONT_DIR / "msyhbd.ttc")
MARGIN = 88
CONTENT_W = W - 2 * MARGIN

VOICE = "zh-CN-YunxiNeural"
RATE = "+8%"
TAIL_PAD = 0.7          # 每页结尾留白秒数
FPS = 25


def font(size, bold=False):
    return ImageFont.truetype(F_BOLD if bold else F_REG, size)


def tokenize(text):
    """把文本拆成可换行的单元：ASCII 单词保持完整，其他按单字。"""
    units, buf = [], ""
    for ch in text:
        if ch.isascii() and (ch.isalnum() or ch in "./_-#"):
            buf += ch
        else:
            if buf:
                units.append(buf)
                buf = ""
            units.append(ch)
    if buf:
        units.append(buf)
    return units


def wrap(draw, text, f, max_w):
    lines, line = [], ""
    for u in tokenize(text):
        cand = line + u
        if draw.textlength(cand, font=f) <= max_w or not line:
            line = cand
        else:
            lines.append(line)
            line = u
    if line:
        lines.append(line)
    return lines


def base_canvas():
    img = Image.new("RGB", (W, H), BG)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, 6, H], fill=ACCENT)            # 左侧竖条
    d.rectangle([0, H - 4, W, H], fill=PANEL)         # 底部细线
    return img, d


def draw_footer(d, stage, idx, total):
    f = font(18)
    left = f"STUDY.md 教学 · 阶段 {stage['num']} {stage['title']}"
    d.text((MARGIN, H - 40), left, font=f, fill=FAINT)
    right = f"{idx + 1} / {total}"
    w = d.textlength(right, font=f)
    d.text((W - MARGIN - w, H - 40), right, font=f, fill=MUTED)


def draw_header(d):
    f = font(20, bold=True)
    d.text((MARGIN, 44), "Hui AI Agent · 从 0 到 1", font=f, fill=MUTED)
    tag = "STUDY 教学"
    w = d.textlength(tag, font=f)
    d.text((W - MARGIN - w, 44), tag, font=f, fill=ACCENT)


def chip_w(d, text, f):
    tw = d.textlength(text, font=f)
    return tw + 36, 34


def draw_chip(d, x, y, text, fg, bg):
    f = font(20)
    cw, ch = chip_w(d, text, f)
    d.rounded_rectangle([x, y, x + cw, y + ch], radius=8, fill=bg, outline=fg, width=1)
    d.text((x + 18, y + 5), text, font=f, fill=fg)
    return cw


def commits_text(commits):
    if not commits:
        return None
    if len(commits) == 1:
        return f"commit {commits[0]}"
    return f"commit {commits[0]} … {commits[-1]}（共 {len(commits)} 个）"


def render_title_slide(stage, _slide, idx, total):
    img, d = base_canvas()
    # 背景大数字
    big = font(300, bold=True)
    num_text = f"{stage['num']:02d}"
    d.text((W - 500, 150), num_text, font=big, fill=DARK_NUM)
    # 顶部小字
    f_kicker = font(26, bold=True)
    kicker = f"STUDY.md 教学 · 阶段 {stage['num']} / 11"
    kw = d.textlength(kicker, font=f_kicker)
    d.text(((W - kw) / 2, 218), kicker, font=f_kicker, fill=ACCENT)
    # 主标题
    f_title = font(58, bold=True)
    lines = wrap(d, stage["title"], f_title, CONTENT_W)
    y = 278
    for ln in lines:
        lw = d.textlength(ln, font=f_title)
        d.text(((W - lw) / 2, y), ln, font=f_title, fill=TITLE_C)
        y += 74
    # 副标题
    f_sub = font(28)
    sw = d.textlength(stage["subtitle"], font=f_sub)
    d.text(((W - sw) / 2, y + 14), stage["subtitle"], font=f_sub, fill=(178, 190, 210))
    # 提交 chip
    ctext = commits_text(stage["commits"])
    if ctext:
        cw, _ = chip_w(d, ctext, font(20))
        draw_chip(d, (W - cw) / 2, y + 84, ctext, GREEN, PANEL)
    draw_footer(d, stage, idx, total)
    return img


def render_content_slide(stage, slide, idx, total):
    img, d = base_canvas()
    draw_header(d)
    # 标题（最多两行）
    f_title = font(42, bold=True)
    lines = wrap(d, slide["heading"], f_title, CONTENT_W)[:2]
    y = 108
    for ln in lines:
        d.text((MARGIN, y), ln, font=f_title, fill=TITLE_C)
        y += 56
    d.rectangle([MARGIN, y + 10, MARGIN + 110, y + 15], fill=ACCENT)
    y += 33
    # 提交 chip
    ctext = commits_text(stage["commits"])
    if ctext:
        draw_chip(d, MARGIN, y, ctext, GREEN, PANEL)
        y += 48
    # 要点
    f_main = font(28)
    f_sub = font(23)
    y += 14
    bottom_limit = H - 70
    for raw in slide["bullets"]:
        sub = raw.startswith("|")
        text = raw[1:].strip() if sub else raw
        if sub:
            lines = wrap(d, text, f_sub, CONTENT_W - 64)
            d.text((MARGIN + 30, y), "—", font=f_sub, fill=MUTED)
            x_text, f_use, lh = MARGIN + 64, f_sub, 34
            color = MUTED
        else:
            lines = wrap(d, text, f_main, CONTENT_W - 34)
            d.ellipse([MARGIN + 2, y + 11, MARGIN + 14, y + 23], fill=ACCENT)
            x_text, f_use, lh = MARGIN + 34, f_main, 42
            color = TEXT_C
        for ln in lines:
            if y > bottom_limit:
                break
            d.text((x_text, y), ln, font=f_use, fill=color)
            y += lh
        y += 14
    draw_footer(d, stage, idx, total)
    return img


# ---------------- 音频 / 视频 ----------------
def tts(text, out_mp3):
    if out_mp3.exists():
        out_mp3.unlink()          # 清理上次失败可能留下的空文件
    for attempt in range(1, 5):
        try:
            r = subprocess.run(
                [sys.executable, "-m", "edge_tts", "--voice", VOICE,
                 "--rate", RATE, "--text", text, "--write-media", str(out_mp3)],
                capture_output=True, text=True, timeout=120)
        except subprocess.TimeoutExpired:
            r = None
        if (r is not None and r.returncode == 0
                and out_mp3.exists() and out_mp3.stat().st_size > 1000):
            return
        err = r.stderr if r else "timeout"
        print(f"    tts retry {attempt}: {err.strip()[:120]}", flush=True)
        time.sleep(2 * attempt)
    raise RuntimeError(f"TTS failed: {out_mp3}")


def probe_duration(path):
    r = subprocess.run(
        ["ffprobe", "-v", "error", "-show_entries", "format=duration",
         "-of", "csv=p=0", str(path)],
        capture_output=True, text=True, check=True)
    return float(r.stdout.strip())


def make_segment(png, mp3, seg):
    dur = probe_duration(mp3) + TAIL_PAD
    subprocess.run(
        ["ffmpeg", "-y", "-loop", "1", "-framerate", str(FPS),
         "-i", str(png), "-i", str(mp3),
         "-vf", "scale=1280:720,format=yuv420p",
         "-af", f"apad=pad_dur={TAIL_PAD}",
         "-ar", "44100", "-ac", "2",
         "-c:v", "libx264", "-tune", "stillimage", "-crf", "22",
         "-preset", "veryfast", "-r", str(FPS),
         "-c:a", "aac", "-b:a", "128k",
         "-t", f"{dur:.3f}", str(seg)],
        capture_output=True, text=True, check=True)


def build_stage(stage, force=False):
    num = stage["num"]
    sdir = WORK / f"stage-{num:02d}"
    sdir.mkdir(parents=True, exist_ok=True)
    out = VIDEO / f"stage-{num:02d}-{stage['slug']}.mp4"
    if out.exists() and not force:
        print(f"[stage {num:02d}] 已存在，跳过（--force 可重建）: {out.name}")
        return out

    slides = stage["slides"]
    n = len(slides)
    print(f"[stage {num:02d}] {stage['title']} —— {n} 页")
    segs = []
    for i, sl in enumerate(slides):
        png = sdir / f"slide-{i:02d}.png"
        mp3 = sdir / f"narr-{i:02d}.mp3"
        seg = sdir / f"seg-{i:02d}.mp4"

        if not png.exists() or force:
            img = (render_title_slide if sl.get("kind") == "title"
                   else render_content_slide)(stage, sl, i, n)
            img.save(png)

        if not mp3.exists() or mp3.stat().st_size < 1000 or force:
            tts(sl["narration"], mp3)

        if not seg.exists() or seg.stat().st_size < 1000 or force:
            make_segment(png, mp3, seg)
        segs.append(seg)
        print(f"    页 {i + 1}/{n} 完成（{probe_duration(seg):.1f}s）")

    lst = sdir / "list.txt"
    lst.write_text("\n".join(f"file '{s.name}'" for s in segs), encoding="utf-8")
    subprocess.run(
        ["ffmpeg", "-y", "-f", "concat", "-safe", "0", "-i", str(lst),
         "-c", "copy", "-movflags", "+faststart", str(out)],
        capture_output=True, text=True, check=True, cwd=sdir)
    print(f"    ✔ {out.name}  总时长 {probe_duration(out):.0f}s")
    return out


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--stage", type=int, help="只生成某个阶段（0-11）")
    ap.add_argument("--all", action="store_true", help="生成全部阶段")
    ap.add_argument("--force", action="store_true", help="忽略缓存重新生成")
    args = ap.parse_args()

    VIDEO.mkdir(exist_ok=True)
    WORK.mkdir(exist_ok=True)

    if args.all or args.stage is None:
        targets = STAGES
    else:
        targets = [s for s in STAGES if s["num"] == args.stage]
        if not targets:
            sys.exit(f"没有阶段 {args.stage}（可选 0-11）")

    for s in targets:
        build_stage(s, force=args.force)


if __name__ == "__main__":
    main()
