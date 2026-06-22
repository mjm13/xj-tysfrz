import fs from 'node:fs'
import path from 'node:path'

const root = path.resolve(import.meta.dirname, '..')
const src = fs.readFileSync(path.join(root, 'frontend/src/mocks/sysu-org.ts'), 'utf8')
const m = src.match(/export const SYSU_ORG: OrgNode\[\] = (\[[\s\S]*?\])\n/)
if (!m) throw new Error('SYSU_ORG not found')
const orgs = JSON.parse(m[1]).sort((a, b) => a.level - b.level || a.code.localeCompare(b.code))

const lines = [
  '-- org_node seed from SYSU_ORG mock',
  'INSERT INTO org_node (code, name, parent_code, level) VALUES',
]
const values = orgs.map((n) => {
  const parent = n.parent === null ? 'NULL' : `'${n.parent.replace(/'/g, "''")}'`
  const name = n.name.replace(/'/g, "''")
  return `  ('${n.code.replace(/'/g, "''")}', '${name}', ${parent}, ${n.level})`
})
lines.push(values.join(',\n'))
lines.push(';')

const out = path.join(root, 'backend/xj-tysfrz-business/src/main/resources/db/migration/V2__org_node.sql')

const ddl = `-- org_node minimal seed for identity-access DepartmentRef / DataScopeResolver
CREATE TABLE IF NOT EXISTS org_node (
    code        VARCHAR(64)  NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    parent_code VARCHAR(64)  NULL,
    level       INT          NOT NULL
);

CREATE INDEX idx_org_node_parent ON org_node (parent_code);

`
fs.writeFileSync(out, ddl + '\n' + lines.join('\n') + '\n')
console.log(`Wrote ${out} with ${orgs.length} nodes`)
