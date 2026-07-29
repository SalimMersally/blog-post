---
description: Scaffold a new blog post directory (or a new part of an existing series) following the conventions in CLAUDE.md.
---

Scaffold a new blog post entry in this repo.

Arguments: $ARGUMENTS
Expected format: `<slug>` for a standalone post, or `<series-slug> <part-number> <part-slug>` for a series part (e.g. `code-liability 1 the-best-code-is-no-code`).

Steps:

1. Parse the arguments to determine whether this is a standalone post or a series part.
2. Determine the target path:
   - Standalone: `/<slug>/`
   - Series part: `/<series-slug>/<NN>-<part-slug>/` where NN is the zero-padded part number (01, 02, ...)
3. Create the directory with:
   - `blog-post.md` — empty, ready for content. Do not write anything into it yet.
   - `metadata.yaml` — copied from `/_templates/metadata.template.yaml`, with `slug` and `date_created` filled in, everything else left as `TODO`.
   - `media/manifest.yaml` — copied from `/_templates/media-manifest.template.yaml`, empty.
   - `demo/README.md` — copied from `/_templates/demo-readme.template.md`, with the post title filled in.
4. If this is the first part of a new series, also create `series-metadata.yaml` at the series root (copy structure from the standalone `metadata.template.yaml`, adapted for a series: series title, series slug, list of parts).
5. Do not write any blog content, code, or media at this step. Stop after scaffolding and confirm the created file tree back to the user.
6. Wait for the user to provide the post's topic, notes, quotes, and references before starting research or drafting.
