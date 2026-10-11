#!/usr/bin/env python3
"""Verify the editable SVG masters and native path constants match the versioned crosswalk."""
import ast
import hashlib
import json
import re
from pathlib import Path
from xml.etree import ElementTree

import jsonschema

root = Path(__file__).resolve().parents[1]
def read_json(path):
    return json.loads((root / path).read_text())

manifest = read_json('assets/anatomy/regions.v1.json')
jsonschema.Draft202012Validator(read_json('docs/anatomy-render-contract.schema.json')).validate(manifest)
asset_manifest = read_json('assets/anatomy/asset-manifest.json')
jsonschema.Draft202012Validator(read_json('docs/asset-manifest.schema.json')).validate(asset_manifest)
taxonomy = read_json('docs/muscle-taxonomy.v1.json')
subdivision_ids = {sub['id'] for group in taxonomy['groups'] for sub in group['subdivisions']}
assert len(subdivision_ids) == 52, 'Canonical taxonomy changed; review the crosswalk'

svg_paths = {}
for view, x in (('front', 0), ('back', 37)):
    master = root / f'design/anatomy/{view}.svg'
    svg = ElementTree.parse(master).getroot()
    assert svg.attrib['viewBox'] == f'{x} 0 35 93', f'Unexpected {view} viewBox'
    assert manifest['views'][view]['view_box'] == [x, 0, 35, 93], f'Wrong {view} manifest viewBox'
    assert manifest['views'][view]['source_svg'] == f'design/anatomy/{view}.svg'
    paths = [(node.attrib['id'], node.attrib['d']) for node in svg if node.tag.endswith('path')]
    assert paths and len(paths) == len({path_id for path_id, _ in paths}), f'Duplicate/empty {view} path IDs'
    svg_paths[view] = paths

runtime_file = root / 'feature/anatomy/src/main/java/com/gymrpg/feature/anatomy/AnatomyPaths.kt'
rows = re.findall(r'^\s*AnatomyRegion\(("(?:[^"\\]|\\.)*"), ("(?:[^"\\]|\\.)*"), ("(?:[^"\\]|\\.)*"), ("(?:[^"\\]|\\.)*"), (null|"(?:[^"\\]|\\.)*"), ("(?:[^"\\]|\\.)*")\),\s*$', runtime_file.read_text(), re.M)
assert sum(len(paths) for paths in svg_paths.values()) == len(rows), 'Native and SVG path counts differ'
actual_paths = {'front': [], 'back': []}
for raw_view, raw_id, _, raw_path, raw_subdivision, raw_precision in rows:
    view, path_id, path_data, precision = (ast.literal_eval(v) for v in (raw_view, raw_id, raw_path, raw_precision))
    subdivision = None if raw_subdivision == 'null' else ast.literal_eval(raw_subdivision)
    actual_paths[view].append((path_id, path_data))
    assert precision in ('exact_visual', 'merged_visual', 'approximation')
    assert subdivision is None or subdivision in subdivision_ids
assert actual_paths == svg_paths, 'Runtime native paths diverged from editable SVG source (including z-order)'

rendered = set()
for region in manifest['regions']:
    path_id = region['path_ids'][0]
    view = region['view']
    subdivision = region['subdivision_ids'][0]
    rendered.add(subdivision)
    assert any(ast.literal_eval(row_view) == view and ast.literal_eval(raw_id) == path_id and (raw_subdivision != 'null' and ast.literal_eval(raw_subdivision) == subdivision) for row_view, raw_id, _, _, raw_subdivision, _ in rows if ast.literal_eval(row_view) == view), f'Unmatched crosswalk {view}/{path_id}'
assert set(manifest['unrendered_subdivision_ids']) == subdivision_ids - rendered, 'Unrendered taxonomy IDs are stale'
assert len(rendered) == 33 and len(subdivision_ids - rendered) == 19, 'Coverage changed; review the M0 UI count'
assert (root / 'assets/anatomy/LICENSE').is_file() and (root / 'assets/anatomy/NOTICE').is_file()
for asset in asset_manifest['assets']:
    path = root / asset['path']
    assert path.is_file() and asset['license_verified']
    assert hashlib.sha256(path.read_bytes()).hexdigest() == asset['sha256'], f'Checksum mismatch {path}'
print(f'PASS anatomy: {len(rows)} native paths match SVG, {len(rendered)} mapped / {len(subdivision_ids-rendered)} list-only taxonomy subdivisions')
