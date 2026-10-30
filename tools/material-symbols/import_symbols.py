#!/usr/bin/env python3
"""Imports the curated Material Symbols Rounded icons as Android vector drawables.

Stdlib only. Source: google/material-design-icons (Apache-2.0), pinned to COMMIT, default axes
(weight 400, grade 0, optical size 24, FILL 0). Guide: docs/architecture/design-system.md
("Adding an icon").

    python3 -I tools/material-symbols/import_symbols.py \\
        --download-dir <NEW EMPTY DIR> --res-dir app/src/main/res/drawable

The committed svg-sha256.txt is a check, not just a record: a downloaded file whose hash differs
from its committed one aborts the run before anything is written (exit 1), a key not yet listed
has its hash recorded, and a changed hash is only accepted with --update-hashes.

Every downloaded SVG is untrusted data. It is accepted only if it is exactly one <svg> holding
one <path>, with the expected viewBox and a path-data string of path-command characters. Only
that path data is kept; nothing else from the file reaches the drawable. Output files are
`sym_<key>.xml`; the key is the permanent one in app/src/test/resources/pft-symbol-keys.txt.
"""
import argparse
import hashlib
import re
import sys
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path

COMMIT = "10ecc14790612ca8fd164e3f4ffe9c16fc50d5b7"
BASE = f"https://raw.githubusercontent.com/google/material-design-icons/{COMMIT}/symbols/web"
MAX_SVG_BYTES = 16 * 1024
KEY_RE = re.compile(r"[a-z0-9_]{1,40}")
NAME_RE = re.compile(r"[a-z0-9_]{1,60}")
PATH_DATA_RE = re.compile(r"[MmLlHhVvCcSsQqTtAaZz0-9eE.,\s-]+")
SVG_NS = "{http://www.w3.org/2000/svg}"
VIEW_BOX = "0 -960 960 960"


class UnsafeSvg(Exception):
    """The downloaded file is not the plain one-path icon we accept."""


def parse_svg(data: bytes) -> str:
    """Returns the single path's `d` string, or raises UnsafeSvg."""
    if len(data) > MAX_SVG_BYTES:
        raise UnsafeSvg("file too large")
    lowered = data.lower()
    if b"<!doctype" in lowered or b"<!entity" in lowered:
        raise UnsafeSvg("DOCTYPE or entity declarations are not accepted")
    try:
        root = ET.fromstring(data)
    except ET.ParseError as e:
        raise UnsafeSvg(f"malformed XML: {e}") from e
    if root.tag != f"{SVG_NS}svg" or root.get("viewBox") != VIEW_BOX:
        raise UnsafeSvg("not the expected <svg viewBox='0 -960 960 960'>")
    children = list(root)
    if len(children) != 1 or children[0].tag != f"{SVG_NS}path" or len(children[0]) != 0:
        raise UnsafeSvg("expected exactly one <path> and nothing else")
    d = children[0].get("d", "")
    if not d or not PATH_DATA_RE.fullmatch(d):
        raise UnsafeSvg("path data has unexpected characters")
    return d


def to_vector_drawable(path_data: str) -> str:
    """The SVG's y axis runs from -960 to 0, so a group shifts it down by 960 into 0..960."""
    return (
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
        '    android:width="24dp"\n'
        '    android:height="24dp"\n'
        '    android:viewportWidth="960"\n'
        '    android:viewportHeight="960">\n'
        '    <group android:translateY="960">\n'
        "        <path\n"
        '            android:fillColor="#FF000000"\n'
        f'            android:pathData="{path_data}" />\n'
        "    </group>\n"
        "</vector>\n"
    )


def parse_manifest(text: str) -> list[tuple[str, str]]:
    """`<key> <material symbol name>` per line; `#` starts a comment. Keys are permanent."""
    entries: list[tuple[str, str]] = []
    seen: set[str] = set()
    for raw in text.splitlines():
        line = raw.split("#", 1)[0].strip()
        if not line:
            continue
        parts = line.split()
        if len(parts) != 2:
            raise ValueError(f"expected '<key> <name>': {raw!r}")
        key, name = parts
        if not KEY_RE.fullmatch(key):
            raise ValueError(f"key {key!r} does not match [a-z0-9_]{{1,40}}")
        if not NAME_RE.fullmatch(name):
            raise ValueError(f"symbol name {name!r} is not a plain identifier")
        if key in seen:
            raise ValueError(f"duplicate key {key!r}")
        seen.add(key)
        entries.append((key, name))
    return entries


