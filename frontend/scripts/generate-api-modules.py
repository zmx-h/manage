import json,re
from pathlib import Path
r=Path(__file__).resolve().parents[1]
c=json.loads((r/'src/config/contracts.json').read_text(encoding='utf-8'))
for module,number in [('auth',1),('repo',2),('budget',3),('asset',4),('seat',5),('honor',6),('duty',7),('common',8)]:
 if module=='auth': continue
 lines=["// Generated from docs/IECMP-接口开发文档-v1.6.md; each method accepts documented field names.","import { invoke } from './client'",'export default {']
 for id,v in c.items():
  if not id.startswith(str(number)+'-'):continue
  lines.extend([f"  /** {id} {v['method'].upper()} {v['path']} · {v['name']} */",f"  '{id}': (values, options) => invoke('{id}', values, options),"])
 lines.append('}')
 (r/f'src/api/{module}.js').write_text('\n'.join(lines)+'\n',encoding='utf-8')
# Derived labels are convenience text; request keys and descriptions remain authoritative.
labels={}
for v in c.values():
 for f in v['fields']+v['returns']:
  d=f['description'].replace('；',' ').strip(' -')
  d=re.sub(r'^(?:必须|非必须)\s*[-\w.]*\s*','',d)
  d=re.split(r'[，；（(：:]|format:|例如|如 |，',d)[0].strip()
  if d and not re.match(r'^\d|^0 |^1 |^-',d) and len(d)<=12 and f['key'] not in labels: labels[f['key']]=d
(r/'src/config/field-labels.json').write_text(json.dumps(labels,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
for id in ['4-03','4-04','5-07','5-13','8-13','7-19','2-11']:
 print(id,c[id]['path'],[x['key'] for x in c[id]['returns']])
