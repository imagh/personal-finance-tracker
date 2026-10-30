"""Tests for import_symbols.py: SVG validation (untrusted input) and the vector-drawable output."""
import contextlib
import hashlib
import io
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import import_symbols as imp  # noqa: E402

HOME_D = "M240-200h120v-200q0-17 11.5-28.5T400-440h160q17 0 28.5 11.5T600-400v200h120v-360L480-740Zm320-270Z"


def svg(d=HOME_D, extra="", attrs='height="24" viewBox="0 -960 960 960" width="24"'):
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" {attrs}>{extra}<path d="{d}"/></svg>'
    ).encode()


class ParseSvgTest(unittest.TestCase):
    def test_returns_the_path_data_verbatim(self):
        self.assertEqual(HOME_D, imp.parse_svg(svg()))

    def test_rejects_a_script_element(self):
        with self.assertRaises(imp.UnsafeSvg):
            imp.parse_svg(svg(extra="<script>alert(1)</script>"))

    def test_rejects_an_external_reference(self):
        with self.assertRaises(imp.UnsafeSvg):
            imp.parse_svg(svg(extra='<image href="http://evil.example/x.png"/>'))

    def test_rejects_a_second_path(self):
        with self.assertRaises(imp.UnsafeSvg):
            imp.parse_svg(svg(extra='<path d="M0 0h1Z"/>'))

    def test_rejects_a_doctype_or_entity(self):
        bomb = b'<?xml version="1.0"?><!DOCTYPE svg [<!ENTITY a "x">]>' + svg()
        with self.assertRaises(imp.UnsafeSvg):
            imp.parse_svg(bomb)

    def test_rejects_path_data_with_unexpected_characters(self):
        with self.assertRaises(imp.UnsafeSvg):
            imp.parse_svg(svg(d="M0 0 url(http://evil.example)"))

    def test_rejects_an_unexpected_view_box(self):
        with self.assertRaises(imp.UnsafeSvg):
            imp.parse_svg(svg(attrs='height="24" viewBox="0 0 24 24" width="24"'))

    def test_rejects_oversized_input(self):
        with self.assertRaises(imp.UnsafeSvg):
            imp.parse_svg(svg(d="M0 0" + " h1" * 20000))

    def test_rejects_malformed_xml(self):
        with self.assertRaises(imp.UnsafeSvg):
            imp.parse_svg(b"<svg><path d='M0 0'")


class DrawableTest(unittest.TestCase):
    def test_wraps_the_path_in_a_group_that_undoes_the_negative_viewbox_origin(self):
        xml = imp.to_vector_drawable(HOME_D)
        self.assertIn('android:viewportWidth="960"', xml)
        self.assertIn('android:viewportHeight="960"', xml)
        self.assertIn('android:translateY="960"', xml)
        self.assertIn(f'android:pathData="{HOME_D}"', xml)
        self.assertIn('android:fillColor="#FF000000"', xml)
        self.assertTrue(xml.endswith("</vector>\n"))


class ManifestTest(unittest.TestCase):
    def test_parses_keys_and_names_and_ignores_comments(self):
        text = "# header\n\nhome home\nback arrow_back  # chrome\n"
        self.assertEqual([("home", "home"), ("back", "arrow_back")], imp.parse_manifest(text))

    def test_rejects_a_key_outside_the_permanent_key_grammar(self):
        with self.assertRaises(ValueError):
            imp.parse_manifest("Home home\n")

    def test_rejects_a_duplicate_key(self):
        with self.assertRaises(ValueError):
            imp.parse_manifest("home home\nhome house\n")

    def test_rejects_a_symbol_name_that_is_not_a_plain_identifier(self):
        with self.assertRaises(ValueError):
            imp.parse_manifest("home ../home\n")