def download(name: str) -> bytes:
    url = f"{BASE}/{name}/materialsymbolsrounded/{name}_24px.svg"
    try:
        with urllib.request.urlopen(url, timeout=30) as response:  # noqa: S310 (pinned https URL)
            return response.read(MAX_SVG_BYTES + 1)
    except OSError as e:
        raise SystemExit(f"could not download {name!r} from {url}: {e}") from e


HASH_LINE_RE = re.compile(r"([0-9a-f]{64})  ([a-z0-9_]{1,40})  ([a-z0-9_]{1,60})")


def parse_hashes(text: str) -> dict[str, str]:
    """`<sha256>  <key>  <name>` per line (the committed svg-sha256.txt), as key -> sha256."""
    hashes: dict[str, str] = {}
    for line in text.splitlines():
        match = HASH_LINE_RE.fullmatch(line)
        if not match or match[2] in hashes:
            raise ValueError(f"bad or duplicate line in the hashes file: {line!r}")
        hashes[match[2]] = match[1]
    return hashes


def main(argv: list[str] | None = None, fetch=download) -> int:
    """Returns 0 on success, 1 if a source file is unsafe or does not match its committed hash
    (nothing is written), 2 for a bad invocation. Every file is fetched, validated and hash-checked
    before the first drawable is written."""
    here = Path(__file__).resolve().parent
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--download-dir", type=Path, required=True, help="a new, empty directory for the raw SVGs")
    parser.add_argument("--res-dir", type=Path, required=True, help="app/src/main/res/drawable")
    parser.add_argument("--manifest", type=Path, default=here / "symbols.txt")
    parser.add_argument("--hashes", type=Path, default=here / "svg-sha256.txt", help="the committed source hashes")
    parser.add_argument(
        "--update-hashes",
        action="store_true",
        help="accept a changed source file and overwrite its committed hash (review the diff!)",
    )
    args = parser.parse_args(argv)

    if args.download_dir.exists() and any(args.download_dir.iterdir()):
        print("--download-dir must be empty", file=sys.stderr)
        return 2
    entries = parse_manifest(args.manifest.read_text(encoding="utf-8"))
    try:
        committed = parse_hashes(args.hashes.read_text(encoding="utf-8")) if args.hashes.exists() else {}
    except ValueError as e:
        print(e, file=sys.stderr)
        return 1

    fetched: list[tuple[str, str, bytes, str, str]] = []  # key, name, svg bytes, sha256, drawable xml
    problems: list[str] = []
    for key, name in entries:
        data = fetch(name)
        try:
            xml = to_vector_drawable(parse_svg(data))
        except UnsafeSvg as e:
            problems.append(f"{key} ({name}): unsafe SVG, {e}")
            continue
        digest = hashlib.sha256(data).hexdigest()
        if key in committed and committed[key] != digest and not args.update_hashes:
            problems.append(f"{key} ({name}): source changed, committed {committed[key]} but got {digest}")
        fetched.append((key, name, data, digest, xml))
    if problems:
        print("nothing written:\n  " + "\n  ".join(problems), file=sys.stderr)
        print("If the change is intended, review it and re-run with --update-hashes.", file=sys.stderr)
        return 1

    args.download_dir.mkdir(parents=True, exist_ok=True)
    for key, name, data, _, xml in fetched:
        (args.download_dir / f"{name}.svg").write_bytes(data)
        (args.res_dir / f"sym_{key}.xml").write_text(xml, encoding="utf-8")
    lines = [f"{digest}  {key}  {name}" for key, name, _, digest, _ in fetched]
    args.hashes.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"wrote {len(fetched)} drawables from commit {COMMIT}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
