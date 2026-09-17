# Repo purpose

This repo holds a series of technical blog posts and their runnable demos, published to a personal portfolio site via Sanity CMS. Audience: general developer audience, with particular attention to newer generations of developers who may not recognize older tech-culture references.

# Structure

```
/CLAUDE.md
/README.md                      <- index / bookmark table, generated from metadata.yaml files
/.claude/
  settings.json
  commands/
    new-post.md                 <- /new-post scaffolds a new post or series part
/_templates/
  metadata.template.yaml
  media-manifest.template.yaml
  demo-readme.template.md
/<slug>/                        <- standalone post
  blog-post.md
  metadata.yaml
  media/
    manifest.yaml
  demo/
    README.md
    ...runnable demo code...
/<series-slug>/                 <- multi-part series, shares a common root
  series-metadata.yaml
  01-<part-slug>/
    blog-post.md
    metadata.yaml
    media/
    demo/
  02-<part-slug>/
    ...
```

Directory names should match the post's slug for traceability back to Sanity.

# Editorial rules

- No em dashes anywhere in blog copy.
- Prefer inline links over reference lists. No "References" section at the end of a post.
- Hooks and cultural references should favor widely-recognized, current formats (e.g. Reddit memes) over niche or dated ones (e.g. xkcd) unless the reference is paired with enough context that a reader unfamiliar with it still gets the joke.
- Quotes with uncertain/unverified attribution can be kept if they're widely circulated and recognizable, but flag the attribution uncertainty to the user before finalizing rather than silently including or silently dropping it.
- Before finalizing a post, check for formatting collisions: images or embeds breaking numbered lists, code blocks splitting mid-thought, etc.
- Editorial restraint: trim counterpoint sections, tangents, and name-drops that don't serve the core argument. Keep focus tight.
- Match the prose rhythm of the legacy-code posts: develop each idea in a natural paragraph of several connected sentences, vary sentence length, and reserve one-sentence paragraphs for deliberate emphasis. Avoid choppy runs of very short paragraphs and excessive punctuation used to manufacture rhythm. Bulleted and ordered lists are welcome when they make definitions, comparisons, or sequences clearer.
- Keep article and section titles short enough to remain on one line in the normal portfolio layout.

# Code conventions

- Default language for code examples: **Java**.
- Frontend code examples: **Angular + TypeScript**.
- Only deviate from these defaults if the post is specifically about another language/framework, and confirm with the user first.

# Metadata

Every post gets its own `metadata.yaml` (see `_templates/metadata.template.yaml`). Do not fold metadata into `blog-post.md` as prose. Required fields: title, slug, tags, meta description (2-3 options), estimated read time, publish status, references (list of source links used while researching/writing; empty list if none). Series posts also get a `series-metadata.yaml` at the series root.

# Media

Every `media/` folder gets a `manifest.yaml` (see `_templates/media-manifest.template.yaml`) listing, per asset: source URL, license, required attribution text, and whether it's a placeholder pending a licensed replacement. Never add media without a manifest entry.

# Demos

Each `demo/` folder is self-contained and runnable on its own (own build file, own README explaining how to run it). Don't let it become a dumping ground for inline snippets that don't execute.

# README index

`README.md`'s bookmark table is generated from each post's `metadata.yaml`, not hand-maintained. If you add or rename a post, regenerate the table rather than editing it directly.

# Workflow

When given a new blog topic: research it (check any references the user provides, plus relevant discussion on Medium, Reddit, Quora, etc.), draft the post, then produce the metadata package. Use the `/new-post` command to scaffold the directory before writing content into it.
