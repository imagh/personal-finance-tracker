"""Tests for import_symbols.py: SVG validation (untrusted input) and the vector-drawable output."""
import sys
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


if __name__ == "__main__":
    unittest.main()
