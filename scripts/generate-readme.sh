#!/usr/bin/env bash
# Regenerates the "Posts" table in README.md from every metadata.yaml in the repo.
#
# Usage:
#   scripts/generate-readme.sh
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
README="$ROOT/README.md"
START="<!-- POSTS:START -->"
END="<!-- POSTS:END -->"

# extract <file> <key> -> quoted scalar value on a "key: "value"" line, or empty
extract() {
  awk -v key="$2" -F'"' '$0 ~ "^"key":" {print $2; exit}' "$1"
}

# extract_tags <file> -> comma-joined values from a "tags:\n  - "x"" block
extract_tags() {
  awk -F'"' '
    /^tags:/ { f=1; next }
    f && /^[a-zA-Z]/ { exit }
    f && /- *"/ { tags[n++] = $2 }
    END { for (i = 0; i < n; i++) printf "%s%s", (i ? ", " : ""), tags[i] }
  ' "$1"
}

dated=()
undated=()
while IFS= read -r -d '' meta; do
  dir="$(dirname "$meta")"
  rel="${dir#"$ROOT"/}"
  title="$(extract "$meta" title)"; title="${title:-(untitled)}"
  status="$(extract "$meta" status)"; status="${status:-draft}"
  tags="$(extract_tags "$meta")"
  date="$(extract "$meta" date_published)"
  [ "$date" = "TODO" ] && date=""

  link="./$rel/blog-post.md"
  demo="—"
  [ -d "$dir/demo" ] && demo="[demo](./$rel/demo/)"
  row="| [$title]($link) | ${date:-TBD} | $status | $tags | $demo |"

  if [ -n "$date" ]; then
    dated+=("$date"$'\t'"$row")
  else
    undated+=("$title"$'\t'"$row")
  fi
done < <(find "$ROOT" \
  \( -path "$ROOT/_templates" -o -path "$ROOT/.claude" -o -path "$ROOT/.git" \
     -o -path "$ROOT/node_modules" -o -path "$ROOT/scripts" \) -prune \
  -o -name metadata.yaml -print0)

tablefile="$(mktemp)"
trap 'rm -f "$tablefile"' EXIT

{
  echo "| Post | Published | Status | Tags | Demo |"
  echo "|---|---|---|---|---|"
  if [ "${#dated[@]}" -gt 0 ]; then
    printf '%s\n' "${dated[@]}" | sort -t$'\t' -k1,1r | cut -f2-
  fi
  if [ "${#undated[@]}" -gt 0 ]; then
    printf '%s\n' "${undated[@]}" | sort -t$'\t' -k1,1 | cut -f2-
  fi
} > "$tablefile"

awk -v tablefile="$tablefile" -v start="$START" -v end="$END" '
  $0 == start {
    print
    while ((getline line < tablefile) > 0) print line
    close(tablefile)
    skipping=1
    next
  }
  $0 == end { skipping=0 }
  !skipping
' "$README" > "$README.tmp" && mv "$README.tmp" "$README"

count=$(( ${#dated[@]} + ${#undated[@]} ))
echo "Updated README.md with $count post(s)."
