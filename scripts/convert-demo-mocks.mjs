import fs from 'node:fs'
import path from 'node:path'

const root = path.resolve(import.meta.dirname, '..')

const mocksDir = path.join(root, 'frontend/src/mocks')
fs.mkdirSync(mocksDir, { recursive: true })

function convertCls() {
  const src = fs.readFileSync(path.join(root, 'docs/原始demo/sysu-cls.js'), 'utf8')
  const m = src.match(/const SYSU_CLASSIFICATION = (\[.*\]);/s)
  if (!m) throw new Error('SYSU_CLASSIFICATION not found')
  const out = `export interface ClassificationNode {
  code: string
  name: string
  level: number
  root: string
  parent?: string
  source?: string
  unit?: string
  hr_code?: string
  note?: string
}

export const SYSU_CLASSIFICATION: ClassificationNode[] = ${m[1]}
`
  fs.writeFileSync(path.join(mocksDir, 'sysu-cls.ts'), out)
}

function convertOrg() {
  const src = fs.readFileSync(path.join(root, 'docs/原始demo/sysu-org.js'), 'utf8')
  const m = src.match(/const SYSU_ORG = (\[.*?\]);/s)
  if (!m) throw new Error('SYSU_ORG not found')
  const out = `export interface OrgNode {
  code: string
  name: string
  type: string
  parent: string | null
  level: number
  staff: number
}

export const SYSU_ORG: OrgNode[] = ${m[1]}

export const ORG_BY_CODE: Record<string, OrgNode> = Object.fromEntries(
  SYSU_ORG.map((n) => [n.code, n]),
)

export const ORG_UNITS = SYSU_ORG.filter((n) => n.level >= 3)

export function orgChildren(code: string): OrgNode[] {
  return SYSU_ORG.filter((n) => n.parent === code)
}

export function orgName(code: string): string {
  return ORG_BY_CODE[code]?.name ?? code
}
`
  fs.writeFileSync(path.join(mocksDir, 'sysu-org.ts'), out)
}

convertCls()
convertOrg()
console.log('mock conversion done')
