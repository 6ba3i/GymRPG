#!/usr/bin/env python3
"""Fail CI on unverified/missing/corrupted runtime asset records and schema drift."""
import hashlib
import json
from pathlib import Path
import jsonschema

root = Path(__file__).resolve().parents[1]

def load(path):
    return json.loads((root / path).read_text())

for name in ('food-import', 'exercise-import', 'asset-manifest'):
    jsonschema.Draft202012Validator(load(f'docs/{name}.schema.json')).validate(load(f'docs/{name}.example.json'))

manifests = list((root / 'assets').glob('**/*manifest*.json'))
assert manifests, 'No real asset provenance manifest exists'
count = 0
for path in manifests:
    data = json.loads(path.read_text())
    if 'assets' not in data:
        continue
    jsonschema.Draft202012Validator(load('docs/asset-manifest.schema.json')).validate(data)
    for asset in data['assets']:
        assert asset['license_verified'], f"Unverified {asset['id']}"
        file = root / asset['path']
        assert file.is_file(), file
        assert hashlib.sha256(file.read_bytes()).hexdigest() == asset['sha256'], f"Checksum mismatch: {file}"
        notice = asset.get('license_notice_path')
        assert notice and (root / notice).is_file(), f"Missing notice: {file}"
        count += 1
assert count > 0, 'No licensed assets verified'
print(f'PASS: example schemas and {count} licensed asset checksums')