class ImportRunTest(unittest.TestCase):
    """`main` against a fake downloader: committed hashes are a real check, and nothing is half-written."""

    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        root = Path(self.tmp.name)
        self.download_dir = root / "download"
        self.res_dir = root / "res"
        self.res_dir.mkdir()
        self.manifest = root / "symbols.txt"
        self.manifest.write_text("home home\nback arrow_back\n")
        self.hashes = root / "svg-sha256.txt"
        self.svgs = {"home": svg(), "arrow_back": svg(d="M0-100h100Z")}

    def sha(self, name):
        return hashlib.sha256(self.svgs[name]).hexdigest()

    def run_main(self, *extra):
        argv = [
            "--download-dir", str(self.download_dir),
            "--res-dir", str(self.res_dir),
            "--manifest", str(self.manifest),
            "--hashes", str(self.hashes),
            *extra,
        ]
        err = io.StringIO()
        with contextlib.redirect_stderr(err), contextlib.redirect_stdout(io.StringIO()):
            code = imp.main(argv, fetch=lambda name: self.svgs[name])
        return code, err.getvalue()

    def written(self):
        return sorted(p.name for p in self.res_dir.iterdir())

    def test_a_first_import_records_every_hash_and_writes_the_drawables(self):
        code, _ = self.run_main()
        self.assertEqual(0, code)
        self.assertEqual(["sym_back.xml", "sym_home.xml"], self.written())
        self.assertEqual(
            f"{self.sha('home')}  home  home\n{self.sha('arrow_back')}  back  arrow_back\n",
            self.hashes.read_text(),
        )

    def test_a_matching_committed_hash_passes_and_leaves_the_file_as_it_was(self):
        self.run_main()
        before = self.hashes.read_text()
        self.download_dir = Path(self.tmp.name) / "download2"
        code, _ = self.run_main()
        self.assertEqual(0, code)
        self.assertEqual(before, self.hashes.read_text())

    def test_a_mismatch_for_a_listed_key_fails_clearly_and_writes_nothing(self):
        self.hashes.write_text(f"{'0' * 64}  home  home\n")
        code, err = self.run_main()
        self.assertEqual(1, code)
        self.assertIn("home", err)
        self.assertIn("--update-hashes", err)
        self.assertEqual([], self.written())
        self.assertEqual(f"{'0' * 64}  home  home\n", self.hashes.read_text())
        self.assertFalse(self.download_dir.exists() and any(self.download_dir.iterdir()))

    def test_a_mismatch_on_a_later_key_still_writes_no_drawable_for_an_earlier_one(self):
        self.hashes.write_text(f"{self.sha('home')}  home  home\n{'1' * 64}  back  arrow_back\n")
        code, _ = self.run_main()
        self.assertEqual(1, code)
        self.assertEqual([], self.written())

    def test_a_new_key_is_recorded_while_listed_keys_are_kept(self):
        self.hashes.write_text(f"{self.sha('home')}  home  home\n")
        code, _ = self.run_main()
        self.assertEqual(0, code)
        self.assertEqual(["sym_back.xml", "sym_home.xml"], self.written())
        self.assertIn(f"{self.sha('arrow_back')}  back  arrow_back", self.hashes.read_text())

    def test_update_hashes_overwrites_a_mismatching_hash(self):
        self.hashes.write_text(f"{'0' * 64}  home  home\n")
        code, _ = self.run_main("--update-hashes")
        self.assertEqual(0, code)
        self.assertIn(f"{self.sha('home')}  home  home", self.hashes.read_text())
        self.assertEqual(["sym_back.xml", "sym_home.xml"], self.written())

    def test_an_unsafe_svg_aborts_before_anything_is_written(self):
        self.svgs["arrow_back"] = svg(extra="<script>alert(1)</script>")
        code, err = self.run_main()
        self.assertEqual(1, code)
        self.assertIn("arrow_back", err)
        self.assertEqual([], self.written())
        self.assertFalse(self.hashes.exists())

    def test_a_malformed_hashes_file_is_refused(self):
        self.hashes.write_text("not a hash line\n")
        code, _ = self.run_main()
        self.assertEqual(1, code)
        self.assertEqual([], self.written())

    def test_a_non_empty_download_dir_is_refused(self):
        self.download_dir.mkdir()
        (self.download_dir / "x").write_text("x")
        code, _ = self.run_main()
        self.assertEqual(2, code)
        self.assertEqual([], self.written())


if __name__ == "__main__":
    unittest.main()
