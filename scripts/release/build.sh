#!/usr/bin/env bash
# 构建平台发行包：./scripts/release/build.sh <semver>
# 示例：./scripts/release/build.sh 0.2.0
set -euo pipefail

VERSION="${1:?用法: $0 <version>  例如 0.2.0}"
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
ARTIFACT="xj-tysfrz-${VERSION}"
OUT_DIR="${ROOT}/dist/${ARTIFACT}"
BACKEND_ARTIFACT="xj-tysfrz-backend-${VERSION}.jar"

echo "==> 构建发行版 ${VERSION}"

cd "${ROOT}/backend"
mvn -q versions:set -DnewVersion="${VERSION}" -DgenerateBackupPoms=false
mvn -q clean package -DskipTests
mvn -q versions:revert

cd "${ROOT}/frontend"
VITE_APP_VERSION="${VERSION}" VITE_BUILD_TIME="$(date -u +%Y-%m-%dT%H:%M:%SZ)" npm run build

rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}/backend" "${OUT_DIR}/frontend" "${OUT_DIR}/config" "${OUT_DIR}/scripts"

cp "${ROOT}/backend/target/${BACKEND_ARTIFACT}" "${OUT_DIR}/backend/xj-tysfrz-backend-${VERSION}.jar"
cp -r "${ROOT}/frontend/dist" "${OUT_DIR}/frontend/"
cp "${ROOT}/config/nginx.conf.example" "${OUT_DIR}/config/"
cp "${ROOT}/docker/nginx/503.html" "${OUT_DIR}/config/503.html" 2>/dev/null || true

JAR_SHA="$(sha256sum "${OUT_DIR}/backend/xj-tysfrz-backend-${VERSION}.jar" | awk '{print $1}')"
DIST_TAR="${OUT_DIR}/frontend-dist.tar"
tar -cf "${DIST_TAR}" -C "${OUT_DIR}/frontend" dist
DIST_SHA="$(sha256sum "${DIST_TAR}" | awk '{print $1}')"
rm -f "${DIST_TAR}"

cat > "${OUT_DIR}/manifest.json" <<EOF
{
  "release": "${VERSION}",
  "builtAt": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
  "components": {
    "backend": {
      "version": "${VERSION}",
      "artifact": "backend/xj-tysfrz-backend-${VERSION}.jar",
      "sha256": "${JAR_SHA}",
      "minJvm": "21"
    },
    "frontend": {
      "version": "${VERSION}",
      "artifact": "frontend/dist",
      "sha256": "${DIST_SHA}"
    }
  },
  "compatibility": {
    "frontendRequiresBackend": "${VERSION}",
    "backendRequiresFrontend": "${VERSION}"
  },
  "database": {
    "flywayTarget": "1",
    "breaking": false
  }
}
EOF

echo "==> 产物目录: ${OUT_DIR}"
echo "==> manifest.json 已生成"
