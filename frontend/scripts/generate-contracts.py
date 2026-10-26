"""Generate frontend contracts from the authoritative API markdown. No business data."""
import json, re
from pathlib import Path
root = Path(__file__).resolve().parents[2]
text = (root/'docs/IECMP-接口开发文档-v1.6.md').read_text(encoding='utf-8')
heads = list(re.finditer(r'^#### .+?（(?:[^\n]*?，)?\s*([1-8]-\d{2})）\s*$', text, re.M))
contracts = {}
def clean(value):
    return value.strip().replace('`', '').replace('**','')
for index, match in enumerate(heads):
    block = text[match.end():heads[index+1].start() if index+1 < len(heads) else text.index('## 第 15 章')]
    basic = {}
    for name in ['接口名称','Path','Method','权限码','接口描述','是否需令牌']:
        hit = re.search(r'^\| '+name+r' \| (.+?) \|\s*$',block,re.M)
        if hit: basic[name] = clean(hit.group(1))
    if 'Path' not in basic: continue
    section, location, request, returns = 'basic', 'query', [], []
    response_heading = ''; parent = None
    for line in block.splitlines():
        if line.startswith('**请求参数'): section = 'request'; parent = None
        if line.startswith('**返回数据'): section = 'response'
        if line.startswith('**错误码') or line.startswith('**响应示例'): section = 'end'
        if section == 'request' and not line.startswith('|'):
            nested = re.search(r'^`([^`]+)`.*(?:元素结构|对象结构)', line)
            if nested: parent = nested.group(1)
            if '路径参数' in line: location = 'path'
            elif 'Query 参数' in line: location = 'query'
            elif 'Body 参数' in line: location = 'body'
            elif 'Form' in line or 'multipart' in line: location = 'form'
        if not line.startswith('|'): continue
        cells = [clean(x) for x in line.strip().strip('|').split('|')]
        if len(cells) < 3 or not re.match(r'^[a-zA-Z]\w*$',cells[0]): continue
        if section == 'request':
            request.append({'key':cells[0], 'type':cells[1], 'required':cells[2]=='必须', 'location':location, 'parent':parent, 'description': '；'.join(cells[3:])})
        elif section == 'response':
            returns.append({'key':cells[0], 'type':cells[1], 'description':'；'.join(cells[2:])})
    contracts[match.group(1)] = {'id':match.group(1),'name':basic.get('接口名称'), 'path':basic['Path'], 'method':basic['Method'].lower(), 'permission':basic.get('权限码'), 'description':basic.get('接口描述'), 'auth':basic.get('是否需令牌')=='是','fields':request,'returns':returns}
assert len(contracts)==186, f'Expected 186 contracts, got {len(contracts)}'
out = root/'frontend/src/config/contracts.json'
out.write_text(json.dumps(contracts,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print(f'Generated {len(contracts)} contracts: {out}')
for id in ['1-10','3-21','4-27','5-26','7-12','8-01']:
    c=contracts[id]; print(id,c['method'],c['path'],[(f['key'],f['location']) for f in c['fields']])
